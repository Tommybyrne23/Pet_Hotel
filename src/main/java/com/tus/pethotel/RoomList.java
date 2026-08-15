package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tus.Services.Species;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("roomList")
@ApplicationScoped
public class RoomList implements Serializable {

	private static final long serialVersionUID = 1L;

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

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public int getNumberOfRooms() {
		return rooms.size();
	}
}