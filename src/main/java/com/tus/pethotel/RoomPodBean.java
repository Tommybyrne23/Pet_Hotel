package com.tus.pethotel;

import java.io.Serializable;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import com.tus.Services.Species;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("RoomPodBean")
@SessionScoped
public class RoomPodBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// Message client ids are built from these, so if a form id changes in
	// the page it must change here too.
	private static final String ROOM_FORM_ID = "adminAddRoom";
	private static final String POD_FORM_ID = "adminAddPod";

	// Add-a-room form
	private String roomName;
	private String roomLocation;
	private Species roomSpecies;

	// Room currently being edited
	private int editingRoomID = 0;

	// Temporary values used while editing a room
	private String editRoomName;
	private String editRoomLocation;
	private Species editRoomSpecies;

	// Table filter, not part of either form. Defaults to today.
	private String availabilityDate = LocalDate.now().toString();

	// Add-a-pod form
	private String podLabel;
	private Integer podRoomID;

	@Inject
	private RoomList roomList;

	@Inject
	private PodList podList;


	// ADDING A ROOM

	public String addRoom() {

		if (roomName == null || roomName.trim().isEmpty()) {
			return blocked(ROOM_FORM_ID, "roomNameInput", "A room name is required.");
		}

		if (roomLocation == null || roomLocation.trim().isEmpty()) {
			return blocked(ROOM_FORM_ID, "roomLocationInput", "An area or location is required.");
		}

		if (roomSpecies == null) {
			return blocked(ROOM_FORM_ID, "roomSpeciesInput", "Choose which animal this room is for.");
		}

		Room room = new Room(roomName.trim(), roomLocation.trim(), roomSpecies);

		if (!roomList.addRoom(room)) {
			return blocked(ROOM_FORM_ID, "roomNameInput", "A room with that name already exists.");
		}

		addSuccess(room.getName() + " has been added. Add pods to it below.");

		return reset();
	}

	// ADDING A POD

	public String addPod() {

		if (podLabel == null || podLabel.trim().isEmpty()) {
			return blocked(POD_FORM_ID, "podLabelInput", "A pod label is required.");
		}

		if (podRoomID == null || podRoomID == 0) {
			return blocked(POD_FORM_ID, "podRoomInput", "Choose which room this pod is in.");
		}

		

		if (podList.isLabelTakenInRoom(podLabel, podRoomID)) {
			return blocked(POD_FORM_ID, "podLabelInput", "That room already has a pod with that label.");
		}

		Pod pod = new Pod(podLabel.trim(), podRoomID);

		if (!podList.addPod(pod)) {
			return blocked(POD_FORM_ID, "podLabelInput", "The pod could not be added.");
		}

		addSuccess("Pod " + pod.getLabel() + " has been added to " + roomList.findByID(podRoomID).getName() + " and is out of service. Return it to service when it is ready for use.");

		return reset();
	}

	// Takes a pod in or out of service, e.g. for cleaning or repairs.
	// Refuses to take a pod offline while a pet is in it - the button is
	// disabled for those pods, but the rule belongs here too rather than
	// relying on the page to enforce it.
	public String toggleOutOfService(Pod pod) {

		if (pod == null) {
			return null;
		}

		if (!pod.isOutOfService() && isPodLocked(pod)) {
			addError("Pod " + pod.getLabel()
			+ " has a pet booked in and cannot be taken out of service.");
			return null;
		}

		pod.setOutOfService(!pod.isOutOfService());
		return null;
	}
	
	/**
	 * Deletes a pod if it is out of service and has no
	 * confirmed booking or active hold within the next 365 days.
	 */
	public String deletePod(Pod pod) {

	    if (pod == null) {
	        return null;
	    }

	    boolean deleted = podList.deletePod(pod.getPodID());

	    FacesContext context = FacesContext.getCurrentInstance();

	    if (context != null) {

	        if (deleted) {

	            context.addMessage(
	                "podTableForm",
	                new FacesMessage(
	                    FacesMessage.SEVERITY_INFO,
	                    "Pod " + pod.getLabel() + " has been deleted.",
	                    null
	                )
	            );

	        } else {

	            context.addMessage(
	                "podTableForm",
	                new FacesMessage(
	                    FacesMessage.SEVERITY_ERROR,
	                    "Pod " + pod.getLabel()
	                        + " cannot be deleted. It must be out of service "
	                        + "and have no bookings within the next 365 days.",
	                    null
	                )
	            );
	        }
	    }

	    return null;
	}

	public String reset() {

		roomName = null;
		roomLocation = null;
		roomSpecies = null;
		podLabel = null;
		podRoomID = null;

		return "viewPodsRooms?faces-redirect=true";
	}


	// TABLE HELPERS

	public String getRoomName(Pod pod) {
		Room room = roomList.findByID(pod.getRoomID());
		return room == null ? "-" : room.getName();
	}

	public String getSpeciesLabel(Pod pod) {
		Species species = podList.getSpeciesFor(pod);
		return species == null ? "-" : species.getLabel();
	}

	/*
	 * Whether the pod is free tonight. The admin page has no date picker, so
	 * "right now" means a one-night stay starting today - the same overlap
	 * check the booking page uses, just with today's dates.
	 */
	public String getPodStatus(Pod pod) {

		if (pod.isOutOfService()) {
			return "Out of service";
		}

		LocalDate night = parseAvailabilityDate();

		return podList.isPodAvailable(pod.getPodID(), night.toString(),
				night.plusDays(1).toString()) ? "Available" : "Occupied";
	}

	// Pill styling for the status column, using the badge classes
	// already in styles.css
	public String getPodStatusClass(Pod pod) {

		if (pod.isOutOfService()) {
			return "status-cancelled";
		}

		return "Available".equals(getPodStatus(pod))
				? "status-available" : "status-pending";
	}
		
		
	// Shows how many pods each room has, e.g. "Cat Haven (3 pods)"
	public String getRoomLabel(Room room) {
		return room.getName() + " (" + podList.countPodsInRoom(room.getRoomID()) + " pods)";
	}

	public Species[] getSpeciesOptions() {
		return Species.values();
	}

	public List<Room> getRoomOptions() {
		return roomList.getRooms();
	}


	// MESSAGE HELPERS

	// Attaches the message to a field and returns null, so callers can
	// write "return blocked(...)"
	private String blocked(String formId, String fieldId, String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(formId + ":" + fieldId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
		}
		return null;
	}

	private void addSuccess(String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));

			// carries the message across the redirect
			context.getExternalContext().getFlash().setKeepMessages(true);
		}
	}
	
	private void addError(String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
		}
	}
	
	// How many of this room's pods are open for business. An offline pod
	// still occupies its physical slot, so it counts toward Total Pods
	// but not toward this.
	public int getPodsInService(Room room) {
		return podList.countInServicePodsInRoom(room.getRoomID());
	}
	
	/*
	 * "3/5" - pods free for the night of the selected date, out of the pods
	 * open for use. An offline pod counts in neither figure.
	 */
	public String getPodsAvailable(Room room) {

		LocalDate night = parseAvailabilityDate();
		String from = night.toString();
		String to = night.plusDays(1).toString();

		int free = 0;

		for (Pod pod : podList.getPodsForRoom(room.getRoomID())) {
			if (podList.isPodAvailable(pod.getPodID(), from, to)) {
				free++;
			}
		}

		return free + "/" + podList.countInServicePodsInRoom(room.getRoomID());
	}

	// Falls back to today if the field is empty or unparseable
	private LocalDate parseAvailabilityDate() {

		if (availabilityDate == null || availabilityDate.isBlank()) {
			return LocalDate.now();
		}
		try {
			return LocalDate.parse(availabilityDate.trim());
		} catch (java.time.format.DateTimeParseException e) {
			return LocalDate.now();
		}
	}
	
	/*
	 * Pods grouped by the room they sit in, so the inventory reads room by
	 * room rather than in the order the admin happened to add them.
	 *
	 * Built by walking the rooms rather than sorting the pods, so the table
	 * follows the same room order as the Rooms list above it.
	 */
	public List<Pod> getPodsByRoom() {

		List<Pod> grouped = new ArrayList<>();

		for (Room room : roomList.getRooms()) {
			grouped.addAll(podList.getPodsForRoom(room.getRoomID()));
		}

		return grouped;
	}

	// Reloads the rooms table against the newly picked date
	public String applyAvailabilityDate() {
		return null;			// the value is already bound; returning null just re-renders
	}
	
	/*
	 * A pod cannot be taken offline while it has a pet in it. Locked when
	 * occupied on the selected date (so the button agrees with the status
	 * column) or occupied today (so viewing a future date cannot unlock a
	 * pod that is in use right now).
	 */
	public boolean isPodLocked(Pod pod) {

		if (pod.isOutOfService()) {
			return false;			// always allow returning a pod to service
		}

		LocalDate selected = parseAvailabilityDate();

		boolean busyOnSelectedDate = !podList.isPodAvailable(pod.getPodID(),
				selected.toString(), selected.plusDays(1).toString());

		LocalDate today = LocalDate.now();

		boolean busyToday = !podList.isPodAvailable(pod.getPodID(),
				today.toString(), today.plusDays(1).toString());

		return busyOnSelectedDate || busyToday;
	}
	
	public String deleteRoom(Room room) {

	    if (room == null) {
	        return null;
	    }

	    boolean deleted = roomList.deleteRoom(room.getRoomID());

	    FacesContext context = FacesContext.getCurrentInstance();

	    if (context != null) {

	        if (deleted) {

	            context.addMessage(
	                "roomsTableForm",
	                new FacesMessage(
	                    FacesMessage.SEVERITY_INFO,
	                    "Room " + room.getName() + " has been deleted.",
	                    null
	                )
	            );

	        } else {

	            context.addMessage(
	                "roomsTableForm",
	                new FacesMessage(
	                    FacesMessage.SEVERITY_ERROR,
	                    "Room " + room.getName()
	                        + " cannot be deleted. All pods in the room "
	                        + "must be out of service and have no bookings "
	                        + "within the next 365 days.",
	                    null
	                )
	            );
	        }
	    }

	    return null;
	}
	
	public String startEditRoom(Room room) {

	    if (room == null) {
	        return null;
	    }

	    // A room can only be edited if every pod in it satisfies
	    // the same rules as deleting that pod.
	    if (!roomList.canEditRoom(room.getRoomID())) {

	        FacesContext context = FacesContext.getCurrentInstance();

	        if (context != null) {
	            context.addMessage(
	                "roomsTableForm",
	                new FacesMessage(
	                    FacesMessage.SEVERITY_ERROR,
	                    "Room " + room.getName()
	                        + " cannot be edited. All pods in the room "
	                        + "must be out of service and have no bookings "
	                        + "within the next 365 days.",
	                    null
	                )
	            );
	        }

	        return null;
	    }

	    editingRoomID = room.getRoomID();

	    editRoomName = room.getName();
	    editRoomLocation = room.getLocation();
	    editRoomSpecies = room.getSpecies();

	    return null;
	}
	
	public boolean isEditingRoom(Room room) {

	    if (room == null) {
	        return false;
	    }

	    return room.getRoomID() == editingRoomID;
	}
	
	public String saveRoom(Room room) {

	    if (room == null) {
	        return null;
	    }

	    if (editRoomName == null || editRoomName.trim().isEmpty()) {

	        return blocked(
	            "roomsTableForm",
	            "roomEditNameInput",
	            "A room name is required."
	        );
	    }

	    if (editRoomLocation == null || editRoomLocation.trim().isEmpty()) {

	        return blocked(
	            "roomsTableForm",
	            "roomEditLocationInput",
	            "An area or location is required."
	        );
	    }

	    if (editRoomSpecies == null) {

	        return blocked(
	            "roomsTableForm",
	            "roomEditSpeciesInput",
	            "Choose which animal this room is for."
	        );
	    }

	    String newName = editRoomName.trim();
	    String newLocation = editRoomLocation.trim();

	    // Room names must be unique, excluding the room being edited.
	    if (roomList.isNameTakenByAnother(
	            newName,
	            room.getRoomID())) {

	        return blocked(
	            "roomsTableForm",
	            "roomEditNameInput",
	            "A room with that name already exists."
	        );
	    }

	    boolean edited = roomList.editRoom(
	        room.getRoomID(),
	        newName,
	        newLocation,
	        editRoomSpecies
	    );

	    if (!edited) {
	        addError("The room could not be updated.");
	        return null;
	    }

	    editingRoomID = 0;
	    editRoomName = null;
	    editRoomLocation = null;
	    editRoomSpecies = null;

	    addSuccess("Room " + room.getName() + " has been updated.");

	    return null;
	}
	
	public String cancelRoomEdit() {

	    editingRoomID = 0;
	    editRoomName = null;
	    editRoomLocation = null;
	    editRoomSpecies = null;

	    return null;
	}
	
	// GETTERS AND SETTERS

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomLocation() {
		return roomLocation;
	}

	public void setRoomLocation(String roomLocation) {
		this.roomLocation = roomLocation;
	}

	public Species getRoomSpecies() {
		return roomSpecies;
	}

	public void setRoomSpecies(Species roomSpecies) {
		this.roomSpecies = roomSpecies;
	}

	public String getPodLabel() {
		return podLabel;
	}

	public void setPodLabel(String podLabel) {
		this.podLabel = podLabel;
	}

	public Integer getPodRoomID() {
		return podRoomID;
	}

	public void setPodRoomID(Integer podRoomID) {
		this.podRoomID = podRoomID;
	}

	// used for testing
	public void setRoomList(RoomList roomList) {
		this.roomList = roomList;
	}

	public void setPodList(PodList podList) {
		this.podList = podList;
	}
	
	// The room's size is however many pods have been added to it
	public int getTotalPods(Room room) {
		return podList.countPodsInRoom(room.getRoomID());
	}
	
	public String getAvailabilityDate() {
		return availabilityDate;
	}

	public void setAvailabilityDate(String availabilityDate) {
		this.availabilityDate = availabilityDate;
	}
	
	public int getEditingRoomID() {
	    return editingRoomID;
	}

	public void setEditingRoomID(int editingRoomID) {
	    this.editingRoomID = editingRoomID;
	}

	public String getEditRoomName() {
	    return editRoomName;
	}

	public void setEditRoomName(String editRoomName) {
	    this.editRoomName = editRoomName;
	}

	public String getEditRoomLocation() {
	    return editRoomLocation;
	}

	public void setEditRoomLocation(String editRoomLocation) {
	    this.editRoomLocation = editRoomLocation;
	}

	public Species getEditRoomSpecies() {
	    return editRoomSpecies;
	}

	public void setEditRoomSpecies(Species editRoomSpecies) {
	    this.editRoomSpecies = editRoomSpecies;
	}
}