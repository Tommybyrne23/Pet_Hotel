package com.tus.registration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tus.pethotel.Pet;
import com.tus.pethotel.PetList;
import com.tus.Services.Species;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("PetEditBean")
@SessionScoped
public class PetEditBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LoginBean loginBean;

    @Inject
    private PetList petList;

    private List<Pet> userPets;

    // Store the ID rather than the whole Pet object
    private int selectedPetID;


    /**
     * Load only the pets belonging to the currently logged-in user.
     */
    public void loadPets() {

        if (loginBean.getLoggedInUser() != null) {

            int userID = loginBean.getLoggedInUser().getUserID();

            userPets = petList.findByUserID(userID);

            // Only select the first pet the first time the list is loaded
            if (userPets != null
                    && !userPets.isEmpty()
                    && selectedPetID == 0) {

                selectedPetID = userPets.get(0).getPetID();
            }
        }
    }


    /**
     * Gets the currently selected Pet object.
     */
    public Pet getSelectedPet() {

        if (userPets == null) {
            return null;
        }

        for (Pet pet : userPets) {

            if (pet.getPetID() == selectedPetID) {
                return pet;
            }
        }

        return null;
    }


    /**
     * Called when the user selects a different pet.
     */
    public void petChanged() {
        // selectedPetID is automatically updated by JSF.
    }


    /**
     * Called when the species is changed.
     *
     * Clears the existing age because the available age ranges
     * depend on the selected species.
     */
    public void speciesChanged() {

        Pet selectedPet = getSelectedPet();

        if (selectedPet != null) {
            selectedPet.setAge(null);
        }
    }


    /**
     * Returns the available age ranges for the selected species.
     */
    public List<SelectItem> getAgeRanges() {

        List<SelectItem> ages = new ArrayList<>();

        Pet selectedPet = getSelectedPet();

        if (selectedPet == null || selectedPet.getSpecies() == null) {
            return ages;
        }

        switch (selectedPet.getSpecies()) {

            case DOG:
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–3 years", "1–3 years"));
                ages.add(new SelectItem("4–7 years", "4–7 years"));
                ages.add(new SelectItem("8–12 years", "8–12 years"));
                ages.add(new SelectItem("13+ years", "13+ years"));
                break;

            case CAT:
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–3 years", "1–3 years"));
                ages.add(new SelectItem("4–7 years", "4–7 years"));
                ages.add(new SelectItem("8–12 years", "8–12 years"));
                ages.add(new SelectItem("13+ years", "13+ years"));
                break;

            case BIRD:
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–5 years", "1–5 years"));
                ages.add(new SelectItem("6–10 years", "6–10 years"));
                ages.add(new SelectItem("11–20 years", "11–20 years"));
                ages.add(new SelectItem("21+ years", "21+ years"));
                break;

            case REPTILE:
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–3 years", "1–3 years"));
                ages.add(new SelectItem("4–7 years", "4–7 years"));
                ages.add(new SelectItem("8–15 years", "8–15 years"));
                ages.add(new SelectItem("16+ years", "16+ years"));
                break;

            case FISH:
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–2 years", "1–2 years"));
                ages.add(new SelectItem("3–5 years", "3–5 years"));
                ages.add(new SelectItem("6+ years", "6+ years"));
                break;

            default:
                break;
        }

        return ages;
    }


    /**
     * Save the selected pet's changes.
     *
     * The selected Pet is already the same object stored inside PetList,
     * so changes made through the XHTML form are already applied to it.
     */
    public String savePet() {

        Pet selectedPet = getSelectedPet();

        if (selectedPet == null) {
            return null;
        }

        FacesContext context = FacesContext.getCurrentInstance();

        context.getExternalContext()
               .getFlash()
               .setKeepMessages(true);

        context.addMessage(
            null,
            new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Pet details have been updated successfully.",
                null
            )
        );

        return "/userDashboard?faces-redirect=true";
    }


    /**
     * Return to the dashboard without saving.
     */
    public String cancel() {
        return "/userDashboard?faces-redirect=true";
    }


    // GETTERS AND SETTERS

    public List<Pet> getUserPets() {
        return userPets;
    }

    public void setUserPets(List<Pet> userPets) {
        this.userPets = userPets;
    }

    public int getSelectedPetID() {
        return selectedPetID;
    }

    public void setSelectedPetID(int selectedPetID) {
        this.selectedPetID = selectedPetID;
    }
}