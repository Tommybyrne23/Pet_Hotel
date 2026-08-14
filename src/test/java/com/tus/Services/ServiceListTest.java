package com.tus.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/* Tests for the service collection. */
class ServiceListTest {

	private ServiceList serviceList;

	@BeforeEach
	void setUp() {
		serviceList = new ServiceList();
	}

	/* Builds a service and adds it, as the admin form would. */
	private Service addService(String name, double price,
			ServiceCategory category, String... species) {

		Service service = new Service(name, "Test description", price, category);
		service.setApplicableSpecies(new ArrayList<>(List.of(species)));
		serviceList.addService(service);

		return service;
	}

	private boolean containsName(List<Service> services, String name) {
		for (Service service : services) {
			if (name.equals(service.getName())) {
				return true;
			}
		}
		return false;
	}

	private Service findInList(List<Service> services, String name) {
		for (Service service : services) {
			if (name.equals(service.getName())) {
				return service;
			}
		}
		return null;
	}


	// A NEW SERVICE APPEARS ON THE HOMEPAGE 

	@Test
	@DisplayName("Test 1: a new service is listed for the species it suits")
	void newServiceAppearsForItsSpecies() {

		addService("Reptile Handling", 18.00, ServiceCategory.EXTRA, "Reptile");

		assertTrue(containsName(serviceList.getServicesForSpecies("Reptile"), "Reptile Handling"));
	}

	@Test
	@DisplayName("Test 2: a new service is not listed for other species")
	void newServiceIsHiddenFromOtherSpecies() {

		addService("Reptile Handling", 18.00, ServiceCategory.EXTRA, "Reptile");

		assertFalse(containsName(serviceList.getServicesForSpecies("Dog"), "Reptile Handling"));
	}

	@Test
	@DisplayName("Test 3: a service with no species is listed for every pet")
	void serviceWithNoSpeciesSuitsEveryPet() {

		addService("Photo Updates", 5.00, ServiceCategory.EXTRA);

		for (Species species : Species.values()) {
			assertTrue(containsName(serviceList.getServicesForSpecies(species.getLabel()),
					"Photo Updates"), "Photo Updates should be listed for " + species.getLabel());
		}
	}

	@Test
	@DisplayName("Test 4: the details entered by the admin are the ones listed")
	void listedServiceKeepsItsDetails() {

		addService("Reptile Handling", 18.00, ServiceCategory.EXTRA, "Reptile");

		Service listed = null;

		for (Service service : serviceList.getServicesForSpecies("Reptile")) {
			if ("Reptile Handling".equals(service.getName())) {
				listed = service;
			}
		}

		assertNotNull(listed);
		assertEquals(18.00, listed.getPrice());
		assertEquals("Test description", listed.getDescription());
	}


	//  A NEW SERVICE IS SELECTABLE WHEN BOOKING 

	@Test
	@DisplayName("Test 5: a new pod is offered when booking a suitable pet")
	void newPodIsSelectable() {

		addService("Small Mammal Boarding", 22.00, ServiceCategory.POD, "Dog");

		assertTrue(containsName(serviceList.getPodsForSpecies("Dog"), "Small Mammal Boarding"));
	}

	@Test
	@DisplayName("Test 6: a new extra is offered when booking a suitable pet")
	void newExtraIsSelectable() {

		addService("Bird Song Training", 9.00, ServiceCategory.EXTRA, "Bird");

		assertTrue(containsName(serviceList.getExtrasForSpecies("Bird"), "Bird Song Training"));
	}

	@Test
	@DisplayName("Test 7: the pod list never contains extras")
	void podListContainsOnlyPods() {

		addService("Bird Song Training", 9.00, ServiceCategory.EXTRA, "Bird");

		for (Service service : serviceList.getPodsForSpecies("Bird")) {
			assertEquals(ServiceCategory.POD, service.getCategory(), service.getName() + " should not be in the pod list");
		}
	}

	@Test
	@DisplayName("Test 8: the extras list never contains pods")
	void extrasListContainsOnlyExtras() {

		addService("Small Mammal Boarding", 22.00, ServiceCategory.POD, "Dog");

		for (Service service : serviceList.getExtrasForSpecies("Dog")) {
			assertEquals(ServiceCategory.EXTRA, service.getCategory(), service.getName() + " should not be in the extras list");
		}
	}

	@Test
	@DisplayName("Test 9: a service is not offered for a pet it does not suit")
	void unsuitableServiceIsNotSelectable() {

		addService("Bird Song Training", 9.00, ServiceCategory.EXTRA, "Bird");

		assertFalse(containsName(serviceList.getExtrasForSpecies("Dog"), "Bird Song Training"));
	}

	@Test
	@DisplayName("Test 10: the booking page can look a service up by its id")
	void serviceCanBeFoundByID() {

		Service added = addService("Bird Song Training", 9.00, ServiceCategory.EXTRA, "Bird");

		Service found = serviceList.findByID(added.getServiceID());

		assertNotNull(found);
		assertEquals("Bird Song Training", found.getName());
		assertEquals(9.00, found.getPrice());
	}

	@Test
	@DisplayName("Test 11: An unknown id returns nothing rather than the wrong service")
	void unknownIDReturnsNull() {
		assertNull(serviceList.findByID(-1));
	}


	// CHECKING UNIQUENESS

	@Test
	@DisplayName("Test 12: A duplicate name is rejected regardless of case or spacing")
	void duplicateNamesAreRejected() {

		addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");

		assertTrue(serviceList.isNameTaken("Nail Clipping"));
		assertTrue(serviceList.isNameTaken("NAIL CLIPPING"));
		assertTrue(serviceList.isNameTaken("  nail clipping  "));
		assertFalse(serviceList.isNameTaken("Nail Filing"));
	}

	@Test
	@DisplayName("Test 13: A service with no name is never added")
	void namelessServiceIsRejected() {

		int before = serviceList.getNumberOfServices();

		assertFalse(serviceList.addService(null));
		assertFalse(serviceList.addService(new Service("   ", "No name", 10.00, ServiceCategory.EXTRA)));
		assertEquals(before, serviceList.getNumberOfServices());
	}

	// UPDATING AN EXISTING SERVICE

	@Test
	@DisplayName("Test 14: an admin's changes are saved against the service")
	void updateChangesTheDetails() {

		Service service = addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");

		boolean updated = serviceList.updateService(service.getServiceID(),
				"Nail Trimming", "A longer trim.", 15.00,
				ChargeType.ONE_OFF, List.of("Dog", "Cat"));

		assertTrue(updated);
		assertEquals("Nail Trimming", service.getName());
		assertEquals("A longer trim.", service.getDescription());
		assertEquals(15.00, service.getPrice());
		assertEquals(ChargeType.ONE_OFF, service.getChargeType());
		assertEquals(List.of("Dog", "Cat"), service.getApplicableSpecies());
	}

	@Test
	@DisplayName("Test 15: a price can be changed without changing the name")
	void priceOnlyUpdateIsAllowed() {

		Service service = addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");
		assertTrue(serviceList.updateService(service.getServiceID(), "Nail Clipping", "Test description", 20.00, ChargeType.PER_NIGHT, List.of("Dog")));
		assertEquals(20.00, service.getPrice());
		assertEquals("Nail Clipping", service.getName());
	}

	@Test
	@DisplayName("Test 16: a service cannot be renamed to another service's name")
	void renameToAnExistingNameIsBlocked() {

		addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");
		Service other = addService("Nail Filing", 9.00, ServiceCategory.EXTRA, "Dog");
		assertFalse(serviceList.updateService(other.getServiceID(), "NAIL CLIPPING", "Test description", 9.00, ChargeType.PER_NIGHT, List.of("Dog")));
		assertEquals("Nail Filing", other.getName());
	}

	@Test
	@DisplayName("Test 17: an invalid price is rejected and nothing is changed")
	void invalidPriceIsRejected() {

		Service service = addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");

		assertFalse(serviceList.updateService(service.getServiceID(), "Nail Trimming", "Changed", -5.00, ChargeType.ONE_OFF, List.of("Cat")));

		assertFalse(serviceList.updateService(service.getServiceID(), "Nail Trimming", "Changed", 0.00, ChargeType.ONE_OFF, List.of("Cat")));

		// a rejected update must not leave the service half-changed
		assertEquals("Nail Clipping", service.getName());
		assertEquals(12.50, service.getPrice());
		assertEquals("Test description", service.getDescription());
	}

	@Test
	@DisplayName("Test 18: a service cannot be renamed to a blank name")
	void blankNameUpdateIsRejected() {

		Service service = addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");

		assertFalse(serviceList.updateService(service.getServiceID(), "   ",
				"Test description", 12.50, ChargeType.PER_NIGHT, List.of("Dog")));

		assertEquals("Nail Clipping", service.getName());
	}

	@Test
	@DisplayName("Test 19: updating a service that does not exist changes nothing")
	void unknownServiceIsNotUpdated() {

		int before = serviceList.getNumberOfServices();

		assertFalse(serviceList.updateService(-1, "Ghost Service", "Nothing here",
				10.00, ChargeType.PER_NIGHT, List.of("Dog")));

		assertEquals(before, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 20: the updated service keeps its own copy of the species list")
	void updatedSpeciesListIsCopied() {

		Service service = addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");
		List<String> species = new ArrayList<>(List.of("Cat"));

		serviceList.updateService(service.getServiceID(), "Nail Clipping",
				"Test description", 12.50, ChargeType.PER_NIGHT, species);

		// changing the list afterwards must not change the saved service
		species.add("Bird");

		assertEquals(List.of("Cat"), service.getApplicableSpecies());
	}


	// AC2 AND AC3: THE CHANGE REACHES THE HOMEPAGE AND THE BOOKING PAGE

	@Test
	@DisplayName("Test 21: AC2 - the homepage listing shows the updated price")
	void homepageListingShowsTheNewPrice() {

		Service service = addService("Reptile Handling", 18.00, ServiceCategory.EXTRA, "Reptile");

		serviceList.updateService(service.getServiceID(), "Reptile Handling",
				"Test description", 25.00, ChargeType.PER_NIGHT, List.of("Reptile"));

		Service listed = findInList(serviceList.getServicesForSpecies("Reptile"), "Reptile Handling");

		assertNotNull(listed);
		assertEquals(25.00, listed.getPrice());
	}

	@Test
	@DisplayName("Test 22: AC3 - the booking page offers the pod at its updated price")
	void bookingPodShowsTheNewPrice() {

		Service pod = addService("Small Mammal Boarding", 22.00, ServiceCategory.POD, "Dog");

		serviceList.updateService(pod.getServiceID(), "Small Mammal Boarding",
				"Test description", 28.00, ChargeType.PER_NIGHT, List.of("Dog"));

		Service offered = findInList(serviceList.getPodsForSpecies("Dog"), "Small Mammal Boarding");

		assertNotNull(offered);
		assertEquals(28.00, offered.getPrice());
	}

	@Test
	@DisplayName("Test 23: changing the species moves the service between listings")
	void changingSpeciesMovesTheService() {

		Service service = addService("Reptile Handling", 18.00, ServiceCategory.EXTRA, "Reptile");

		serviceList.updateService(service.getServiceID(), "Reptile Handling",
				"Test description", 18.00, ChargeType.PER_NIGHT, List.of("Bird"));

		assertFalse(containsName(serviceList.getServicesForSpecies("Reptile"), "Reptile Handling"));
		assertTrue(containsName(serviceList.getServicesForSpecies("Bird"), "Reptile Handling"));
	}

	@Test
	@DisplayName("Test 24: the duplicate check ignores the service being edited")
	void nameCheckSkipsTheServiceBeingEdited() {

		Service service = addService("Nail Clipping", 12.50, ServiceCategory.EXTRA, "Dog");

		assertFalse(serviceList.isNameTakenByAnother("Nail Clipping", service.getServiceID()));
		assertTrue(serviceList.isNameTakenByAnother("Nail Clipping", 0));
	}
}