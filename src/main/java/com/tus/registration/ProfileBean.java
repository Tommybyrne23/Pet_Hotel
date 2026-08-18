package com.tus.registration;

import java.io.Serializable;

import com.tus.pethotel.User;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("ProfileBean")
@SessionScoped
public class ProfileBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String editName;
	private String editEmail;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;

	@Inject
	private LoginBean loginBean;
	private void addFacesMessage(String clientId, FacesMessage.Severity severity, String summary) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			context.addMessage(clientId, new FacesMessage(severity, summary, null));
		}
	}


	public void loadProfile() {
		User user = loginBean.getLoggedInUser();
		if (user != null) {
			editName = user.getName();
			editEmail = user.getEmail();
			// Password fields stay blank - user must type them fresh
			oldPassword = null;
			newPassword = null;
			confirmPassword = null;
		}
	}

	public String saveProfile() {

		boolean hasError = false;

		if (editName == null || editName.trim().isEmpty()) {
			addFacesMessage("profileForm:nameInput", FacesMessage.SEVERITY_ERROR,
					"Name cannot be blank.");
			hasError = true;
		}

		if (editEmail == null || !editEmail.matches(
				"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			addFacesMessage("profileForm:emailInput", FacesMessage.SEVERITY_ERROR,
					"Please enter a valid email (e.g. name@example.com).");
			hasError = true;
		}

		if (hasError) {
			return null;
		}

		User user = loginBean.getLoggedInUser();
		user.setName(editName.trim());
		user.setEmail(editEmail.trim());

		addFacesMessage(null, FacesMessage.SEVERITY_INFO,
				"Your profile has been updated successfully.");

		return null;
	}

	public String changePassword() {

		User user = loginBean.getLoggedInUser();
		boolean hasError = false;

		if (oldPassword == null || oldPassword.trim().isEmpty()) {
			addFacesMessage("profileForm:oldPasswordInput", FacesMessage.SEVERITY_ERROR,
					"Please enter your current password.");
			hasError = true;
		}

		if (!hasError && !user.getPassword().equals(oldPassword)) {
			addFacesMessage("profileForm:oldPasswordInput", FacesMessage.SEVERITY_ERROR,
					"Current password is incorrect.");
			hasError = true;
		}

		if (newPassword == null || newPassword.length() < 6) {
			addFacesMessage("profileForm:newPasswordInput", FacesMessage.SEVERITY_ERROR,
					"New password must be at least 6 characters long.");
			hasError = true;
		}


		if (!PasswordValidator.checkPasswordsMatch(newPassword, confirmPassword)) {
			addFacesMessage("profileForm:confirmPasswordInput", FacesMessage.SEVERITY_ERROR,
					"New password and confirm password do not match.");
			hasError = true;
		}

		if (!hasError && newPassword.equals(oldPassword)) {
			addFacesMessage("profileForm:newPasswordInput", FacesMessage.SEVERITY_ERROR,
					"New password must be different from your current password.");
			hasError = true;
		}

		if (hasError) {
			return null;
		}


		user.setPassword(newPassword);


		oldPassword = null;
		newPassword = null;
		confirmPassword = null;

		addFacesMessage(null, FacesMessage.SEVERITY_INFO,
				"Your password has been changed successfully.");

		return null;
	}

	public String cancel() {
		return "/userDashboard?faces-redirect=true";
	}

	// --- GETTERS AND SETTERS ---

	public String getEditName() { return editName; }
	public void setEditName(String editName) { this.editName = editName; }

	public String getEditEmail() { return editEmail; }
	public void setEditEmail(String editEmail) { this.editEmail = editEmail; }

	public String getOldPassword() { return oldPassword; }
	public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

	public String getNewPassword() { return newPassword; }
	public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

	public String getConfirmPassword() { return confirmPassword; }
	public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

}

