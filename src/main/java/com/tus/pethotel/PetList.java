
package com.tus.pethotel;

import com.tus.Services.Species;
import java.io.Serializable;
import java.util.ArrayList;

import jakarta.annotation.PostConstruct;
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

	//adding pets to the PetList for demonstration and testing, these will appear on the Customer User 
	@PostConstruct
	public void init() {
		if (pets.isEmpty()) {
			// User ID 3 corresponds to "Customer User" (user@3apets.ie)
			addPet(new Pet(3, "Buddy", Species.DOG, "Golden Retriever", "4–7 years", "Requires morning medication."));
			addPet(new Pet(3, "Luna", Species.CAT, "Siamese", "8–12 years ", "Needs a quiet room away from dogs."));
			addPet(new Pet(3, "Max", Species.DOG, "Beagle", "1–3 years", "Loves extra outdoor walks."));
		}
	}

	// How many pets are registered
	public int getNumberOfPets() {
		return pets.size();
	}


	// Add a pet to the list
	public void addPet(Pet pet) {
		pets.add(pet);
	}
	
	public void removePet(int petID) {

	    pets.removeIf(pet -> pet.getPetID() == petID);
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


	// Find a single pet by its petID
	public Pet findByPetID(int petID) {
		for (Pet pet : pets) {
			if (pet.getPetID() == petID) {
				return pet;
			}
		}
		return null;
	}

	// Called directly by #{petList.getRequirements(booking.petID)} in XHTML
	public String getRequirements(int petID) {
		Pet pet = findByPetID(petID);
		if (pet != null && pet.getRequirements() != null && !pet.getRequirements().trim().isEmpty()) {
			return pet.getRequirements();
		}
		return "None"; // Default fallback display if no requirements exist or pet isn't found
	}
}



