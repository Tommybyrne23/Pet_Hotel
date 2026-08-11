package com.tus.pethotel;

import java.io.Serializable;

public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int uuID = 0;

    private int petID;
    private int userID;
    private String name;
    private String species;
    private String breed;
    private String age;
    private String requirements;


    // Default constructor required by Jakarta Faces
    public Pet() {
    }


    // Constructor for creating a new pet
    public Pet(int userID, String name, String species, String breed,
            String age, String requirements) {

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


    public String getSpecies() {
        return species;
    }


    public void setSpecies(String species) {
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

	

	

