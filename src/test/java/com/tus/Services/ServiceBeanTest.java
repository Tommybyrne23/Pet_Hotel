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

	// Mirrors the input of a admin filling out a form on manageServices.xhtml. 
	// Created to quickly set the fields for the bean for each test.
	private void fillValidForm() {
		bean.setEditName("Nail Clipping");
		bean.setEditDescription("A quick nail trim during your pet's stay.");
		bean.setEditPrice(12.50);
		bean.setEditCategory(ServiceCategory.EXTRA);
		bean.setEditChargeType(ChargeType.ONE_OFF);
	}

	/* Adds a service through the form, then hands it back for editing. */
	private Service addThenFetch(String name) {
		fillValidForm();
		bean.setEditName(name);
		bean.addNewService();
		return serviceList.findByName(name);
	}

	// ADMINISTRATOR CREATES A NEW SERVICE ---

	@Test
	@DisplayName("Test 1: a valid service is added to the system")
	void validServiceIsAdded() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();
		String outcome = bean.addNewService();

		assertEquals(before + 1, serviceList.getNumberOfServices());
		assertNotNull(serviceList.findByName("Nail Clipping"));

		// a non-null outcome means the page reloaded, which is the success path
		assertEquals("manageServices?faces-redirect=true", outcome);
	}

	@Test
	@DisplayName("Test 2: every detail entered is stored against the service")
	void allDetailsAreStored() {

		fillValidForm();
		bean.setEditSpecies(new ArrayList<>(List.of("Dog", "Cat")));
		bean.addNewService();

		Service saved = serviceList.findByName("Nail Clipping");

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

		assertNotNull(serviceList.findByName("Nail Clipping"));
	}

	@Test
	@DisplayName("Test 5: A pod is always charged per night, whatever the form said")
	void podIsAlwaysChargedPerNight() {

		fillValidForm();
		bean.setEditName("Small Mammal Boarding");
		bean.setEditCategory(ServiceCategory.POD);
		bean.setEditChargeType(ChargeType.ONE_OFF);
		bean.addNewService();

		assertEquals(ChargeType.PER_NIGHT, serviceList.findByName("Small Mammal Boarding").getChargeType());
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

		assertEquals(List.of("Dog"), serviceList.findByName("Nail Clipping").getApplicableSpecies());
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

	// --- ADMINISTRATOR EDITS AN EXISTING SERVICE ---

	@Test
	@DisplayName("Test 13: choosing Edit loads the service into the form")
	void startEditFillsTheForm() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);

		assertTrue(bean.isEditing());
		assertEquals(service.getServiceID(), bean.getEditingServiceID());
		assertEquals("Nail Clipping", bean.getEditName());
		assertEquals(12.50, bean.getEditPrice());
		assertEquals(ServiceCategory.EXTRA, bean.getEditCategory());
		assertEquals(ChargeType.ONE_OFF, bean.getEditChargeType());
	}

	@Test
	@DisplayName("Test 14: editing nothing leaves the form in add mode")
	void startEditIgnoresNull() {

		bean.startEdit(null);

		assertFalse(bean.isEditing());
	}

	@Test
	@DisplayName("Test 15: the admin's changes are saved to the service")
	void saveEditAppliesTheChanges() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);
		bean.setEditName("Nail Trimming");
		bean.setEditDescription("A longer trim.");
		bean.setEditPrice(20.00);
		bean.saveEdit();

		assertEquals("Nail Trimming", service.getName());
		assertEquals("A longer trim.", service.getDescription());
		assertEquals(20.00, service.getPrice());
	}

	@Test
	@DisplayName("Test 16: editing changes the service instead of adding a new one")
	void saveEditDoesNotAddASecondService() {

		Service service = addThenFetch("Nail Clipping");
		int before = serviceList.getNumberOfServices();

		bean.startEdit(service);
		bean.setEditName("Nail Trimming");
		bean.saveEdit();

		assertEquals(before, serviceList.getNumberOfServices());
		assertNull(serviceList.findByName("Nail Clipping"));
	}

	@Test
	@DisplayName("Test 17: the form returns to add mode after a successful edit")
	void saveEditClearsTheFormAndLeavesEditMode() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);
		String outcome = bean.saveEdit();

		assertFalse(bean.isEditing());
		assertNull(bean.getEditName());
		assertNull(bean.getEditPrice());
		assertEquals("manageServices?faces-redirect=true", outcome);
	}

	@Test
	@DisplayName("Test 18: only the price can be changed, leaving the name as it was")
	void priceOnlyEditIsAllowed() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);
		bean.setEditPrice(20.00);

		assertNotNull(bean.saveEdit());
		assertEquals(20.00, service.getPrice());
	}

	@Test
	@DisplayName("Test 19: AC4 - an invalid price is not saved")
	void invalidPriceIsBlockedOnEdit() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);

		bean.setEditPrice(null);
		assertNull(bean.saveEdit());

		bean.setEditPrice(0.00);
		assertNull(bean.saveEdit());

		bean.setEditPrice(-5.00);
		assertNull(bean.saveEdit());

		assertEquals(12.50, service.getPrice());
	}

	@Test
	@DisplayName("Test 20: a service cannot be renamed to a blank name")
	void blankNameIsBlockedOnEdit() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);
		bean.setEditName("   ");

		assertNull(bean.saveEdit());
		assertEquals("Nail Clipping", service.getName());
	}

	@Test
	@DisplayName("Test 21: a service cannot be renamed to another service's name")
	void duplicateNameIsBlockedOnEdit() {

		addThenFetch("Nail Clipping");
		Service other = addThenFetch("Nail Filing");

		bean.startEdit(other);
		bean.setEditName("NAIL CLIPPING");

		assertNull(bean.saveEdit());
		assertEquals("Nail Filing", other.getName());
	}

	@Test
	@DisplayName("Test 22: a blocked edit keeps the changes on screen and stays in edit mode")
	void blockedEditKeepsTheAdminsWork() {

		Service service = addThenFetch("Nail Clipping");

		bean.startEdit(service);
		bean.setEditName("Nail Trimming");
		bean.setEditPrice(-5.00);
		bean.saveEdit();

		// the admin should not lose their typing or be dropped back into add mode
		assertTrue(bean.isEditing());
		assertEquals("Nail Trimming", bean.getEditName());
		assertEquals(service.getServiceID(), bean.getEditingServiceID());
	}

	@Test
	@DisplayName("Test 23: an edited pod is still always charged per night")
	void editedPodIsAlwaysChargedPerNight() {

		fillValidForm();
		bean.setEditName("Small Mammal Boarding");
		bean.setEditCategory(ServiceCategory.POD);
		bean.addNewService();

		Service pod = serviceList.findByName("Small Mammal Boarding");

		bean.startEdit(pod);
		bean.setEditChargeType(ChargeType.ONE_OFF);
		bean.saveEdit();

		assertEquals(ChargeType.PER_NIGHT, pod.getChargeType());
	}

	@Test
	@DisplayName("Test 24: saving when nothing is being edited does nothing")
	void saveEditDoesNothingInAddMode() {

		int before = serviceList.getNumberOfServices();

		fillValidForm();

		assertNull(bean.saveEdit());
		assertEquals(before, serviceList.getNumberOfServices());
	}

	@Test
	@DisplayName("Test 25: cancelling an edit returns the form to add mode")
	void cancelLeavesEditMode() {

		bean.startEdit(addThenFetch("Nail Clipping"));
		bean.reset();

		assertFalse(bean.isEditing());
		assertNull(bean.getEditName());
	}
}