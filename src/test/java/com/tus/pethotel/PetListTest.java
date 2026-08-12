package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PetListTest {

    private PetList petList;
    private Pet pet1;
    private Pet pet2;
    private Pet pet3;


    @BeforeEach
    void setUp() {

        petList = new PetList();

        pet1 = new Pet(
                1,					//customers userID
                "Buddy",			//Pet
                Species.DOG,		//Species
                "Labrador",			//
                "1-3 years",		//Age 
                ""					//Special Requirements 
        );

        pet2 = new Pet(
                1,					//customers userID
                "Milo",				//Pet name 
                Species.CAT,		//Species 
                "Siamese",			//species type
                "4-7 years",		//age
                ""					//special requirements
        );

        pet3 = new Pet(
                2,					//customer user ID
                "Charlie",			//Pet name 
                Species.BIRD,		//Species type 
                "Budgie",			//Type of species
                "1-5 years",		//age
                ""					//species requirement
        );
    }


    @Test
    @DisplayName("Test: A new PetList starts empty")
    void testPetListStartsEmpty() {

        assertEquals(
                0,
                petList.getNumberOfPets(),
                "A new PetList should contain no pets"
        );
    }


    @Test
    @DisplayName("Test: Adding a pet increases the pet count")
    void testAddPet() {

        petList.addPet(pet1);

        assertEquals(
                1,
                petList.getNumberOfPets(),
                "Adding a pet should increase the pet count"
        );
    }


    @Test
    @DisplayName("Test: Added pet is stored in the list")
    void testPetIsStored() {

        petList.addPet(pet1);

        assertTrue(
                petList.getPets().contains(pet1),
                "The added pet should be stored in the list"
        );
    }


    @Test
    @DisplayName("Test: Multiple pets can be added")
    void testMultiplePets() {

        petList.addPet(pet1);
        petList.addPet(pet2);
        petList.addPet(pet3);

        assertEquals(
                3,
                petList.getNumberOfPets()
        );
    }


    @Test
    @DisplayName("Test: findByUserID returns pets belonging to the user")
    void testFindByUserID() {

        petList.addPet(pet1);
        petList.addPet(pet2);
        petList.addPet(pet3);

        ArrayList<Pet> userPets = petList.findByUserID(1);

        assertEquals(
                2,
                userPets.size(),
                "User 1 should have two pets"
        );

        assertTrue(userPets.contains(pet1));
        assertTrue(userPets.contains(pet2));
    }


    @Test
    @DisplayName("Test: findByUserID does not return another user's pets")
    void testFindByUserIDDoesNotMixUsers() {

        petList.addPet(pet1);
        petList.addPet(pet2);
        petList.addPet(pet3);

        ArrayList<Pet> userPets = petList.findByUserID(1);

        assertFalse(
                userPets.contains(pet3),
                "User 1 should not receive User 2's pet"
        );
    }


    @Test
    @DisplayName("Test: findByUserID returns empty list for user with no pets")
    void testFindByUserIDNoPets() {

        petList.addPet(pet1);
        petList.addPet(pet2);

        ArrayList<Pet> userPets = petList.findByUserID(999);

        assertNotNull(userPets);

        assertTrue(
                userPets.isEmpty(),
                "A user with no pets should receive an empty list"
        );
    }


    @Test
    @DisplayName("Test: getPets returns all registered pets")
    void testGetPets() {

        petList.addPet(pet1);
        petList.addPet(pet2);
        petList.addPet(pet3);

        ArrayList<Pet> pets = petList.getPets();

        assertEquals(3, pets.size());

        assertTrue(pets.contains(pet1));
        assertTrue(pets.contains(pet2));
        assertTrue(pets.contains(pet3));
    }
}