package com.tus.registration;

import java.io.Serializable;
import java.time.LocalDate;
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

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("BookingBean")
@SessionScoped
public class BookingBean implements Serializable {

	private static final long serialVersionUID = 1L;

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



    
    // Get the logged-in user's registered pets for the dropdown.
    
    public ArrayList<Pet> getUserPets() {
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


	public String confirmBooking() {

		if (!priceCalculated) {
			return addError("Please calculate the price before confirming.");
		}

		Pet pet = getSelectedPet();
		Service pod = serviceList.findByID(selectedPodID);
		User user = loginBean.getLoggedInUser();

		Reservation reservation = new Reservation(user.getUserID(), selectedPetID,
				pet.getName(), checkInDate, checkOutDate);

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
		reservation.setStatus("Confirmed");

		reservationList.addReservation(reservation);

		addMessage(FacesMessage.SEVERITY_INFO, "Booking confirmed! Your reservation for "
				+ pet.getName() + " has been saved. Total: \u20AC"
				+ String.format("%.2f", totalPrice));
		resetForm();
		return null;
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
}