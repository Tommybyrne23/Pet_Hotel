package com.tus.Services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("serviceList")
@ApplicationScoped		// one shared copy for the whole application, same as UserList and PetList
public class ServiceList implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Service> services;

	// The species a service can be linked to - feeds the admin form checkboxes
	private static final List<String> SPECIES_OPTIONS =
			Arrays.asList("Dog", "Cat", "Bird", "Reptile", "Fish");


	public ServiceList() {

		this.services = new ArrayList<>();

		// PODS - a booking must have exactly one of these
		seed("Dog Boarding", "Spacious facilities with daily exercise and fresh water.",
				35.00, ServiceCategory.POD, "Dog");

		seed("Cat Boarding", "A quiet space with daily feeding, fresh litter and regular care.",
				30.00, ServiceCategory.POD, "Cat");

		seed("Bird Boarding", "Hygienic cages with daily feeding and cage cleaning.",
				25.00, ServiceCategory.POD, "Bird");

		seed("Reptile Boarding", "Enclosures with appropriate heating, lighting and humidity.",
				40.00, ServiceCategory.POD, "Reptile");

		seed("Aquatic Boarding", "Monitored tanks with filtration and daily feeding.",
				20.00, ServiceCategory.POD, "Fish");

		// EXTRAS - a booking can have any number of these, including none.
		// Rates match the constants currently in BookingBean.

		seed("Daily Walks", "Daily supervised walks with an experienced attendant.",
				10.00, ServiceCategory.EXTRA, "Dog");

		// no species listed = available to every pet type
		seed("Premium Food", "Upgraded meals tailored to your pet's dietary needs.",
				8.00, ServiceCategory.EXTRA);
		
		// One-off services are charged once for the whole stay, not per night
		Service grooming = seed("Grooming",
				"Full grooming session during your pet's stay.",
				15.00, ServiceCategory.EXTRA, "Dog", "Cat");
		grooming.setChargeType(ChargeType.ONE_OFF);

		Service dayOut = seed("Doggie Day Out",
				"A half-day trip out with one of our attendants.",
				45.00, ServiceCategory.EXTRA, "Dog");
		dayOut.setChargeType(ChargeType.ONE_OFF);
	}


	// Builds a starting service and adds it. The "..." lets me pass no species,
	// one species, or several, without writing a list at every call.
	private Service seed(String name, String description, double price,
			ServiceCategory category, String... species) {

		Service service = new Service(name, description, price, category);
		service.setApplicableSpecies(new ArrayList<>(Arrays.asList(species)));
		services.add(service);

		return service;			// so the caller can tag it if it isn't per-night
	}


	public int getNumberOfServices() {
		return services.size();
	}


	// A service name must be unique
	public boolean isNameTaken(String name) {

		if (name == null) {
			return false;
		}

		for (Service existing : services) {
			if (name.trim().equalsIgnoreCase(existing.getName())) {
				return true;
			}
		}
		return false;
	}


	// adds the service only if it has a name and the name is free
	public boolean addService(Service service) {

		if (service == null || service.getName() == null
				|| service.getName().trim().isEmpty()) {
			return false;
		}

		if (isNameTaken(service.getName())) {
			return false;
		}

		services.add(service);
		return true;
	}

	// for selectManyCheckbox on the admin form
	public List<String> getSpeciesOptions() {
		return SPECIES_OPTIONS;
	}


	public ArrayList<Service> getServices() {
		return services;
	}
	
	public List<Service> getServicesForSpecies(String species) {

	    List<Service> matchingServices = new ArrayList<>();

	    for (Service service : services) {

	        // No species means the service is available to every pet type
	        if (service.getApplicableSpecies() == null
	                || service.getApplicableSpecies().isEmpty()) {

	            matchingServices.add(service);
	            continue;
	        }

	        // Add the service if it applies to this species
	        if (service.getApplicableSpecies().contains(species)) {
	            matchingServices.add(service);
	        }
	    }

	    return matchingServices;
	}

	// the booking page needs pods and extras separately, and only the
	// ones that suit the pet being booked
	public List<Service> getPodsForSpecies(String species) {
		return findByCategoryAndSpecies(ServiceCategory.POD, species);
	}

	public List<Service> getExtrasForSpecies(String species) {
		return findByCategoryAndSpecies(ServiceCategory.EXTRA, species);
	}

	private List<Service> findByCategoryAndSpecies(ServiceCategory category, String species) {

		List<Service> matches = new ArrayList<>();

		for (Service service : services) {

			// an empty species list means the service suits every pet
			boolean speciesOk = service.getApplicableSpecies() == null
					|| service.getApplicableSpecies().isEmpty()
					|| service.getApplicableSpecies().contains(species);

			if (service.getCategory() == category && speciesOk) {
				matches.add(service);
			}
		}
		return matches;
	}
	
	public Service findByID(int serviceID) {

		for (Service service : services) {
			if (service.getServiceID() == serviceID) {
				return service;
			}
		}
		return null;
	}
}