package com.tus.pethotel;


public enum Species {

	DOG("Dog"),
	CAT("Cat"),
	BIRD("Bird"),
	REPTILE("Reptile"),
	FISH("Fish");

	private final String label;

	Species(String label) {
		this.label = label;
	}

	
	public String getLabel() {
		return label;
	}
	
}
