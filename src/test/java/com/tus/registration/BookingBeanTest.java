package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.Services.Service;
import com.tus.Services.ServiceList;
import com.tus.Services.Species;
import com.tus.pethotel.Pet;
import com.tus.pethotel.Pod;
import com.tus.pethotel.PetList;
import com.tus.pethotel.PodList;
import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;
import com.tus.pethotel.RoomList;
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
	RoomList roomList;
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
		roomList = new RoomList();        
		reservationList = new ReservationList();
		
		//moved podList down to show the missing links  
		podList = new PodList(); 
		
		//missing the seeded items for the JUNIT testing in the first pass, 
		//this is why the room calculations weren't passing their boolean checks
	
		podList.setRoomList(roomList);
		podList.setReservationList(reservationList);
		podList.init();

		//movedBookingBean to the bottom as the reording of other files meant more tests were failing. 
		bookingBean = new BookingBean();
		bookingBean.setLoginBean(loginBean);
		bookingBean.setPodList(podList);
		bookingBean.setReservationList(reservationList);
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
	@DisplayName("Pet and pod are selected but dates are missing, when calculatePrice is called, then it is blocked")
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

	@Test
	@DisplayName("Valid pet, pod and dates with no extras, when calculatePrice is called then the total price is calculated as pod rate x nights")
	void calculatingPriceWithPodOnlySucceeds() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0); // "Dog Boarding", €35.00/night
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		bookingBean.setCheckInDate(LocalDate.now().plusDays(10).toString());
		bookingBean.setCheckOutDate(LocalDate.now().plusDays(15).toString()); // 5 nights
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertTrue(bookingBean.isPriceCalculated());
		assertEquals(175.00, bookingBean.getTotalPrice(), 0.001); // 35.00 x 5 nights
	}

	@Test
	@DisplayName("Given valid pet, pod, dates and extras (per-night and one-off),when calculatePrice is called, then the total price includes all of them")
	void calculatingPriceWithExtrasSucceeds() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0); // €35.00/night
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		Service dailyWalks = serviceList.findByName("Daily Walks");   // €10.00/night
		Service grooming = serviceList.findByName("Grooming");        // €15.00 one-off
		bookingBean.setSelectedExtraIDs(new Integer[] {
				dailyWalks.getServiceID(), grooming.getServiceID() });
		bookingBean.setCheckInDate(LocalDate.now().plusDays(10).toString());
		bookingBean.setCheckOutDate(LocalDate.now().plusDays(15).toString()); // 5 nights
		String outcome = bookingBean.calculatePrice();
		// Then: (35.00 x 5) + (10.00 x 5) + 15.00 one-off = 240.00
		assertNull(outcome);
		assertTrue(bookingBean.isPriceCalculated());
		assertEquals(240.00, bookingBean.getTotalPrice(), 0.001);
	}

	@Test
	@DisplayName("No pods exist yet for the pet's species, when calculatePrice is called, then it is blocked with a specific message")
	void calculatingPriceWhenNoPodsExistForSpeciesIsBlocked() throws Exception {
		// zero pods for every species, unlike the shared podList from setUp()
		PodList emptyPodList = new PodList();
		emptyPodList.setRoomList(roomList);
		emptyPodList.setReservationList(reservationList);
		bookingBean.setPodList(emptyPodList);
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		Service dogPod = serviceList.getPodsForSpecies("Dog").get(0);
		bookingBean.setSelectedPodID(dogPod.getServiceID());
		bookingBean.setCheckInDate(LocalDate.now().plusDays(10).toString());
		bookingBean.setCheckOutDate(LocalDate.now().plusDays(15).toString());
		String outcome = bookingBean.calculatePrice();
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}

	@Test
	@DisplayName("every pod for the pet's species is already booked for those dates,when calculatePrice is called, then it is blocked")
	void calculatingPriceWhenAllPodsAreTakenForDatesIsBlocked() {
		String checkIn = LocalDate.now().plusDays(10).toString();
		String checkOut = LocalDate.now().plusDays(15).toString();
		// for exactly these dates, so none are left free
		List<Pod> catPods = podList.getPodsForSpecies(Species.CAT);
		for (Pod pod : catPods) {
			Reservation occupied = new Reservation(999, 1, "Someone Else's Cat", checkIn, checkOut);
			occupied.setPodID(pod.getPodID());
			occupied.setStatus("Confirmed");
			reservationList.addReservation(occupied);
		}

		Pet luna = new Pet(customer.getUserID(), "Luna", Species.CAT, "Siamese", "2 years", null);
		petList.addPet(luna);
		bookingBean.setSelectedPetID(luna.getPetID());

		Service catPod = serviceList.getPodsForSpecies("Cat").get(0);
		bookingBean.setSelectedPodID(catPod.getServiceID());

		bookingBean.setCheckInDate(checkIn);
		bookingBean.setCheckOutDate(checkOut);

		// When
		String outcome = bookingBean.calculatePrice();

		// Then
		assertNull(outcome);
		assertFalse(bookingBean.isPriceCalculated());
	}



	@Test
	@DisplayName("Given free pods exist for the dates entered, getPodAvailability Note reports how many")
	void podAvailabilityNoteReportsFreePodCount() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		bookingBean.setCheckInDate(LocalDate.now().plusDays(10).toString());
		bookingBean.setCheckOutDate(LocalDate.now().plusDays(15).toString());
		String note = bookingBean.getPodAvailabilityNote();
		assertEquals("6 dog pod(s) free for those dates.", note);
	}

	@Test
	@DisplayName("no pods are free for the dates entered, getPodAvailability Note says so")
	void podAvailabilityNoteReportsNoneFree() {
		String checkIn = LocalDate.now().plusDays(10).toString();
		String checkOut = LocalDate.now().plusDays(15).toString();
		List<Pod> catPods = podList.getPodsForSpecies(Species.CAT);
		for (Pod pod : catPods) {
			Reservation occupied = new Reservation(999, 1, "Someone Else's Cat", checkIn, checkOut);
			occupied.setPodID(pod.getPodID());
			occupied.setStatus("Confirmed");
			reservationList.addReservation(occupied);
		}

		Pet luna = new Pet(customer.getUserID(), "Luna", Species.CAT, "Siamese", "2 years", null);
		petList.addPet(luna);
		bookingBean.setSelectedPetID(luna.getPetID());
		bookingBean.setCheckInDate(checkIn);
		bookingBean.setCheckOutDate(checkOut);
		String note = bookingBean.getPodAvailabilityNote();
		assertEquals("No cat pods are free for those dates.", note);
	}

	@Test
	@DisplayName("Given dates have not been entered yet, getPodAvailabilityNote returns an empty string")
	void podAvailabilityNoteIsBlankBeforeDatesAreEntered() {
		Pet buddy = new Pet(customer.getUserID(), "Buddy", Species.DOG, "Golden Retriever", "3 years", null);
		petList.addPet(buddy);
		bookingBean.setSelectedPetID(buddy.getPetID());
		assertEquals("", bookingBean.getPodAvailabilityNote());
	}

}

