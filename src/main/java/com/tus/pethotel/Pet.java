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


    // Default constructor required by Jakarta Faces
    public Pet() {
    }


    // Constructor for creating a new pet
    public Pet(int userID, String name, String species, String breed, String age) {
        uuID++;
        petID = uuID;

        this.userID = userID;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
    }


    // GETTERS AND SETTERS

    public int getPetID() {
        return petID;
    }


    /*
     * No setter for petID.
     * The Pet class generates its own unique ID.
     */


    public int getUserID() {
        return userID;
    }


    /*
     * No setter for userID.
     * The userID should come from the currently logged-in user.
     */


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
}

	

	

