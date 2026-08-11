
package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("petList")
@ApplicationScoped
public class PetList implements Serializable {

    private static final long serialVersionUID = 1L;

    // List of all registered pets
    private ArrayList<Pet> pets;


    // Create the Pet list
    public PetList() {
        this.pets = new ArrayList<>();
    }


    // How many pets are registered
    public int getNumberOfPets() {
        return pets.size();
    }


    // Add a pet to the list
    public void addPet(Pet pet) {
        pets.add(pet);
    }


    // Find all pets belonging to a particular user
    public ArrayList<Pet> findByUserID(int userID) {

        ArrayList<Pet> userPets = new ArrayList<>();

        for (Pet existingPet : pets) {

            if (existingPet.getUserID() == userID) {
                userPets.add(existingPet);
            }
        }

        return userPets;
    }


    // Return the complete list of pets
    public ArrayList<Pet> getPets() {
        return pets;
    }
}

