package com.tus.Services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Service implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int uuID = 0;

	private int serviceID;
	private String name;
	private String description;
	private double price;
	private ServiceCategory category = ServiceCategory.EXTRA;
	private ChargeType chargeType = ChargeType.PER_NIGHT;

	public Service() {
	    
	}

	public Service(String name, String description, double price,
	        ServiceCategory category) {
	    uuID++;
	    this.serviceID = uuID;
	    this.name = name;
	    this.description = description;
	    this.price = price;
	    this.category = category;    
	}

	private List<String> applicableSpecies = new ArrayList<>();

	public List<String> getApplicableSpecies() {
	    return applicableSpecies;
	}

	public void setApplicableSpecies(List<String> applicableSpecies) {
	    this.applicableSpecies = applicableSpecies;
	}
	// GETTERS AND SETTERS

	public int getServiceID() {
		return serviceID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public ServiceCategory getCategory() {
		return category;
	}

	public void setCategory(ServiceCategory category) {
		this.category = category;
	}

	public ChargeType getChargeType() {
		return chargeType;
	}

	public void setChargeType(ChargeType chargeType) {
		this.chargeType = chargeType;
	}
}