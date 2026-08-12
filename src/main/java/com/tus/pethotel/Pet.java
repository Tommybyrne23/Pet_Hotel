package com.tus.pethotel;

import java.io.Serializable;

public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int uuID = 0;

    private int petID;
    private int userID;
    private String name;
    private Species species;
    private String breed;
    private String age;
    private String requirements;


    // Default constructor required by Jakarta Faces
    public Pet() {
    }


    // Constructor for creating a new pet
    public Pet(int userID, String name, Species species, String breed,
            String age, String requirements) {
    	

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet name is required.");
        }

        if (species == null) {
            throw new IllegalArgumentException("Species is required.");
        }

        if (breed == null || breed.trim().isEmpty()) {
            throw new IllegalArgumentException("Breed is required.");
        }

        if (age == null || age.trim().isEmpty()) {
            throw new IllegalArgumentException("Age is required.");
        }

        if (requirements != null && requirements.length() > 200) {
            throw new IllegalArgumentException(
                "Requirements must be 200 characters or fewer."
            );
        }

     uuID++;
     petID = uuID;

     this.userID = userID;
     this.name = name;
     this.species = species;
     this.breed = breed;
     this.age = age;
     this.requirements = requirements;
    }


    // GETTERS AND SETTERS

    public int getPetID() {
        return petID;
    }


    public int getUserID() {
        return userID;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Species getSpecies() {
        return species;
    }


    public void setSpecies(Species species) {
        this.species = species;
    }


    public String getBreed() {
        return breed;
    }


    public void setBreed(String breed) {
        this.breed = breed;
    }


    public String getAge() {
        return age;
    }


    public void setAge(String age) {
        this.age = age;
    }
    
    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}

	

	

