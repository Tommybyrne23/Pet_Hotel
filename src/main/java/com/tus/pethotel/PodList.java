package com.tus.pethotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.tus.Services.Species;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("podList")
@ApplicationScoped
public class PodList implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Pod> pods;

	@Inject
	private RoomList roomList;

	@Inject
	private ReservationList reservationList;

	public PodList() {
		this.pods = new ArrayList<>();
	}

	// A pod label must be unique inside its own room. Two rooms can both
	// have a pod called "1" without clashing.
	public boolean isLabelTakenInRoom(String label, int roomID) {

		if (label == null) {
			return false;
		}

		for (Pod pod : pods) {
			if (pod.getRoomID() == roomID
					&& label.trim().equalsIgnoreCase(pod.getLabel())) {
				return true;
			}
		}
		return false;
	}

	public int countPodsInRoom(int roomID) {

		int count = 0;

		for (Pod pod : pods) {
			if (pod.getRoomID() == roomID) {
				count++;
			}
		}
		return count;
	}

	// True when the room has no space left for another pod
	public boolean isRoomFull(int roomID) {

		Room room = roomList.findByID(roomID);

		if (room == null) {
			return true;
		}
		return countPodsInRoom(roomID) >= room.getCapacity();
	}

	/*
	 * Adds a pod. Refuses if the label is missing, the room does not exist,
	 * the label is already used in that room, or the room is at capacity.
	 */
	public boolean addPod(Pod pod) {

		if (pod == null || pod.getLabel() == null || pod.getLabel().trim().isEmpty()) {
			return false;
		}

		if (roomList.findByID(pod.getRoomID()) == null) {
			return false;
		}

		if (isLabelTakenInRoom(pod.getLabel(), pod.getRoomID())) {
			return false;
		}

		if (isRoomFull(pod.getRoomID())) {
			return false;
		}

		pod.setLabel(pod.getLabel().trim());
		pods.add(pod);
		return true;
	}

	public Pod findByID(int podID) {

		for (Pod pod : pods) {
			if (pod.getPodID() == podID) {
				return pod;
			}
		}
		return null;
	}

	// A pod takes its species from the room it sits in
	public Species getSpeciesFor(Pod pod) {

		Room room = roomList.findByID(pod.getRoomID());
		return room == null ? null : room.getSpecies();
	}

	public List<Pod> getPodsForRoom(int roomID) {

		List<Pod> matches = new ArrayList<>();

		for (Pod pod : pods) {
			if (pod.getRoomID() == roomID) {
				matches.add(pod);
			}
		}
		return matches;
	}

	public List<Pod> getPodsForSpecies(Species species) {

		List<Pod> matches = new ArrayList<>();

		for (Pod pod : pods) {
			if (getSpeciesFor(pod) == species) {
				matches.add(pod);
			}
		}
		return matches;
	}


	// AVAILABILITY

	/*
	 * Two stays clash only if each starts before the other ends.
	 *
	 * Both comparisons are strictly "before", which is what allows same-day
	 * turnover: checkout is 11:00 and check-in is 14:00, so a pet leaving on
	 * the 20th and another arriving on the 20th do not clash. Using "not
	 * after" here would lose a night on every changeover.
	 */
	private boolean datesOverlap(LocalDate startA, LocalDate endA,
			LocalDate startB, LocalDate endB) {

		return startA.isBefore(endB) && startB.isBefore(endA);
	}

	// Parses a yyyy-MM-dd string, returning null rather than throwing
	private LocalDate parseDate(String date) {

		if (date == null || date.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(date.trim());
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/*
	 * A pod is free for these dates when it is in service and no confirmed
	 * booking against it overlaps them. Cancelled bookings are ignored, so
	 * cancelling a stay puts the pod straight back into circulation.
	 */
	public boolean isPodAvailable(int podID, String checkInDate, String checkOutDate) {

		Pod pod = findByID(podID);
		LocalDate checkIn = parseDate(checkInDate);
		LocalDate checkOut = parseDate(checkOutDate);

		if (pod == null || checkIn == null || checkOut == null || !checkOut.isAfter(checkIn) || pod.isOutOfService()) {
			return false;
		}

		for (Reservation reservation : reservationList.getReservations()) {

			if (reservation.getPodID() != podID || !"Confirmed".equalsIgnoreCase(reservation.getStatus())) {
				continue;
			}

			LocalDate bookedIn = parseDate(reservation.getCheckInDate());
			LocalDate bookedOut = parseDate(reservation.getCheckOutDate());

			if (bookedIn == null || bookedOut == null) {
				continue;
			}

			if (datesOverlap(checkIn, checkOut, bookedIn, bookedOut)) {
				return false;
			}
		}
		return true;
	}

	// The first free pod for this species, or null when they are all taken
	public Pod findAvailablePod(Species species, String checkInDate, String checkOutDate) {

		for (Pod pod : getPodsForSpecies(species)) {
			if (isPodAvailable(pod.getPodID(), checkInDate, checkOutDate)) {
				return pod;
			}
		}
		return null;
	}

	// Feeds the "3 of 10 free" style message on the booking page
	public int countAvailablePods(Species species, String checkInDate, String checkOutDate) {

		int free = 0;

		for (Pod pod : getPodsForSpecies(species)) {
			if (isPodAvailable(pod.getPodID(), checkInDate, checkOutDate)) {
				free++;
			}
		}
		return free;
	}

	public ArrayList<Pod> getPods() {
		return pods;
	}

	public int getNumberOfPods() {
		return pods.size();
	}

	// used for testing
	public void setRoomList(RoomList roomList) {
		this.roomList = roomList;
	}

	public void setReservationList(ReservationList reservationList) {
		this.reservationList = reservationList;
	}
}