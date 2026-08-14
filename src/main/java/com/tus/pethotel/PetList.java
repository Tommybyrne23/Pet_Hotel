package com.tus.pethotel;

import com.tus.Services.Species;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("petList")
@ApplicationScoped
public class PetList implements Serializable {

	private static final long serialVersionUID = 1L;

	// List of all registered pets
	private List<Pet> pets;

	// Create the Pet list
	public PetList() {
		this.pets = new ArrayList<>();
	}

	// Adding pets to the PetList for demonstration and testing
	@PostConstruct
	public void init() {
		if (pets.isEmpty()) {
			// Updated to User ID 6 to match Customer User and ReservationList records
			addPet(new Pet(6, "Buddy", Species.DOG, "Golden Retriever", "4–7 years", "Requires morning medication."));
			addPet(new Pet(6, "Luna", Species.CAT, "Siamese", "8–12 years ", "Needs a quiet room away from dogs."));
			addPet(new Pet(6, "Max", Species.DOG, "Beagle", "1–3 years", "Loves extra outdoor walks."));
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
	public List<Pet> findByUserID(int userID) {
		List<Pet> userPets = new ArrayList<>();
		for (Pet existingPet : pets) {
			if (existingPet.getUserID() == userID) {
				userPets.add(existingPet);
			}
		}
		return userPets;
	}

	// Return the complete list of pets
	public List<Pet> getPets() {
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
	
	// Search pet by user ID - for the customer dashboard 
	public List<Pet> getPetsByUserId(int userId) {
		List<Pet> userPets = new ArrayList<>();
		for (Pet p : pets) {
			if (p.getUserID() == userId) { // Matches pet owner to logged-in user ID
				userPets.add(p);
			}
		}
		return userPets;
	}
}