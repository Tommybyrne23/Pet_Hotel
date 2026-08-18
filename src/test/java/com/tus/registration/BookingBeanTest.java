package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Service;
import com.tus.Services.ServiceList;
import com.tus.Services.Species;
import com.tus.pethotel.Pet;
import com.tus.pethotel.PetList;
import com.tus.pethotel.PodList;
import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

class BookingBeanTest {

	BookingBean bookingBean;
	UserList userList;
	LoginBean loginBean;
	PetList petList;
	ServiceList serviceList;
	PodList podList;
	ReservationList reservationList;
	User customer;

	@BeforeEach
	void setUp() throws Exception {
		userList = new UserList();
		customer = new User("Rupali", "rupali@tus.ie", "password");
		userList.addUser(customer);
		loginBean = new LoginBean();
		loginBean.setUserList(userList);
		loginBean.setEmail("rupali@tus.ie");
		loginBean.setPassword("password");
		loginBean.login();
		petList = new PetList();          
		serviceList = new ServiceList(); 
		podList = new PodList();          
		reservationList = new ReservationList();
		bookingBean = new BookingBean();
		bookingBean.setLoginBean(loginBean);
		bookingBean.setPodList(podList);
		bookingBean.setReservationList(reservationList);

		// BookingBean has no setPetList()/setServiceList() test-support setters
		// (unlike setPodList()/setLoginBean()/setReservationList()). Rather than
		// change the bean without checking first, these two are injected via
		// reflection - a test-only technique that makes zero changes to
		// production code.
		setPrivateField(bookingBean, "petList", petList);
		setPrivateField(bookingBean, "serviceList", serviceList);
	}

	private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}


	@Test
	@DisplayName("User is logged in and has registered pets, when they open the booking page, then  pets are available to select")
	void loggedInUserWithPetsSeesTheirPetsOnTheBookingPage() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		List<Pet> userPets = bookingBean.getUserPets();
		assertEquals(1, userPets.size());
		assertEquals("Buddy", userPets.get(0).getName());
		assertTrue(bookingBean.isHasPets());
	}

	@Test
	@DisplayName("A pet is selected, then the services (pods and extras) to that pet's species are shown")
	void selectingAPetShowsMatchingPodsAndExtras() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		List<Service> pods = bookingBean.getAvailablePods();
		List<Service> extras = bookingBean.getAvailableExtras();
		assertEquals(1, pods.size());
		assertEquals("Dog Boarding", pods.get(0).getName());
		assertEquals(4, extras.size());
	}

	@Test
	@DisplayName("No pet has been selected yet, then no pods or extras are shown")
	void noPetSelectedShowsNoPodsOrExtras() {
		assertTrue(bookingBean.getAvailablePods().isEmpty());
		assertTrue(bookingBean.getAvailableExtras().isEmpty());
	}

	@Test
	@DisplayName("The pet selection is changed, then any previously chosen pod/extras are cleared")
	void changingThePetClearsThePreviousPodAndExtraSelections() {
		bookingBean.setSelectedPodID(99);
		bookingBean.setSelectedExtraIDs(new Integer[] { 1, 2 });
		bookingBean.petChanged();
		assertEquals(0, bookingBean.getSelectedPodID());
		assertEquals(0, bookingBean.getSelectedExtraIDs().length);
		assertFalse(bookingBean.isPriceCalculated());
	}


	@Test
	@DisplayName("User has no registered pets,they try to make a booking, then isHasPets is false so the page can prompt them to register a pet")
	void userWithNoPetsIsPromptedToRegisterAPet() {
		assertTrue(bookingBean.getUserPets().isEmpty());
		assertFalse(bookingBean.isHasPets());
	}

	@Test
	@DisplayName("No pet is selected, when calculatePrice is called, then it is blocked")
	void calculatingPriceWithNoPetSelectedIsBlocked() {
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName("A pet is selected but no pod, when calculatePrice is called, then it is blocked")
	void calculatingPriceWithNoPodSelectedIsBlocked() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName(" pet and pod are selected but dates are missing, when calculatePrice is called, then it is blocked")
	void calculatingPriceWithMissingDatesIsBlocked() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0);
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName("A check-in date in the past, when calculatePrice is called, then it is blocked")
	void calculatingPriceWithPastCheckInDateIsBlocked() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0);
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		bookingBean.setCheckInDate("2020-01-01");
		bookingBean.setCheckOutDate("2020-01-05");
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName("Check-out is not after check-in, when calculatePrice is called, then it is blocked")
	void calculatingPriceWithCheckOutNotAfterCheckInIsBlocked() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0);
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		bookingBean.setCheckInDate("2026-09-10");
		bookingBean.setCheckOutDate("2026-09-10"); // same day
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName("Dates in an invalid format, when calculatePrice is called, then it is blocked")
	void calculatingPriceWithInvalidDateFormatIsBlocked() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0);
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		bookingBean.setCheckInDate("10/09/2026"); 
		bookingBean.setCheckOutDate("15/09/2026");
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName("User has both pending and confirmed bookings,getConfirmedUserBookings only returns the confirmed ones")
	void confirmedBookingsOnlyReturnsConfirmedStatus() {
		Reservation pending = new Reservation(customer.getUserID(), 1, "Buddy",	"2026-09-01", "2026-09-05");
		pending.setStatus("Pending");
		Reservation confirmed = new Reservation(customer.getUserID(), 1, "Buddy","2026-09-10", "2026-09-15");
		confirmed.setStatus("Confirmed");
		reservationList.addReservation(pending);
		reservationList.addReservation(confirmed);
		List<Reservation> confirmedOnly = bookingBean.getConfirmedUserBookings();
		assertEquals(1, confirmedOnly.size());
		assertEquals("Confirmed", confirmedOnly.get(0).getStatus());
	}

	@Test
	@DisplayName("Nobody is logged in, getUserBookings and getConfirmedUserBookings return empty lists")
	void noLoggedInUserReturnsEmptyBookingLists() {
		LoginBean emptyLoginBean = new LoginBean();
		emptyLoginBean.setUserList(userList);
		bookingBean.setLoginBean(emptyLoginBean);
		assertTrue(bookingBean.getUserBookings().isEmpty());
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	@Test
	@DisplayName("Cancel is called, then the form resets and the user returns to the dashboard")
	void cancelResetsTheFormAndReturnsToDashboard() {
		bookingBean.setSelectedPetID(5);
		bookingBean.setSelectedPodID(9);
		bookingBean.setCheckInDate("2026-09-01");
		bookingBean.setCheckOutDate("2026-09-05");
		String outcome = bookingBean.cancel();
		assertEquals("/userDashboard?faces-redirect=true", outcome);
		assertEquals(0, bookingBean.getSelectedPetID());
		assertEquals(0, bookingBean.getSelectedPodID());
		assertNull(bookingBean.getCheckInDate());
		assertNull(bookingBean.getCheckOutDate());
	}
}

