package com.tus.pethotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("reservationList")
@ApplicationScoped
public class ReservationList implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Reservation> reservations;

    public ReservationList() {
        this.reservations = new ArrayList<>();
    }

    // Add a new reservation
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    // Find all reservations for a particular user
    public ArrayList<Reservation> findByUserID(int userID) {
        ArrayList<Reservation> userReservations = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getUserID() == userID) {
                userReservations.add(r);
            }
        }
        return userReservations;
    }

    // Get all reservations (for admin view)
    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    // How many reservations exist
    public int getNumberOfReservations() {
        return reservations.size();
    }
    
    public ArrayList<Reservation> getPastReservations(String checkOutDate){
    	ArrayList<Reservation> pastReservations = new ArrayList<>();
    	LocalDate Today = LocalDate.now(); 			//set the date to today for check in 
    	LocalDate checkOut = LocalDate.parse(checkOutDate);
    	for (Reservation r : reservations) {
    		if (checkOut.isBefore(Today)) {
    			pastReservations.add(r);
    		}
    	}
    	return pastReservations;
    }
    
}
