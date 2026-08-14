package com.tus.registration;

import java.io.Serializable;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

import com.tus.pethotel.Pet;
import com.tus.pethotel.PetList;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("petRegistrationBean")
@SessionScoped
public class PetRegistrationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private boolean vaccinated;

    // The pet currently being registered
    private Pet pet;

    // Pet list containing all registered pets
    @Inject
    private PetList petList;

    // Login bean containing the currently logged-in user
    @Inject
    private LoginBean loginBean;
    
    public void setPetList(PetList petList) {
        this.petList = petList;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    @PostConstruct
    public void init() {
        pet = new Pet();
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
    
    public boolean isVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public List<SelectItem> getAgeRanges() {

        List<SelectItem> ages = new ArrayList<>();

        if (pet.getSpecies() == null) {
            return ages;
        }

        switch (pet.getSpecies()) {

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
        }

        return ages;
    }

    public void speciesChanged() {
        pet.setBreed(null);
        pet.setAge(null);
    }

    // Register the pet
    public String register() {
    	
    	if (!vaccinated) {
    	    return null;
    	}

        int userID = loginBean.getLoggedInUser().getUserID();

        Pet newPet = new Pet(
                userID,
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getAge(),
                pet.getRequirements()
        );

        petList.addPet(newPet);

        pet = new Pet();

        return "userDashboard?faces-redirect=true";
    }

    // Reset the registration form
    public String reset() {

        pet = new Pet();

        return "addPet?faces-redirect=true";
    }
}

