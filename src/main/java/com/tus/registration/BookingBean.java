package com.tus.registration;

import java.io.Serializable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.tus.Services.ChargeType;
import com.tus.Services.Service;
import com.tus.Services.ServiceList;
import com.tus.pethotel.Pet;
import com.tus.pethotel.PetList;
import com.tus.pethotel.Reservation;
import com.tus.pethotel.ReservationList;
import com.tus.pethotel.User;
import com.tus.Services.Species;
import com.tus.payment.PaymentServices;
import com.tus.pethotel.Pod;
import com.tus.pethotel.PodList;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.paypal.sdk.exceptions.ApiException;

import com.tus.payment.OrderDetail;
import com.tus.payment.PaymentServices;

@Named("BookingBean")
@SessionScoped
public class BookingBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int pendingReservationID;		//setup to link with Paypal payment
	private int selectedPetID;
	private int selectedPodID;
	private Integer[] selectedExtraIDs = new Integer[0];  
	private String checkInDate;   // yyyy-MM-dd
	private String checkOutDate;  // yyyy-MM-dd
	private double totalPrice;
	private boolean priceCalculated;

	@Inject private LoginBean loginBean;
	@Inject private PetList petList;
	@Inject private ReservationList reservationList;
	@Inject private ServiceList serviceList;
	@Inject private PodList podList;
	@Inject private PaymentServices paymentServices; 		//links to PayPal payment methods


	
    // Get the logged-in user's registered pets for the dropdown.
    
    public List<Pet> getUserPets() {
        User user = loginBean.getLoggedInUser();
        if (user != null) {
            return petList.findByUserID(user.getUserID());
        }
        return new ArrayList<>();
    }



    // Check if the user has any registered pets.
    
    public boolean isHasPets() {
        return !getUserPets().isEmpty();
    }

    // Get all bookings for the logged-in user (for viewBookings page).
    
    public ArrayList<Reservation> getUserBookings() {
        User user = loginBean.getLoggedInUser();
        if (user != null) {
            return reservationList.findByUserID(user.getUserID());
        }
        return new ArrayList<>();
    }

    // Calculate the total price based on dates and services.
    
 

	public Pet getSelectedPet() {
		for (Pet pet : getUserPets()) {
			if (pet.getPetID() == selectedPetID) {
				return pet;
			}
		}
		return null;
	}


	// Pods and extras that suit the chosen pet. Empty until a pet is picked.
	public List<Service> getAvailablePods() {
		Pet pet = getSelectedPet();
		return pet == null ? new ArrayList<>()
				: serviceList.getPodsForSpecies(pet.getSpecies().getLabel());
	}

	public List<Service> getAvailableExtras() {
		Pet pet = getSelectedPet();
		return pet == null ? new ArrayList<>()
				: serviceList.getExtrasForSpecies(pet.getSpecies().getLabel());
	}
	// A pod or extra chosen for a dog may not exist for a cat, so switching
	// pet clears the selections
	public void petChanged() {
		selectedPodID = 0;
		selectedExtraIDs = new Integer[0];
		priceCalculated = false;
	}

	// Turns the ticked checkbox ids back into Service objects
	private List<Service> getSelectedExtras() {

		List<Service> chosen = new ArrayList<>();

		if (selectedExtraIDs != null) {
			for (Integer id : selectedExtraIDs) {
				chosen.add(serviceList.findByID(id));
			}
		}
		return chosen;
	}


	public String calculatePrice() {

		if (selectedPetID == 0) {
			return addError("Please select a pet.");
		}

		if (selectedPodID == 0) {
			return addError("Please select a pod for your pet's stay.");
		}

		if (checkInDate == null || checkInDate.trim().isEmpty()
				|| checkOutDate == null || checkOutDate.trim().isEmpty()) {
			return addError("Please enter both check-in and check-out dates.");
		}

		try {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate checkIn = LocalDate.parse(checkInDate.trim(), fmt);
			LocalDate checkOut = LocalDate.parse(checkOutDate.trim(), fmt);

			if (checkIn.isBefore(LocalDate.now())) {
				return addError("Check-in date cannot be in the past.");
			}

			if (!checkOut.isAfter(checkIn)) {
				return addError("Check-out date must be after check-in date.");
			}

			long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

			// A booking needs a physical pod for the pet's species. Checked
			// here so the customer finds out before seeing a price, but not
			// allocated until they confirm.
			Pet pet = getSelectedPet();
			Species species = pet.getSpecies();

			if (podList.getPodsForSpecies(species).isEmpty()) {
				return addError("We do not have any " + species.getLabel().toLowerCase()
						+ " pods set up at the moment. Please contact us to arrange a stay.");
			}

			if (podList.findAvailablePod(species, checkInDate, checkOutDate) == null) {
				return addError("All of our " + species.getLabel().toLowerCase()
						+ " pods are taken for those dates. Please try different dates.");
			}

			// The pod is always charged per night
			double total = serviceList.findByID(selectedPodID).getPrice() * nights;

			// Each extra is charged either per night or once for the whole stay
			for (Service extra : getSelectedExtras()) {
				total += extra.getChargeType() == ChargeType.ONE_OFF
						? extra.getPrice()
								: extra.getPrice() * nights;
			}

			totalPrice = Math.round(total * 100.0) / 100.0;
			priceCalculated = true;

		} catch (DateTimeParseException e) {
			return addError("Invalid date format. Please use the calendar to select dates.");
		}
		return null;
	}


	public String confirmAndPay() {

		if (!priceCalculated) {
			return addError("Please calculate the price before confirming.");
		}

		Pet pet = getSelectedPet();
		Service pod = serviceList.findByID(selectedPodID);
		User user = loginBean.getLoggedInUser();

		// Re-checked rather than trusting the check at calculate time, since
		// another customer may have taken the last pod in between
		Pod allocatedPod = podList.findAvailablePod(pet.getSpecies(),
				checkInDate, checkOutDate);

		if (allocatedPod == null) {
			return addError("Sorry, the last " + pet.getSpecies().getLabel().toLowerCase()
					+ " pod was taken while you were booking. Please try different dates.");
		}

		Reservation reservation = new Reservation(user.getUserID(), selectedPetID,
				pet.getName(), checkInDate, checkOutDate);

		reservation.setPodID(allocatedPod.getPodID());
		reservation.setPodName(pod.getName());
		reservation.setPodPricePerNight(pod.getPrice());

		// Record the extras as text, so the booking still reads correctly even
		// if an admin later renames or reprices the service
		List<String> extraLabels = new ArrayList<>();

		for (Service extra : getSelectedExtras()) {
			String charge = extra.getChargeType() == ChargeType.ONE_OFF ? "one-off" : "per night";
			extraLabels.add(extra.getName() + " (€"
					+ String.format("%.2f", extra.getPrice()) + " " + charge + ")");
		}

		reservation.setExtras(extraLabels);
		reservation.setTotalPrice(totalPrice);
		reservation.setStatus("Pending");
		reservation.setHoldExpiry(LocalDateTime.now().plusMinutes(15));		// added 15 minutes searching here --> https://stackoverflow.com/questions/30374796/how-can-i-add-an-int-minutes-to-localdatetime-now
		reservationList.addReservation(reservation);
		pendingReservationID = reservation.getReservationID();
		reservation.setStatusUpdatedAt(LocalDateTime.now());					//records the time of the booking and when it was updated
		
		// PAYPAL Starts and app hands detail over to paypal 
		try {
			
			ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
			//sets the url to the local host and appropriate ports dependent on the developer and users. 
			String baseUrl = ext.getRequestScheme() + "://"  + ext.getRequestServerName() + ":" + ext.getRequestServerPort() + ext.getRequestContextPath();
				
			OrderDetail orderDetail = new OrderDetail(pod.getName()+ " for " + pet.getName(),0f, 0f, 0f, (float) totalPrice);
			String approvalLink = new PaymentServices().authorizePayment(orderDetail, baseUrl);
			
			if (approvalLink != null) {
				ext.redirect(approvalLink);
			} else {
				failReservation(reservation, "Paypal did not return an approval link");
			} return addError("Failed to transfer to PayPal payments. Please try again!");
		
			
			
		} catch (ApiException | IOException ex) {
			ex.printStackTrace();
			failReservation(reservation, "Create order failed: " + ex.getMessage());
			return addError("Could Not Start the Paypal Payment. Please try again.");
		}
		return null;
//		old code change 
//
//		addMessage(FacesMessage.SEVERITY_INFO, "Booking confirmed! Your reservation for "
//				+ pet.getName() + " has been saved. Total: \u20AC"
//				+ String.format("%.2f", totalPrice));
//		resetForm();
//		return null;
	}


	public void resetForm() {
		selectedPetID = 0;
		selectedPodID = 0;
		selectedExtraIDs = new Integer[0];
		checkInDate = null;
		checkOutDate = null;
		totalPrice = 0;
		priceCalculated = false;
		
	}

	public String cancel() {
		resetForm();
		return "/userDashboard?faces-redirect=true";
	}

	// returns null so callers can write "return addError(...)"
	private String addError(String summary) {
		priceCalculated = false;
		addMessage(FacesMessage.SEVERITY_ERROR, summary);
		return null;
	}

	private void addMessage(FacesMessage.Severity severity, String summary) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			context.addMessage(null, new FacesMessage(severity, summary, null));
		}
	}
	
	// Feeds the availability note on the booking page. Empty until both
	// dates are filled in, since availability is date-dependent.
	public String getPodAvailabilityNote() {

		Pet pet = getSelectedPet();

		if (pet == null || checkInDate == null || checkInDate.isBlank()
				|| checkOutDate == null || checkOutDate.isBlank()) {
			return "";
		}

		int free = podList.countAvailablePods(pet.getSpecies(), checkInDate, checkOutDate);

		if (free == 0) {
			return "No " + pet.getSpecies().getLabel().toLowerCase()
					+ " pods are free for those dates.";
		}

		return free + " " + pet.getSpecies().getLabel().toLowerCase()
				+ " pod(s) free for those dates.";
	}

	public int getSelectedPetID() { 
		return selectedPetID; 
	}
	public void setSelectedPetID(int selectedPetID) { 
		this.selectedPetID = selectedPetID; 
	}

	public int getSelectedPodID() {
		return selectedPodID; 
	}
	public void setSelectedPodID(int selectedPodID) {
		this.selectedPodID = selectedPodID; 
	}
	public Integer[] getSelectedExtraIDs() {
		return selectedExtraIDs; 
	}
	public void setSelectedExtraIDs(Integer[] selectedExtraIDs) {
		this.selectedExtraIDs = selectedExtraIDs; 
	}
	public String getCheckInDate() {
		return checkInDate; 
	}
	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate; 
	}
	public String getCheckOutDate() {
		return checkOutDate; 
	}
	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate; 
	}
	public double getTotalPrice() {
		return totalPrice; 
	}
	public boolean isPriceCalculated() {
		return priceCalculated; 
	}
	public void setPodList(PodList podList) {
		this.podList = podList;
	}
}