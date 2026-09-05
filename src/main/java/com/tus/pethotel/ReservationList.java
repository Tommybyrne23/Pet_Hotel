package com.tus.pethotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;

@Named("reservationList")
@ApplicationScoped
public class ReservationList implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Reservation> reservations;

    public ReservationList() {
        this.reservations = new ArrayList<>();
    }

    @Inject
    private PodList podList;

    @PostConstruct
    public void init() {

        if (!reservations.isEmpty()) {
            return;
        }

        // ---------------------------------------------------------
        // 1. Buddy -> K1
        // ---------------------------------------------------------
        Pod k1 = findPodByLabel("K1");

        if (k1 != null) {
            Reservation r1 = new Reservation(
                    6,                              // userID
                    1,                              // petID
                    "Buddy",                        // petName
                    "2026-10-01",                   // check-in
                    "2026-10-07"                    // check-out
            );

            r1.setPodID(k1.getPodID());
            r1.setPodName(k1.getLabel());
            r1.setPodPricePerNight(35.00);

            r1.setExtras(new ArrayList<>(List.of(
                    "Daily Walks (€10.00 per night)",
                    "Grooming (€15.00 one-off)"
            )));

            r1.setTotalPrice(285.00);
            r1.setStatus("Confirmed");

            addReservation(r1);
        }


        // ---------------------------------------------------------
        // 2. Luna -> C1
        // ---------------------------------------------------------
        Pod c1 = findPodByLabel("C1");

        if (c1 != null) {
            Reservation r2 = new Reservation(
                    6,
                    2,
                    "Luna",
                    "2026-10-03",
                    "2026-10-08"
            );

            r2.setPodID(c1.getPodID());
            r2.setPodName(c1.getLabel());
            r2.setPodPricePerNight(30.00);

            r2.setExtras(new ArrayList<>(List.of(
                    "Premium Food (€8.00 per night)"
            )));

            r2.setTotalPrice(190.00);
            r2.setStatus("Confirmed");

            addReservation(r2);
        }


        // ---------------------------------------------------------
        // 3. Buddy -> K2
        // ---------------------------------------------------------
        Pod k2 = findPodByLabel("K2");

        if (k2 != null) {
            Reservation r3 = new Reservation(
                    6,
                    3,
                    "Buddy",
                    "2026-10-10",
                    "2026-10-16"
            );

            r3.setPodID(k2.getPodID());
            r3.setPodName(k2.getLabel());
            r3.setPodPricePerNight(35.00);

            r3.setExtras(new ArrayList<>(List.of(
                    "Daily Walks (€10.00 per night)",
                    "Premium Food (€8.00 per night)"
            )));

            r3.setTotalPrice(318.00);
            r3.setStatus("Confirmed");

            addReservation(r3);
        }


        // ---------------------------------------------------------
        // 4. Max -> K3
        // This one is active on 17 Aug 2026
        // ---------------------------------------------------------
        Pod k3 = findPodByLabel("K3");

        if (k3 != null) {
            Reservation r4 = new Reservation(
                    6,
                    3,
                    "Max",
                    "2026-08-12",
                    "2026-08-20"
            );

            r4.setPodID(k3.getPodID());
            r4.setPodName(k3.getLabel());
            r4.setPodPricePerNight(35.00);

            r4.setExtras(new ArrayList<>(List.of(
                    "Grooming (€15.00 one-off)"
            )));

            r4.setTotalPrice(295.00);
            r4.setStatus("Confirmed");

            addReservation(r4);
        }


        // ---------------------------------------------------------
        // 5. Luna -> C2
        // ---------------------------------------------------------
        Pod c2 = findPodByLabel("C2");

        if (c2 != null) {
            Reservation r5 = new Reservation(
                    6,
                    2,
                    "Luna",
                    "2026-08-18",
                    "2026-08-22"
            );

            r5.setPodID(c2.getPodID());
            r5.setPodName(c2.getLabel());
            r5.setPodPricePerNight(30.00);

            r5.setExtras(new ArrayList<>(List.of(
                    "Premium Food (€8.00 per night)",
                    "Grooming (€15.00 one-off)"
            )));

            r5.setTotalPrice(167.00);
            r5.setStatus("Confirmed");

            addReservation(r5);
        }
    }


    /*
     * Finds the physical pod using its label and then returns
     * the pod's actual generated podID.
     *
     * This is the same physical pod that PodList uses.
     */
    private Pod findPodByLabel(String label) {

        if (label == null) {
            return null;
        }

        for (Pod pod : podList.getPods()) {

            if (label.equalsIgnoreCase(pod.getLabel())) {
                return pod;
            }
        }

        return null;
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
    
   public Reservation findByID(int reservationID) {
	   for (Reservation r: reservations ) {
		   if(r.getReservationID() == reservationID) {
			   return r; 
		   }
	   }return null;
   }
    
   
   //Reservations that did not result in a confirmed, paid booking can still be shown in the admin dashboard
   
   public ArrayList<Reservation>getUnsuccessfulPayments(){
	   ArrayList<Reservation> failedTxns = new ArrayList<>();		//create a new arrayList for failed transactions from Paypal 
	   for (Reservation r : reservations) {
		   String s = r.getStatus();
		   if ("Cancelled".equalsIgnoreCase(s) || "Payment Failed".equalsIgnoreCase(s) || "Expired".equalsIgnoreCase(s)) {
			   failedTxns.add(r);
		   }
	   }
	   return failedTxns;		//return the failed transactions 
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
