package com.tus.pethotel;

import java.io.Serializable;

public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int uuID = 0;

    private int reservationID;
    private int userID;          // which user made the booking
    private int petID;           // which pet is being booked
    private String petName;      // pet name for easy display
    private String checkInDate;  // format: yyyy-MM-dd
    private String checkOutDate; // format: yyyy-MM-dd
    private boolean grooming;    // optional service
    private boolean walks;       // optional service
    private boolean premiumFood; // optional service
    private double totalPrice;   // calculated price
    private String status;       // "Pending", "Confirmed", "Cancelled"

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

    public boolean isGrooming() {
        return grooming;
    }

    public void setGrooming(boolean grooming) {
        this.grooming = grooming;
    }

    public boolean isWalks() {
        return walks;
    }

    public void setWalks(boolean walks) {
        this.walks = walks;
    }

    public boolean isPremiumFood() {
        return premiumFood;
    }

    public void setPremiumFood(boolean premiumFood) {
        this.premiumFood = premiumFood;
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
}
