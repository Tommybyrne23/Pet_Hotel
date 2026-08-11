package com.tus.pethotel;

public enum ServiceCategory {

	POD("Pod / Boarding"),
	EXTRA("Extra Service");

	private final String label;

	ServiceCategory(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}