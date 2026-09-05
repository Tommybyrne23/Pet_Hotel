package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.services.Species;

class RoomPodBeanTest {

    private static final String RELOAD_PAGE = "viewPodsRooms?faces-redirect=true";

    private RoomList roomList;
    private PodList podList;
    private ReservationList reservationList;
    private RoomPodBean bean;

    private Room dogRoom;
    private Room catRoom;

    private int dogRoomID;
    private int catRoomID;

    @BeforeEach
    void setUp() {

        roomList = new RoomList();

        reservationList = new ReservationList();

        podList = new PodList();
        podList.setRoomList(roomList);
        podList.setReservationList(reservationList);

        roomList.setPodList(podList);

        bean = new RoomPodBean();
        bean.setRoomList(roomList);
        bean.setPodList(podList);

        dogRoom = roomList.getRoomsForSpecies(Species.DOG).get(0);
        catRoom = roomList.getRoomsForSpecies(Species.CAT).get(0);

        dogRoomID = dogRoom.getRoomID();
        catRoomID = catRoom.getRoomID();
    }

    private static String daysFromToday(int days) {
        return LocalDate.now().plusDays(days).toString();
    }

    private Pod addPod(String label, int roomID) {

        Pod pod = new Pod(label, roomID);
        podList.addPod(pod);
        return pod;
    }

    private Reservation confirmedBooking(int podID, int start, int end) {

        Reservation booking = new Reservation(
                1,
                1,
                "Rex",
                daysFromToday(start),
                daysFromToday(end)
        );

        booking.setPodID(podID);
        booking.setStatus("Confirmed");

        reservationList.addReservation(booking);

        return booking;
    }


    // ---------------------------------------------------------
    // ADD ROOM
    // ---------------------------------------------------------

    @Test
    @DisplayName("A complete room form adds the room and reloads the page")
    void addRoomWithCompleteFormSucceeds() {

        int before = roomList.getNumberOfRooms();

        bean.setRoomName("New Kennels");
        bean.setRoomLocation("North Block");
        bean.setRoomSpecies(Species.DOG);

        assertEquals(RELOAD_PAGE, bean.addRoom());

        assertEquals(before + 1, roomList.getNumberOfRooms());

        Room added = roomList.getRooms()
                .get(roomList.getRooms().size() - 1);

        assertEquals("New Kennels", added.getName());
        assertEquals("North Block", added.getLocation());
        assertEquals(Species.DOG, added.getSpecies());
    }

    @Test
    @DisplayName("Room details are trimmed before the room is stored")
    void addRoomTrimsValues() {

        bean.setRoomName("  New Kennels  ");
        bean.setRoomLocation("  North Block  ");
        bean.setRoomSpecies(Species.DOG);

        bean.addRoom();

        Room added = roomList.getRooms()
                .get(roomList.getRooms().size() - 1);

        assertEquals("New Kennels", added.getName());
        assertEquals("North Block", added.getLocation());
    }

    @Test
    @DisplayName("A room with a blank name is refused")
    void addRoomRefusesBlankName() {

        int before = roomList.getNumberOfRooms();

        bean.setRoomName("   ");
        bean.setRoomLocation("North Block");
        bean.setRoomSpecies(Species.DOG);

        assertNull(bean.addRoom());

        assertEquals(before, roomList.getNumberOfRooms());
    }

    @Test
    @DisplayName("A room with no location is refused")
    void addRoomRefusesMissingLocation() {

        int before = roomList.getNumberOfRooms();

        bean.setRoomName("New Kennels");
        bean.setRoomLocation(null);
        bean.setRoomSpecies(Species.DOG);

        assertNull(bean.addRoom());

        assertEquals(before, roomList.getNumberOfRooms());
    }

    @Test
    @DisplayName("A room with no species is refused")
    void addRoomRefusesMissingSpecies() {

        int before = roomList.getNumberOfRooms();

        bean.setRoomName("New Kennels");
        bean.setRoomLocation("North Block");
        bean.setRoomSpecies(null);

        assertNull(bean.addRoom());

        assertEquals(before, roomList.getNumberOfRooms());
    }

    @Test
    @DisplayName("A duplicate room name is refused")
    void addRoomRefusesDuplicateName() {

        int before = roomList.getNumberOfRooms();

        bean.setRoomName("  lakeside kennels  ");
        bean.setRoomLocation("Another Block");
        bean.setRoomSpecies(Species.DOG);

        assertNull(bean.addRoom());

        assertEquals(before, roomList.getNumberOfRooms());
    }


    // ---------------------------------------------------------
    // ADD POD
    // ---------------------------------------------------------

    @Test
    @DisplayName("A complete pod form adds the pod and reloads the page")
    void addPodWithCompleteFormSucceeds() {

        bean.setPodLabel("K99");
        bean.setPodRoomID(dogRoomID);

        assertEquals(RELOAD_PAGE, bean.addPod());

        assertEquals(1, podList.countPodsInRoom(dogRoomID));

        Pod added = podList.getPodsForRoom(dogRoomID).get(0);

        assertEquals("K99", added.getLabel());
        assertTrue(added.isOutOfService());
    }

    @Test
    @DisplayName("A pod label is trimmed before it is stored")
    void addPodTrimsLabel() {

        bean.setPodLabel("  K99  ");
        bean.setPodRoomID(dogRoomID);

        bean.addPod();

        Pod added = podList.getPodsForRoom(dogRoomID).get(0);

        assertEquals("K99", added.getLabel());
    }

    @Test
    @DisplayName("A pod with a blank label is refused")
    void addPodRefusesBlankLabel() {

        bean.setPodLabel("   ");
        bean.setPodRoomID(dogRoomID);

        assertNull(bean.addPod());

        assertEquals(0, podList.getPodsForRoom(dogRoomID).size());
    }

    @Test
    @DisplayName("A pod with no room is refused")
    void addPodRefusesMissingRoom() {

        bean.setPodLabel("K99");
        bean.setPodRoomID(null);

        assertNull(bean.addPod());

        assertEquals(0, podList.getNumberOfPods());
    }

    @Test
    @DisplayName("A duplicate pod label in the same room is refused")
    void addPodRefusesDuplicateLabel() {

        addPod("K99", dogRoomID);

        bean.setPodLabel("  k99  ");
        bean.setPodRoomID(dogRoomID);

        assertNull(bean.addPod());

        assertEquals(1, podList.countPodsInRoom(dogRoomID));
    }


    // ---------------------------------------------------------
    // POD SERVICE STATUS
    // ---------------------------------------------------------

    @Test
    @DisplayName("A free pod can be taken out of service")
    void freePodCanBeTakenOutOfService() {

        Pod pod = addPod("K99", dogRoomID);

        // addPod creates pods out of service, so return it to service first.
        pod.setOutOfService(false);

        bean.toggleOutOfService(pod);

        assertTrue(pod.isOutOfService());
    }

    @Test
    @DisplayName("An offline pod can be returned to service")
    void offlinePodCanBeReturnedToService() {

        Pod pod = addPod("K99", dogRoomID);

        assertTrue(pod.isOutOfService());

        bean.toggleOutOfService(pod);

        assertFalse(pod.isOutOfService());
    }

    @Test
    @DisplayName("A pod with a current booking cannot be taken out of service")
    void occupiedPodCannotBeTakenOutOfService() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        confirmedBooking(pod.getPodID(), -1, 2);

        bean.toggleOutOfService(pod);

        assertFalse(pod.isOutOfService());
    }

    @Test
    @DisplayName("Toggling a null pod does nothing")
    void toggleNullPodDoesNothing() {

        assertNull(bean.toggleOutOfService(null));
    }


    // ---------------------------------------------------------
    // DELETE POD
    // ---------------------------------------------------------

    @Test
    @DisplayName("An eligible pod can be deleted")
    void deleteEligiblePod() {

        Pod pod = addPod("K99", dogRoomID);

        assertEquals(1, podList.countPodsInRoom(dogRoomID));

        assertNull(bean.deletePod(pod));

        assertEquals(0, podList.countPodsInRoom(dogRoomID));
    }

    @Test
    @DisplayName("A pod in service cannot be deleted")
    void deletePodRefusesPodInService() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        assertNull(bean.deletePod(pod));

        assertEquals(1, podList.countPodsInRoom(dogRoomID));
    }

    @Test
    @DisplayName("A pod with a future booking cannot be deleted")
    void deletePodRefusesBookedPod() {

        Pod pod = addPod("K99", dogRoomID);

        confirmedBooking(pod.getPodID(), 10, 12);

        assertNull(bean.deletePod(pod));

        assertEquals(1, podList.countPodsInRoom(dogRoomID));
    }

    @Test
    @DisplayName("Deleting a null pod does nothing")
    void deleteNullPodDoesNothing() {

        assertNull(bean.deletePod(null));

        assertEquals(0, podList.getNumberOfPods());
    }


    // ---------------------------------------------------------
    // DELETE ROOM
    // ---------------------------------------------------------

    @Test
    @DisplayName("A room with eligible pods can be deleted")
    void deleteRoomRemovesRoomAndPods() {

        Pod pod = addPod("K99", dogRoomID);

        int beforeRooms = roomList.getNumberOfRooms();

        assertNull(bean.deleteRoom(dogRoom));

        assertEquals(beforeRooms - 1, roomList.getNumberOfRooms());
        assertNull(roomList.findByID(dogRoomID));
        assertEquals(0, podList.getPodsForRoom(dogRoomID).size());

        // Keep the variable meaningful so the test explicitly confirms
        // the pod belonged to the deleted room.
        assertEquals(dogRoomID, pod.getRoomID());
    }

    @Test
    @DisplayName("A room containing an in-service pod cannot be deleted")
    void deleteRoomRefusesInServicePod() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        int beforeRooms = roomList.getNumberOfRooms();

        assertNull(bean.deleteRoom(dogRoom));

        assertEquals(beforeRooms, roomList.getNumberOfRooms());
        assertEquals(1, podList.getPodsForRoom(dogRoomID).size());
        assertEquals(dogRoomID, pod.getRoomID());
    }

    @Test
    @DisplayName("A room containing a booked pod cannot be deleted")
    void deleteRoomRefusesBookedPod() {

        Pod pod = addPod("K99", dogRoomID);

        confirmedBooking(pod.getPodID(), 10, 12);

        int beforeRooms = roomList.getNumberOfRooms();

        assertNull(bean.deleteRoom(dogRoom));

        assertEquals(beforeRooms, roomList.getNumberOfRooms());
        assertEquals(1, podList.getPodsForRoom(dogRoomID).size());
    }

    @Test
    @DisplayName("Deleting a null room does nothing")
    void deleteNullRoomDoesNothing() {

        int beforeRooms = roomList.getNumberOfRooms();

        assertNull(bean.deleteRoom(null));

        assertEquals(beforeRooms, roomList.getNumberOfRooms());
    }


    // ---------------------------------------------------------
    // ROOM EDITING
    // ---------------------------------------------------------

    @Test
    @DisplayName("Starting a room edit stores the room details")
    void startEditRoomStoresDetails() {

        bean.startEditRoom(dogRoom);

        assertEquals(dogRoomID, bean.getEditingRoomID());
        assertEquals(dogRoom.getName(), bean.getEditRoomName());
        assertEquals(dogRoom.getLocation(), bean.getEditRoomLocation());
        assertEquals(dogRoom.getSpecies(), bean.getEditRoomSpecies());
    }

    @Test
    @DisplayName("A room with eligible pods can be edited")
    void startEditRoomAllowsEligibleRoom() {

        addPod("K99", dogRoomID);

        bean.startEditRoom(dogRoom);

        assertTrue(bean.isEditingRoom(dogRoom));
    }

    @Test
    @DisplayName("A room containing an in-service pod cannot be edited")
    void startEditRoomRefusesInServicePod() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        bean.startEditRoom(dogRoom);

        assertEquals(0, bean.getEditingRoomID());
        assertFalse(bean.isEditingRoom(dogRoom));
    }

    @Test
    @DisplayName("Starting an edit with null does nothing")
    void startEditRoomHandlesNull() {

        bean.startEditRoom(null);

        assertEquals(0, bean.getEditingRoomID());
        assertNull(bean.getEditRoomName());
    }

    @Test
    @DisplayName("isEditingRoom returns false when nothing is being edited")
    void isEditingRoomReturnsFalseInitially() {

        assertFalse(bean.isEditingRoom(dogRoom));
    }

    @Test
    @DisplayName("isEditingRoom handles null")
    void isEditingRoomHandlesNull() {

        assertFalse(bean.isEditingRoom(null));
    }

    @Test
    @DisplayName("isEditingRoom returns true only for the current room")
    void isEditingRoomIdentifiesCurrentRoom() {

        bean.startEditRoom(dogRoom);

        assertTrue(bean.isEditingRoom(dogRoom));
        assertFalse(bean.isEditingRoom(catRoom));
    }


    // ---------------------------------------------------------
    // SAVE ROOM
    // ---------------------------------------------------------

    @Test
    @DisplayName("Saving a room edit changes its details")
    void saveRoomChangesDetails() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName("  New Kennels  ");
        bean.setEditRoomLocation("  New Location  ");
        bean.setEditRoomSpecies(Species.CAT);

        assertNull(bean.saveRoom(dogRoom));

        assertEquals("New Kennels", dogRoom.getName());
        assertEquals("New Location", dogRoom.getLocation());
        assertEquals(Species.CAT, dogRoom.getSpecies());

        assertEquals(0, bean.getEditingRoomID());
        assertNull(bean.getEditRoomName());
        assertNull(bean.getEditRoomLocation());
        assertNull(bean.getEditRoomSpecies());
    }

    @Test
    @DisplayName("A blank room name is not saved")
    void saveRoomRefusesBlankName() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName("   ");
        bean.setEditRoomLocation("North Block");
        bean.setEditRoomSpecies(Species.DOG);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals(dogRoomID, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("A null room name is not saved")
    void saveRoomRefusesNullName() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName(null);
        bean.setEditRoomLocation("North Block");
        bean.setEditRoomSpecies(Species.DOG);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals(dogRoomID, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("A blank room location is not saved")
    void saveRoomRefusesBlankLocation() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName("New Kennels");
        bean.setEditRoomLocation("   ");
        bean.setEditRoomSpecies(Species.DOG);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals(dogRoomID, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("A null room location is not saved")
    void saveRoomRefusesNullLocation() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName("New Kennels");
        bean.setEditRoomLocation(null);
        bean.setEditRoomSpecies(Species.DOG);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals(dogRoomID, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("A missing species is not saved")
    void saveRoomRefusesMissingSpecies() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName("New Kennels");
        bean.setEditRoomLocation("North Block");
        bean.setEditRoomSpecies(null);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals(dogRoomID, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("A duplicate room name is refused")
    void saveRoomRefusesDuplicateName() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName(catRoom.getName());
        bean.setEditRoomLocation("North Block");
        bean.setEditRoomSpecies(Species.DOG);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals(dogRoomID, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("Saving an edit with the existing name is allowed")
    void saveRoomAllowsExistingName() {

        bean.startEditRoom(dogRoom);

        bean.setEditRoomName(dogRoom.getName());
        bean.setEditRoomLocation("New Location");
        bean.setEditRoomSpecies(Species.DOG);

        bean.saveRoom(dogRoom);

        assertEquals("Lakeside Kennels", dogRoom.getName());
        assertEquals("New Location", dogRoom.getLocation());
        assertEquals(0, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("Saving a null room does nothing")
    void saveRoomHandlesNull() {

        bean.setEditRoomName("New Name");
        bean.setEditRoomLocation("New Location");
        bean.setEditRoomSpecies(Species.DOG);

        assertNull(bean.saveRoom(null));

        assertEquals("New Name", bean.getEditRoomName());
    }

    @Test
    @DisplayName("Cancelling a room edit clears the editing state")
    void cancelRoomEditClearsState() {

        bean.startEditRoom(dogRoom);

        assertTrue(bean.isEditingRoom(dogRoom));

        assertNull(bean.cancelRoomEdit());

        assertEquals(0, bean.getEditingRoomID());
        assertNull(bean.getEditRoomName());
        assertNull(bean.getEditRoomLocation());
        assertNull(bean.getEditRoomSpecies());

        assertFalse(bean.isEditingRoom(dogRoom));
    }


    // ---------------------------------------------------------
    // TABLE HELPERS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Room name is returned for a pod")
    void getRoomNameReturnsRoomName() {

        Pod pod = addPod("K99", dogRoomID);

        assertEquals(dogRoom.getName(), bean.getRoomName(pod));
    }

    @Test
    @DisplayName("Unknown room is displayed as a dash")
    void getRoomNameReturnsDashForUnknownRoom() {

        Pod pod = new Pod("Unknown", 99999);

        assertEquals("-", bean.getRoomName(pod));
    }

    @Test
    @DisplayName("Species label is returned for a pod")
    void getSpeciesLabelReturnsSpecies() {

        Pod pod = addPod("K99", dogRoomID);

        assertEquals(Species.DOG.getLabel(), bean.getSpeciesLabel(pod));
    }

    @Test
    @DisplayName("Room label includes the number of pods")
    void getRoomLabelIncludesPodCount() {

        addPod("K99", dogRoomID);
        addPod("K100", dogRoomID);

        assertEquals(
                dogRoom.getName() + " (2 pods)",
                bean.getRoomLabel(dogRoom)
        );
    }

    @Test
    @DisplayName("Species options contain all species")
    void getSpeciesOptionsReturnsAllSpecies() {

        assertEquals(Species.values().length,
                bean.getSpeciesOptions().length);
    }

    @Test
    @DisplayName("Room options return the rooms")
    void getRoomOptionsReturnsRooms() {

        assertEquals(roomList.getNumberOfRooms(),
                bean.getRoomOptions().size());
    }

    @Test
    @DisplayName("Total pods returns all pods in the room")
    void getTotalPodsReturnsPodCount() {

        addPod("K99", dogRoomID);
        addPod("K100", dogRoomID);

        assertEquals(2, bean.getTotalPods(dogRoom));
    }

    @Test
    @DisplayName("Pods in service excludes offline pods")
    void getPodsInServiceCountsOnlyInServicePods() {

        Pod first = addPod("K99", dogRoomID);
        Pod second = addPod("K100", dogRoomID);

        first.setOutOfService(false);
        second.setOutOfService(true);

        assertEquals(1, bean.getPodsInService(dogRoom));
    }


    // ---------------------------------------------------------
    // POD STATUS
    // ---------------------------------------------------------

    @Test
    @DisplayName("An in-service pod with no booking is available")
    void podStatusAvailable() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        bean.setAvailabilityDate(daysFromToday(5));

        assertEquals("Available", bean.getPodStatus(pod));
        assertEquals("status-available", bean.getPodStatusClass(pod));
    }

    @Test
    @DisplayName("An offline pod is out of service")
    void podStatusOutOfService() {

        Pod pod = addPod("K99", dogRoomID);

        bean.setAvailabilityDate(daysFromToday(5));

        assertEquals("Out of service", bean.getPodStatus(pod));
        assertEquals("status-cancelled", bean.getPodStatusClass(pod));
    }

    @Test
    @DisplayName("A pod with a booking on the selected date is occupied")
    void podStatusOccupied() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        confirmedBooking(pod.getPodID(), 5, 7);

        bean.setAvailabilityDate(daysFromToday(5));

        assertEquals("Occupied", bean.getPodStatus(pod));
        assertEquals("status-pending", bean.getPodStatusClass(pod));
    }


    // ---------------------------------------------------------
    // AVAILABILITY
    // ---------------------------------------------------------

    @Test
    @DisplayName("Pods available returns free pods out of in-service pods")
    void getPodsAvailableCountsFreePods() {

        Pod first = addPod("K99", dogRoomID);
        Pod second = addPod("K100", dogRoomID);

        first.setOutOfService(false);
        second.setOutOfService(false);

        confirmedBooking(first.getPodID(), 5, 7);

        bean.setAvailabilityDate(daysFromToday(5));

        assertEquals("1/2", bean.getPodsAvailable(dogRoom));
    }

    @Test
    @DisplayName("An offline pod is excluded from the availability total")
    void getPodsAvailableExcludesOfflinePods() {

        Pod first = addPod("K99", dogRoomID);
        Pod second = addPod("K100", dogRoomID);

        first.setOutOfService(false);
        second.setOutOfService(true);

        bean.setAvailabilityDate(daysFromToday(5));

        assertEquals("1/1", bean.getPodsAvailable(dogRoom));
    }

    @Test
    @DisplayName("Blank availability date falls back to today")
    void blankAvailabilityDateFallsBackToToday() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        bean.setAvailabilityDate("   ");

        assertEquals("Available", bean.getPodStatus(pod));
    }

    @Test
    @DisplayName("Invalid availability date falls back to today")
    void invalidAvailabilityDateFallsBackToToday() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        bean.setAvailabilityDate("not-a-date");

        assertEquals("Available", bean.getPodStatus(pod));
    }

    @Test
    @DisplayName("Applying an availability date only re-renders the page")
    void applyAvailabilityDateReturnsNull() {

        bean.setAvailabilityDate(daysFromToday(10));

        assertNull(bean.applyAvailabilityDate());
        assertEquals(daysFromToday(10), bean.getAvailabilityDate());
    }


    // ---------------------------------------------------------
    // POD LOCKING
    // ---------------------------------------------------------

    @Test
    @DisplayName("An offline pod is not locked")
    void offlinePodIsNotLocked() {

        Pod pod = addPod("K99", dogRoomID);

        bean.setAvailabilityDate(daysFromToday(5));

        assertFalse(bean.isPodLocked(pod));
    }

    @Test
    @DisplayName("A free pod is not locked")
    void freePodIsNotLocked() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        bean.setAvailabilityDate(daysFromToday(5));

        assertFalse(bean.isPodLocked(pod));
    }

    @Test
    @DisplayName("A pod booked on the selected date is locked")
    void selectedDateBookingLocksPod() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        confirmedBooking(pod.getPodID(), 5, 7);

        bean.setAvailabilityDate(daysFromToday(5));

        assertTrue(bean.isPodLocked(pod));
    }

    @Test
    @DisplayName("A pod occupied today remains locked when viewing another date")
    void currentBookingLocksPodRegardlessOfSelectedDate() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        confirmedBooking(pod.getPodID(), -1, 2);

        bean.setAvailabilityDate(daysFromToday(30));

        assertTrue(bean.isPodLocked(pod));
    }


    // ---------------------------------------------------------
    // PODS BY ROOM
    // ---------------------------------------------------------

    @Test
    @DisplayName("Pods are returned grouped by room order")
    void getPodsByRoomGroupsPodsByRoom() {

        Pod dogPod = addPod("K99", dogRoomID);
        Pod catPod = addPod("C99", catRoomID);

        List<Pod> grouped = bean.getPodsByRoom();

        assertEquals(2, grouped.size());

        assertEquals(dogPod.getPodID(),
                grouped.get(0).getPodID());

        assertEquals(catPod.getPodID(),
                grouped.get(1).getPodID());
    }


    // ---------------------------------------------------------
    // AVAILABILITY DATE GETTER / SETTER
    // ---------------------------------------------------------

    @Test
    @DisplayName("Availability date getter and setter work")
    void availabilityDateGetterSetterWork() {

        bean.setAvailabilityDate("2026-09-01");

        assertEquals("2026-09-01", bean.getAvailabilityDate());
    }


    // ---------------------------------------------------------
    // EDIT ROOM GETTERS / SETTERS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Editing room ID getter and setter work")
    void editingRoomIDGetterSetterWork() {

        bean.setEditingRoomID(123);

        assertEquals(123, bean.getEditingRoomID());
    }

    @Test
    @DisplayName("Edit room name getter and setter work")
    void editRoomNameGetterSetterWork() {

        bean.setEditRoomName("Test Room");

        assertEquals("Test Room", bean.getEditRoomName());
    }

    @Test
    @DisplayName("Edit room location getter and setter work")
    void editRoomLocationGetterSetterWork() {

        bean.setEditRoomLocation("Test Location");

        assertEquals("Test Location", bean.getEditRoomLocation());
    }

    @Test
    @DisplayName("Edit room species getter and setter work")
    void editRoomSpeciesGetterSetterWork() {

        bean.setEditRoomSpecies(Species.CAT);

        assertEquals(Species.CAT, bean.getEditRoomSpecies());
    }
    
    // ---------------------------------------------------------
    // ROOM POD SERVICE STATUS
    // ---------------------------------------------------------

    @Test
    @DisplayName("A room is locked when a pod is occupied on the selected date")
    void roomIsLockedWhenPodIsOccupiedOnSelectedDate() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        confirmedBooking(pod.getPodID(), 5, 7);

        bean.setAvailabilityDate(daysFromToday(5));

        assertTrue(bean.isRoomLocked(dogRoom));
    }

    @Test
    @DisplayName("A room is not locked when no pod is occupied on the selected date")
    void roomIsNotLockedWhenNoPodIsOccupied() {

        Pod pod = addPod("K99", dogRoomID);
        pod.setOutOfService(false);

        bean.setAvailabilityDate(daysFromToday(5));

        assertFalse(bean.isRoomLocked(dogRoom));
    }

    @Test
    @DisplayName("A null room is never locked")
    void nullRoomIsNotLocked() {

        assertFalse(bean.isRoomLocked(null));
    }


    // ---------------------------------------------------------
    // BULK ROOM POD TOGGLE
    // ---------------------------------------------------------

    @Test
    @DisplayName("A room with in-service pods can take all pods out of service")
    void roomCanTakeAllPodsOutOfService() {

        Pod first = addPod("K99", dogRoomID);
        Pod second = addPod("K100", dogRoomID);

        first.setOutOfService(false);
        second.setOutOfService(false);

        bean.setAvailabilityDate(daysFromToday(5));

        assertFalse(bean.allPodsOutOfService(dogRoom));

        assertNull(bean.toggleRoomPodsOutOfService(dogRoom));

        assertTrue(first.isOutOfService());
        assertTrue(second.isOutOfService());
        assertTrue(bean.allPodsOutOfService(dogRoom));
    }

    @Test
    @DisplayName("A room with all pods out of service can return all pods to service")
    void roomCanReturnAllPodsToService() {

        Pod first = addPod("K99", dogRoomID);
        Pod second = addPod("K100", dogRoomID);

        // Pods are created out of service by default.
        assertTrue(first.isOutOfService());
        assertTrue(second.isOutOfService());
        assertTrue(bean.allPodsOutOfService(dogRoom));

        bean.setAvailabilityDate(daysFromToday(5));

        assertNull(bean.toggleRoomPodsOutOfService(dogRoom));

        assertFalse(first.isOutOfService());
        assertFalse(second.isOutOfService());
        assertFalse(bean.allPodsOutOfService(dogRoom));
    }
    
    @Test
    @DisplayName("A room with no pods is not considered all out of service")
    void roomWithNoPodsIsNotAllOutOfService() {

        assertFalse(bean.allPodsOutOfService(dogRoom));
    }

    @Test
    @DisplayName("A null room cannot bulk toggle pods")
    void toggleRoomPodsWithNullDoesNothing() {

        assertNull(bean.toggleRoomPodsOutOfService(null));
    }

    @Test
    @DisplayName("A room with mixed pod status is not all out of service")
    void roomWithMixedPodStatusIsNotAllOutOfService() {

        Pod first = addPod("K99", dogRoomID);
        Pod second = addPod("K100", dogRoomID);

        first.setOutOfService(true);
        second.setOutOfService(false);

        assertFalse(bean.allPodsOutOfService(dogRoom));
    }
}