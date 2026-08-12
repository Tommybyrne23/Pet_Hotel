package com.tus.registration;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import jakarta.enterprise.context.RequestScoped; 
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.tus.pethotel.ReservationList;
import com.tus.pethotel.Reservation;

@Named("AdminBookingBean")
@RequestScoped
public class AdminViewBookingBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String selectedFilter = "ALL"; // Options: "ALL", "UPCOMING", "ONSITE", "PAST"

    @Inject
    private ReservationList reservationList;

    public ArrayList<Reservation> getFilteredReservations() {
        if (reservationList == null || reservationList.getReservations() == null) {
            return new ArrayList<>();
        }

        ArrayList<Reservation> allReservations = reservationList.getReservations();

        if ("ALL".equalsIgnoreCase(selectedFilter)) {
            return allReservations;
        }

        ArrayList<Reservation> filtered = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Reservation res : allReservations) {
            // Prevent NullPointerException on empty date strings
            if (res == null || res.getCheckInDate() == null || res.getCheckOutDate() == null 
                    || res.getCheckInDate().isBlank() || res.getCheckOutDate().isBlank()) {
                continue;
            }

            try {
                LocalDate checkIn = LocalDate.parse(res.getCheckInDate().trim());
                LocalDate checkOut = LocalDate.parse(res.getCheckOutDate().trim());

                switch (selectedFilter.toUpperCase()) {
                    case "UPCOMING":
                        if (checkIn.isAfter(today)) { filtered.add(res); }
                        break;
                    case "ONSITE":
                        if (!checkIn.isAfter(today) && !checkOut.isBefore(today)) { filtered.add(res); }
                        break;
                    case "PAST":
                        if (checkOut.isBefore(today)) { filtered.add(res); }
                        break;
                }
            } catch (DateTimeParseException e) {
                // Ignore malformed dates
            }
        }

        return filtered;
    }

    public int getFilteredCount() {
        return getFilteredReservations().size();
    }

    // Getters and Setters
    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }
}
