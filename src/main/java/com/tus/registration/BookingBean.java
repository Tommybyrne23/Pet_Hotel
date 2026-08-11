package com.tus.registration;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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

    // Form fields
    private int selectedPetID;
    private String checkInDate;   // yyyy-MM-dd
    private String checkOutDate;  // yyyy-MM-dd
    private boolean grooming;
    private boolean walks;
    private boolean premiumFood;
    private double totalPrice;
    private boolean priceCalculated;

    // Pricing constants (per night)
    private static final double BASE_RATE = 35.00;
    private static final double GROOMING_RATE = 15.00;
    private static final double WALKS_RATE = 10.00;
    private static final double PREMIUM_FOOD_RATE = 8.00;

    @Inject
    private LoginBean loginBean;

    @Inject
    private PetList petList;

    @Inject
    private ReservationList reservationList;

    /**
     * AC1: Get the logged-in user's registered pets for the dropdown.
     */
    public ArrayList<Pet> getUserPets() {
        User user = loginBean.getLoggedInUser();
        if (user != null) {
            return petList.findByUserID(user.getUserID());
        }
        return new ArrayList<>();
    }

    /**
     * AC4: Check if the user has any registered pets.
     */
    public boolean isHasPets() {
        return !getUserPets().isEmpty();
    }

    /**
     * Get all bookings for the logged-in user (for viewBookings page).
     */
    public ArrayList<Reservation> getUserBookings() {
        User user = loginBean.getLoggedInUser();
        if (user != null) {
            return reservationList.findByUserID(user.getUserID());
        }
        return new ArrayList<>();
    }

    /**
     * AC2: Calculate the total price based on dates and services.
     */
    public String calculatePrice() {

        FacesContext context = FacesContext.getCurrentInstance();

        // Validate pet selected
        if (selectedPetID == 0) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please select a pet.", null));
            priceCalculated = false;
            return null;
        }

        // Validate dates are entered
        if (checkInDate == null || checkInDate.trim().isEmpty()
                || checkOutDate == null || checkOutDate.trim().isEmpty()) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter both check-in and check-out dates.", null));
            priceCalculated = false;
            return null;
        }

        // Parse and validate dates
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate checkIn = LocalDate.parse(checkInDate.trim(), fmt);
            LocalDate checkOut = LocalDate.parse(checkOutDate.trim(), fmt);

            // Check-in must be today or later
            if (checkIn.isBefore(LocalDate.now())) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Check-in date cannot be in the past.", null));
                priceCalculated = false;
                return null;
            }

            // Check-out must be after check-in
            if (!checkOut.isAfter(checkIn)) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Check-out date must be after check-in date.", null));
                priceCalculated = false;
                return null;
            }

            // Calculate number of nights
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

            // Calculate nightly rate (base + selected services)
            double nightlyRate = BASE_RATE;
            if (grooming)    { nightlyRate += GROOMING_RATE; }
            if (walks)       { nightlyRate += WALKS_RATE; }
            if (premiumFood) { nightlyRate += PREMIUM_FOOD_RATE; }

            // Total = nightly rate x number of nights
            totalPrice = nightlyRate * nights;

            // Round to 2 decimal places
            totalPrice = Math.round(totalPrice * 100.0) / 100.0;

            priceCalculated = true;

        } catch (DateTimeParseException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid date format. Please use the calendar to select dates.", null));
            priceCalculated = false;
        }

        return null;
    }

    /**
     * AC3: Confirm the booking.
     */
    public String confirmBooking() {

        FacesContext context = FacesContext.getCurrentInstance();

        // Make sure price was calculated first
        if (!priceCalculated) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please calculate the price before confirming.", null));
            return null;
        }

        // Find the pet name for the reservation record
        String petName = "";
        for (Pet pet : getUserPets()) {
            if (pet.getPetID() == selectedPetID) {
                petName = pet.getName();
                break;
            }
        }

        // Create the reservation
        User user = loginBean.getLoggedInUser();
        Reservation reservation = new Reservation(
            user.getUserID(),
            selectedPetID,
            petName,
            checkInDate,
            checkOutDate
        );
        reservation.setGrooming(grooming);
        reservation.setWalks(walks);
        reservation.setPremiumFood(premiumFood);
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("Confirmed");

        // Save it
        reservationList.addReservation(reservation);

        // Format the price for the message
        String formattedPrice = String.format("%.2f", totalPrice);

        // Success message
        context.addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Booking confirmed! Your reservation for " + petName
                + " has been saved. Total: \u20AC" + formattedPrice, null));

        // Reset the form
        resetForm();

        return null;
    }

    /**
     * Reset all form fields.
     */
    public void resetForm() {
        selectedPetID = 0;
        checkInDate = null;
        checkOutDate = null;
        grooming = false;
        walks = false;
        premiumFood = false;
        totalPrice = 0;
        priceCalculated = false;
    }

    /**
     * Go back to dashboard.
     */
    public String cancel() {
        resetForm();
        return "userDashboard?faces-redirect=true";
    }

    // --- GETTERS AND SETTERS ---

    public int getSelectedPetID() { return selectedPetID; }
    public void setSelectedPetID(int selectedPetID) { this.selectedPetID = selectedPetID; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public boolean isGrooming() { return grooming; }
    public void setGrooming(boolean grooming) { this.grooming = grooming; }

    public boolean isWalks() { return walks; }
    public void setWalks(boolean walks) { this.walks = walks; }

    public boolean isPremiumFood() { return premiumFood; }
    public void setPremiumFood(boolean premiumFood) { this.premiumFood = premiumFood; }

    public double getTotalPrice() { return totalPrice; }

    public boolean isPriceCalculated() { return priceCalculated; }
}
