package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;

class PetListTest {

    private PetList petList;
    private Pet pet1;
    private Pet pet2;
    private Pet pet3;


    @BeforeEach
    void setUp() {

        petList = new PetList();

        pet1 = new Pet(
                1,
                "Buddy",
                Species.DOG,
                "Labrador",
                "1-3 years",
                "Needs medication with food."
        );

        pet2 = new Pet(
                1,
                "Milo",
                Species.CAT,
                "Siamese",
                "4-7 years",
                ""
        );

        pet3 = new Pet(
                2,
                "Charlie",
                Species.BIRD,
                "Budgie",
                "1-5 years",
                ""
        );
    }


    @Test
    @DisplayName("Test: A new PetList starts empty")
    void testPetListStartsEmpty() {

        assertEquals(
                0,
                petList.getNumberOfPets()
        );
    }


    @Test
    @DisplayName("Test: Adding a pet increases the pet count")
    void testAddPet() {

        petList.addPet(pet1);

        assertEquals(
                1,
                petList.getNumberOfPets()
        );
    }


    @Test
    @DisplayName("Test: Added pet is stored in the list")
    void testPetIsStored() {

        petList.addPet(pet1);

        assertTrue(
                petList.getPets().contains(pet1)
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

        List<Pet> userPets = petList.findByUserID(1);

        assertEquals(
                2,
                userPets.size()
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

        List<Pet> userPets = petList.findByUserID(1);

        assertFalse(
                userPets.contains(pet3)
        );
    }


    @Test
    @DisplayName("Test: findByUserID returns empty list for user with no pets")
    void testFindByUserIDNoPets() {

        petList.addPet(pet1);
        petList.addPet(pet2);

        List<Pet> userPets = petList.findByUserID(999);

        assertNotNull(userPets);
        assertTrue(userPets.isEmpty());
    }


    @Test
    @DisplayName("Test: getPets returns all registered pets")
    void testGetPets() {

        petList.addPet(pet1);
        petList.addPet(pet2);
        petList.addPet(pet3);

        List<Pet> pets = petList.getPets();

        assertEquals(
                3,
                pets.size()
        );

        assertTrue(pets.contains(pet1));
        assertTrue(pets.contains(pet2));
        assertTrue(pets.contains(pet3));
    }


    @Test
    @DisplayName("Test: findByPetID returns the correct pet")
    void testFindByPetID() {

        petList.addPet(pet1);
        petList.addPet(pet2);

        Pet foundPet = petList.findByPetID(
                pet1.getPetID()
        );

        assertNotNull(foundPet);

        assertEquals(
                pet1,
                foundPet
        );
    }


    @Test
    @DisplayName("Test: findByPetID returns null when pet does not exist")
    void testFindByPetIDNotFound() {

        petList.addPet(pet1);

        Pet foundPet = petList.findByPetID(999);

        assertNull(foundPet);
    }


    @Test
    @DisplayName("Test: removePet removes the correct pet")
    void testRemovePet() {

        petList.addPet(pet1);
        petList.addPet(pet2);

        assertEquals(
                2,
                petList.getNumberOfPets()
        );

        petList.removePet(
                pet1.getPetID()
        );

        assertEquals(
                1,
                petList.getNumberOfPets()
        );

        assertFalse(
                petList.getPets().contains(pet1)
        );

        assertTrue(
                petList.getPets().contains(pet2)
        );
    }


    @Test
    @DisplayName("Test: removePet does nothing when pet does not exist")
    void testRemovePetNotFound() {

        petList.addPet(pet1);

        petList.removePet(999);

        assertEquals(
                1,
                petList.getNumberOfPets()
        );

        assertTrue(
                petList.getPets().contains(pet1)
        );
    }


    @Test
    @DisplayName("Test: getRequirements returns pet requirements")
    void testGetRequirements() {

        petList.addPet(pet1);

        String requirements = petList.getRequirements(
                pet1.getPetID()
        );

        assertEquals(
                "Needs medication with food.",
                requirements
        );
    }


    @Test
    @DisplayName("Test: getRequirements returns None when requirements are empty")
    void testGetRequirementsEmpty() {

        petList.addPet(pet2);

        String requirements = petList.getRequirements(
                pet2.getPetID()
        );

        assertEquals(
                "None",
                requirements
        );
    }


    @Test
    @DisplayName("Test: getRequirements returns None for unknown pet")
    void testGetRequirementsUnknownPet() {

        String requirements = petList.getRequirements(999);

        assertEquals(
                "None",
                requirements
        );
    }


    @Test
    @DisplayName("Test: getPetsByUserId returns the user's pets")
    void testGetPetsByUserId() {

        petList.addPet(pet1);
        petList.addPet(pet2);
        petList.addPet(pet3);

        List<Pet> userPets = petList.getPetsByUserId(1);

        assertEquals(
                2,
                userPets.size()
        );

        assertTrue(
                userPets.contains(pet1)
        );

        assertTrue(
                userPets.contains(pet2)
        );

        assertFalse(
                userPets.contains(pet3)
        );
    }


    @Test
    @DisplayName("Test: getPetsByUserId returns empty list for unknown user")
    void testGetPetsByUserIdNoPets() {

        petList.addPet(pet1);

        List<Pet> userPets = petList.getPetsByUserId(999);

        assertNotNull(userPets);

        assertTrue(
                userPets.isEmpty()
        );
    }
}