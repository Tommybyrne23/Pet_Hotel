package com.tus.pethotel;

import java.io.Serializable;

import com.tus.Services.Species;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("podBean")
@SessionScoped
public class PodBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private PodList podList;

    @Inject
    private RoomList roomList;

    private int selectedPodID;

    private Pod selectedPod;

    private String editPodLabel;


    // ---------------------------------------------------------
    // LOAD SELECTED POD
    // ---------------------------------------------------------

    /**
     * Loads the pod selected by the admin.
     *
     * This is called when the edit page is opened.
     */
    public void loadPod() {

        if (selectedPodID <= 0) {
            selectedPod = null;
            editPodLabel = null;
            return;
        }

        selectedPod = podList.findByID(selectedPodID);

        if (selectedPod != null) {
            editPodLabel = selectedPod.getLabel();
        }
    }


    // ---------------------------------------------------------
    // SAVE POD
    // ---------------------------------------------------------

    /**
     * Saves the new pod name.
     */
    public String savePod() {

        // Make sure the selected pod exists
        if (selectedPod == null || selectedPod.getPodID() != selectedPodID) {
            selectedPod = podList.findByID(selectedPodID);
        }

        // Pod not found
        if (selectedPod == null) {
            return "/viewPodsRooms.xhtml?faces-redirect=true";
        }

        FacesContext context = FacesContext.getCurrentInstance();

        // ---------------------------------------------------------
        // CHECK FOR BLANK NAME
        // ---------------------------------------------------------

        if (editPodLabel == null || editPodLabel.trim().isEmpty()) {

            context.addMessage(
                "editPodForm:podName",
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Please enter a pod name.",
                    null
                )
            );

            return null;
        }

        String newLabel = editPodLabel.trim();

        // ---------------------------------------------------------
        // CHECK FOR DUPLICATE NAME
        // ---------------------------------------------------------

        boolean sameAsCurrentName =
            newLabel.equalsIgnoreCase(selectedPod.getLabel());

        if (!sameAsCurrentName
                && podList.isLabelTakenInRoom(
                    newLabel,
                    selectedPod.getRoomID())) {

            context.addMessage(
                "editPodForm:podName",
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "A pod with this name already exists in this room.",
                    null
                )
            );

            return null;
        }

        // ---------------------------------------------------------
        // SAVE / RENAME POD
        // ---------------------------------------------------------

        boolean renamed = podList.renamePod(
            selectedPod.getPodID(),
            newLabel
        );

        // Save failed
        if (!renamed) {

            context.addMessage(
                "editPodForm:podName",
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "The pod could not be saved. Please try again.",
                    null
                )
            );

            return null;
        }

        // ---------------------------------------------------------
        // SUCCESSFUL SAVE
        // ---------------------------------------------------------

        selectedPod = null;
        selectedPodID = 0;
        editPodLabel = null;

        // Redirect to the pods/rooms page
        return "/viewPodsRooms.xhtml?faces-redirect=true";
    }

    // ---------------------------------------------------------
    // SELECTED POD
    // ---------------------------------------------------------

    public Pod getSelectedPod() {

        if (selectedPod == null && selectedPodID > 0) {
            selectedPod = podList.findByID(selectedPodID);

            if (selectedPod != null && editPodLabel == null) {
                editPodLabel = selectedPod.getLabel();
            }
        }

        return selectedPod;
    }


    // ---------------------------------------------------------
    // ROOM
    // ---------------------------------------------------------

    public Room getSelectedPodRoom() {

        Pod pod = getSelectedPod();

        if (pod == null) {
            return null;
        }

        return roomList.findByID(pod.getRoomID());
    }


    // ---------------------------------------------------------
    // SPECIES
    // ---------------------------------------------------------

    public Species getSelectedPodSpecies() {

        Pod pod = getSelectedPod();

        if (pod == null) {
            return null;
        }

        return podList.getSpeciesFor(pod);
    }


    // ---------------------------------------------------------
    // STATUS
    // ---------------------------------------------------------

    public String getSelectedPodStatus() {

        Pod pod = getSelectedPod();

        if (pod == null) {
            return "";
        }

        if (pod.isOutOfService()) {
            return "Out of service";
        }

        return "Available";
    }


    // ---------------------------------------------------------
    // CANCEL
    // ---------------------------------------------------------

    public String cancel() {

        // Clear the editing state
        selectedPod = null;
        selectedPodID = 0;
        editPodLabel = null;

        // Redirect back to the pods/rooms page
        return "/viewPodsRooms.xhtml?faces-redirect=true";
    }


    // ---------------------------------------------------------
    // MESSAGES
    // ---------------------------------------------------------

    private void addMessage(
            FacesMessage.Severity severity,
            String message) {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context != null) {

            context.addMessage(
                null,
                new FacesMessage(
                    severity,
                    message,
                    null
                )
            );
        }
    }


    // ---------------------------------------------------------
    // GETTERS / SETTERS
    // ---------------------------------------------------------

    public int getSelectedPodID() {
        return selectedPodID;
    }

    public void setSelectedPodID(int selectedPodID) {

        this.selectedPodID = selectedPodID;

        // Load the pod immediately when the ID changes
        if (selectedPodID > 0) {

            selectedPod = podList.findByID(selectedPodID);

            if (selectedPod != null) {
                editPodLabel = selectedPod.getLabel();
            }
        }
    }


    public Pod getSelectedPodObject() {
        return selectedPod;
    }


    public void setSelectedPod(Pod selectedPod) {

        this.selectedPod = selectedPod;

        if (selectedPod != null) {

            this.selectedPodID = selectedPod.getPodID();
            this.editPodLabel = selectedPod.getLabel();
        }
    }


    public String getEditPodLabel() {
        return editPodLabel;
    }


    public void setEditPodLabel(String editPodLabel) {
        this.editPodLabel = editPodLabel;
    }


    // ---------------------------------------------------------
    // TESTING / CDI SETTERS
    // ---------------------------------------------------------

    public void setPodList(PodList podList) {
        this.podList = podList;
    }

    public void setRoomList(RoomList roomList) {
        this.roomList = roomList;
    }
}