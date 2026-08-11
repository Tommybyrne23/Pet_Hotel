
package com.tus.registration;

import java.io.Serializable;

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


    // The pet currently being registered
    private Pet pet;


    // Pet list containing all registered pets
    @Inject
    private PetList petList;


    // Login bean containing the currently logged-in user
    @Inject
    private LoginBean loginBean;


    @PostConstruct
    public void init() {
        pet = new Pet();
    }


    // Allows Jakarta Faces to access the current pet
    public Pet getPet() {
        return pet;
    }


    // Allows Jakarta Faces to update the current pet
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    
    public List<SelectItem> getBreeds() {

        List<SelectItem> breeds = new ArrayList<>();

        if (pet.getSpecies() == null) {
            return breeds;
        }

        switch (pet.getSpecies()) {

            case "Dog":
                breeds.add(new SelectItem("Labrador", "Labrador"));
                breeds.add(new SelectItem("German Shepherd", "German Shepherd"));
                breeds.add(new SelectItem("Poodle", "Poodle"));
                breeds.add(new SelectItem("Golden Retriever", "Golden Retriever"));
                breeds.add(new SelectItem("Bulldog", "Bulldog"));
                break;

            case "Cat":
                breeds.add(new SelectItem("Siamese", "Siamese"));
                breeds.add(new SelectItem("Persian", "Persian"));
                breeds.add(new SelectItem("Maine Coon", "Maine Coon"));
                breeds.add(new SelectItem("British Shorthair", "British Shorthair"));
                breeds.add(new SelectItem("Bengal", "Bengal"));
                break;

            case "Bird":
                breeds.add(new SelectItem("Budgie", "Budgie"));
                breeds.add(new SelectItem("Canary", "Canary"));
                breeds.add(new SelectItem("Parrot", "Parrot"));
                breeds.add(new SelectItem("Cockatiel", "Cockatiel"));
                break;

            case "Reptile":
                breeds.add(new SelectItem("Bearded Dragon", "Bearded Dragon"));
                breeds.add(new SelectItem("Gecko", "Gecko"));
                breeds.add(new SelectItem("Corn Snake", "Corn Snake"));
                breeds.add(new SelectItem("Tortoise", "Tortoise"));
                break;

            case "Fish":
                breeds.add(new SelectItem("Goldfish", "Goldfish"));
                breeds.add(new SelectItem("Betta", "Betta"));
                breeds.add(new SelectItem("Guppy", "Guppy"));
                breeds.add(new SelectItem("Tetra", "Tetra"));
                break;
        }

        return breeds;
    }
    
    public List<SelectItem> getAgeRanges() {

        List<SelectItem> ages = new ArrayList<>();

        if (pet.getSpecies() == null) {
            return ages;
        }

        switch (pet.getSpecies()) {

            case "Dog":
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–3 years", "1–3 years"));
                ages.add(new SelectItem("4–7 years", "4–7 years"));
                ages.add(new SelectItem("8–12 years", "8–12 years"));
                ages.add(new SelectItem("13+ years", "13+ years"));
                break;

            case "Cat":
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–3 years", "1–3 years"));
                ages.add(new SelectItem("4–7 years", "4–7 years"));
                ages.add(new SelectItem("8–12 years", "8–12 years"));
                ages.add(new SelectItem("13+ years", "13+ years"));
                break;

            case "Bird":
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–5 years", "1–5 years"));
                ages.add(new SelectItem("6–10 years", "6–10 years"));
                ages.add(new SelectItem("11–20 years", "11–20 years"));
                ages.add(new SelectItem("21+ years", "21+ years"));
                break;

            case "Reptile":
                ages.add(new SelectItem("Less than 1 year", "Less than 1 year"));
                ages.add(new SelectItem("1–3 years", "1–3 years"));
                ages.add(new SelectItem("4–7 years", "4–7 years"));
                ages.add(new SelectItem("8–15 years", "8–15 years"));
                ages.add(new SelectItem("16+ years", "16+ years"));
                break;

            case "Fish":
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

        // Get the currently logged-in user
        int userID = loginBean.getLoggedInUser().getUserID();

        // Create the pet using the logged-in user's ID
        Pet newPet = new Pet(
                userID,
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getAge()
        );

        // Add the pet to the PetList
        petList.addPet(newPet);

        // Reset the form
        pet = new Pet();

        // Return to the user's dashboard
        return "userDashboard?faces-redirect=true";
    }


    // Reset the registration form
    public String reset() {

        pet = new Pet();

        return "addPet?faces-redirect=true";
    }
}


