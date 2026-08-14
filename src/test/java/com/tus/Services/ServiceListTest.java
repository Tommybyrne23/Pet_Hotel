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
}