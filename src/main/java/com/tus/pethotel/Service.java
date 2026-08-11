package com.tus.pethotel;

import java.io.Serializable;

public class Service implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int uuID = 0;

	private int serviceID;
	private String name;
	private String description;
	private double price;
	private ServiceCategory category = ServiceCategory.EXTRA;

	public Service() {
		uuID++;
		serviceID = uuID;
	}

	public Service(String name, String description, double price,
			ServiceCategory category) {
		this();
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
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
}