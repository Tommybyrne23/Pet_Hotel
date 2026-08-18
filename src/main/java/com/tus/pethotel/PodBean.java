package com.tus.pethotel;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named("podBean")
@SessionScoped
public class PodBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private PodList podList;

    // The pod currently being edited
    private int editingPodID = 0;

    // Temporary value typed into the input box
    private String editPodLabel;
    private int editPodRoomID;


    // ---------------------------------------------------------
    // START EDITING
    // ---------------------------------------------------------

    public void startEdit(Pod pod) {

        if (pod == null) {
            return;
        }

        // Use the same rules as deleting a pod.
        if (!podList.canDeletePod(pod.getPodID())) {

            FacesContext context = FacesContext.getCurrentInstance();

            if (context != null) {
            	context.addMessage(
            		    "editPodButton",
            		    new FacesMessage(
            		        FacesMessage.SEVERITY_ERROR,
            		        "This pod cannot be edited because it has a reservation within the next 365 days.",
            		        null
            		    )
            	);
            }

            return;
        }

        editingPodID = pod.getPodID();
        editPodLabel = pod.getLabel();
        editPodRoomID = pod.getRoomID();
    }


    // ---------------------------------------------------------
    // CHECK IF POD IS BEING EDITED
    // ---------------------------------------------------------

    public boolean isEditing(Pod pod) {

        if (pod == null) {
            return false;
        }

        return pod.getPodID() == editingPodID;
    }


    // ---------------------------------------------------------
    // SAVE POD
    // ---------------------------------------------------------

    public void savePod(Pod pod) {

        if (pod == null) {
            return;
        }

        if (editPodLabel == null || editPodLabel.trim().isEmpty()) {
            return;
        }

        boolean updated = podList.updatePod(
            pod.getPodID(),
            editPodLabel,
            editPodRoomID
        );

        if (!updated) {

            FacesContext context = FacesContext.getCurrentInstance();

            if (context != null) {
                context.addMessage(
                    "editPodButton",
                    new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "A pod with this name already exists in that room.",
                        null
                    )
                );
            }

            return;
        }

        editingPodID = 0;
        editPodLabel = null;
    }


    // ---------------------------------------------------------
    // CANCEL EDIT
    // ---------------------------------------------------------

    public void cancelEdit() {

        editingPodID = 0;
        editPodLabel = null;
    }


    // ---------------------------------------------------------
    // GETTERS / SETTERS
    // ---------------------------------------------------------

    public int getEditingPodID() {
        return editingPodID;
    }

    public void setEditingPodID(int editingPodID) {
        this.editingPodID = editingPodID;
    }

    public String getEditPodLabel() {
        return editPodLabel;
    }

    public void setEditPodLabel(String editPodLabel) {
        this.editPodLabel = editPodLabel;
    }
    
    public int getEditPodRoomID() {
        return editPodRoomID;
    }

    public void setEditPodRoomID(int editPodRoomID) {
        this.editPodRoomID = editPodRoomID;
    }


    // ---------------------------------------------------------
    // TESTING / CDI SETTER
    // ---------------------------------------------------------

    public void setPodList(PodList podList) {
        this.podList = podList;
    }
}