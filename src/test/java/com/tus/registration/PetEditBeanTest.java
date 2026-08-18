package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Species;
import com.tus.pethotel.Pet;
import com.tus.pethotel.PetList;
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

import jakarta.faces.model.SelectItem;


class PetEditBeanTest {

    PetEditBean petEditBean;
    PetList petList;
    LoginBean loginBean;
    User user;

    Pet pet1;
    Pet pet2;


    @BeforeEach
    void setUp() throws Exception {

        // Create PetList
        petList = new PetList();

        // Create user
        user = new User(
                "Jonathan",
                "a00347373@student.tus.ie",
                "password"
        );

        // Create LoginBean
        loginBean = new LoginBean();

        // Create UserList and add user
        UserList userList = new UserList();
        userList.addUser(user);

        loginBean.setUserList(userList);

        // Log user in
        loginBean.setEmail("a00347373@student.tus.ie");
        loginBean.setPassword("password");
        loginBean.login();

        // Create two pets belonging to the user
        pet1 = new Pet(
                user.getUserID(),
                "Buddy",
                Species.DOG,
                "Labrador",
                "4–7 years",
                ""
        );

        pet2 = new Pet(
                user.getUserID(),
                "Milo",
                Species.CAT,
                "Tabby",
                "1–3 years",
                ""
        );

        petList.addPet(pet1);
        petList.addPet(pet2);

        // Create PetEditBean
        petEditBean = new PetEditBean();

        // Manually inject dependencies
        petEditBean.setLoginBean(loginBean);
        petEditBean.setPetList(petList);

        // Load user's pets
        petEditBean.loadPets();
    }


    @Test
    @DisplayName("Test: User Has Two Pets")
    void testLoadPets() {

        assertNotNull(petEditBean.getUserPets());

        assertEquals(
                2,
                petEditBean.getUserPets().size()
        );
    }


    @Test
    @DisplayName("Test: First Pet Automatically Selected")
    void testFirstPetAutomaticallySelected() {

        assertEquals(
                pet1.getPetID(),
                petEditBean.getSelectedPetID()
        );
    }


    @Test
    @DisplayName("Test: Selecting Different Pet")
    void testSelectingDifferentPet() {

        petEditBean.setSelectedPetID(pet2.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        assertNotNull(selectedPet);

        assertEquals(
                pet2.getPetID(),
                selectedPet.getPetID()
        );

        assertEquals(
                "Milo",
                selectedPet.getName()
        );

        assertEquals(
                "Tabby",
                selectedPet.getBreed()
        );
    }


    @Test
    @DisplayName("Test: User Can Update Pet Details")
    void testUpdatePetDetails() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        selectedPet.setName("Charlie");
        selectedPet.setBreed("German Shepherd");
        selectedPet.setSpecies(Species.DOG);
        selectedPet.setAge("1–3 years");

        String outcome = petEditBean.savePet();

        assertEquals(
                "/userDashboard?faces-redirect=true",
                outcome
        );

        assertEquals(
                "Charlie",
                pet1.getName()
        );

        assertEquals(
                "German Shepherd",
                pet1.getBreed()
        );

        assertEquals(
                Species.DOG,
                pet1.getSpecies()
        );

        assertEquals(
                "1–3 years",
                pet1.getAge()
        );
    }


    @Test
    @DisplayName("Test: Blank Pet Name Does Not Save")
    void testBlankNameDoesNotSave() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        assertEquals("Buddy", selectedPet.getName());

        selectedPet.setName("");

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }

    @Test
    @DisplayName("Test: Blank Breed Does Not Save")
    void testBlankBreedDoesNotSave() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        assertEquals("Labrador", selectedPet.getBreed());

        selectedPet.setBreed("");

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }


    @Test
    @DisplayName("Test: Null Breed Does Not Save")
    void testNullBreedDoesNotSave() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        selectedPet.setBreed(null);

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }


    @Test
    @DisplayName("Test: Blank Age Does Not Save")
    void testBlankAgeDoesNotSave() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        selectedPet.setAge("");

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }


    @Test
    @DisplayName("Test: Null Age Does Not Save")
    void testNullAgeDoesNotSave() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        selectedPet.setAge(null);

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }


    @Test
    @DisplayName("Test: Blank Name Does Not Save")
    void testBlankNameDoesNotSaveAgain() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        selectedPet.setName("   ");

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }


    @Test
    @DisplayName("Test: Null Species Does Not Save")
    void testNullSpeciesDoesNotSave() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        Pet selectedPet = petEditBean.getSelectedPet();

        selectedPet.setSpecies(null);

        String outcome = petEditBean.savePet();

        assertNull(outcome);
    }


    @Test
    @DisplayName("Test: Species Change Clears Age")
    void testSpeciesChangedClearsAge() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        assertEquals(
                "4–7 years",
                pet1.getAge()
        );

        petEditBean.speciesChanged();

        assertNull(
                pet1.getAge()
        );
    }


    @Test
    @DisplayName("Test: Dog Age Ranges")
    void testDogAgeRanges() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        List<SelectItem> ages =
                petEditBean.getAgeRanges();

        assertEquals(5, ages.size());

        assertEquals(
                "Less than 1 year",
                ages.get(0).getLabel()
        );

        assertEquals(
                "1–3 years",
                ages.get(1).getLabel()
        );

        assertEquals(
                "4–7 years",
                ages.get(2).getLabel()
        );

        assertEquals(
                "8–12 years",
                ages.get(3).getLabel()
        );

        assertEquals(
                "13+ years",
                ages.get(4).getLabel()
        );
    }


    @Test
    @DisplayName("Test: No Selected Pet Returns Empty Age Ranges")
    void testNoPetAgeRanges() {

        petEditBean.setSelectedPetID(0);

        List<SelectItem> ages =
                petEditBean.getAgeRanges();

        assertTrue(ages.isEmpty());
    }


    @Test
    @DisplayName("Test: Delete Selected Pet")
    void testDeletePet() {

        petEditBean.setSelectedPetID(pet1.getPetID());

        assertNotNull(
                petList.findByPetID(pet1.getPetID())
        );

        petEditBean.deletePet();

        assertNull(
                petList.findByPetID(pet1.getPetID())
        );

        assertEquals(
                1,
                petEditBean.getUserPets().size()
        );

        assertEquals(
                pet2.getPetID(),
                petEditBean.getSelectedPetID()
        );
    }


    @Test
    @DisplayName("Test: Delete Last Remaining Pet")
    void testDeleteLastPet() {

        // Remove pet2 first
        petList.removePet(pet2.getPetID());

        // Refresh bean's list
        petEditBean.loadPets();

        petEditBean.setSelectedPetID(pet1.getPetID());

        petEditBean.deletePet();

        assertTrue(
                petEditBean.getUserPets().isEmpty()
        );

        assertEquals(
                0,
                petEditBean.getSelectedPetID()
        );
    }


    @Test
    @DisplayName("Test: Cancel Redirects To Dashboard")
    void testCancel() {

        String outcome = petEditBean.cancel();

        assertEquals(
                "/userDashboard?faces-redirect=true",
                outcome
        );
    }
    
    @Test
    @DisplayName("Test: Cat Age Ranges")
    void testCatAgeRanges() {

        petEditBean.setSelectedPetID(pet2.getPetID());
        pet2.setSpecies(Species.CAT);

        List<SelectItem> ages = petEditBean.getAgeRanges();

        assertEquals(5, ages.size());
        assertEquals("Less than 1 year", ages.get(0).getLabel());
        assertEquals("13+ years", ages.get(4).getLabel());
    }


    @Test
    @DisplayName("Test: Bird Age Ranges")
    void testBirdAgeRanges() {

        pet1.setSpecies(Species.BIRD);
        petEditBean.setSelectedPetID(pet1.getPetID());

        List<SelectItem> ages = petEditBean.getAgeRanges();

        assertEquals(5, ages.size());
        assertEquals("1–5 years", ages.get(1).getLabel());
        assertEquals("11–20 years", ages.get(3).getLabel());
        assertEquals("21+ years", ages.get(4).getLabel());
    }


    @Test
    @DisplayName("Test: Reptile Age Ranges")
    void testReptileAgeRanges() {

        pet1.setSpecies(Species.REPTILE);
        petEditBean.setSelectedPetID(pet1.getPetID());

        List<SelectItem> ages = petEditBean.getAgeRanges();

        assertEquals(5, ages.size());
        assertEquals("1–3 years", ages.get(1).getLabel());
        assertEquals("8–15 years", ages.get(3).getLabel());
        assertEquals("16+ years", ages.get(4).getLabel());
    }


    @Test
    @DisplayName("Test: Fish Age Ranges")
    void testFishAgeRanges() {

        pet1.setSpecies(Species.FISH);
        petEditBean.setSelectedPetID(pet1.getPetID());

        List<SelectItem> ages = petEditBean.getAgeRanges();

        assertEquals(4, ages.size());
        assertEquals("1–2 years", ages.get(1).getLabel());
        assertEquals("3–5 years", ages.get(2).getLabel());
        assertEquals("6+ years", ages.get(3).getLabel());
    }

}