package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.services.Species;

class PodBeanTest {

    private RoomList roomList;
    private PodList podList;
    private PodBean podBean;

    private int dogRoomID;
    private int catRoomID;

    private Pod dogPod;
    private Pod secondDogPod;
    private Pod catPod;

    @BeforeEach
    void setUp() {

        roomList = new RoomList();

        podList = new PodList();
        podList.setRoomList(roomList);
        podList.setReservationList(new ReservationList());

        podBean = new PodBean();
        podBean.setPodList(podList);

        dogRoomID = roomList.getRoomsForSpecies(Species.DOG)
                .get(0)
                .getRoomID();

        catRoomID = roomList.getRoomsForSpecies(Species.CAT)
                .get(0)
                .getRoomID();

        dogPod = new Pod("K1", dogRoomID);
        secondDogPod = new Pod("K2", dogRoomID);
        catPod = new Pod("C1", catRoomID);

        podList.addPod(dogPod);
        podList.addPod(secondDogPod);
        podList.addPod(catPod);

        // New pods are out of service, which makes them eligible for editing.
    }


    // ---------------------------------------------------------
    // START EDITING
    // ---------------------------------------------------------

    @Test
    @DisplayName("Starting an edit stores the selected pod's details")
    void startEditStoresPodDetails() {

        podBean.startEdit(dogPod);

        assertEquals(dogPod.getPodID(), podBean.getEditingPodID());
        assertEquals("K1", podBean.getEditPodLabel());
        assertEquals(dogRoomID, podBean.getEditPodRoomID());
    }

    @Test
    @DisplayName("A pod can be edited when it is out of service and has no blocking reservations")
    void startEditAllowsEligiblePod() {

        podBean.startEdit(dogPod);

        assertTrue(podBean.isEditing(dogPod));
    }

    @Test
    @DisplayName("Starting an edit with null does nothing")
    void startEditHandlesNull() {

        podBean.startEdit(null);

        assertEquals(0, podBean.getEditingPodID());
        assertNull(podBean.getEditPodLabel());
    }

    @Test
    @DisplayName("A pod in service cannot be edited")
    void startEditRefusesPodInService() {

        dogPod.setOutOfService(false);

        podBean.startEdit(dogPod);

        assertEquals(0, podBean.getEditingPodID());
        assertNull(podBean.getEditPodLabel());
    }


    // ---------------------------------------------------------
    // IS EDITING
    // ---------------------------------------------------------

    @Test
    @DisplayName("isEditing returns true for the pod currently being edited")
    void isEditingReturnsTrueForCurrentPod() {

        podBean.startEdit(dogPod);

        assertTrue(podBean.isEditing(dogPod));
        assertFalse(podBean.isEditing(secondDogPod));
    }

    @Test
    @DisplayName("isEditing returns false when no pod is being edited")
    void isEditingReturnsFalseWhenNothingIsBeingEdited() {

        assertFalse(podBean.isEditing(dogPod));
    }

    @Test
    @DisplayName("isEditing handles null")
    void isEditingHandlesNull() {

        assertFalse(podBean.isEditing(null));
    }


    // ---------------------------------------------------------
    // SAVE POD
    // ---------------------------------------------------------

    @Test
    @DisplayName("Saving an edit changes the pod label")
    void savePodChangesLabel() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel("K9");
        podBean.setEditPodRoomID(dogRoomID);

        podBean.savePod(dogPod);

        assertEquals("K9", dogPod.getLabel());
        assertEquals(0, podBean.getEditingPodID());
        assertNull(podBean.getEditPodLabel());
    }

    @Test
    @DisplayName("Saving an edit can move the pod to another room")
    void savePodMovesPodToAnotherRoom() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel("K1");
        podBean.setEditPodRoomID(catRoomID);

        podBean.savePod(dogPod);

        assertEquals(catRoomID, dogPod.getRoomID());
        assertEquals("K1", dogPod.getLabel());
        assertEquals(0, podBean.getEditingPodID());
    }

    @Test
    @DisplayName("Saving an edit can change both the label and room")
    void savePodChangesLabelAndRoom() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel("NewPod");
        podBean.setEditPodRoomID(catRoomID);

        podBean.savePod(dogPod);

        assertEquals("NewPod", dogPod.getLabel());
        assertEquals(catRoomID, dogPod.getRoomID());
    }

    @Test
    @DisplayName("A blank label is not saved")
    void savePodRefusesBlankLabel() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel("   ");
        podBean.setEditPodRoomID(dogRoomID);

        podBean.savePod(dogPod);

        assertEquals("K1", dogPod.getLabel());
        assertEquals(dogPod.getPodID(), podBean.getEditingPodID());
    }

    @Test
    @DisplayName("A null label is not saved")
    void savePodRefusesNullLabel() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel(null);
        podBean.setEditPodRoomID(dogRoomID);

        podBean.savePod(dogPod);

        assertEquals("K1", dogPod.getLabel());
        assertEquals(dogPod.getPodID(), podBean.getEditingPodID());
    }

    @Test
    @DisplayName("A duplicate label in the destination room is refused")
    void savePodRefusesDuplicateLabel() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel("K2");
        podBean.setEditPodRoomID(dogRoomID);

        podBean.savePod(dogPod);

        assertEquals("K1", dogPod.getLabel());
        assertEquals(dogRoomID, dogPod.getRoomID());
        assertEquals(dogPod.getPodID(), podBean.getEditingPodID());
    }

    @Test
    @DisplayName("The same label is allowed when moving to a different room")
    void savePodAllowsSameLabelInDifferentRoom() {

        podBean.startEdit(dogPod);

        podBean.setEditPodLabel("K2");
        podBean.setEditPodRoomID(catRoomID);

        podBean.savePod(dogPod);

        assertEquals("K2", dogPod.getLabel());
        assertEquals(catRoomID, dogPod.getRoomID());
        assertEquals(0, podBean.getEditingPodID());
    }

    @Test
    @DisplayName("Saving a null pod does nothing")
    void savePodHandlesNull() {

        podBean.setEditPodLabel("NewName");
        podBean.setEditPodRoomID(catRoomID);

        podBean.savePod(null);

        assertEquals("NewName", podBean.getEditPodLabel());
    }


    // ---------------------------------------------------------
    // CANCEL EDIT
    // ---------------------------------------------------------

    @Test
    @DisplayName("Cancelling an edit clears the editing state")
    void cancelEditClearsEditingState() {

        podBean.startEdit(dogPod);

        assertTrue(podBean.isEditing(dogPod));

        podBean.cancelEdit();

        assertEquals(0, podBean.getEditingPodID());
        assertNull(podBean.getEditPodLabel());
        assertFalse(podBean.isEditing(dogPod));
    }


    // ---------------------------------------------------------
    // GETTERS / SETTERS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Editing pod ID getter and setter work")
    void editingPodIDGetterSetterWork() {

        podBean.setEditingPodID(123);

        assertEquals(123, podBean.getEditingPodID());
    }

    @Test
    @DisplayName("Edit pod label getter and setter work")
    void editPodLabelGetterSetterWork() {

        podBean.setEditPodLabel("TestLabel");

        assertEquals("TestLabel", podBean.getEditPodLabel());
    }

    @Test
    @DisplayName("Edit pod room ID getter and setter work")
    void editPodRoomIDGetterSetterWork() {

        podBean.setEditPodRoomID(catRoomID);

        assertEquals(catRoomID, podBean.getEditPodRoomID());
    }
}