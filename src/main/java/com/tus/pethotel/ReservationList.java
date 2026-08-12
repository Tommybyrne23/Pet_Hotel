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
    
    
    //filter previous booking 
    public ArrayList<Reservation> getPastReservations(){
    	ArrayList<Reservation> pastReservations = new ArrayList<>();
    	LocalDate today = LocalDate.now(); 			//set the date to today for check in 

    	for (Reservation r : reservations) {
    		if (r != null && r.getCheckOutDate() != null && !r.getCheckOutDate().isBlank()) {
    			try {
    				LocalDate checkoutDate = LocalDate.parse(r.getCheckOutDate().trim());
    				if (checkoutDate.isBefore(today)) {
    					pastReservations.add(r);
    				}
    			} catch (DateTimeParseException e) {
    		
    			}
    		}
    	}
    	return pastReservations;
    }

    //filter bookings in the future 
    
    public ArrayList<Reservation> getFutureReservations(){
    	ArrayList<Reservation> upcomingReservations = new ArrayList<>();
    	LocalDate today = LocalDate.now(); 	
    	
    	   for (Reservation r : reservations) {
    	        if (r != null && r.getCheckInDate() != null && !r.getCheckInDate().isBlank()) {
    	            try {
    	                LocalDate checkInDate = LocalDate.parse(r.getCheckInDate().trim());
    	                if (checkInDate.isAfter(today)) {
    	                    upcomingReservations.add(r);
    	                }
    	            } catch (DateTimeParseException e) {
    	            
    	            }
    	        }
    	    }
    	    return upcomingReservations;
    	}
    
    //filter bookings currently on site  
    
    public ArrayList<Reservation> getActiveBookings(){
    	ArrayList<Reservation> reservationOnSiteToday= new ArrayList<>();
    	LocalDate today = LocalDate.now(); 	
    	

    	for (Reservation r : reservations) {
    		if (r != null && r.getCheckInDate() != null && !r.getCheckInDate().isBlank()
    				&& r.getCheckOutDate() != null && !r.getCheckOutDate().isBlank()) {
    			try {
    				LocalDate checkIn = LocalDate.parse(r.getCheckInDate().trim());
    				LocalDate checkOut = LocalDate.parse(r.getCheckOutDate().trim());

    				if (!checkIn.isAfter(today) && !checkOut.isBefore(today)) {
    					reservationOnSiteToday.add(r);
    				}
    			} catch (DateTimeParseException e) {
    				
    			}
    		}
    	}
    	return reservationOnSiteToday;
    }   
}
