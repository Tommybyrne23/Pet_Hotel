package com.tus.pethotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        if (reservations.isEmpty()) {
            // 1. Past Booking (Checkout before current date)
            Reservation r1 = new Reservation(6, 1, "Buddy", "2026-08-01", "2026-08-07");
            r1.setGrooming(true);
            r1.setWalks(true);
            r1.setTotalPrice(360.00);
            r1.setStatus("Confirmed");
            addReservation(r1);

            // 2. Past Booking
            Reservation r2 = new Reservation(6, 2, "Luna", "2026-08-03", "2026-08-08");
            r2.setPremiumFood(true);
            r2.setTotalPrice(215.00);
            r2.setStatus("Confirmed");
            addReservation(r2);

            // 3. Currently On-Site (Active through Thursday)
            Reservation r3 = new Reservation(6, 1, "Buddy", "2026-08-10", "2026-08-16");
            r3.setWalks(true);
            r3.setPremiumFood(true);
            r3.setTotalPrice(318.00);
            r3.setStatus("Confirmed");
            addReservation(r3);

            // 4. Currently On-Site (Active through Thursday)
            Reservation r4 = new Reservation(6, 3, "Max", "2026-08-12", "2026-08-15");
            r4.setGrooming(true);
            r4.setTotalPrice(150.00);
            r4.setStatus("Confirmed");
            addReservation(r4);

            // 5. Upcoming Booking (Check-in after Thursday)
            Reservation r5 = new Reservation(6, 2, "Luna", "2026-08-18", "2026-08-22");
            r5.setGrooming(true);
            r5.setWalks(true);
            r5.setPremiumFood(true);
            r5.setTotalPrice(272.00);
            r5.setStatus("Confirmed");
            addReservation(r5);
        }
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
