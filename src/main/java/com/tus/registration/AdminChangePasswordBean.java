package com.tus.registration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/*
 * Lets an admin set a new password for another user from the registered
 * users page.
 */
@Named("AdminPasswordBean")
@ViewScoped
public class AdminChangePasswordBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<Integer, String> newPasswords = new HashMap<>();
	private Map<Integer, String> confirmPasswords = new HashMap<>();

	@Inject
	private UserList userList;

	@Inject
	private LoginBean loginBean;

	/*
	 * An admin cannot reset their own password here. With a single admin
	 * account and an in-memory store, a typo would lock the only admin out
	 * with no way back in short of restarting the server. The page hides
	 * the fields for the logged-in admin, but the rule belongs here too
	 * rather than relying on the page to enforce it.
	 */
	public boolean isSelf(User user) {

		if (user == null || loginBean == null) {
			return false;
		}

		User loggedIn = loginBean.getLoggedInUser();

		return loggedIn != null && loggedIn.getUserID() == user.getUserID();
	}

	public String resetPassword(User target) {

		if (target == null) {
			return null;
		}

		if (isSelf(target)) {
			addError("You cannot reset your own password from this page.");
			return null;
		}

		String newPassword = newPasswords.get(target.getUserID());
		String confirmPassword = confirmPasswords.get(target.getUserID());

		// Checked before the match test because two blank entries match
		// each other and would otherwise pass
		if (newPassword == null || newPassword.trim().isEmpty()) {
			addError("A new password is required for " + target.getName() + ".");
			return null;
		}

		if (!PasswordValidator.checkPasswordsMatch(newPassword, confirmPassword)) {
			addError("The passwords entered for " + target.getName() + " do not match.");
			return null;
		}

		if (!userList.updatePassword(target.getUserID(), newPassword)) {
			addError("The password for " + target.getName() + " could not be updated.");
			return null;
		}

		clearEntriesFor(target.getUserID());

		addSuccess("The password for " + target.getName()
				+ " has been updated. They can log in with it straight away.");

		// null keeps the admin on the page with the confirmation showing
		return null;
	}

	// Clears both fields for one row so the next render comes up empty
	private void clearEntriesFor(int userID) {
		newPasswords.remove(userID);
		confirmPasswords.remove(userID);
	}

	// MESSAGE HELPERS

	private void addSuccess(String summary) {
		addFacesMessage(FacesMessage.SEVERITY_INFO, summary);
	}

	private void addError(String summary) {
		addFacesMessage(FacesMessage.SEVERITY_ERROR, summary);
	}

	private void addFacesMessage(FacesMessage.Severity severity, String summary) {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context != null) {
			context.addMessage(null, new FacesMessage(severity, summary, null));
		}
	}

	// GETTERS AND SETTERS

	public Map<Integer, String> getNewPasswords() {
		return newPasswords;
	}

	public Map<Integer, String> getConfirmPasswords() {
		return confirmPasswords;
	}

	// used for testing
	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
}
