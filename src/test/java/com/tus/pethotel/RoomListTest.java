package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;

/*
 * Tests for the read-only helpers behind the Rooms and Pod inventory
 * tables on viewPodsRooms.xhtml
 */
class RoomListTest {

	private RoomList roomList;
	private PodList podList;
	private ReservationList reservationList;
	private RoomPodBean bean;

	private int dogRoomID;

	@BeforeEach
	void setUp() {
		roomList = new RoomList();
		reservationList = new ReservationList();

		podList = new PodList();
		podList.setRoomList(roomList);
		podList.setReservationList(reservationList);

		bean = new RoomPodBean();
		bean.setRoomList(roomList);
		bean.setPodList(podList);
		dogRoomID = roomList.getRoomsForSpecies(Species.DOG).get(0).getRoomID();
	}

	private static String daysFromToday(int days) {
		return LocalDate.now().plusDays(days).toString();
	}

	// addPod parks a new pod offline, so put it back into service
	private Pod podInService(String label) {
		Pod pod = new Pod(label, dogRoomID);
		podList.addPod(pod);
		pod.setOutOfService(false);
		return pod;
	}

	private void bookPodToday(int podID) {

		Reservation booking = new Reservation(1, 1, "Rex",
				daysFromToday(-1), daysFromToday(2));
		booking.setPodID(podID);
		booking.setStatus("Confirmed");
		reservationList.addReservation(booking);
	}



	@Test
	@DisplayName("The status column reads offline, occupied or available")
	void podStatusReflectsTheDateAndTheService() {

		Pod free = podInService("K1");
		Pod busy = podInService("K2");
		Pod offline = podInService("K3");
		bookPodToday(busy.getPodID());
		offline.setOutOfService(true);
		bean.setAvailabilityDate(LocalDate.now().toString());
		assertEquals("Available", bean.getPodStatus(free));
		assertEquals("Occupied", bean.getPodStatus(busy));
		assertEquals("Out of service", bean.getPodStatus(offline));
		assertEquals("status-available", bean.getPodStatusClass(free));
		assertEquals("status-pending", bean.getPodStatusClass(busy));
		assertEquals("status-cancelled", bean.getPodStatusClass(offline));
	}

	@Test
	@DisplayName("An occupied pod is locked, an offline one is not")
	void podLockingFollowsOccupancy() {
		Pod free = podInService("K1");
		Pod busy = podInService("K2");
		Pod offline = podInService("K3");
		bookPodToday(busy.getPodID());
		offline.setOutOfService(true);
		assertFalse(bean.isPodLocked(free));
		assertTrue(bean.isPodLocked(busy));
		assertFalse(bean.isPodLocked(offline), "Returning a pod to service is always allowed");
	}

	@Test
	@DisplayName("An unreadable date filter falls back to today")
	void badAvailabilityDateFallsBackToToday() {
		Pod busy = podInService("K1");
		bookPodToday(busy.getPodID());
		bean.setAvailabilityDate("not a date");
		assertEquals("Occupied", bean.getPodStatus(busy));
		bean.setAvailabilityDate(null);
		assertEquals("Occupied", bean.getPodStatus(busy));
	}

	@Test
	@DisplayName("Room totals count offline pods but availability does not")
	void roomCountsSeparateTotalFromInService() {
		podInService("K1");
		Pod offline = podInService("K2");
		offline.setOutOfService(true);
		Room dogRoom = roomList.findByID(dogRoomID);
		bean.setAvailabilityDate(LocalDate.now().toString());
		assertEquals(2, bean.getTotalPods(dogRoom));
		assertEquals(1, bean.getPodsInService(dogRoom));
		assertEquals("1/1", bean.getPodsAvailable(dogRoom));
	}

	@Test
	@DisplayName("The inventory is grouped in the same order as the rooms")
	void podsByRoomFollowRoomOrder() {
		int catRoomID = roomList.getRoomsForSpecies(Species.CAT).get(0).getRoomID();
		podList.addPod(new Pod("C1", catRoomID));
		podInService("K1");
		assertEquals(2, bean.getPodsByRoom().size());
		assertEquals("K1", bean.getPodsByRoom().get(0).getLabel(), "Lakeside Kennels is created before Cat Haven");
	}

	@Test
	@DisplayName("The room dropdown shows how many pods each room has")
	void roomLabelShowsPodCount() {
		podInService("K1");
		assertEquals("Lakeside Kennels (1 pods)", bean.getRoomLabel(roomList.findByID(dogRoomID)));
	}
}
