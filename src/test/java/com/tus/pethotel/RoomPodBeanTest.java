package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;

/*
 * Tests for the admin Pod set-up page behind viewPodsRooms.xhtml.
 */
class RoomPodBeanTest {

	private static final String STAY_ON_PAGE = null;
	private static final String RELOAD_PAGE = "viewPodsRooms?faces-redirect=true";

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

	private Pod podInService(String label) {

		Pod pod = new Pod(label, dogRoomID);
		podList.addPod(pod);
		pod.setOutOfService(false);
		return pod;
	}

	private void bookPodToday(int podID) {

		Reservation booking = new Reservation(1, 1, "Rex", daysFromToday(-1), daysFromToday(2));
		booking.setPodID(podID);
		booking.setStatus("Confirmed");
		reservationList.addReservation(booking);
	}

	// A COMPLETED FORM UPDATES THE PODS

	@Test
	@DisplayName("A completed pod form adds the pod and reloads the page")
	void addPodWithCompleteFormSucceeds() {
		bean.setPodLabel("K1");
		bean.setPodRoomID(dogRoomID);
		assertEquals(RELOAD_PAGE, bean.addPod());
		assertEquals(1, podList.countPodsInRoom(dogRoomID));
	}

	@Test
	@DisplayName("The added pod is stored trimmed and out of service")
	void addedPodIsTrimmedAndOffline() {
		bean.setPodLabel("  K1  ");
		bean.setPodRoomID(dogRoomID);
		bean.addPod();
		Pod added = podList.getPodsForRoom(dogRoomID).get(0);
		assertEquals("K1", added.getLabel());
		assertTrue(added.isOutOfService());
		assertEquals(0, podList.countInServicePodsInRoom(dogRoomID));
	}

	@Test
	@DisplayName("The form is cleared after a pod is added")
	void addPodClearsTheForm() {
		bean.setPodLabel("K1");
		bean.setPodRoomID(dogRoomID);
		bean.addPod();
		assertNull(bean.getPodLabel());
		assertNull(bean.getPodRoomID());
	}

	// AN INCOMPLETE FORM DOES NOT UPDATE THE PODS

	@Test
	@DisplayName("A pod with no label is refused and nothing is stored")
	void addPodWithoutLabelIsRefused() {
		bean.setPodLabel("   ");
		bean.setPodRoomID(dogRoomID);
		assertEquals(STAY_ON_PAGE, bean.addPod());
		assertEquals(0, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A pod with no room chosen is refused")
	void addPodWithoutRoomIsRefused() {
		bean.setPodLabel("K1");
		bean.setPodRoomID(null);
		assertEquals(STAY_ON_PAGE, bean.addPod());
		assertEquals(0, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A duplicate label in the same room is refused")
	void addPodWithDuplicateLabelIsRefused() {
		podInService("K1");
		bean.setPodLabel("K1");
		bean.setPodRoomID(dogRoomID);
		assertEquals(STAY_ON_PAGE, bean.addPod());
		assertEquals(1, podList.countPodsInRoom(dogRoomID));
	}

	@Test
	@DisplayName("An incomplete room form is refused and nothing is stored")
	void addRoomWithMissingFieldsIsRefused() {
		int before = roomList.getNumberOfRooms();
		bean.setRoomName("  ");
		bean.setRoomLocation("North Block");
		bean.setRoomSpecies(Species.DOG);
		assertEquals(STAY_ON_PAGE, bean.addRoom());
		bean.setRoomName("New Room");
		bean.setRoomLocation(null);
		assertEquals(STAY_ON_PAGE, bean.addRoom());
		bean.setRoomLocation("North Block");
		bean.setRoomSpecies(null);
		assertEquals(STAY_ON_PAGE, bean.addRoom());
		assertEquals(before, roomList.getNumberOfRooms());
	}

	// TAKING A POD IN AND OUT OF SERVICE

	@Test
	@DisplayName("A free pod can be taken offline and put back")
	void togglingAFreePodWorksBothWays() {
		Pod pod = podInService("K1");
		bean.toggleOutOfService(pod);
		assertTrue(pod.isOutOfService());
		bean.toggleOutOfService(pod);
		assertFalse(pod.isOutOfService());
	}

	@Test
	@DisplayName("A pod with a pet in it today cannot be taken offline")
	void occupiedPodCannotBeTakenOffline() {
		Pod pod = podInService("K1");
		bookPodToday(pod.getPodID());
		bean.toggleOutOfService(pod);
		assertFalse(pod.isOutOfService(), "The rule belongs in the bean, not only in the disabled button");
	}
}
