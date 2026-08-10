package com.tus.pethotel;


public enum Role {

	CUSTOMER("Customer"),
	ADMIN("Administrator"),
	ATTENDANT("Pet Attendant");

	private final String label;

	Role(String label) {
		this.label = label;
	}

	// friendly text for the UI (e.g. the admin user table)
	public String getLabel() {
		return label;
	}
}
