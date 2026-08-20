package com.tus.payment;

import com.tus.pethotel.*;
import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.models.Refund;
/*
 * Refund bean should review the row and collect the order ID and check in Date. 
 * 
 * It uses the refund method to call isRefundAllowed (
 * if the date is less than 48 hours from check in)
 * return a message explaining cancellations are only allowed 
 * 
 * Its more than 48 hours, capture the PapyalCapture ID and send this to the Portal   
 *
 * If the cancellation/refund is processed  succesfully from Payapl Return the booking status to Refunded. 
 * 
 * Bean Flow:
 * 
 * 	No Booking? 				-> return null // stop 
 *	Status is Not Confirmed  	->	return//stop
 *	isRefundAllowed == False	-> STOP - return 48 hour message 
 * 	no captureID 				-> we can't process the payment, ask them to contact us to do it manually
 * 		Otherwise 	
 * 	
 * 		Payment services.refundCapture(CaptureID)
 * 
 *			Completed -> Status -> refund 
 *			Error message 		->  do not change the status, reportt the error message and ask them 
									to contact us 
 *  
 *  
 */

@Named("RefundBean")
@SessionScoped
public class RefundBookingBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Inject	private ReservationList reservationList;
	@Inject	private PaymentServices paymentServices;
	@Inject	private PodList podlist;

	/*
	 * Called from the page with the id of the booking to refund, e.g.
	 *   action="#{RefundBean.processRefund(booking.reservationID)}"
	 *
	 * Returns null to stay on the same page; the outcome should show 
	 * as a FacesMessage.
	 */

	public String processRefund(int reservationID) {
		Reservation r = reservationList.findByID(reservationID);
		//if no reservation is foudn report message 
		if (r == null) {
			return addError("No booking was found for that booking ID");
		}
		// safety check 
		//if the booking status isn't confirmed do not allow the refund to be processed any further
		
		if (!(r.getStatus().equalsIgnoreCase("Confirmed"))) {
			return addError("Refunds are only available ");
		}

		//check if the refund is eligible (time is greater than 48 hours)
		RefundCheck refundCheck = new RefundCheck(reservationList);
		//if the check returns false, add an error message 
		if(!refundCheck.isRefundAllowed(reservationID)) {
			return addError("Refunds and cancellations are only available more than 48 hours before the checkin date.");
		}

		//Check the Paypal captureID, if there is no capture ID stored on the system 
		//(this could be because a payment was made in person), ask for them to contact us 
		// to process the refund off the system.
		String captureID = r.getPaypalCaptureId(); 
		if (captureID == null || captureID.isBlank()) {
			return addError("We could not find the payment reference stored for this booking. Please contact us. ");
		}

		//if the booking passes all these filters then it is eligible for a refund. 
		try {
			
			//paymentServices is where the app talks to PAYPAL api. 
			Refund refund = paymentServices.refundCapture(captureID);
			String status;
			if (refund != null && refund.getStatus() != null) {
				status = refund.getStatus().toString();
			} else {
				status = "UNKNOWN";
			}
			//if Paypal reports that the process has been completed 
			//change the status to refunded.
			if (status.equalsIgnoreCase("COMPLETED")) {
				r.setStatus("Refunded");							//refunded status means the pod ID associated with the record will not be checked against active bookings. 
																	// PODLIST will only block a booking on that date if the pod is "CONFIRMED"
				r.setStatusUpdatedAt(LocalDateTime.now());			//update the time the refund was processed for admin records
				addMessage(FacesMessage.SEVERITY_INFO, "Refund complete for " + r.getPetName()+ "'s booking. A refund of "+ r.getTotalPrice() + " should be with you shortly.");
			} 
			//if the payment fails for some reason here and Paypal returns an error messgae 
			else {
				addMessage(FacesMessage.SEVERITY_ERROR, "This refund was not completed ("+ status + "). Please try again or please contact us");
				
			}
			//Stays on the page, the FacesMessage will appear on the page with the response.
			return null; 

		} //end try bracket to lead into catch ,comment here so I dind't lost myself when coding. 
		
		catch (ApiException | IOException ex) {
			//catch will cover unexpected errors in the refund process 
			//if an API or IO error occurs, ensure nothing changes and an error message is returned
			addMessage(FacesMessage.SEVERITY_ERROR,"The refund couldn't be processed at this moment in time, please try again later. ");
			return null;
		}



	}

	/*
	 * AC2 - No button when payment is not refundable. 
	 * If a booking has been confirmed from outside of paypal (could be historical, through a previous
	 * payment processor. Any refunds have to be made manually by the 3A team.
	 * 
	 * more or less the same conditions as the process refund checks without calling paypal. 
	 */
	public boolean isRefundable(int reservationID) {
		Reservation r = reservationList.findByID(reservationID);
		if (r == null) {
			return false;
		}
		// safety check 
		//if the booking status isn't confirmed do not allow the refund to be processed any further
		if (!(r.getStatus().equalsIgnoreCase("Confirmed"))) {
			return false;
		}
		
		//check if the refund is eligible (time is greater than 48 hours)
		RefundCheck refundCheck = new RefundCheck(reservationList);
		//if the check returns false, add an error message 
		if(!refundCheck.isRefundAllowed(reservationID)) {
			return false;
		}
		//does the booking have a PAYPAL CAPTURE ID
		String captureID = r.getPaypalCaptureId(); 
		if (captureID == null || captureID.isBlank()) {
			return false;
		}
		return true;
		
	}
	
	
	
	private String addError(String summary) {
		addMessage(FacesMessage.SEVERITY_ERROR, summary);
		return null;
	}

	private void addMessage(FacesMessage.Severity severity, String summary) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			context.addMessage(null, new FacesMessage(severity, summary, null));
		}
	}
	
	
	
	// Getter for the booking page to block refunds who's check in date is less than 
	// 48 hours from the check in date (@2pm, allowing some leeway)
	public boolean isRefundAllowed(int reservationId) {
	    return new RefundCheck(reservationList).isRefundAllowed(reservationId);
	}

	//add setter so that the RefundBean can be added to a JUNIT test without 
	//additional container 
	public void setReservationList(ReservationList reservationList) {
		this.reservationList = reservationList;
	}

	//setter for linking payment services to RefundBean 
	public void setPaymentServices(PaymentServices paymentServices) {
		this.paymentServices = paymentServices;
	}



}
