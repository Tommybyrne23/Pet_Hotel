package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int uuID = 0;

    private int reservationID;
    private int userID;          // which user made the booking
    private int petID;           // which pet is being booked
    private String petName;      // pet name for easy display
    private String checkInDate;  // format: yyyy-MM-dd
    private String checkOutDate; // format: yyyy-MM-dd
    private double totalPrice;   // calculated price
    private String status;       // "Pending", "Confirmed", "Cancelled"
    private String podName;              // the pod booked, e.g. "Dog Boarding"
    private double podPricePerNight;     // the rate at the time of booking

    // extras chosen, stored as text so the booking still reads correctly
    // if an admin later renames or reprices a service
    private List<String> extras = new ArrayList<>();

    // Default constructor required by Jakarta Faces
    public Reservation() {
        this.status = "Pending";
    }

    // Constructor with required fields
    public Reservation(int userID, int petID, String petName,
                       String checkInDate, String checkOutDate) {
        uuID++;
        this.reservationID = uuID;
        this.userID = userID;
        this.petID = petID;
        this.petName = petName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = "Pending";
    }

    // GETTERS AND SETTERS

    public int getReservationID() {
        return reservationID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPetID() {
        return petID;
    }

    public void setPetID(int petID) {
        this.petID = petID;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
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

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public double getPodPricePerNight() {
        return podPricePerNight;
    }

    public void setPodPricePerNight(double podPricePerNight) {
        this.podPricePerNight = podPricePerNight;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }
}
