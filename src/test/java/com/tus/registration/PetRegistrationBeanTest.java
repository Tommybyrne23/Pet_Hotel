package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.Pet;
import com.tus.pethotel.PetList;
import com.tus.Services.Species;
import com.tus.pethotel.User;

import jakarta.faces.model.SelectItem;

class PetRegistrationBeanTest {

    PetRegistrationBean bean;
    PetList petList;
    LoginBean loginBean;


    @BeforeEach
    void setUp() {

        petList = new PetList();

        bean = new PetRegistrationBean();

        loginBean = new LoginBean();

        bean.setPetList(petList);
        bean.setLoginBean(loginBean);

        bean.init();
    }


    @Test
    @DisplayName("Test: init creates a blank pet to bind the form")
    void testInit() {

        assertNotNull(bean.getPet());
    }


    @Test
    @DisplayName("Test: registering a new pet is successful and redirects to dashboard")
    void testPetRegistrationSuccess() {

        // Create the logged-in user
        User user = new User(
                "Mary",
                "mary@gmail.com",
                "pass123"
        );

        loginBean.setLoggedInUser(user);

        // Enter pet details
        bean.getPet().setName("Buddy");
        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labrador");
        bean.getPet().setAge("1-3 years");
        bean.getPet().setRequirements("");

        // Register the pet
        String outcome = bean.register();

        // Check redirect
        assertEquals(
                "userDashboard?faces-redirect=true",
                outcome
        );

        // Check that pet was added
        assertEquals(
                1,
                petList.getNumberOfPets()
        );

        Pet registeredPet = petList.getPets().get(0);

        assertEquals("Buddy", registeredPet.getName());
        assertEquals(Species.DOG, registeredPet.getSpecies());
        assertEquals("Labrador", registeredPet.getBreed());
        assertEquals("1-3 years", registeredPet.getAge());
        assertEquals("", registeredPet.getRequirements());
    }


    @Test
    @DisplayName("Test: registered pet belongs to logged-in user")
    void testPetBelongsToLoggedInUser() {

        User user = new User(
                "Mary",
                "mary@gmail.com",
                "pass123"
        );

        loginBean.setLoggedInUser(user);

        bean.getPet().setName("Buddy");
        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labrador");
        bean.getPet().setAge("1-3 years");
        bean.getPet().setRequirements("");

        bean.register();

        List<Pet> userPets =
                petList.findByUserID(user.getUserID());

        assertEquals(1, userPets.size());

        assertEquals(
                "Buddy",
                userPets.get(0).getName()
        );

        assertEquals(
                user.getUserID(),
                userPets.get(0).getUserID()
        );
    }


    @Test
    @DisplayName("Test: Dog age ranges are returned correctly")
    void testDogAgeRanges() {

        bean.getPet().setSpecies(Species.DOG);

        List<SelectItem> ages = bean.getAgeRanges();

        assertEquals(5, ages.size());

        assertEquals(
                "Less than 1 year",
                ages.get(0).getValue()
        );

        assertEquals(
                "1–3 years",
                ages.get(1).getValue()
        );

        assertEquals(
                "4–7 years",
                ages.get(2).getValue()
        );

        assertEquals(
                "8–12 years",
                ages.get(3).getValue()
        );

        assertEquals(
                "13+ years",
                ages.get(4).getValue()
        );
    }


    @Test
    @DisplayName("Test: Bird age ranges are returned correctly")
    void testBirdAgeRanges() {

        bean.getPet().setSpecies(Species.BIRD);

        List<SelectItem> ages = bean.getAgeRanges();

        assertEquals(5, ages.size());

        assertEquals(
                "Less than 1 year",
                ages.get(0).getValue()
        );

        assertEquals(
                "1–5 years",
                ages.get(1).getValue()
        );

        assertEquals(
                "6–10 years",
                ages.get(2).getValue()
        );

        assertEquals(
                "11–20 years",
                ages.get(3).getValue()
        );

        assertEquals(
                "21+ years",
                ages.get(4).getValue()
        );
    }


    @Test
    @DisplayName("Test: Reptile age ranges are returned correctly")
    void testReptileAgeRanges() {

        bean.getPet().setSpecies(Species.REPTILE);

        List<SelectItem> ages = bean.getAgeRanges();

        assertEquals(5, ages.size());

        assertEquals(
                "Less than 1 year",
                ages.get(0).getValue()
        );

        assertEquals(
                "1–3 years",
                ages.get(1).getValue()
        );

        assertEquals(
                "4–7 years",
                ages.get(2).getValue()
        );

        assertEquals(
                "8–15 years",
                ages.get(3).getValue()
        );

        assertEquals(
                "16+ years",
                ages.get(4).getValue()
        );
    }


    @Test
    @DisplayName("Test: Fish age ranges are returned correctly")
    void testFishAgeRanges() {

        bean.getPet().setSpecies(Species.FISH);

        List<SelectItem> ages = bean.getAgeRanges();

        assertEquals(4, ages.size());

        assertEquals(
                "Less than 1 year",
                ages.get(0).getValue()
        );

        assertEquals(
                "1–2 years",
                ages.get(1).getValue()
        );

        assertEquals(
                "3–5 years",
                ages.get(2).getValue()
        );

        assertEquals(
                "6+ years",
                ages.get(3).getValue()
        );
    }


    @Test
    @DisplayName("Test: Cat uses the correct age ranges")
    void testCatAgeRanges() {

        bean.getPet().setSpecies(Species.CAT);

        List<SelectItem> ages = bean.getAgeRanges();

        assertEquals(5, ages.size());

        assertEquals(
                "13+ years",
                ages.get(4).getValue()
        );
    }


    @Test
    @DisplayName("Test: No age ranges are returned when species is null")
    void testNoSpeciesAgeRanges() {

        bean.getPet().setSpecies(null);

        List<SelectItem> ages = bean.getAgeRanges();

        assertNotNull(ages);
        assertTrue(ages.isEmpty());
    }


    @Test
    @DisplayName("Test: Changing species clears the selected age")
    void testSpeciesAgeChanged() {

        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labrador");
        bean.getPet().setAge("1–3 years");

        bean.speciesChanged();

        assertNull(
                bean.getPet().getAge()
        );
    }


    @Test
    @DisplayName("Test: Changing species clears breed and age")
    void testSpeciesBreedChanged() {

        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labradoodle");
        bean.getPet().setAge("1–3 years");

        bean.speciesChanged();

        assertNull(
                bean.getPet().getBreed(),
                "Changing species should clear the breed"
        );

        assertNull(
                bean.getPet().getAge(),
                "Changing species should clear the age"
        );
    }


    @Test
    @DisplayName("Test: Reset creates a new blank pet")
    void testResetForm() {

        bean.getPet().setName("Buddy");
        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labrador");
        bean.getPet().setAge("1–3 years");
        bean.getPet().setRequirements("Needs medication");

        Pet firstPet = bean.getPet();

        String outcome = bean.reset();

        assertEquals(
                "addPet?faces-redirect=true",
                outcome
        );

        assertNotNull(bean.getPet());

        assertNotSame(
                firstPet,
                bean.getPet()
        );

        assertNull(bean.getPet().getName());
        assertNull(bean.getPet().getSpecies());
        assertNull(bean.getPet().getBreed());
        assertNull(bean.getPet().getAge());
        assertNull(bean.getPet().getRequirements());
    }
    
    @Test
    @DisplayName("Test: Special requirements are stored correctly")
    void testSpecialRequirements() {

        User user = new User(
                "Mary",
                "mary@gmail.com",
                "pass123"
        );

        loginBean.setLoggedInUser(user);

        bean.getPet().setName("Buddy");
        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labrador");
        bean.getPet().setAge("1–3 years");
        bean.getPet().setRequirements(
                "Needs medication with food and should be kept away from other dogs."
        );

        String outcome = bean.register();

        assertEquals(
                "userDashboard?faces-redirect=true",
                outcome
        );

        assertEquals(1, petList.getNumberOfPets());

        Pet registeredPet = petList.getPets().get(0);

        assertEquals(
                "Needs medication with food and should be kept away from other dogs.",
                registeredPet.getRequirements()
        );
    }


    @Test
    @DisplayName("Test: Special requirements over 200 characters are rejected")
    void testSpecialRequirementsOverMaximumLength() {

        User user = new User(
                "Mary",
                "mary@gmail.com",
                "pass123"
        );

        loginBean.setLoggedInUser(user);

        bean.getPet().setName("Buddy");
        bean.getPet().setSpecies(Species.DOG);
        bean.getPet().setBreed("Labrador");
        bean.getPet().setAge("1–3 years");

        String requirements = "a".repeat(201);

        bean.getPet().setRequirements(requirements);

        assertThrows(
                IllegalArgumentException.class,
                () -> bean.register()
        );

        assertEquals(
                0,
                petList.getNumberOfPets(),
                "Pet should not be added when requirements exceed 200 characters"
        );
    }
}