package com.tus.Services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("ServiceBean")
@SessionScoped
public class ServiceBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// The id of the form in manageServices.xhtml. Message client ids are built
	// from this, so if the form id changes in the page it must change here too.
	private static final String FORM_ID = "adminAddService";

	// Form fields. Held separately from the Service object so a half-filled
	// form never becomes a half-built Service. Same pattern as ProfileBean.
	private String editName;
	private String editDescription;
	private Double editPrice;					// object, not primitive - see note below
	private ServiceCategory editCategory;
	private ChargeType editChargeType = ChargeType.PER_NIGHT; // defaults to per night so the radio always has a selection
	private List<String> editSpecies = new ArrayList<>();

	@Inject
	private ServiceList serviceList;


	//  create the service
	public String addNewService() {

		//  the page marks these required, this catches anything
		// that gets past the view layer
		if (editName == null || editName.trim().isEmpty()) {
			addError("nameInput", "A service name is required.");
			return null;
		}

		if (editPrice == null || editPrice <= 0) {
			addError("priceInput", "Enter a price greater than zero.");
			return null;
		}

		if (editCategory == null) {
			addError("categoryInput", "Choose whether this is a pod or an extra.");
			return null;
		}
		
		// A pod is the nightly boarding rate, so it is always per night
		if (editCategory == ServiceCategory.POD) {
			editChargeType = ChargeType.PER_NIGHT;
			}

		// Build the service only once the input is known to be good
		Service newService = new Service(
				editName.trim(),
				editDescription == null ? "" : editDescription.trim(),
				editPrice,
				editCategory);

		newService.setApplicableSpecies(new ArrayList<>(editSpecies));
		
		newService.setChargeType(editChargeType);
		
		boolean added = serviceList.addService(newService);

		if (!added) {
			addError("nameInput", "A service with that name already exists.");
			return null;
		}

		addSuccess(newService.getName() + " has been added to the service list.");

		return reset();
	}


	// Clears the form and reloads the page. The redirect matters - see notes.
	public String reset() {

		editName = null;
		editDescription = null;
		editPrice = null;
		editCategory = null;
		editChargeType = ChargeType.PER_NIGHT;
		editSpecies = new ArrayList<>();

		return "manageServices?faces-redirect=true";
	}


	// Fills the pod / extra dropdown
	public ServiceCategory[] getCategoryOptions() {
		return ServiceCategory.values();
	}


	// MESSAGE HELPERS

	// Attaches the message to a specific field so it appears beside that input
	private void addError(String fieldId, String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(FORM_ID + ":" + fieldId,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
		}
	}

	// null client id = a global message, shown by h:messages
	private void addSuccess(String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));

			// the redirect starts a new request, which would normally throw the
			// message away - this carries it over to the reloaded page
			context.getExternalContext().getFlash().setKeepMessages(true);
		}
	}


	// GETTERS AND SETTERS

	public String getEditName() {
		return editName;
	}

	public void setEditName(String editName) {
		this.editName = editName;
	}

	public String getEditDescription() {
		return editDescription;
	}

	public void setEditDescription(String editDescription) {
		this.editDescription = editDescription;
	}

	public Double getEditPrice() {
		return editPrice;
	}

	public void setEditPrice(Double editPrice) {
		this.editPrice = editPrice;
	}

	public ServiceCategory getEditCategory() {
		return editCategory;
	}

	public void setEditCategory(ServiceCategory editCategory) {
		this.editCategory = editCategory;
	}

	public List<String> getEditSpecies() {
		return editSpecies;
	}

	public void setEditSpecies(List<String> editSpecies) {
		this.editSpecies = editSpecies;
	}

	// used for testing
	public void setServiceList(ServiceList serviceList) {
		this.serviceList = serviceList;
	}
	
	public ChargeType getEditChargeType() {
		return editChargeType;
	}

	public void setEditChargeType(ChargeType editChargeType) {
		this.editChargeType = editChargeType;
	}
	
	// Fills the per night / one-off radio buttons
		public ChargeType[] getChargeTypeOptions() {
			return ChargeType.values();
		}
}