package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

class ProfileBeanTest {

	ProfileBean profileBean;
	LoginBean loginBean;
	UserList userList;
	User loggedInUser;

	@BeforeEach
	void setUp() throws Exception {
		userList = new UserList();
		User customer = new User("Rupali", "rupali@tus.ie", "password");
		userList.addUser(customer);

		loginBean = new LoginBean();
		loginBean.setUserList(userList);
		loginBean.setEmail("rupali@tus.ie");
		loginBean.setPassword("password");
		loginBean.login();
		loggedInUser = loginBean.getLoggedInUser();

		profileBean = new ProfileBean();
		profileBean.setLoginBean(loginBean);
	}

	@Test
	@DisplayName("Test the user information visible")
	void loadProfileShowsCurrentSavedDetails() {
		profileBean.loadProfile();
		assertEquals("Rupali", profileBean.getEditName());
		assertEquals("rupali@tus.ie", profileBean.getEditEmail());
	}

	@Test
	@DisplayName("details updated after user enter new details")
	void savingValidDetailsUpdatesProfileImmediately() {
		profileBean.loadProfile();
		profileBean.setEditName("Rupali Motwani");
		profileBean.setEditEmail("rupali.motwani@tus.ie");
		String outcome = profileBean.saveProfile();
		assertNull(outcome); // stays on the same page, no redirect needed
		assertEquals("Rupali Motwani", loggedInUser.getName());
		assertEquals("rupali.motwani@tus.ie", loggedInUser.getEmail());
	}

	@Test
	@DisplayName("invalid email format")
	void savingInvalidEmailFormatBlocksTheUpdate() {
		profileBean.loadProfile();
		String originalEmail = loggedInUser.getEmail();
		profileBean.setEditName("Rupali");
		profileBean.setEditEmail("rupali.tus.ie"); // missing "@" - invalid format
		String outcome = profileBean.saveProfile();
		assertNull(outcome);
		assertEquals(originalEmail, loggedInUser.getEmail());
	}

	@Test
	@DisplayName("Given a blank name, when the customer saves, then the update is blocked")
	void savingBlankNameBlocksTheUpdate() {
		profileBean.loadProfile();
		String originalName = loggedInUser.getName();
		profileBean.setEditName("   ");
		profileBean.setEditEmail("rupali@tus.ie");
		String outcome = profileBean.saveProfile();
		assertNull(outcome);
		assertEquals(originalName, loggedInUser.getName());
	}
	@Test
	@DisplayName("Matching password")
	void changingPasswordWithValidMatchingNewPasswordSucceeds() {
		profileBean.setOldPassword("password");       
		profileBean.setNewPassword("newPass123@");     
		profileBean.setConfirmPassword("newPass123@"); 
		String outcome = profileBean.changePassword();
		assertNull(outcome);
		assertEquals("newPass123@", loggedInUser.getPassword());
		assertNull(profileBean.getOldPassword());
		assertNull(profileBean.getNewPassword());
		assertNull(profileBean.getConfirmPassword());
	}

	@Test
	@DisplayName("New password and confirm password do not match")
	void changingPasswordWithMismatchedConfirmationIsBlocked() {
		profileBean.setOldPassword("password");
		profileBean.setNewPassword("newPass123@");
		profileBean.setConfirmPassword("differentPass456"); 
		String outcome = profileBean.changePassword();
		assertNull(outcome);
		assertEquals("password", loggedInUser.getPassword()); 
	}

	@Test
	@DisplayName("new password shorter than 6 characters the password is not changed")
	void changingPasswordWithNewPasswordTooShortIsBlocked() {
		// Given
		profileBean.setOldPassword("password");
		profileBean.setNewPassword("abc");   
		profileBean.setConfirmPassword("abc");
		String outcome = profileBean.changePassword();
		assertNull(outcome);
		assertEquals("password", loggedInUser.getPassword()); 
	}

	@Test
	@DisplayName("new password identical to the current password then the password is not changed")
	void changingPasswordSameAsCurrentIsBlocked() {
		profileBean.setOldPassword("password");
		profileBean.setNewPassword("password");
		profileBean.setConfirmPassword("password");
		String outcome = profileBean.changePassword();
		assertNull(outcome);
		assertEquals("password", loggedInUser.getPassword()); 
	}

	@Test
	@DisplayName("Given the current password entered is incorrect then the password is not changed")
	void changingPasswordWithWrongCurrentPasswordIsBlocked() {
		profileBean.setOldPassword("wrongCurrentPassword");
		profileBean.setNewPassword("newPass123@");
		profileBean.setConfirmPassword("newPass123@");
		String outcome = profileBean.changePassword();
		assertNull(outcome);
		assertEquals("password", loggedInUser.getPassword()); // unchanged
	}

	@Test
	@DisplayName("Given the current password field is left blankmthen the password is not changed")
	void changingPasswordWithBlankCurrentPasswordIsBlocked() {
		profileBean.setOldPassword("");
		profileBean.setNewPassword("newPass123@");
		profileBean.setConfirmPassword("newPass123@");
		String outcome = profileBean.changePassword();
		assertNull(outcome);
		assertEquals("password", loggedInUser.getPassword()); // unchanged
	}
}



