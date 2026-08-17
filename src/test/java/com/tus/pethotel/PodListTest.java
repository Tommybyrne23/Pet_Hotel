package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;

class PodListTest {

	private RoomList roomList;
	private PodList podList;

	private int dogRoomID;
	private int catRoomID;

	// A room ID that cannot exist, for an "unknown room" path
	private static final int MISSING_ROOM_ID = -1;

	@BeforeEach
	void setUp() {

		roomList = new RoomList();

		podList = new PodList();
		podList.setRoomList(roomList);
		podList.setReservationList(new ReservationList());

		// init() is not called, so the store starts empty and the counts
		dogRoomID = roomList.getRoomsForSpecies(Species.DOG).get(0).getRoomID();
		catRoomID = roomList.getRoomsForSpecies(Species.CAT).get(0).getRoomID();
	}

	// ADDING A POD

	@Test
	@DisplayName("A pod with a label and a real room is accepted")
	void addPodAcceptsValidPod() {
		assertTrue(podList.addPod(new Pod("K1", dogRoomID)));
		assertEquals(1, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A newly added pod starts out of service")
	void addPodParksNewPodOffline() {
		Pod pod = new Pod("K1", dogRoomID);
		podList.addPod(pod);
		assertTrue(pod.isOutOfService(), "An admin has to return a new pod to service once it is ready");
	}

	@Test
	@DisplayName("A null pod is refused")
	void addPodRefusesNull() {
		assertFalse(podList.addPod(null));
		assertEquals(0, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A pod with no label is refused")
	void addPodRefusesMissingLabel() {
		assertFalse(podList.addPod(new Pod(null, dogRoomID)));
		assertFalse(podList.addPod(new Pod("", dogRoomID)));
		assertFalse(podList.addPod(new Pod("   ", dogRoomID)));
		assertEquals(0, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A pod in a room that does not exist is refused")
	void addPodRefusesUnknownRoom() {
		assertFalse(podList.addPod(new Pod("K1", MISSING_ROOM_ID)));
		assertEquals(0, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A label already used in that room is refused")
	void addPodRefusesDuplicateLabelInSameRoom() {
		podList.addPod(new Pod("K1", dogRoomID));
		assertFalse(podList.addPod(new Pod("K1", dogRoomID)));
		assertEquals(1, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("Duplicate labels are caught regardless of case or spacing")
	void addPodRefusesDuplicateIgnoringCaseAndSpacing() {
		podList.addPod(new Pod("K1", dogRoomID));
		assertFalse(podList.addPod(new Pod("k1", dogRoomID)));
		assertFalse(podList.addPod(new Pod("  K1  ", dogRoomID)));
		assertEquals(1, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("The same label is allowed in a different room")
	void addPodAllowsSameLabelInAnotherRoom() {
		podList.addPod(new Pod("1", dogRoomID));
		assertTrue(podList.addPod(new Pod("1", catRoomID)));
		assertEquals(2, podList.getNumberOfPods());
	}

	@Test
	@DisplayName("A stored label has its surrounding spaces removed")
	void addPodTrimsLabel() {
		Pod pod = new Pod("  K1  ", dogRoomID);
		podList.addPod(pod);
		assertEquals("K1", pod.getLabel());
	}

	// LABEL LOOKUP

	@Test
	@DisplayName("A null label is never reported as taken")
	void isLabelTakenInRoomHandlesNull() {
		assertFalse(podList.isLabelTakenInRoom(null, dogRoomID));
	}

	@Test
	@DisplayName("A label free in one room is not reported as taken in another")
	void isLabelTakenInRoomIsScopedToTheRoom() {
		podList.addPod(new Pod("K1", dogRoomID));
		assertTrue(podList.isLabelTakenInRoom("K1", dogRoomID));
		assertFalse(podList.isLabelTakenInRoom("K1", catRoomID));
	}

	// COUNTING

	@Test
	@DisplayName("Total pods counts offline pods, pods in service does not")
	void countsTreatOfflinePodsDifferently() {
		Pod inService = new Pod("K1", dogRoomID);
		Pod offline = new Pod("K2", dogRoomID);
		podList.addPod(inService);
		podList.addPod(offline);
		inService.setOutOfService(false);
		assertEquals(2, podList.countPodsInRoom(dogRoomID));
		assertEquals(1, podList.countInServicePodsInRoom(dogRoomID));
	}

	@Test
	@DisplayName("Counting a room with no pods gives zero, not an error")
	void countsHandleEmptyRoom() {
		assertEquals(0, podList.countPodsInRoom(catRoomID));
		assertEquals(0, podList.countInServicePodsInRoom(catRoomID));
	}

	// LOOKUPS

	@Test
	@DisplayName("A pod can be found by its own ID")
	void findByIDReturnsThePod() {
		Pod pod = new Pod("K1", dogRoomID);
		podList.addPod(pod);
		assertNotNull(podList.findByID(pod.getPodID()));
		assertEquals("K1", podList.findByID(pod.getPodID()).getLabel());
	}

	@Test
	@DisplayName("An unknown pod ID returns null rather than throwing")
	void findByIDReturnsNullWhenMissing() {
		assertNull(podList.findByID(MISSING_ROOM_ID));
	}

	@Test
	@DisplayName("A pod takes its species from the room it sits in")
	void getSpeciesForReadsTheRoom() {
		Pod pod = new Pod("C1", catRoomID);
		podList.addPod(pod);
		assertEquals(Species.CAT, podList.getSpeciesFor(pod));
	}

	@Test
	@DisplayName("Pods are listed by room and by species")
	void podsCanBeListedByRoomAndSpecies() {
		podList.addPod(new Pod("K1", dogRoomID));
		podList.addPod(new Pod("K2", dogRoomID));
		podList.addPod(new Pod("C1", catRoomID));
		assertEquals(2, podList.getPodsForRoom(dogRoomID).size());
		assertEquals(1, podList.getPodsForRoom(catRoomID).size());
		assertEquals(2, podList.getPodsForSpecies(Species.DOG).size());
		assertEquals(0, podList.getPodsForSpecies(Species.BIRD).size());
	}
}