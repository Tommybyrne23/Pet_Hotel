package com.tus.payment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;


@DisplayName("RefundCheck - 48 hour refund window")
class RefundCheckTest {

	private static final String CHECK_IN = "2026-10-01"; // -> 2026-10-01T14:00

	private ReservationList reservationList;

	@BeforeEach
	void setUp() {
		reservationList = new ReservationList(); // constructor creates an empty list
	}

	/*
	 * Add a reservation with the given check-in date and return its
	 * reservation ID (the ID is assigned by the Reservation constructor, 
	 * so we read it back rather than assume a value).
	 */
	private int addReservationCheckingInOn(String checkInDate) {
		Reservation r = new Reservation(1, 1, "Buddy", checkInDate, "2026-10-05");
		reservationList.addReservation(r);
		return r.getReservationID();
	}

	/*
	 * Helper: build a RefundCheck whose "now" is pinned to a fixed instant so the
	 * hours-until-check-in are fully deterministic.
	 */
	private RefundCheck refundCheckAt(LocalDateTime pinnedNow) {
		RefundCheck refundCheck = new RefundCheck(reservationList);
		refundCheck.timeNow = pinnedNow;
		return refundCheck;
	}

	@Test
	@DisplayName("No booking found for the ID -> false")
	void testNoBookingFound() {
		// List is empty, so any ID resolves to no reservation.
		RefundCheck refundCheck = refundCheckAt(LocalDateTime.of(2026, 9, 1, 0, 0));

		assertFalse(refundCheck.isRefundAllowed(999),
				"With no matching booking the payment/refund must be refused");
	}

	@Test
	@DisplayName("Check-in within 48 hours (47h away) -> false")
	void testWithin48Hours() {
		int id = addReservationCheckingInOn(CHECK_IN);
		// 47 hours before 2026-10-01T14:00
		RefundCheck refundCheck = refundCheckAt(LocalDateTime.of(2026, 9, 29, 15, 0));

		assertFalse(refundCheck.isRefundAllowed(id),
				"Only 47 hours before check-in should be refused");
	}

	@Test
	@DisplayName("Check-in more than 48 hours away (72h away) -> true")
	void testOver48Hours() {
		int id = addReservationCheckingInOn(CHECK_IN);
		// 72 hours before 2026-10-01T14:00
		RefundCheck refundCheck = refundCheckAt(LocalDateTime.of(2026, 9, 28, 14, 0));

		assertTrue(refundCheck.isRefundAllowed(id),
				"Comfortably more than 48 hours before check-in should be allowed");
	}

	@Test
	@DisplayName("Check-in date is in the past -> false")
	void testCheckInInThePast() {
		int id = addReservationCheckingInOn(CHECK_IN);
		// 2 days AFTER check-in -> negative hours
		RefundCheck refundCheck = refundCheckAt(LocalDateTime.of(2026, 10, 3, 0, 0));

		assertFalse(refundCheck.isRefundAllowed(id),
				"A check-in date already in the past must be refused");
	}

	@Test
	@DisplayName("Check-in exactly 48 hours away -> true (boundary is inclusive)")
	void testExactly48Hours() {
		int id = addReservationCheckingInOn(CHECK_IN);
		// Exactly 48 hours before 2026-10-01T14:00
		RefundCheck refundCheck = refundCheckAt(LocalDateTime.of(2026, 9, 29, 14, 0));

		assertTrue(refundCheck.isRefundAllowed(id),
				"Exactly 48 hours away should pass because the check is >= 48");
	}
}
