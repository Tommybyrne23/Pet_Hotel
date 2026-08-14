package com.tus.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/* Tests for the admin "add a service" form.*/
 
class ServiceBeanTest {

	private ServiceBean bean;
	private ServiceList serviceList;
	private Service service;
	

	@BeforeEach
	void setUp() {
		serviceList = new ServiceList();
		bean = new ServiceBean();
		bean.setServiceList(serviceList);
	}

	/* Fills the form with details that should always be accepted. */
	Service s = new Service("Nail Clipping", "A quick nail trim during your pet's stay.",12.50, ServiceCategory.EXTRA,ChargeType.ONE_OFF ); 
	
	private void fillValidForm() {
		bean.setEditName("Nail Clipping");
		bean.setEditDescription("A quick nail trim during your pet's stay.");
		bean.setEditPrice(12.50);
		bean.setEditCategory(ServiceCategory.EXTRA);
		bean.setEditChargeType(ChargeType.ONE_OFF);
	}

	private Service findByName(String name) {
		for (Service service : serviceList.getServices()) {
			if (name.equals(service.getName())) {
				return service;
			}
		}
		return null;
	}


	// ADMINISTRATOR CREATES A NEW SERVICE ---

	@Test
	@DisplayName("Test 1: a valid service is added to the system")
	void validServiceIsAdded() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();
		String outcome = bean.addNewService();

		assertEquals(before + 1, serviceList.getNumberOfServices());
		assertNotNull(findByName("Nail Clipping"));

		// a non-null outcome means the page reloaded, which is the success path
		assertEquals("manageServices?faces-redirect=true", outcome);
	}

	@Test
	@DisplayName("Test 2: every detail entered is stored against the service")
	void allDetailsAreStored() {

		fillValidForm();
		bean.setEditSpecies(new ArrayList<>(List.of("Dog", "Cat")));
		bean.addNewService();

		Service saved = findByName("Nail Clipping");

		assertNotNull(saved);
		assertEquals("A quick nail trim during your pet's stay.", saved.getDescription());
		assertEquals(12.50, saved.getPrice());
		assertEquals(ServiceCategory.EXTRA, saved.getCategory());
		assertEquals(ChargeType.ONE_OFF, saved.getChargeType());
		assertEquals(List.of("Dog", "Cat"), saved.getApplicableSpecies());
	}

	@Test
	@DisplayName("Test 3: the form is cleared after a successful save")
	void formIsClearedAfterSaving() {

		fillValidForm();
		bean.addNewService();

		assertNull(bean.getEditName());
		assertNull(bean.getEditDescription());
		assertNull(bean.getEditPrice());
		assertNull(bean.getEditCategory());
		assertTrue(bean.getEditSpecies().isEmpty());
		assertEquals(ChargeType.PER_NIGHT, bean.getEditChargeType());
	}

	@Test
	@DisplayName("Test 4: whitespace around the name is trimmed")
	void nameIsTrimmed() {

		fillValidForm();
		bean.setEditName("   Nail Clipping   ");
		bean.addNewService();

		assertNotNull(findByName("Nail Clipping"));
	}

	@Test
	@DisplayName("Test 5: A pod is always charged per night, whatever the form said")
	void podIsAlwaysChargedPerNight() {

		fillValidForm();
		bean.setEditName("Small Mammal Boarding");
		bean.setEditCategory(ServiceCategory.POD);
		bean.setEditChargeType(ChargeType.ONE_OFF);
		bean.addNewService();

		assertEquals(ChargeType.PER_NIGHT, findByName("Small Mammal Boarding").getChargeType());
	}

	@Test
	@DisplayName("Test 6: The saved service keeps its own copy of the species list")
	void speciesListIsCopied() {

		List<String> species = new ArrayList<>(List.of("Dog"));

		fillValidForm();
		bean.setEditSpecies(species);
		bean.addNewService();

		// changing the list afterwards must not change the saved service
		species.add("Cat");

		assertEquals(List.of("Dog"), findByName("Nail Clipping").getApplicableSpecies());
	}


	// --- AC4: SAVING WITHOUT REQUIRED DETAILS IS BLOCKED ---

	@Test
	@DisplayName("Test 7: a service with no name is not saved")
	void blankNameIsBlocked() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();
		bean.setEditName("   ");

		assertNull(bean.addNewService());
		assertEquals(before, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 8: a service with no price is not saved")
	void missingPriceIsBlocked() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();
		bean.setEditPrice(null);

		assertNull(bean.addNewService());
		assertEquals(before, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 9: a price of zero or less is not saved")
	void zeroOrNegativePriceIsBlocked() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();
		bean.setEditPrice(0.00);
		assertNull(bean.addNewService());

		bean.setEditPrice(-5.00);
		assertNull(bean.addNewService());

		assertEquals(before, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 10: a service with no category is not saved")
	void missingCategoryIsBlocked() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();
		bean.setEditCategory(null);

		assertNull(bean.addNewService());
		assertEquals(before, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 11: a duplicate service name is not saved")
	void duplicateNameIsBlocked() {

		fillValidForm();
		bean.addNewService();

		int after = serviceList.getNumberOfServices();

		// same name again, different case
		fillValidForm();
		bean.setEditName("NAIL CLIPPING");

		assertNull(bean.addNewService());
		assertEquals(after, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 12: a blocked save leaves the form filled in")
	void blockedSaveKeepsTheEnteredDetails() {

		fillValidForm();
		bean.setEditPrice(null);
		bean.addNewService();

		// the admin should not have to retype everything
		assertEquals("Nail Clipping", bean.getEditName());
		assertEquals(ServiceCategory.EXTRA, bean.getEditCategory());
	}
}