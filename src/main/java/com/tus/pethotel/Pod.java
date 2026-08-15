package com.tus.pethotel;

import java.io.Serializable;

/*
 * A single physical space for one pet. A pod belongs to exactly one room
 * and takes its species from that room.
 *
 * A pod is free is worked out from
 * the reservations against it for a given date range
 */
public class Pod implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int uuID = 0;

	private int podID;
	private String label;		// e.g. "K1"
	private int roomID;
	private boolean outOfService;	// admin can take a pod offline for repairs

	public Pod() {
	}

	public Pod(String label, int roomID) {
		uuID++;
		this.podID = uuID;
		this.label = label;
		this.roomID = roomID;
	}

	// GETTERS AND SETTERS

	public int getPodID() {
		return podID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public boolean isOutOfService() {
		return outOfService;
	}

	public void setOutOfService(boolean outOfService) {
		this.outOfService = outOfService;
	}
}