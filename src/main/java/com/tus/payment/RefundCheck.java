package com.tus.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;

public class RefundCheck {

	/*
	 * Refund check, search the reservationID and capture the checkinDate, 
	 * if the time to check in is less than 48 hours. 
	 * 
	 * Do not allow the payment to be processed (method returns false). 
	 * 
	 * 
	 * AND return a message saying "refunds are only available 48hours before check in date. 
	 * (this will be done in the paypal check) 
	 * 
	 * 
	 * otherwise return true to approve the payment and send the details to PayPal API
	 */

	private ReservationList reservationList;
	LocalDateTime checkInDate;
	LocalDateTime timeNow = LocalDateTime.now();

	public RefundCheck(ReservationList reservationList) {
		this.reservationList = reservationList;
	}

	public boolean isRefundAllowed(int reservationId) {
			Reservation r = reservationList.findByID(reservationId);
			if (r == null) {
				return false; 		//if reservation ID is not returned there is no booking. 
				}
			LocalDate checkInDay = LocalDate.parse(r.getCheckInDate());
			LocalDateTime checkInDateTime = checkInDay.atTime(14,0);		// set time for 14:00 check in time.
			
			
			//ChronoUnit is an Enum used to identify standard measure of time. 
			//refernce https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/ChronoUnit.html
			long hoursUntilCheckIn = ChronoUnit.HOURS.between(timeNow, checkInDateTime);	
			
			// if the value is 48 hours or over, the check passes as true, a refund can pass.
			// otherwise return false. Too close to 
			return hoursUntilCheckIn >= 48; 				
			
	}

}

