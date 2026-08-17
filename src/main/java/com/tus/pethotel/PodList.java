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
import jakarta.annotation.PostConstruct;

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
	
	/*
	 * Seeds a starting set of pods. Runs after CDI has injected roomList,
	 * so each pod takes its room ID from the actual Room object rather
	 * than assuming the rooms were numbered from 1.
	 */
	@PostConstruct
	public void init() {

		if (!pods.isEmpty()) {
			return;
		}

		for (Room room : roomList.getRooms()) {

			String prefix;
			int count;

			switch (room.getSpecies()) {
				case DOG:     prefix = "K"; count = 6; break;
				case CAT:     prefix = "C"; count = 5; break;
				case BIRD:    prefix = "A"; count = 4; break;
				case REPTILE: prefix = "R"; count = 3; break;
				case FISH:    prefix = "F"; count = 4; break;
				default:      prefix = "P"; count = 3; break;
			}

			for (int i = 1; i <= count; i++) {
				pods.add(new Pod(prefix + i, room.getRoomID()));
			}
		}

		// One pod starts offline so the inventory shows the Out of service
		// state without an admin having to take one offline by hand
		List<Pod> reptilePods = getPodsForSpecies(Species.REPTILE);
		if (!reptilePods.isEmpty()) {
			reptilePods.get(reptilePods.size() - 1).setOutOfService(true);
		}
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

	// Pods in this room that are open for business. Differs from
		// countPodsInRoom, which counts every pod whether offline or not,
		// because an offline pod still takes up its physical slot.
		public int countInServicePodsInRoom(int roomID) {

			int count = 0;

			for (Pod pod : pods) {
				if (pod.getRoomID() == roomID && !pod.isOutOfService()) {
					count++;
				}
			}
			return count;
		}
		
	/*
	 * Adds a pod. Refuses if the label is missing, the room does not exist,
	 * the label is already used in that room.
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

		pod.setLabel(pod.getLabel().trim());
		pod.setOutOfService(true);
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
			
			//updated the checker to add a block. If the pod has a confirmed BOOKING or the reservation status is Active 
			boolean podBlock = "Confirmed".equalsIgnoreCase(reservation.getStatus()) || reservation.isHoldActive() ;
																										  
			if (reservation.getPodID() != podID || !podBlock ) {
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
	
	public boolean deletePod(int podID) {

	    Pod pod = findByID(podID);

	    if (pod == null) {
	        return false;
	    }

	    // Pod must be out of service before it can be deleted
	    if (!pod.isOutOfService()) {
	        return false;
	    }

	    LocalDate today = LocalDate.now();
	    LocalDate oneYearFromNow = today.plusDays(365);

	    for (Reservation reservation : reservationList.getReservations()) {

	        // A confirmed booking or active hold prevents deletion
	        boolean podBlock =
	                "Confirmed".equalsIgnoreCase(reservation.getStatus())
	                || reservation.isHoldActive();

	        if (reservation.getPodID() != podID || !podBlock) {
	            continue;
	        }

	        LocalDate bookedIn = parseDate(reservation.getCheckInDate());
	        LocalDate bookedOut = parseDate(reservation.getCheckOutDate());

	        if (bookedIn == null || bookedOut == null) {
	            continue;
	        }

	        // Check if the booking overlaps the next 365 days
	        if (datesOverlap(
	                today,
	                oneYearFromNow,
	                bookedIn,
	                bookedOut)) {

	            return false;
	        }
	    }

	    return pods.removeIf(p -> p.getPodID() == podID);
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