package com.tus.pethotel;

import java.io.Serializable;
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
	private Integer roomCapacity = 10;

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

		if (roomCapacity == null || roomCapacity <= 0) {
			return blocked(ROOM_FORM_ID, "roomCapacityInput", "Enter a capacity of at least one pod.");
		}

		Room room = new Room(roomName.trim(), roomLocation.trim(), roomSpecies, roomCapacity);

		if (!roomList.addRoom(room)) {
			return blocked(ROOM_FORM_ID, "roomNameInput", "A room with that name already exists.");
		}

		addSuccess(room.getName() + " has been added, with space for "
				+ room.getCapacity() + " pods.");

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

		// Checked here so the admin gets a message that names the problem,
		// rather than the plain false that addPod returns
		if (podList.isRoomFull(podRoomID)) {
			Room full = roomList.findByID(podRoomID);
			return blocked(POD_FORM_ID, "podRoomInput", full.getName()
					+ " is already at its capacity of " + full.getCapacity() + " pods.");
		}

		if (podList.isLabelTakenInRoom(podLabel, podRoomID)) {
			return blocked(POD_FORM_ID, "podLabelInput",
					"That room already has a pod with that label.");
		}

		Pod pod = new Pod(podLabel.trim(), podRoomID);

		if (!podList.addPod(pod)) {
			return blocked(POD_FORM_ID, "podLabelInput", "The pod could not be added.");
		}

		addSuccess("Pod " + pod.getLabel() + " has been added to "
				+ roomList.findByID(podRoomID).getName() + ".");

		return reset();
	}


	// Takes a pod in or out of service, e.g. for cleaning or repairs
	public String toggleOutOfService(Pod pod) {

		if (pod != null) {
			pod.setOutOfService(!pod.isOutOfService());
		}
		return null;
	}


	public String reset() {

		roomName = null;
		roomLocation = null;
		roomSpecies = null;
		roomCapacity = 10;
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

		String today = LocalDate.now().toString();
		String tomorrow = LocalDate.now().plusDays(1).toString();

		return podList.isPodAvailable(pod.getPodID(), today, tomorrow)
				? "Available" : "Occupied";
	}

	// Shows how full each room is in the dropdown, e.g. "The Cattery (3/10)"
	public String getRoomLabel(Room room) {
		return room.getName() + " (" + podList.countPodsInRoom(room.getRoomID())
				+ "/" + room.getCapacity() + ")";
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
			context.addMessage(formId + ":" + fieldId,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
		}
		return null;
	}

	private void addSuccess(String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));

			// carries the message across the redirect
			context.getExternalContext().getFlash().setKeepMessages(true);
		}
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

	public Integer getRoomCapacity() {
		return roomCapacity;
	}

	public void setRoomCapacity(Integer roomCapacity) {
		this.roomCapacity = roomCapacity;
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
}