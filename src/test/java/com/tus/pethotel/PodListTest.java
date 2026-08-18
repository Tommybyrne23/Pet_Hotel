package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;

class PodListTest {

    private RoomList roomList;
    private PodList podList;
    private ReservationList reservationList;

    private int dogRoomID;
    private int catRoomID;
    private int birdRoomID;
    private int reptileRoomID;
    private int fishRoomID;

    private static final int MISSING_ROOM_ID = -1;
    private static final int MISSING_POD_ID = -1;

    @BeforeEach
    void setUp() {

        roomList = new RoomList();

        reservationList = new ReservationList();

        podList = new PodList();
        podList.setRoomList(roomList);
        podList.setReservationList(reservationList);

        dogRoomID = roomList.getRoomsForSpecies(Species.DOG).get(0).getRoomID();
        catRoomID = roomList.getRoomsForSpecies(Species.CAT).get(0).getRoomID();
        birdRoomID = roomList.getRoomsForSpecies(Species.BIRD).get(0).getRoomID();
        reptileRoomID = roomList.getRoomsForSpecies(Species.REPTILE).get(0).getRoomID();
        fishRoomID = roomList.getRoomsForSpecies(Species.FISH).get(0).getRoomID();
    }


    // =========================================================
    // CONSTRUCTOR / INITIALISATION
    // =========================================================

    @Test
    @DisplayName("A new PodList starts with an empty pod list")
    void constructorStartsEmpty() {

        PodList fresh = new PodList();

        assertNotNull(fresh.getPods());
        assertEquals(0, fresh.getNumberOfPods());
    }

    @Test
    @DisplayName("init creates the expected number of pods for every room")
    void initCreatesPodsForAllRooms() {

        podList.init();

        // DOG = 6
        assertEquals(6, podList.getPodsForRoom(dogRoomID).size());

        // CAT = 5
        assertEquals(5, podList.getPodsForRoom(catRoomID).size());

        // BIRD = 4
        assertEquals(4, podList.getPodsForRoom(birdRoomID).size());

        // REPTILE = 3
        assertEquals(3, podList.getPodsForRoom(reptileRoomID).size());

        // FISH = 4
        assertEquals(4, podList.getPodsForRoom(fishRoomID).size());

        assertEquals(22, podList.getNumberOfPods());
    }

    @Test
    @DisplayName("init gives pods the correct species prefixes")
    void initUsesCorrectPrefixes() {

        podList.init();

        assertNotNull(podList.findByID(
            podList.getPodsForRoom(dogRoomID).get(0).getPodID()
        ));

        assertEquals(
            "K1",
            podList.getPodsForRoom(dogRoomID).get(0).getLabel()
        );

        assertEquals(
            "C1",
            podList.getPodsForRoom(catRoomID).get(0).getLabel()
        );

        assertEquals(
            "A1",
            podList.getPodsForRoom(birdRoomID).get(0).getLabel()
        );

        assertEquals(
            "R1",
            podList.getPodsForRoom(reptileRoomID).get(0).getLabel()
        );

        assertEquals(
            "F1",
            podList.getPodsForRoom(fishRoomID).get(0).getLabel()
        );
    }

    @Test
    @DisplayName("init creates the expected number of pods for each species")
    void initCreatesCorrectSpeciesCounts() {

        podList.init();

        assertEquals(6, podList.getPodsForSpecies(Species.DOG).size());
        assertEquals(5, podList.getPodsForSpecies(Species.CAT).size());
        assertEquals(4, podList.getPodsForSpecies(Species.BIRD).size());
        assertEquals(3, podList.getPodsForSpecies(Species.REPTILE).size());
        assertEquals(4, podList.getPodsForSpecies(Species.FISH).size());
    }

    @Test
    @DisplayName("init puts the last reptile pod out of service")
    void initPutsLastReptilePodOffline() {

        podList.init();

        List<Pod> reptilePods =
            podList.getPodsForSpecies(Species.REPTILE);

        assertEquals(3, reptilePods.size());

        assertTrue(
            reptilePods.get(reptilePods.size() - 1).isOutOfService()
        );
    }

    @Test
    @DisplayName("init does not add pods twice")
    void initDoesNotDuplicatePods() {

        podList.init();

        assertEquals(22, podList.getNumberOfPods());

        podList.init();

        assertEquals(22, podList.getNumberOfPods());
    }


    // =========================================================
    // LABEL CHECKING
    // =========================================================

    @Test
    @DisplayName("A null label is never reported as taken")
    void isLabelTakenHandlesNull() {

        assertFalse(
            podList.isLabelTakenInRoom(null, dogRoomID)
        );
    }

    @Test
    @DisplayName("A label is recognised as taken in its own room")
    void isLabelTakenInRoomFindsExistingLabel() {

        podList.addPod(new Pod("K1", dogRoomID));

        assertTrue(
            podList.isLabelTakenInRoom("K1", dogRoomID)
        );
    }

    @Test
    @DisplayName("Label checking ignores case")
    void isLabelTakenIgnoresCase() {

        podList.addPod(new Pod("K1", dogRoomID));

        assertTrue(
            podList.isLabelTakenInRoom("k1", dogRoomID)
        );
    }

    @Test
    @DisplayName("Label checking ignores surrounding spaces")
    void isLabelTakenIgnoresSpaces() {

        podList.addPod(new Pod("K1", dogRoomID));

        assertTrue(
            podList.isLabelTakenInRoom("  K1  ", dogRoomID)
        );
    }

    @Test
    @DisplayName("The same label is allowed in another room")
    void sameLabelAllowedInDifferentRoom() {

        podList.addPod(new Pod("1", dogRoomID));

        assertFalse(
            podList.isLabelTakenInRoom("1", catRoomID)
        );
    }


    // =========================================================
    // COUNTING
    // =========================================================

    @Test
    @DisplayName("countPodsInRoom counts all pods")
    void countPodsInRoomCountsAllPods() {

        podList.addPod(new Pod("K1", dogRoomID));
        podList.addPod(new Pod("K2", dogRoomID));

        assertEquals(
            2,
            podList.countPodsInRoom(dogRoomID)
        );
    }

    @Test
    @DisplayName("countPodsInRoom returns zero for an empty room")
    void countPodsInRoomReturnsZeroForEmptyRoom() {

        assertEquals(
            0,
            podList.countPodsInRoom(dogRoomID)
        );
    }

    @Test
    @DisplayName("countInServicePodsInRoom excludes out-of-service pods")
    void countInServicePodsExcludesOfflinePods() {

        Pod first = new Pod("K1", dogRoomID);
        Pod second = new Pod("K2", dogRoomID);

        podList.addPod(first);
        podList.addPod(second);

        first.setOutOfService(false);

        assertEquals(
            1,
            podList.countInServicePodsInRoom(dogRoomID)
        );
    }

    @Test
    @DisplayName("countInServicePodsInRoom returns zero when all pods are offline")
    void countInServicePodsReturnsZeroWhenAllOffline() {

        podList.addPod(new Pod("K1", dogRoomID));
        podList.addPod(new Pod("K2", dogRoomID));

        assertEquals(
            0,
            podList.countInServicePodsInRoom(dogRoomID)
        );
    }


    // =========================================================
    // ADDING PODS
    // =========================================================

    @Test
    @DisplayName("A valid pod is accepted")
    void addPodAcceptsValidPod() {

        Pod pod = new Pod("K1", dogRoomID);

        assertTrue(podList.addPod(pod));
        assertEquals(1, podList.getNumberOfPods());
    }

    @Test
    @DisplayName("A null pod is rejected")
    void addPodRejectsNull() {

        assertFalse(podList.addPod(null));
        assertEquals(0, podList.getNumberOfPods());
    }

    @Test
    @DisplayName("A pod with a null label is rejected")
    void addPodRejectsNullLabel() {

        assertFalse(
            podList.addPod(new Pod(null, dogRoomID))
        );
    }

    @Test
    @DisplayName("A pod with a blank label is rejected")
    void addPodRejectsBlankLabel() {

        assertFalse(
            podList.addPod(new Pod("", dogRoomID))
        );

        assertFalse(
            podList.addPod(new Pod("   ", dogRoomID))
        );
    }

    @Test
    @DisplayName("A pod for an unknown room is rejected")
    void addPodRejectsUnknownRoom() {

        assertFalse(
            podList.addPod(new Pod("K1", MISSING_ROOM_ID))
        );
    }

    @Test
    @DisplayName("A duplicate pod label in the same room is rejected")
    void addPodRejectsDuplicateLabel() {

        podList.addPod(new Pod("K1", dogRoomID));

        assertFalse(
            podList.addPod(new Pod("K1", dogRoomID))
        );

        assertEquals(1, podList.getNumberOfPods());
    }

    @Test
    @DisplayName("A newly added pod is automatically out of service")
    void addPodStartsOutOfService() {

        Pod pod = new Pod("K1", dogRoomID);

        podList.addPod(pod);

        assertTrue(pod.isOutOfService());
    }

    @Test
    @DisplayName("A pod label is trimmed when added")
    void addPodTrimsLabel() {

        Pod pod = new Pod("  K1  ", dogRoomID);

        podList.addPod(pod);

        assertEquals("K1", pod.getLabel());
    }


    // =========================================================
    // RENAMING PODS
    // =========================================================

    @Test
    @DisplayName("A pod can be renamed")
    void renamePodWorks() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.renamePod(pod.getPodID(), "K9")
        );

        assertEquals("K9", pod.getLabel());
    }

    @Test
    @DisplayName("Renaming trims the new label")
    void renamePodTrimsLabel() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.renamePod(pod.getPodID(), "  K9  ")
        );

        assertEquals("K9", pod.getLabel());
    }

    @Test
    @DisplayName("Renaming to blank is rejected")
    void renamePodRejectsBlank() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.renamePod(pod.getPodID(), null)
        );

        assertFalse(
            podList.renamePod(pod.getPodID(), "")
        );

        assertFalse(
            podList.renamePod(pod.getPodID(), "   ")
        );

        assertEquals("K1", pod.getLabel());
    }

    @Test
    @DisplayName("Renaming an unknown pod is rejected")
    void renamePodRejectsUnknownPod() {

        assertFalse(
            podList.renamePod(MISSING_POD_ID, "K9")
        );
    }

    @Test
    @DisplayName("A pod can keep its existing label")
    void renamePodAllowsExistingLabel() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.renamePod(pod.getPodID(), "k1")
        );
    }

    @Test
    @DisplayName("A pod cannot take another pod's label in the same room")
    void renamePodRejectsDuplicateLabel() {

        Pod first = new Pod("K1", dogRoomID);
        Pod second = new Pod("K2", dogRoomID);

        podList.addPod(first);
        podList.addPod(second);

        assertFalse(
            podList.renamePod(second.getPodID(), "K1")
        );

        assertEquals("K2", second.getLabel());
    }


    // =========================================================
    // LOOKUPS
    // =========================================================

    @Test
    @DisplayName("findByID returns the correct pod")
    void findByIDReturnsPod() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertEquals(
            pod,
            podList.findByID(pod.getPodID())
        );
    }

    @Test
    @DisplayName("findByID returns null for an unknown ID")
    void findByIDReturnsNull() {

        assertNull(
            podList.findByID(MISSING_POD_ID)
        );
    }

    @Test
    @DisplayName("getSpeciesFor gets species from the pod's room")
    void getSpeciesForReturnsRoomSpecies() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertEquals(
            Species.DOG,
            podList.getSpeciesFor(pod)
        );
    }

    @Test
    @DisplayName("getPodsForRoom returns only pods belonging to that room")
    void getPodsForRoomFiltersCorrectly() {

        podList.addPod(new Pod("K1", dogRoomID));
        podList.addPod(new Pod("K2", dogRoomID));
        podList.addPod(new Pod("C1", catRoomID));

        assertEquals(
            2,
            podList.getPodsForRoom(dogRoomID).size()
        );

        assertEquals(
            1,
            podList.getPodsForRoom(catRoomID).size()
        );
    }

    @Test
    @DisplayName("getPodsForSpecies returns only matching species")
    void getPodsForSpeciesFiltersCorrectly() {

        podList.addPod(new Pod("K1", dogRoomID));
        podList.addPod(new Pod("K2", dogRoomID));
        podList.addPod(new Pod("C1", catRoomID));

        assertEquals(
            2,
            podList.getPodsForSpecies(Species.DOG).size()
        );

        assertEquals(
            1,
            podList.getPodsForSpecies(Species.CAT).size()
        );

        assertEquals(
            0,
            podList.getPodsForSpecies(Species.BIRD).size()
        );
    }


    // =========================================================
    // MOVE POD
    // =========================================================

    @Test
    @DisplayName("A pod can be moved to another room")
    void movePodChangesRoom() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.movePod(pod.getPodID(), catRoomID)
        );

        assertEquals(
            catRoomID,
            pod.getRoomID()
        );
    }

    @Test
    @DisplayName("Moving an unknown pod is rejected")
    void movePodRejectsUnknownPod() {

        assertFalse(
            podList.movePod(MISSING_POD_ID, catRoomID)
        );
    }

    @Test
    @DisplayName("Moving to an unknown room is rejected")
    void movePodRejectsUnknownRoom() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.movePod(pod.getPodID(), MISSING_ROOM_ID)
        );

        assertEquals(
            dogRoomID,
            pod.getRoomID()
        );
    }

    @Test
    @DisplayName("Moving a pod to its current room succeeds")
    void movePodToSameRoomSucceeds() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.movePod(pod.getPodID(), dogRoomID)
        );

        assertEquals(
            dogRoomID,
            pod.getRoomID()
        );
    }

    @Test
    @DisplayName("A pod cannot move to a room containing its label")
    void movePodRejectsDuplicateLabelInDestination() {

        Pod moving = new Pod("K1", dogRoomID);
        Pod existing = new Pod("K1", catRoomID);

        podList.addPod(moving);
        podList.addPod(existing);

        assertFalse(
            podList.movePod(
                moving.getPodID(),
                catRoomID
            )
        );

        assertEquals(
            dogRoomID,
            moving.getRoomID()
        );
    }

    @Test
    @DisplayName("A pod can move to another room when its label is free there")
    void movePodAllowsUniqueLabelInDestination() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.movePod(
                pod.getPodID(),
                catRoomID
            )
        );

        assertEquals(catRoomID, pod.getRoomID());
    }


    // =========================================================
    // UPDATE POD
    // =========================================================

    @Test
    @DisplayName("updatePod changes both label and room")
    void updatePodChangesLabelAndRoom() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.updatePod(
                pod.getPodID(),
                "C9",
                catRoomID
            )
        );

        assertEquals("C9", pod.getLabel());
        assertEquals(catRoomID, pod.getRoomID());
    }

    @Test
    @DisplayName("updatePod trims the new label")
    void updatePodTrimsLabel() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.updatePod(
                pod.getPodID(),
                "  C9  ",
                catRoomID
            )
        );

        assertEquals("C9", pod.getLabel());
    }

    @Test
    @DisplayName("updatePod rejects an unknown pod")
    void updatePodRejectsUnknownPod() {

        assertFalse(
            podList.updatePod(
                MISSING_POD_ID,
                "K9",
                dogRoomID
            )
        );
    }

    @Test
    @DisplayName("updatePod rejects a blank label")
    void updatePodRejectsBlankLabel() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.updatePod(
                pod.getPodID(),
                null,
                dogRoomID
            )
        );

        assertFalse(
            podList.updatePod(
                pod.getPodID(),
                "",
                dogRoomID
            )
        );

        assertFalse(
            podList.updatePod(
                pod.getPodID(),
                "   ",
                dogRoomID
            )
        );

        assertEquals("K1", pod.getLabel());
    }

    @Test
    @DisplayName("updatePod rejects an unknown destination room")
    void updatePodRejectsUnknownRoom() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.updatePod(
                pod.getPodID(),
                "K9",
                MISSING_ROOM_ID
            )
        );

        assertEquals("K1", pod.getLabel());
        assertEquals(dogRoomID, pod.getRoomID());
    }

    @Test
    @DisplayName("updatePod rejects duplicate label in destination room")
    void updatePodRejectsDuplicateDestinationLabel() {

        Pod first = new Pod("K1", dogRoomID);
        Pod second = new Pod("C1", catRoomID);

        podList.addPod(first);
        podList.addPod(second);

        assertFalse(
            podList.updatePod(
                first.getPodID(),
                "C1",
                catRoomID
            )
        );

        assertEquals("K1", first.getLabel());
        assertEquals(dogRoomID, first.getRoomID());
    }

    @Test
    @DisplayName("updatePod allows the pod to keep its existing label")
    void updatePodAllowsExistingLabel() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.updatePod(
                pod.getPodID(),
                "K1",
                dogRoomID
            )
        );
    }


    // =========================================================
    // AVAILABILITY
    // =========================================================

    @Test
    @DisplayName("An in-service pod with no reservations is available")
    void availablePodWithNoReservations() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertTrue(
            podList.isPodAvailable(
                pod.getPodID(),
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("An out-of-service pod is not available")
    void offlinePodIsNotAvailable() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(pod.isOutOfService());

        assertFalse(
            podList.isPodAvailable(
                pod.getPodID(),
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("An unknown pod is not available")
    void unknownPodIsNotAvailable() {

        assertFalse(
            podList.isPodAvailable(
                MISSING_POD_ID,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("Invalid check-in date makes a pod unavailable")
    void invalidCheckInDateMakesUnavailable() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.isPodAvailable(
                pod.getPodID(),
                "not-a-date",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("Invalid check-out date makes a pod unavailable")
    void invalidCheckOutDateMakesUnavailable() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.isPodAvailable(
                pod.getPodID(),
                "2026-08-20",
                "not-a-date"
            )
        );
    }

    @Test
    @DisplayName("A null date makes a pod unavailable")
    void nullDateMakesUnavailable() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.isPodAvailable(
                pod.getPodID(),
                null,
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("A checkout date must be after check-in")
    void checkoutMustBeAfterCheckIn() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.isPodAvailable(
                pod.getPodID(),
                "2026-08-21",
                "2026-08-20"
            )
        );
    }

    @Test
    @DisplayName("The same date for check-in and check-out is invalid")
    void sameCheckInAndCheckOutIsInvalid() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.isPodAvailable(
                pod.getPodID(),
                "2026-08-20",
                "2026-08-20"
            )
        );
    }


    // =========================================================
    // AVAILABLE POD SEARCH
    // =========================================================

    @Test
    @DisplayName("findAvailablePod returns a pod when one is available")
    void findAvailablePodReturnsPod() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertEquals(
            pod,
            podList.findAvailablePod(
                Species.DOG,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("findAvailablePod returns null when all pods are offline")
    void findAvailablePodReturnsNullWhenAllOffline() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertNull(
            podList.findAvailablePod(
                Species.DOG,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("findAvailablePod returns null when the species has no available pods")
    void findAvailablePodReturnsNullWhenNoneAvailable() {

        assertNull(
            podList.findAvailablePod(
                Species.BIRD,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }


    // =========================================================
    // AVAILABLE POD COUNT
    // =========================================================

    @Test
    @DisplayName("countAvailablePods counts available pods")
    void countAvailablePodsCountsAvailable() {

        Pod first = new Pod("K1", dogRoomID);
        Pod second = new Pod("K2", dogRoomID);

        podList.addPod(first);
        podList.addPod(second);

        first.setOutOfService(false);
        second.setOutOfService(false);

        assertEquals(
            2,
            podList.countAvailablePods(
                Species.DOG,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("countAvailablePods excludes offline pods")
    void countAvailablePodsExcludesOffline() {

        Pod inService = new Pod("K1", dogRoomID);
        Pod offline = new Pod("K2", dogRoomID);

        podList.addPod(inService);
        podList.addPod(offline);

        inService.setOutOfService(false);

        assertEquals(
            1,
            podList.countAvailablePods(
                Species.DOG,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    @DisplayName("An out-of-service pod with no booking can be deleted")
    void canDeleteOfflinePodWithNoBooking() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.canDeletePod(pod.getPodID())
        );
    }

    @Test
    @DisplayName("A pod in service cannot be deleted")
    void cannotDeletePodInService() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.canDeletePod(pod.getPodID())
        );
    }

    @Test
    @DisplayName("An unknown pod cannot be deleted")
    void cannotDeleteUnknownPod() {

        assertFalse(
            podList.canDeletePod(MISSING_POD_ID)
        );
    }

    @Test
    @DisplayName("deletePod removes an eligible pod")
    void deletePodRemovesPod() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertTrue(
            podList.deletePod(pod.getPodID())
        );

        assertEquals(
            0,
            podList.getNumberOfPods()
        );

        assertNull(
            podList.findByID(pod.getPodID())
        );
    }

    @Test
    @DisplayName("deletePod refuses a pod that cannot be deleted")
    void deletePodRefusesInServicePod() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);
        pod.setOutOfService(false);

        assertFalse(
            podList.deletePod(pod.getPodID())
        );

        assertEquals(
            1,
            podList.getNumberOfPods()
        );
    }


    // =========================================================
    // OCCUPANCY
    // =========================================================

    @Test
    @DisplayName("An out-of-service pod with no reservation is not occupied")
    void offlinePodWithNoReservationIsNotOccupied() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.isPodOccupied(
                pod.getPodID(),
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("An unknown pod is not occupied")
    void unknownPodIsNotOccupied() {

        assertFalse(
            podList.isPodOccupied(
                MISSING_POD_ID,
                "2026-08-20",
                "2026-08-21"
            )
        );
    }

    @Test
    @DisplayName("Invalid occupancy dates return false")
    void invalidOccupancyDatesReturnFalse() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.isPodOccupied(
                pod.getPodID(),
                "not-a-date",
                "2026-08-21"
            )
        );

        assertFalse(
            podList.isPodOccupied(
                pod.getPodID(),
                "2026-08-20",
                "not-a-date"
            )
        );
    }

    @Test
    @DisplayName("Occupancy requires check-out to be after check-in")
    void occupancyRequiresValidDateRange() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertFalse(
            podList.isPodOccupied(
                pod.getPodID(),
                "2026-08-21",
                "2026-08-20"
            )
        );

        assertFalse(
            podList.isPodOccupied(
                pod.getPodID(),
                "2026-08-20",
                "2026-08-20"
            )
        );
    }


    // =========================================================
    // GENERAL GETTERS / SETTERS
    // =========================================================

    @Test
    @DisplayName("getPods returns the underlying pod list")
    void getPodsReturnsPods() {

        Pod pod = new Pod("K1", dogRoomID);
        podList.addPod(pod);

        assertEquals(
            1,
            podList.getPods().size()
        );

        assertEquals(
            pod,
            podList.getPods().get(0)
        );
    }

    @Test
    @DisplayName("getNumberOfPods returns the current number of pods")
    void getNumberOfPodsReturnsCount() {

        assertEquals(0, podList.getNumberOfPods());

        podList.addPod(new Pod("K1", dogRoomID));

        assertEquals(1, podList.getNumberOfPods());
    }


    // =========================================================
    // DEPENDENCY SETTERS
    // =========================================================

    @Test
    @DisplayName("setRoomList replaces the RoomList dependency")
    void setRoomListReplacesDependency() {

        RoomList replacement = new RoomList();

        podList.setRoomList(replacement);

        assertNotNull(
            replacement.getRoomsForSpecies(Species.DOG)
        );
    }

    @Test
    @DisplayName("setReservationList replaces the ReservationList dependency")
    void setReservationListReplacesDependency() {

        ReservationList replacement = new ReservationList();

        podList.setReservationList(replacement);

        assertNotNull(replacement.getReservations());
    }
}
