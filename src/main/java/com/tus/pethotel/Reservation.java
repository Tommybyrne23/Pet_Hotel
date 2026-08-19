package com.tus.pethotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private int podID;          // the physical pod allocated to this stay

    // Payment / hold tracking
    private LocalDateTime holdExpiry;      // pod held until this time while the user pays
    private String paypalOrderId;          // PayPal order id, for admin reconciliation
    private String failureReason;          // why a payment ended up cancelled/failed/expired
    private LocalDateTime statusUpdatedAt; // when the status last changed
    private String paypalCaptureId;   		// this ID is captured when payment is confirmed 
    										//and then needed if we need to issue a refund later, 
    										//this value is sent to the refund processor. 

  
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
    
    public int getPodID() {
        return podID;
    }

    public void setPodID(int podID) {
        this.podID = podID;
    }

    public LocalDateTime getHoldExpiry() {
        return holdExpiry;
    }

    public void setHoldExpiry(LocalDateTime holdExpiry) {
        this.holdExpiry = holdExpiry;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    // A pod is "held" only while the booking is Pending and the 15-minute window is still open.
    public boolean isHoldActive() {
        return "Pending".equalsIgnoreCase(status)
                && holdExpiry != null
                && holdExpiry.isAfter(LocalDateTime.now());
    }

    public String getPaypalCaptureId(){ 
    	return paypalCaptureId; 
    }

    public void   setPaypalCaptureId(String id) {
    	this.paypalCaptureId = id; 
    }
    
/* -----------------------------------------
 *
 * Formatting for date and time so it is 
 * displayed in the user pages
 * in a clearer manner
 * 
 ------------------------------------------*/
public String getFormattedCheckInDate() {
    return formatFriendlyDate(this.checkInDate);
}

public String getFormattedCheckOutDate() {
    return formatFriendlyDate(this.checkOutDate);
}

private String formatFriendlyDate(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) {
        return "";
    }
    try {
        LocalDate date = LocalDate.parse(dateStr.trim());
        int day = date.getDayOfMonth();
        String suffix = getOrdinalSuffix(day);
        String monthYear = date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
        
        return day + suffix + " " + monthYear; // Returns "12th Aug 2026"
    } catch (Exception e) {
        return dateStr; // Fallback to raw string if parsing fails
    }
}

private String getOrdinalSuffix(int day) {
    if (day >= 11 && day <= 13) {
        return "th";
    }
    switch (day % 10) {
        case 1:  return "st";
        case 2:  return "nd";
        case 3:  return "rd";
        default: return "th";
    }
}
    
}