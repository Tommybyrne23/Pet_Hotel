package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;

class RoomListTest {

    private RoomList roomList;
    private PodList podList;
    private ReservationList reservationList;

    private int dogRoomID;

    @BeforeEach
    void setUp() {
        roomList = new RoomList();

        reservationList = new ReservationList();

        podList = new PodList();
        podList.setRoomList(roomList);
        podList.setReservationList(reservationList);

        roomList.setPodList(podList);

        dogRoomID = roomList
                .getRoomsForSpecies(Species.DOG)
                .get(0)
                .getRoomID();
    }

    private Pod offlinePod(String label) {
        Pod pod = new Pod(label, dogRoomID);
        assertTrue(podList.addPod(pod));
        pod.setOutOfService(true);
        return pod;
    }

    private Pod inServicePod(String label) {
        Pod pod = new Pod(label, dogRoomID);
        assertTrue(podList.addPod(pod));
        pod.setOutOfService(false);
        return pod;
    }

    // ---------------------------------------------------------
    // NAME CHECKING
    // ---------------------------------------------------------

    @Test
    void nameChecksCoverExistingNewNullAndIgnoreCurrentRoom() {

        assertTrue(roomList.isNameTaken("Lakeside Kennels"));
        assertTrue(roomList.isNameTaken("  lakeside kennels  "));
        assertFalse(roomList.isNameTaken("New Room"));
        assertFalse(roomList.isNameTaken(null));

        assertFalse(
                roomList.isNameTakenByAnother(
                        "Lakeside Kennels",
                        dogRoomID
                )
        );

        assertTrue(
                roomList.isNameTakenByAnother(
                        "Cat Haven",
                        dogRoomID
                )
        );

        assertFalse(
                roomList.isNameTakenByAnother(null, dogRoomID)
        );
    }

    // ---------------------------------------------------------
    // ADD ROOM
    // ---------------------------------------------------------

    @Test
    void addRoomValidatesInputAndRejectsDuplicates() {

        assertFalse(roomList.addRoom(null));

        assertFalse(
                roomList.addRoom(
                        new Room(null, "North Block", Species.DOG)
                )
        );

        assertFalse(
                roomList.addRoom(
                        new Room("   ", "North Block", Species.DOG)
                )
        );

        assertFalse(
                roomList.addRoom(
                        new Room("New Room", "North Block", null)
                )
        );

        assertFalse(
                roomList.addRoom(
                        new Room("Lakeside Kennels", "Other", Species.DOG)
                )
        );

        assertFalse(
                roomList.addRoom(
                        new Room("  lakeside kennels  ", "Other", Species.DOG)
                )
        );

        Room newRoom = new Room(
                "  New Room  ",
                "North Block",
                Species.DOG
        );

        assertTrue(roomList.addRoom(newRoom));
        assertEquals(6, roomList.getNumberOfRooms());

        // Current implementation validates using trim()
        // but does not trim the stored name.
        assertEquals("  New Room  ", newRoom.getName());
    }

    // ---------------------------------------------------------
    // LOOKUPS
    // ---------------------------------------------------------

    @Test
    void roomLookupsWork() {

        Room room = roomList.findByID(dogRoomID);

        assertNotNull(room);
        assertEquals("Lakeside Kennels", room.getName());
        assertEquals(Species.DOG, room.getSpecies());

        assertNull(roomList.findByID(-1));

        assertEquals(
                1,
                roomList.getRoomsForSpecies(Species.DOG).size()
        );

        assertEquals(
                1,
                roomList.getRoomsForSpecies(Species.CAT).size()
        );

        assertEquals(5, roomList.getNumberOfRooms());
        assertEquals(5, roomList.getRooms().size());
    }

    // ---------------------------------------------------------
    // CAN EDIT ROOM
    // ---------------------------------------------------------

    @Test
    void canEditRoomCoversValidAndInvalidCases() {

        // Empty room is editable.
        assertTrue(roomList.canEditRoom(dogRoomID));

        // Unknown room cannot be edited.
        assertFalse(roomList.canEditRoom(-1));

        // Offline pod with no booking is eligible.
        offlinePod("K1");
        assertTrue(roomList.canEditRoom(dogRoomID));

        // An in-service pod makes the room ineligible.
        RoomList anotherRoomList = new RoomList();

        PodList anotherPodList = new PodList();
        ReservationList anotherReservationList = new ReservationList();

        anotherPodList.setRoomList(anotherRoomList);
        anotherPodList.setReservationList(anotherReservationList);
        anotherRoomList.setPodList(anotherPodList);

        int anotherDogRoomID = anotherRoomList
                .getRoomsForSpecies(Species.DOG)
                .get(0)
                .getRoomID();

        Pod activePod = new Pod("K1", anotherDogRoomID);
        assertTrue(anotherPodList.addPod(activePod));
        activePod.setOutOfService(false);

        assertFalse(anotherRoomList.canEditRoom(anotherDogRoomID));
    }

    // ---------------------------------------------------------
    // EDIT ROOM
    // ---------------------------------------------------------

    @Test
    void editRoomRejectsInvalidInput() {

        assertFalse(
                roomList.editRoom(
                        -1,
                        "New",
                        "Location",
                        Species.DOG
                )
        );

        assertFalse(
                roomList.editRoom(
                        dogRoomID,
                        null,
                        "Location",
                        Species.DOG
                )
        );

        assertFalse(
                roomList.editRoom(
                        dogRoomID,
                        "   ",
                        "Location",
                        Species.DOG
                )
        );

        assertFalse(
                roomList.editRoom(
                        dogRoomID,
                        "New Name",
                        null,
                        Species.DOG
                )
        );

        assertFalse(
                roomList.editRoom(
                        dogRoomID,
                        "New Name",
                        "   ",
                        Species.DOG
                )
        );

        assertFalse(
                roomList.editRoom(
                        dogRoomID,
                        "New Name",
                        "Location",
                        null
                )
        );

        // Duplicate name.
        assertFalse(
                roomList.editRoom(
                        dogRoomID,
                        "  cat haven  ",
                        "Location",
                        Species.DOG
                )
        );

        // Existing name is allowed.
        assertTrue(
                roomList.editRoom(
                        dogRoomID,
                        "Lakeside Kennels",
                        "New Location",
                        Species.CAT
                )
        );

        Room room = roomList.findByID(dogRoomID);

        assertEquals("Lakeside Kennels", room.getName());
        assertEquals("New Location", room.getLocation());
        assertEquals(Species.CAT, room.getSpecies());
    }

    @Test
    void editRoomTrimsNameAndLocation() {

        assertTrue(
                roomList.editRoom(
                        dogRoomID,
                        "  Updated Kennels  ",
                        "  New Location  ",
                        Species.DOG
                )
        );

        Room room = roomList.findByID(dogRoomID);

        assertEquals("Updated Kennels", room.getName());
        assertEquals("New Location", room.getLocation());
    }

    // ---------------------------------------------------------
    // DELETE ROOM
    // ---------------------------------------------------------

    @Test
    void deleteRoomHandlesUnknownAndEmptyRoom() {

        assertFalse(roomList.deleteRoom(-1));

        assertTrue(roomList.deleteRoom(dogRoomID));

        assertNull(roomList.findByID(dogRoomID));
        assertEquals(4, roomList.getNumberOfRooms());
    }

    @Test
    void deleteRoomDeletesEligiblePodsWithRoom() {

        Pod first = offlinePod("K1");
        Pod second = offlinePod("K2");

        assertTrue(roomList.deleteRoom(dogRoomID));

        assertNull(roomList.findByID(dogRoomID));
        assertNull(podList.findByID(first.getPodID()));
        assertNull(podList.findByID(second.getPodID()));
        assertEquals(0, podList.getPodsForRoom(dogRoomID).size());
    }

    @Test
    void deleteRoomRefusesIneligiblePod() {

        Pod active = inServicePod("K1");

        assertFalse(roomList.deleteRoom(dogRoomID));

        assertNotNull(roomList.findByID(dogRoomID));
        assertNotNull(podList.findByID(active.getPodID()));
        assertEquals(5, roomList.getNumberOfRooms());
    }

    @Test
    void deleteRoomRefusesBookedPod() {

        Pod pod = offlinePod("K1");

        Reservation reservation = new Reservation(
                1,
                1,
                "Rex",
                "2026-08-01",
                "2026-08-30"
        );

        reservation.setPodID(pod.getPodID());
        reservation.setStatus("Confirmed");

        reservationList.addReservation(reservation);

        assertFalse(roomList.deleteRoom(dogRoomID));

        assertNotNull(roomList.findByID(dogRoomID));
        assertNotNull(podList.findByID(pod.getPodID()));
    }
}