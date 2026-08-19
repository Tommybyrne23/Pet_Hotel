package com.tus.pethotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tus.services.Species;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;

@Named("roomList")
@ApplicationScoped
public class RoomList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PodList podList;

	private ArrayList<Room> rooms;

	public RoomList() {

		this.rooms = new ArrayList<>();

		rooms.add(new Room("Lakeside Kennels", "North Block", Species.DOG));
		rooms.add(new Room("Cat Haven", "South Block", Species.CAT));
		rooms.add(new Room("Aviary", "East Wing", Species.BIRD));
		rooms.add(new Room("Reptile House", "East Wing", Species.REPTILE));
		rooms.add(new Room("Aquatics Room", "West Wing", Species.FISH));
	}

	// A room name must be unique
	public boolean isNameTaken(String name) {
		return isNameTakenByAnother(name, 0);
	}

	public boolean isNameTakenByAnother(String name, int ignoreRoomID) {

		if (name == null) {
			return false;
		}

		for (Room existing : rooms) {

			if (existing.getRoomID() == ignoreRoomID) {
				continue;
			}

			if (name.trim().equalsIgnoreCase(existing.getName())) {
				return true;
			}
		}
		return false;
	}

	// adds the room only if it is complete and the name is free
	public boolean addRoom(Room room) {

		if (room == null || room.getName() == null || room.getName().trim().isEmpty()
				|| room.getSpecies() == null) {
			return false;
		}

		if (isNameTaken(room.getName())) {
			return false;
		}

		rooms.add(room);
		return true;
	}

	public Room findByID(int roomID) {

		for (Room room : rooms) {
			if (room.getRoomID() == roomID) {
				return room;
			}
		}
		return null;
	}

	public List<Room> getRoomsForSpecies(Species species) {

		List<Room> matches = new ArrayList<>();

		for (Room room : rooms) {
			if (room.getSpecies() == species) {
				matches.add(room);
			}
		}
		return matches;
	}
	
	public boolean deleteRoom(int roomID) {

	    Room room = findByID(roomID);

	    if (room == null) {
	        return false;
	    }

	    List<Pod> roomPods = podList.getPodsForRoom(roomID);

	    // Every pod in the room must be eligible for deletion
	    for (Pod pod : roomPods) {

	        if (!podList.canDeletePod(pod.getPodID())) {
	            return false;
	        }
	    }

	    // All pods passed the check, so delete the pods first
	    for (Pod pod : roomPods) {
	        podList.deletePod(pod.getPodID());
	    }

	    // Now remove the room itself
	    rooms.remove(room);

	    return true;
	}
	
	public boolean canEditRoom(int roomID) {

	    Room room = findByID(roomID);

	    if (room == null) {
	        return false;
	    }

	    List<Pod> roomPods = podList.getPodsForRoom(roomID);

	    // Every pod in the room must satisfy the same rules
	    // as a pod being deleted.
	    for (Pod pod : roomPods) {

	        if (!podList.canDeletePod(pod.getPodID())) {
	            return false;
	        }
	    }

	    return true;
	}


	public boolean editRoom(int roomID, String newName,
	        String newLocation, Species newSpecies) {

	    Room room = findByID(roomID);

	    if (room == null) {
	        return false;
	    }

	    // Name is required
	    if (newName == null || newName.trim().isEmpty()) {
	        return false;
	    }

	    // Location is required
	    if (newLocation == null || newLocation.trim().isEmpty()) {
	        return false;
	    }

	    // Species is required
	    if (newSpecies == null) {
	        return false;
	    }

	    newName = newName.trim();
	    newLocation = newLocation.trim();

	    // Room names must be unique, but allow the room
	    // to keep its existing name.
	    if (isNameTakenByAnother(newName, roomID)) {
	        return false;
	    }

	    room.setName(newName);
	    room.setLocation(newLocation);
	    room.setSpecies(newSpecies);

	    return true;
	}
	
	public boolean isRoomLocked(int roomID, String selectedDate) {

	    if (selectedDate == null || selectedDate.isBlank()) {
	        return false;
	    }

	    List<Pod> roomPods = podList.getPodsForRoom(roomID);

	    for (Pod pod : roomPods) {

	        if (podList.isPodOccupied(
	                pod.getPodID(),
	                selectedDate,
	                LocalDate.parse(selectedDate).plusDays(1).toString())) {

	            return true;
	        }
	    }

	    return false;
	}
	
	public boolean toggleRoomPodsOutOfService(int roomID, String selectedDate) {

	    Room room = findByID(roomID);

	    if (room == null) {
	        return false;
	    }

	    List<Pod> roomPods = podList.getPodsForRoom(roomID);

	    if (roomPods.isEmpty()) {
	        return false;
	    }

	    // If at least one pod is currently in service,
	    // this is a "take all offline" operation.
	    boolean takingOffline = false;

	    for (Pod pod : roomPods) {
	        if (!pod.isOutOfService()) {
	            takingOffline = true;
	            break;
	        }
	    }

	    // Only check occupancy when taking pods offline.
	    if (takingOffline && isRoomLocked(roomID, selectedDate)) {
	        return false;
	    }

	    // Bulk toggle all pods.
	    for (Pod pod : roomPods) {
	        pod.setOutOfService(takingOffline);
	    }

	    return true;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public int getNumberOfRooms() {
		return rooms.size();
	}
	
	public void setPodList(PodList podList) {
	    this.podList = podList;
	}
}