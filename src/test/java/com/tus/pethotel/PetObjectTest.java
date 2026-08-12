package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PetObjectTest {

    Pet pet1;

    @BeforeEach
    void setUp() {

        pet1 = new Pet(
                1,
                "Buddy",
                Species.DOG,
                "Labrador",
                "1-3 years",
                ""
        );
    }


    @Test
    @DisplayName("Test: A valid pet can be created")
    void testCreatePet() {

        assertNotNull(pet1);
    }


    @Test
    @DisplayName("Test: Pet stores its details correctly")
    void testPetDetails() {

        assertEquals("Buddy", pet1.getName());
        assertEquals(Species.DOG, pet1.getSpecies());
        assertEquals("Labrador", pet1.getBreed());
        assertEquals("1-3 years", pet1.getAge());
        assertEquals("", pet1.getRequirements());
    }


    @Test
    @DisplayName("Test: Pet stores the correct user ID")
    void testUserID() {

        assertEquals(
                1,
                pet1.getUserID(),
                "Pet should belong to the correct user"
        );
    }


    @Test
    @DisplayName("Test: Setters update the pet's fields")
    void testSetters() {

        pet1.setName("Max");
        pet1.setSpecies(Species.CAT);
        pet1.setBreed("Siamese");
        pet1.setAge("4-7 years");
        pet1.setRequirements("Needs medication with food.");

        assertEquals("Max", pet1.getName());
        assertEquals(Species.CAT, pet1.getSpecies());
        assertEquals("Siamese", pet1.getBreed());
        assertEquals("4-7 years", pet1.getAge());
        assertEquals(
                "Needs medication with food.",
                pet1.getRequirements()
        );
    }


    @Test
    @DisplayName("Test: Pet ID is assigned automatically")
    void testPetID() {

        assertTrue(
                pet1.getPetID() > 0,
                "A new pet should receive a positive ID"
        );
    }


    @Test
    @DisplayName("Test: Pet IDs increase between pets")
    void testUniqueIDs() {

        Pet pet2 = new Pet(
                1,
                "Max",
                Species.DOG,
                "Poodle",
                "1-3 years",
                ""
        );

        assertTrue(
                pet2.getPetID() > pet1.getPetID(),
                "Each new pet should receive a higher ID"
        );
    }


    @Test
    @DisplayName("Test: Empty requirements are allowed")
    void testEmptyRequirements() {

        Pet pet = new Pet(
                1,
                "Buddy",
                Species.DOG,
                "Labrador",
                "1-3 years",
                ""
        );

        assertEquals("", pet.getRequirements());
    }


    @Test
    @DisplayName("Test: Requirements can contain 200 characters")
    void testRequirementsMaximumLength() {

        String requirements = "a".repeat(200);

        Pet pet = new Pet(
                1,
                "Buddy",
                Species.DOG,
                "Labrador",
                "1-3 years",
                requirements
        );

        assertEquals(200, pet.getRequirements().length());
    }


    @Test
    @DisplayName("Test: Requirements over 200 characters are rejected")
    void testRequirementsOverMaximumLength() {

        String requirements = "a".repeat(201);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "Buddy",
                        Species.DOG,
                        "Labrador",
                        "1-3 years",
                        requirements
                )
        );
    }


    @Test
    @DisplayName("Test: Blank pet name is rejected")
    void testBlankName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "",
                        Species.DOG,
                        "Labrador",
                        "1-3 years",
                        ""
                )
        );
    }


    @Test
    @DisplayName("Test: Null species is rejected")
    void testNullSpecies() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "Buddy",
                        null,
                        "Labrador",
                        "1-3 years",
                        ""
                )
        );
    }


    @Test
    @DisplayName("Test: Blank breed is rejected")
    void testBlankBreed() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "Buddy",
                        Species.DOG,
                        "",
                        "1-3 years",
                        ""
                )
        );
    }


    @Test
    @DisplayName("Test: Blank age is rejected")
    void testBlankAge() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "Buddy",
                        Species.DOG,
                        "Labrador",
                        "",
                        ""
                )
        );
    }


    @Test
    @DisplayName("Test: Null pet name is rejected")
    void testNullName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        null,
                        Species.DOG,
                        "Labrador",
                        "1-3 years",
                        ""
                )
        );
    }


    @Test
    @DisplayName("Test: Null breed is rejected")
    void testNullBreed() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "Buddy",
                        Species.DOG,
                        null,
                        "1-3 years",
                        ""
                )
        );
    }


    @Test
    @DisplayName("Test: Null age is rejected")
    void testNullAge() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Pet(
                        1,
                        "Buddy",
                        Species.DOG,
                        "Labrador",
                        null,
                        ""
                )
        );
    }
}