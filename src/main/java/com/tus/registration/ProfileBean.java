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

    // Temporary fields to hold the edits before the user clicks save
    private String editName;
    private String editEmail;
    private String editPassword;

    @Inject
    private LoginBean loginBean;

    /**
     * Load current user details into the form fields.
     * Called automatically when the profile page opens.
     user sees their current saved contact details.
     */
    public void loadProfile() {
        User user = loginBean.getLoggedInUser();
        if (user != null) {
            editName = user.getName();
            editEmail = user.getEmail();
            editPassword = user.getPassword();
        }
    }

//    
//     Save the updated profile details.
//     Validate fields before saving.
//      (save + success message) and (invalid input + error).
//    
    public String saveProfile() {

        FacesContext context = FacesContext.getCurrentInstance();
        boolean hasError = false;

        // VALIDATION

        // Check name is not blank
        if (editName == null || editName.trim().isEmpty()) {
            context.addMessage("profileForm:nameInput",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Name cannot be blank.", null));
            hasError = true;
        }

        // Check email format
        if (editEmail == null || !editEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            context.addMessage("profileForm:emailInput",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter a valid email address (e.g. name@example.com).", null));
            hasError = true;
        }

        // Check password length (at least 6 characters)
        if (editPassword == null || editPassword.length() < 6) {
            context.addMessage("profileForm:passwordInput",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Password must be at least 6 characters long.", null));
            hasError = true;
        }

        // If any validation failed, stop here and show all errors (AC3)
        if (hasError) {
            return null; // stay on page, errors are displayed
        }

        // --- SAVE 

        User user = loginBean.getLoggedInUser();
        user.setName(editName.trim());
        user.setEmail(editEmail.trim());
        user.setPassword(editPassword);

        // Display success message
        context.addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Your profile has been updated successfully.", null));

        return null; // stay on the profile page to show the success message
    }

    /**
     * Go back to the dashboard without saving.
     */
    public String cancel() {
        return "userDashboard?faces-redirect=true";
    }

    // --- GETTERS AND SETTERS ---

    public String getEditName() {
        return editName;
    }

    public void setEditName(String editName) {
        this.editName = editName;
    }

    public String getEditEmail() {
        return editEmail;
    }

    public void setEditEmail(String editEmail) {
        this.editEmail = editEmail;
    }

    public String getEditPassword() {
        return editPassword;
    }

    public void setEditPassword(String editPassword) {
        this.editPassword = editPassword;
    }
}
