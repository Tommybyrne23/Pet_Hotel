package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate; 			//to allow for date searches 

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("reservationSearch")
@ViewScoped
public class ReservationSearchBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject  //link this bean to the class below
    private ReservationList reservationList;
    
    @Inject
    private UserList userList;

    
    LocalDate today = LocalDate.now();		//use this reference to search for todays date
    
    // Search filter parameters
    private String searchCustomerID;		//search for all bookings by a customer
    private String searchCustomerName;		//search all bookings by a customer name
    private String searchCustomerEmail;		//search by customer email
    private String searchPetID;				//search for bookings for a specific pet - using string to search mataches  
    private String searchPetName;			//search by the name of the pet
    private String searchPodName;			//search by the PodName 
    private LocalDate searchCheckInDate;	//search the date the pet checks in 
    private LocalDate searchCheckOutDate;	//search the date the pet checks out
    


	// Holds the filtered list displayed in the UI table
    private List<Reservation> filteredReservations;

    @PostConstruct
    public void init() {
        // Initially show all reservations on page load
        if (reservationList != null) {
            filteredReservations = new ArrayList<>(reservationList.getReservations());
        } else {
            filteredReservations = new ArrayList<>();
        }
    }

    /**
     * AJAX Listener/filter methods
     * https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/faces-ajax/faces-ajax.html
     */
    public void filter() {
        if (reservationList == null || reservationList.getReservations() == null) {
            filteredReservations = new ArrayList<>();
            return;
        }

        filteredReservations = reservationList.getReservations().stream()			//brings in each fiultered response, one by one
        	.filter(r -> matchesPetID(r))
            .filter(r -> matchesPetName(r))											// r declared as a reservationList with "->"
            .filter(r -> matchesPodName(r))
            .filter(r -> matchesCustomerID(r))
            .filter(r -> matchesCustomerName(r))
            .filter(r -> matchesCustomerEmail(r))
            .filter(r -> matchesCheckInDate(r))
            .filter(r -> matchesCheckOutDate(r))
            .collect(Collectors.toList());
    }

    private boolean matchesPetID(Reservation r) {
        if (searchPetID == null || searchPetID.isBlank()) {
            return true;
        }

        String petIdStr = String.valueOf(r.getPetID());								//pet ID is an int so the search method is different 
        return petIdStr.contains(searchPetID.trim());
    }

    
    private boolean matchesPetName(Reservation r) {
    	if (searchPetName == null || searchPetName.isBlank()) {
    		return true;
    	}
    	return r.getPetName() != null && 
    			r.getPetName().toLowerCase().contains(searchPetName.trim().toLowerCase());			//search allows for partial matching (bud would match Buddy and also Buddington....should an animal be called something)
    }

    private boolean matchesPodName(Reservation r) {
        if (searchPodName == null || searchPodName.isBlank()) {
            return true;
        }
        return r.getPodName() != null && 
               r.getPodName().toLowerCase().contains(searchPodName.trim().toLowerCase());
    }
    
    private boolean matchesCustomerID(Reservation r) {
        if (searchCustomerID == null || searchCustomerID.isBlank()) {
            return true;
        }

        String userIdStr = String.valueOf(r.getUserID());
        return userIdStr.contains(searchCustomerID.trim());
    }

    
    private boolean matchesCustomerName(Reservation r) {
        if (searchCustomerName == null || searchCustomerName.isBlank()) {
            return true;
        }
        
        String customerName = userList.getUserNameById(r.getUserID());
        return customerName != null && 
               customerName.toLowerCase().contains(searchCustomerName.trim().toLowerCase());
    }
    
    
    private boolean matchesCustomerEmail(Reservation r) {
        if (searchCustomerEmail == null || searchCustomerEmail.isBlank()) {
            return true;
        }
        
        User customer = userList.findByUserID(r.getUserID());
        return customer != null && customer.getEmail() != null && 
               customer.getEmail().toLowerCase().contains(searchCustomerEmail.trim().toLowerCase());
    }

    
    
    private boolean matchesCheckInDate(Reservation r) {
    	if (searchCheckInDate == null) {
    		return true;
    	}
    	return r.getCheckInDate() != null && 
    			r.getCheckInDate().trim().equals(searchCheckInDate.toString());			//search matches against PrimeFaces LocalDate object
    }
    
    
    private boolean matchesCheckOutDate(Reservation r) {
    	if (searchCheckOutDate == null) {
    		return true;
    	}
    	return r.getCheckOutDate() != null && 
    			r.getCheckOutDate().trim().equals(searchCheckOutDate.toString());			//search matches against PrimeFaces LocalDate object
    }
    
    
    // --- GETTERS AND SETTERS ---

    public String getSearchPetName() {
        return searchPetName;
    }

    public void setSearchPetName(String searchPetName) {
        this.searchPetName = searchPetName;
    }

    public String getSearchPodName() {
        return searchPodName;
    }

    public void setSearchPodName(String searchPodName) {
        this.searchPodName = searchPodName;
    }

    public List<Reservation> getFilteredReservations() {
        return filteredReservations;
    }

    public void setFilteredReservations(List<Reservation> filteredReservations) {
        this.filteredReservations = filteredReservations;
    }
    

    public String getSearchCustomerID() {
		return searchCustomerID;
	}

	public void setSearchCustomerID(String searchCustomerID) {
		this.searchCustomerID = searchCustomerID;
	}

	public String getSearchCustomerName() {
		return searchCustomerName;
	}

	public void setSearchCustomerName(String searchCustomerName) {
		this.searchCustomerName = searchCustomerName;
	}

	public String getSearchCustomerEmail() {
		return searchCustomerEmail;
	}

	public void setSearchCustomerEmail(String searchCustomerEmail) {
		this.searchCustomerEmail = searchCustomerEmail;
	}

	public String getSearchPetID() {
		return searchPetID;
	}

	public void setSearchPetID(String searchPetID) {
		this.searchPetID = searchPetID;
	}

	public LocalDate getSearchCheckInDate() {
		return searchCheckInDate;
	}

	public void setSearchCheckInDate(LocalDate searchCheckIn) {
		this.searchCheckInDate = searchCheckIn;
	}

	public LocalDate getSearchCheckOutDate() {
		return searchCheckOutDate;
	}

	public void setSearchCheckOutDate(LocalDate searchCheckout) {
		this.searchCheckOutDate = searchCheckout;
	}
}
