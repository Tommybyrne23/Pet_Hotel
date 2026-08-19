package com.tus.payment;

import com.tus.pethotel.*;
import com.tus.registration.*;
import com.tus.services.*;

public class RefundBookingBean {

	private ReservationList reservationList;
	public RefundCheck refundCheck;
	Room room;
	Pod pod;
	RoomList roomList;
	PodList podlist;
	BookingBean bookingBean;
	
	/*
	 * Refund bean should review the row and collect the order ID and check in Date. 
	 * 
	 * It uses the refund method to call isRefundAllowed (
	 * if the date is less than 48 hours from check in)
	 * return a message explaining cancellations are only allowed 
	 * 
	 * Its more than 48 hours, capture the PapyalCapture ID and send this to the Portal   
	 *
	 * If the cancellation is succesful from Payapl Return the booking status to Refunded. 
	 * 
	 */
	public static void main(String[] args) {
		
		
		
		
		/*
		null? → stop
				not Confirmed? → stop
				isRefundAllowed == false? → stop, "48 hours" message
				→ get the CAPTURE id (the real work you're missing)
				→ PaymentServices.refund(captureId)
				   success → status "Refunded" (+ free pod?)
				   failure → error messa
		 */
	}
	
	
	
}
