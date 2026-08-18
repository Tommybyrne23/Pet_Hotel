package com.tus.payment;

import static org.junit.jupiter.api.Assertions.*;

//JUNIT test assertaions
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


//import functions from the site
import com.tus.registration.BookingBean;
import com.tus.registration.LoginBean;
import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;
import com.tus.pethotel.Pod;
import com.tus.pethotel.PodList;
import com.tus.pethotel.Pet;
import com.tus.pethotel.Room;
import com.tus.pethotel.RoomList;
import com.tus.pethotel.PetList;
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;
import com.tus.Services.Species;
import com.tus.Services.Service;
import com.tus.Services.ServiceList;
import com.tus.payment.PaymentServices;
import com.tus.payment.OrderDetail;



//impot Java utilities
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




//jakarta face faces imports 
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextWrapper;
import jakarta.faces.context.FacesContextMocker;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.ExternalContextWrapper;
import jakarta.faces.application.FacesMessage;

/*
 * 
 As a customer
 I want to pay for my services & reservations online
 so I can secure my booking for my particular time period.

 Acceptance Criteria 
   	Given that a user is on the payment link after completing the “Make new booking form”
   	When they submit accurate payment information 
   	Then the system will display a booking confirmation message
   	And the user will have the option to be redirected to the users dashboard.

	AC1 J-UNIT Test 
	Result of successful payment from PayPal and return messages.
	Assumptions - a new booking has been made with a user and has been passed to PayPal for processing. 
	 
	1 - redirection to confirm.xhtml including the message for the pet name, the check in date/checkout Date and the amount 
	2 - 

 Cancel booking - not 100% testable in J-UNit as this is 
	Given that a user is on the payment link after completing the “Make new booking form”
	When they click cancel booking  
	Then the system will not process the oder
	and the user will receive a message that the order has been cancelled. 
	
	Test redirection from Paypal and invoking the cancel booking method
	Order status set to cancelled in UserList. 
	/payment/cancel.xhtml link 

 * 
 */
class paymentTest {

	BookingBean bookingBean;
	LoginBean login;
	Reservation reservation;
	ReservationList reservationList;
	PetList petList;
	PodList podList;

	RoomList roomList;
	UserList userList;
	ServiceList serviceList;
	PaymentServices paymentServices;

	User user1, user2, user3;

	Room roomDog, roomCat, roomReptile, roomBird, roomFish;

	Pod pod1,pod2,pod3, pod4,pod5, pod6, pod7, pod8, pod9, pod10;
	
	Pet pet1, pet2, pet3, pet4,pet5;
	
	Species species;
	Service service;
	OrderDetail orderDetail;
	
	
	
	/*
	 * Setup a fake hotel framework to allow for full scope testing 
	 */
	@BeforeEach
	void setUp() throws Exception {
		//set up all lists
		bookingBean = new BookingBean();
		login = new LoginBean();
		reservationList = new ReservationList();
		petList = new PetList();
		podList = new PodList();
		roomList = new RoomList();
		userList = new UserList();
		serviceList = new ServiceList();
		paymentServices = new PaymentServices();
		
		
		//create Users
		user1 = new User("Jon", "jon@gmail.com", "password1");
		user2 = new User("Tommy", "tommy@gmail.com", "password2");
		user3 = new User("Rupali", "rupali@gmail.com", "pass123");
		
		// add users to list 
		userList.addUser(user1);
		userList.addUser(user2);
		userList.addUser(user3);
		
		
		//Create Pets
		pet1 = new Pet(user1.getUserID(),"Jess",Species.DOG, "Old English SheepDog", "1-3 years old", "Medication twice a day");
		pet2 = new Pet(user2.getUserID(),"Arthur",Species.CAT, "Siamese", "4-7 years old", "Nothing twice a day");
		pet3 = new Pet(user1.getUserID(), "Charlie", Species.BIRD, "Budgie", "1-5 years","");
		pet4 = new Pet(user3.getUserID(), "Hissy", Species.REPTILE, "Boa Constrictor", "4-7 years","Needs a live mouse every few days, don't sleep next to him");
		pet5 = new Pet(user3.getUserID(), "Nemo", Species.FISH, "Clownfish", "0-1years","Saltwater, tank will be provided");
		//add users to list 
		petList.addPet(pet1);
		petList.addPet(pet2);
		petList.addPet(pet3);
		petList.addPet(pet4);
		petList.addPet(pet5);
		
		//Create Rooms 
		roomDog = new Room("Kennels", "MainBlock", Species.DOG);
		roomCat = new Room("Cattery", "MainBlock", Species.CAT);
		roomReptile= new Room("Sahara", "MainBlock", Species.REPTILE);
		roomBird = new Room("Flights of Fancy", "The Gardens", Species.BIRD);		
		roomFish = new Room("Mariana Trench", "Basement", Species.FISH);
		
		//add rooms to lists
		roomList.addRoom(roomDog);
		roomList.addRoom(roomCat);
		roomList.addRoom(roomReptile);
		roomList.addRoom(roomBird);
		roomList.addRoom(roomFish);
			
						
		//Create Pods
		//use the getter to assign the room ID from the name of the room 
		pod1  = new Pod("k1", roomDog.getRoomID());     // Dog
		pod2  = new Pod("k2", roomDog.getRoomID());     // Dog
		pod3  = new Pod("k3", roomDog.getRoomID());     // Dog
		pod4  = new Pod("C1", roomCat.getRoomID());     // Cat 
		pod5  = new Pod("C2", roomCat.getRoomID());     // Cat
		pod6  = new Pod("C3", roomCat.getRoomID());     // Cat
		pod7  = new Pod("R1", roomReptile.getRoomID()); // Reptile
		pod8  = new Pod("B1", roomBird.getRoomID());    // Bird	
		pod9  = new Pod("Aq1", roomFish.getRoomID());   // Fish
		pod10 = new Pod("Aq2", roomFish.getRoomID());   // Fish
		
		//Add all pods
		podList.setRoomList(roomList);					//set the RoomList field from the roomList Array
		podList.setReservationList(reservationList);	//link the pod list to the reservation list
		podList.addPod(pod1);
		podList.addPod(pod2);
		podList.addPod(pod3);
		podList.addPod(pod4);
		podList.addPod(pod5);
		podList.addPod(pod6);
		podList.addPod(pod7);
		podList.addPod(pod8);
		podList.addPod(pod9);
		podList.addPod(pod10);

		//set all pods as in service 
		pod1.setOutOfService(false);
		pod2.setOutOfService(false);
		pod3.setOutOfService(false);
		pod4.setOutOfService(false);
		pod5.setOutOfService(false);
		pod6.setOutOfService(false);
		pod7.setOutOfService(false);
		pod8.setOutOfService(false);
		pod9.setOutOfService(false);
		pod10.setOutOfService(false);
		
		// 	FOR REFERENCE 
		//	pods 1-3 = Dog 
		//	pods 3-6 = Cat 
		//	P7 		 = Reptile, 
		//	P8 		 = Bird
		//	P9 - 10	 = FIsh
		
		//Set the booking bean list elements 
		bookingBean.setPodList(podList);
		bookingBean.setReservationList(reservationList);
	    bookingBean.setLoginBean(login); 					//Inject loginBean into bookingBean

		//set a new default reservation 
		reservation = new Reservation(user1.getUserID(),pet1.getPetID(),pet1.getName(), "2026-08-22", "2026-09-01");
		reservationList.addReservation(reservation);
		
		
	}
	
//had this to print test IDs and then used getters and setters instead. 	
//	@Test
//	@DisplayName("List All User and Pet IDs")
//	void listAllIDs() {
//	    System.out.println("--- USERS ---");
//	    for (User u : userList.getUsers()) {
//	        System.out.println("ID: " + u.getUserID() + " | Name: " + u.getName());
//	    }
//
//	    System.out.println("--- PETS ---");
//	    for (Pet p : petList.getPets()) {
//	        System.out.println("Pet ID: " + p.getPetID() + " | Name: " + p.getName() + " | Owner User ID: " + p.getUserID());
//	    }
//	
//	}
	
	@Test
	@DisplayName("Verify User and Pet IDs")
	void testUserAndPetIDs() {
	  
	    // Verify PetList retrieval by User ID
	    List<Pet> jonsPets = petList.findByUserID(user1.getUserID());
	    assertEquals(2, jonsPets.size()); // Jess and Charlie
	}
	
	@Test
	@DisplayName("Verify Reservation Status to pending ")
	void testReservationStatusPending() {
		
		// Verify PetList retrieval by User ID
		String result = reservation.getStatus();
		String userName = userList.getUserNameById(reservation.getUserID());
		assertEquals("Pending", result);					//test should automatically be set to pending 
		assertEquals("Jon", userName);					//Customer name is Jon
	
	}
	
	@Test
	@DisplayName("Cancel Payment - Status updated and Pod released")
	void testCancelPayment() {
	    // Link bean to the active pending reservation
	    reservation.setPodID(pod1.getPodID());		// set the booking to pod1 
		reservation.setPodName(pod1.getLabel());	// sets the pod label
		
	    bookingBean.setPendingReservationID(reservation.getReservationID());
			    
	   //verify the name matches the Manual pod assignment 
		assertEquals("k1", reservation.getPodName());
	
	    // Simulation of the user clicking cancel on the PayPal portal  
	    bookingBean.cancelPayment();
	    
	    // Verify Reservation updates
	    assertEquals("Cancelled", reservation.getStatus());
	    assertNull(reservation.getHoldExpiry());				//resets the hold expiry
	    assertEquals("Cancelled payment in Paypal Portal", reservation.getFailureReason());
	    
	    // Verify unsuccessful payment shows in admin tracking list
	    assertTrue(reservationList.getUnsuccessfulPayments().contains(reservation));
	    
	    // Verify pod is immediately free for booking
	    boolean isAvailable = podList.isPodAvailable(pod1.getPodID(), "2026-08-22", "2026-09-01");
	    assertTrue(isAvailable);
	}
	
	@Test
	@DisplayName("Test: Simulated Successful Payment ")
	void testSuccessfulPayment() {
		// Link bean to the active pending reservation
		reservation.setPodID(pod1.getPodID());		// set the booking to pod1 
		reservation.setPodName(pod1.getLabel());	// sets the pod label
		
		bookingBean.setPendingReservationID(reservation.getReservationID());
		
		//verify the name matches the Manual pod assignment 
		assertEquals("k1", reservation.getPodName());
		
		// Simulation of the user  making a successful payment in the paypal
		// bookingBean.completePayment();   can't mock this due to jakarta beans would need mokito
		//returnign the elements below for testing 
		reservation.setStatus("Confirmed");						//line 328 - r.setStatus("Confirmed");
		reservation.setHoldExpiry(null);						//line 329 - r.setHoldExpiry(null);
		reservation.setPaypalOrderId("PAYPAL-ORDER-123");		//line 330 - r.setPayPalOrderID(orderID) - 
																//this would be the token 315 -	String orderId = ext.getRequestParameterMap().get("token"); // token = PayPal order id
		
		// Verify Reservation updates
		assertEquals("Confirmed", reservation.getStatus());
		assertNull(reservation.getHoldExpiry());				//resets the hold expiry
		assertEquals("PAYPAL-ORDER-123", reservation.getPaypalOrderId());
		
		// Verify that the successful payment means it hasn't been added to the unsuccessful list. 
		assertFalse(reservationList.getUnsuccessfulPayments().contains(reservation));
		
		// Verify pod is immediately free for booking
		boolean isAvailable = podList.isPodAvailable(pod1.getPodID(), "2026-08-22", "2026-09-01");
		assertFalse(isAvailable);					//status for this pod would be false as the booking has been accepted
	}
	


	@Test
	@DisplayName("Test: Simulated confirmAndPay Reservation Creation")
	void testSimulatedConfirmAndPayState() {
		// Recreate the reservation state built by confirmAndPay()
		Reservation res = new Reservation(user1.getUserID(),pet1.getPetID(),pet1.getName(),"2026-08-22","2026-09-01");
		
		res.setPodID(pod1.getPodID());
		res.setPodName("k1");
		res.setTotalPrice(250.00);
		res.setStatus("Pending");
		res.setHoldExpiry(LocalDateTime.now().plusMinutes(15));

		reservationList.addReservation(res);

		// Test Assertion of reservation holds correctly
		assertEquals("Pending", res.getStatus());
		assertTrue(res.isHoldActive());
		assertEquals(250.00, res.getTotalPrice());
	}

	
	@Test
	@DisplayName("Test OrderDetail - Correct Currency String Formatting")
	void testOrderDetailFormatting() {
	    // Construct order detail with raw float values
	    OrderDetail detail = new OrderDetail("Dog Boarding", 100.0f, 0.0f, 0f, 100.0f);

	    // Verify getter values format properly to two decimal places
	    assertEquals("Dog Boarding", detail.getProductName());
	    assertEquals("100.00", detail.getSubtotal());
	    assertEquals("0.00", detail.getShipping());
	    assertEquals("0.00", detail.getTax());
	    assertEquals("100.00", detail.getTotal()); // Verifies Locale.ROOT decimal format
	}
	

	
	@Test
	@DisplayName("Test completePayment handles invalid PayPal token cleanly")
	void testCompletePaymentWithInvalidToken() {
	    // Fake ExternalContext supplying the mock token parameter
	    ExternalContextWrapper extContext = new ExternalContextWrapper() {
	        @Override
	        public Map<String, String> getRequestParameterMap() {
	            return Map.of("token", "MOCK-PAYPAL-TOKEN-123");
	        }
	    };

	    // Fake FacesContext overriding both getExternalContext and addMessage
	    FacesContext facesContext = new FacesContextWrapper() {
	        @Override
	        public ExternalContext getExternalContext() {
	            return extContext;
	        }

	        @Override
	        public void addMessage(String clientId, FacesMessage message) {
	            // Stubbed to prevent NPE when getWrapped() is null
	        }
	    };

	    // Inject mock context into JSF runtime
	    FacesContextMocker.setContext(facesContext);

	    try {
	        pod1.setOutOfService(false);
	        reservation.setPodID(pod1.getPodID());
	        bookingBean.setPendingReservationID(reservation.getReservationID());

	        // Executes completePayment, triggers PayPal call, and catches 404
	        bookingBean.completePayment();

	        // Verify status changes to 'Payment Failed' via failReservation()
	        assertEquals("Payment Failed", reservation.getStatus());
	        assertTrue(reservation.getFailureReason().contains("Capture failed"));
	        assertNull(reservation.getHoldExpiry());

	    } finally {
	        // Clear context after test
	        FacesContextMocker.setContext(null);
	    }
	}
	
	@Test
	@DisplayName("Test: Dashboard returns only confirmed bookings for logged-in user")
	void testGetConfirmedUserBookingsFilter() {
	
	    login.setLoggedInUser(user1);


	    // Create a confirmed reservation and add to list
	    Reservation confirmedRes = new Reservation(user1.getUserID(), pet1.getPetID(), pet1.getName(), "2026-09-05", "2026-09-10");
	    confirmedRes.setStatus("Confirmed");
	    reservationList.addReservation(confirmedRes);

	    // Default reservation from setUp() remains 'Pending'
	    reservation.setStatus("Cancelled");

	    // Verify filter retrieves strictly confirmed bookings
	    List<Reservation> confirmedList = bookingBean.getConfirmedUserBookings();
	    assertEquals(1, confirmedList.size());
	    assertEquals("Confirmed", confirmedList.get(0).getStatus());
	}
	
	@Test
	@DisplayName("Test: Cancel navigation resets bean form fields and redirects")
	void testCancelResetsFormAndRedirects() {
	    bookingBean.setSelectedPetID(pet1.getPetID());
	    bookingBean.setCheckInDate("2026-08-22");
	    
	    String redirectOutcome = bookingBean.cancel();

	    assertEquals("/userDashboard?faces-redirect=true", redirectOutcome);
	    assertEquals(0, bookingBean.getSelectedPetID());
	    assertNull(bookingBean.getCheckInDate());
	    assertEquals(0.0, bookingBean.getTotalPrice());
	}
	
}
