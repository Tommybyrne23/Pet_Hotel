package com.tus.services;

public enum ChargeType {

	PER_NIGHT("Per night"),
	ONE_OFF("One-off charge");

	private final String label;

	ChargeType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
