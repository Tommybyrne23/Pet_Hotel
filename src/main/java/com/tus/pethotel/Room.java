package com.tus.pethotel;

import java.io.Serializable;

import com.tus.Services.Species;

/*
 * A physical area of the hotel, e.g. "Lakeside Kennels".
 *
 * A room holds pods for one species only, which is how the hotel is laid
 * out in practice and stops a cat pod ending up in the kennels.
 *
 * Not to be confused with ServiceCategory.POD, which is the boarding
 * listing a customer books. This is the physical space they board in.
 */
public class Room implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int uuID = 0;

	private int roomID;
	private String name;
	private String location;
	private Species species;
	private int capacity;		// the most pods that fit in this room

	public Room() {
	}

	public Room(String name, String location, Species species, int capacity) {
		uuID++;
		this.roomID = uuID;
		this.name = name;
		this.location = location;
		this.species = species;
		this.capacity = capacity;
	}

	// GETTERS AND SETTERS

	public int getRoomID() {
		return roomID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
