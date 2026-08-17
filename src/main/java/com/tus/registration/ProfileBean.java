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
 
    // Profile fields
    private String editName;
    private String editEmail;
 
    // Password change fields
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
 
    @Inject
    private LoginBean loginBean;
 
    // centralised helper - every FacesContext-touching method routes through
    // this (mirrors LoginBean.addFacesMessage), so calling these methods
    // outside a real Faces request (e.g. in a unit test) no longer throws
    // a NullPointerException
    private void addFacesMessage(String clientId, FacesMessage.Severity severity, String summary) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(clientId, new FacesMessage(severity, summary, null));
        }
    }
 
    /**
     * Load current user details into the form fields.
     * Called when the profile page opens.
     */
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
 
    /**
     * Save updated name and email only.
     */
    public String saveProfile() {
 
        boolean hasError = false;
 
        // Validate name is not blank
        if (editName == null || editName.trim().isEmpty()) {
            addFacesMessage("profileForm:nameInput", FacesMessage.SEVERITY_ERROR,
                "Name cannot be blank.");
            hasError = true;
        }
 
        // Validate email format
        if (editEmail == null || !editEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            addFacesMessage("profileForm:emailInput", FacesMessage.SEVERITY_ERROR,
                "Please enter a valid email (e.g. name@example.com).");
            hasError = true;
        }
 
        if (hasError) {
            return null;
        }
 
        // Update the user
        User user = loginBean.getLoggedInUser();
        user.setName(editName.trim());
        user.setEmail(editEmail.trim());
 
        addFacesMessage(null, FacesMessage.SEVERITY_INFO,
            "Your profile has been updated successfully.");
 
        return null;
    }
 
    /**
     * Change password with old password verification.
     * Uses PasswordValidator to check new and confirm match.
     */
    public String changePassword() {
 
        User user = loginBean.getLoggedInUser();
        boolean hasError = false;
 
        // 1. Check old password is entered
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            addFacesMessage("profileForm:oldPasswordInput", FacesMessage.SEVERITY_ERROR,
                "Please enter your current password.");
            hasError = true;
        }
 
        // 2. Check old password matches the stored password
        if (!hasError && !user.getPassword().equals(oldPassword)) {
            addFacesMessage("profileForm:oldPasswordInput", FacesMessage.SEVERITY_ERROR,
                "Current password is incorrect.");
            hasError = true;
        }
 
        // 3. Check new password is at least 6 characters
        if (newPassword == null || newPassword.length() < 6) {
            addFacesMessage("profileForm:newPasswordInput", FacesMessage.SEVERITY_ERROR,
                "New password must be at least 6 characters long.");
            hasError = true;
        }
 
        // 4. Check new password and confirm match using PasswordValidator
        if (!PasswordValidator.checkPasswordsMatch(newPassword, confirmPassword)) {
            addFacesMessage("profileForm:confirmPasswordInput", FacesMessage.SEVERITY_ERROR,
                "New password and confirm password do not match.");
            hasError = true;
        }
 
        // 5. Check new password is different from old password
        if (!hasError && newPassword.equals(oldPassword)) {
            addFacesMessage("profileForm:newPasswordInput", FacesMessage.SEVERITY_ERROR,
                "New password must be different from your current password.");
            hasError = true;
        }
 
        if (hasError) {
            return null;
        }
 
        // All checks passed - update the password
        user.setPassword(newPassword);
 
        // Clear the password fields
        oldPassword = null;
        newPassword = null;
        confirmPassword = null;
 
        addFacesMessage(null, FacesMessage.SEVERITY_INFO,
            "Your password has been changed successfully.");
 
        return null;
    }
 
    /**
     * Cancel and go back to dashboard.
     */
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
 
    // test-support setter, mirrors BookingBean.setPodList() / LoginBean.setUserList()
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
 
}
 
