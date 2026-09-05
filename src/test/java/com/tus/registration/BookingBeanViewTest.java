package com.tus.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;
import com.tus.pethotel.User;

/**
 * A confirmed booking is visible in My Bookings.
 * A user with no bookings sees nothing.
 */
class BookingBeanViewTest {

	private BookingBean bookingBean;
	private ReservationList reservationList;

	// User assigns IDs from a static counter, so they are read back rather
	// than assumed
	private int userID;
	private int otherUserID;

	private Reservation reservation(int userID, String petName, String status) {
		Reservation r = new Reservation(userID, 10, petName, "2026-09-01", "2026-09-05");
		r.setStatus(status);
		return r;
	}
	
	@BeforeEach
	void setUp() {
		reservationList = new ReservationList();

		User customer = new User("Test Customer", "customer@3a.ie", "password");
		userID = customer.getUserID();
		otherUserID = new User("Other Customer", "other@3a.ie", "password").getUserID();
		LoginBean loginBean = new LoginBean();
		loginBean.setLoggedInUser(customer);
		bookingBean = new BookingBean();
		bookingBean.setReservationList(reservationList);
		bookingBean.setLoginBean(loginBean);
	}

	// A confirmed booking is visible 

	@Test
	@DisplayName("A confirmed booking is returned for the logged-in user")
	void confirmedBookingIsReturned() {
		reservationList.addReservation(reservation(userID, "Rex", "Confirmed"));
		ArrayList<Reservation> result = bookingBean.getConfirmedUserBookings();
		assertEquals(1, result.size());
		assertEquals("Rex", result.get(0).getPetName());
		assertEquals("Confirmed", result.get(0).getStatus());
	}

	@Test
	@DisplayName("Every confirmed booking is returned when the user has several")
	void allConfirmedBookingsAreReturned() {
		reservationList.addReservation(reservation(userID, "Rex", "Confirmed"));
		reservationList.addReservation(reservation(userID, "Milo", "Confirmed"));
		reservationList.addReservation(reservation(userID, "Bella", "Confirmed"));
		assertEquals(3, bookingBean.getConfirmedUserBookings().size());
	}

	@Test
	@DisplayName("Booking details needed by the table are carried through")
	void bookingDetailsAreCarriedThrough() {
		Reservation booked = reservation(userID, "Rex", "Confirmed");
		booked.setTotalPrice(285.00);
		reservationList.addReservation(booked);
		Reservation shown = bookingBean.getConfirmedUserBookings().get(0);
		assertEquals(booked.getReservationID(), shown.getReservationID());
		assertEquals("Rex", shown.getPetName());
		assertEquals("2026-09-01", shown.getCheckInDate());
		assertEquals("2026-09-05", shown.getCheckOutDate());
		assertEquals(285.00, shown.getTotalPrice(), 0.001);
	}

	@Test
	@DisplayName("Status matching ignores case")
	void statusMatchingIsCaseInsensitive() {
		reservationList.addReservation(reservation(userID, "Rex", "confirmed"));
		reservationList.addReservation(reservation(userID, "Milo", "CONFIRMED"));
		assertEquals(2, bookingBean.getConfirmedUserBookings().size());
	}

	// Nothing appears when there is nothing to show

	@Test
	@DisplayName("A user with no bookings gets an empty list")
	void noBookingsReturnsEmptyList() {
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	@Test
	@DisplayName("An unpaid pending booking is not counted as confirmed")
	void pendingBookingIsExcluded() {
		reservationList.addReservation(reservation(userID, "Rex", "Pending"));
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	@Test
	@DisplayName("Cancelled and failed payments are not counted as confirmed")
	void cancelledAndFailedBookingsAreExcluded() {
		reservationList.addReservation(reservation(userID, "Rex", "Cancelled"));
		reservationList.addReservation(reservation(userID, "Milo", "Payment Failed"));
		reservationList.addReservation(reservation(userID, "Bella", "Expired"));
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	@Test
	@DisplayName("A booking with no status set is excluded rather than causing an error")
	void nullStatusIsExcluded() {
		reservationList.addReservation(reservation(userID, "Rex", null));
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	// Filtering: only the right bookings, for the right user

	@Test
	@DisplayName("Only confirmed bookings are returned when statuses are mixed")
	void mixedStatusesReturnsOnlyConfirmed() {
		reservationList.addReservation(reservation(userID, "Rex", "Confirmed"));
		reservationList.addReservation(reservation(userID, "Milo", "Pending"));
		reservationList.addReservation(reservation(userID, "Bella", "Confirmed"));
		reservationList.addReservation(reservation(userID, "Coco", "Cancelled"));
		ArrayList<Reservation> result = bookingBean.getConfirmedUserBookings();
		assertEquals(2, result.size());
		assertEquals("Rex", result.get(0).getPetName());
		assertEquals("Bella", result.get(1).getPetName());
	}

	@Test
	@DisplayName("Another customer's confirmed booking is not returned")
	void otherUsersBookingsAreNotReturned() {
		reservationList.addReservation(reservation(otherUserID, "Shadow", "Confirmed"));
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	@Test
	@DisplayName("No user logged in returns an empty list rather than throwing")
	void noLoggedInUserReturnsEmptyList() {
		LoginBean loggedOut = new LoginBean();
		loggedOut.setLoggedInUser(null);
		bookingBean.setLoginBean(loggedOut);
		reservationList.addReservation(reservation(userID, "Rex", "Confirmed"));
		assertTrue(bookingBean.getConfirmedUserBookings().isEmpty());
	}

	@Test
	@DisplayName("Two customers with bookings do not see each other's")
	void bookingsAreSeparatedBetweenCustomers() {
		reservationList.addReservation(reservation(userID, "Rex", "Confirmed"));
		reservationList.addReservation(reservation(otherUserID, "Shadow", "Confirmed"));
		ArrayList<Reservation> result = bookingBean.getConfirmedUserBookings();
		assertEquals(1, result.size());
		assertEquals("Rex", result.get(0).getPetName());
	}
}
