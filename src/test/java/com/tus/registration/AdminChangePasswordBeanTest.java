package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

class AdminChangePasswordBeanTest {

	AdminChangePasswordBean adminPasswordBean;
	UserList userList;
	LoginBean loginBean;
	User adminUser;
	User targetUser;

	@BeforeEach
	void setUp() {
		userList = new UserList();
		loginBean = new LoginBean();
		loginBean.setUserList(userList);
		loginBean.setEmail("admin@3apets.ie");
		loginBean.setPassword("admin123");
		loginBean.login();
		adminUser = loginBean.getLoggedInUser();

		targetUser = new User("Rupali", "rupali@tus.ie", "originalPass1");
		userList.addUser(targetUser);
		adminPasswordBean = new AdminChangePasswordBean();
		adminPasswordBean.setUserList(userList);
		adminPasswordBean.setLoginBean(loginBean);
	}

	@Test
	@DisplayName("Admin change user password and update.")
	void resettingPasswordUpdatesImmediately() {
		adminPasswordBean.getNewPasswords().put(targetUser.getUserID(), "newSecurePass1");
		adminPasswordBean.getConfirmPasswords().put(targetUser.getUserID(), "newSecurePass1");
		String outcome = adminPasswordBean.resetPassword(targetUser);
		assertNull(outcome); 
		assertEquals("newSecurePass1", targetUser.getPassword()); 
		assertFalse(adminPasswordBean.getNewPasswords().containsKey(targetUser.getUserID()));
		assertFalse(adminPasswordBean.getConfirmPasswords().containsKey(targetUser.getUserID()));
	}

	@Test
	@DisplayName("New password field is left blank then the password is not changed")
	void resettingWithBlankNewPasswordIsBlocked() {
		adminPasswordBean.getNewPasswords().put(targetUser.getUserID(), "");
		adminPasswordBean.getConfirmPasswords().put(targetUser.getUserID(), "");
		String outcome = adminPasswordBean.resetPassword(targetUser);
		assertNull(outcome);
		assertEquals("originalPass1", targetUser.getPassword()); // unchanged
	}

	@Test
	@DisplayName("new and confirm password fields do not match then the password is not changed")
	void resettingWithMismatchedConfirmationIsBlocked() {
		adminPasswordBean.getNewPasswords().put(targetUser.getUserID(), "newSecurePass1");
		adminPasswordBean.getConfirmPasswords().put(targetUser.getUserID(), "differentPass1");
		String outcome = adminPasswordBean.resetPassword(targetUser);
		assertNull(outcome);
		assertEquals("originalPass1", targetUser.getPassword()); // unchanged
	}

	@Test
	@DisplayName("admin tries to reset their own password from this page, then it is blocked")
	void adminCannotResetOwnPassword() {
		adminPasswordBean.getNewPasswords().put(adminUser.getUserID(), "someNewPass1");
		adminPasswordBean.getConfirmPasswords().put(adminUser.getUserID(), "someNewPass1");
		String originalAdminPassword = adminUser.getPassword();
		String outcome = adminPasswordBean.resetPassword(adminUser);
		assertNull(outcome);
		assertEquals(originalAdminPassword, adminUser.getPassword()); // unchanged
	}

	@Test
	@DisplayName("resetPassword is called with no target user, then it returns safely with no error")
	void resetPasswordWithNullTargetReturnsNullSafely() {
		assertNull(adminPasswordBean.resetPassword(null));
	}



	@Test
	@DisplayName("admin reset the user's password, when the user logs in with the new password, then they log in successfully")
	void userLogsInSuccessfullyWithNewlyAssignedPassword() {
		adminPasswordBean.getNewPasswords().put(targetUser.getUserID(), "newSecurePass1");
		adminPasswordBean.getConfirmPasswords().put(targetUser.getUserID(), "newSecurePass1");
		adminPasswordBean.resetPassword(targetUser);
		LoginBean userLogin = new LoginBean();
		userLogin.setUserList(userList);
		userLogin.setEmail("rupali@tus.ie");
		userLogin.setPassword("newSecurePass1");
		String outcome = userLogin.login();
		assertEquals("/userDashboard?faces-redirect=true", outcome);
		assertTrue(userLogin.isLoggedIn());
	}

	@Test
	@DisplayName("admin has reset the user's password, when anyone tries to log in with the old password, then the login attempt is rejected")
	void loginWithPreviousPasswordIsRejectedAfterReset() {
		adminPasswordBean.getNewPasswords().put(targetUser.getUserID(), "newSecurePass1");
		adminPasswordBean.getConfirmPasswords().put(targetUser.getUserID(), "newSecurePass1");
		adminPasswordBean.resetPassword(targetUser);
		LoginBean oldPasswordAttempt = new LoginBean();
		oldPasswordAttempt.setUserList(userList);
		oldPasswordAttempt.setEmail("rupali@tus.ie");
		oldPasswordAttempt.setPassword("originalPass1");
		String outcome = oldPasswordAttempt.login();
		assertNull(outcome);
		assertFalse(oldPasswordAttempt.isLoggedIn());
	}
}

