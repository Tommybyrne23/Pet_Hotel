package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.*;

class LoginBeanTest {

	LoginBean loginBean;
	UserList userList;
	User user;
	
	@BeforeEach
	void setUp() throws Exception {
		userList = new UserList();
		User user1 = new User("Jonathan", "a00347373@student.tus.ie", "password");
		userList.addUser(user1);
		loginBean = new LoginBean();
		loginBean.setUserList(userList);
	}

	@Test
	void testCustomerLoginSuccess() {
		loginBean.setEmail("a00347373@student.tus.ie");
		loginBean.setPassword("password");
		String outcome = loginBean.login();
		assertEquals("userDashboard?faces-redirect=true", outcome);
		assertEquals("a00347373@student.tus.ie", loginBean.getLoggedInUser().getEmail());
	}
	
	/*
	 * Test for verifying login function when the password is incorrect
	 * Need to work out issue with jakarta faces
	 * 
	 */
	
	@Test
	void testCustomerLoginfailure() {
		loginBean.setEmail("a00347373@student.tus.ie");
		loginBean.setPassword("wrong");
		loginBean.login();
		boolean loggedInCheck = loginBean.isLoggedIn();
		assertFalse(loggedInCheck);
	}
	
	@Test
	@DisplayName("Test: Admin Logs in")
	void testAdminLogin() {
		loginBean.setEmail("admin@3apets.ie");
		loginBean.setPassword("admin123");
		String outcome = loginBean.login();
		assertEquals("adminDashboard?faces-redirect=true", outcome);
		boolean isAdmin = loginBean.isAdmin();
		assertTrue(isAdmin);
		boolean attendantCheck =loginBean.isAttendant();
		boolean customerCheck =loginBean.isCustomer();
		assertTrue(attendantCheck);
		assertTrue(customerCheck);
		}
	
	@Test
	@DisplayName("Test: User Profile fails admin checks")
	void testUserAsAdmin() {
		loginBean.setEmail("user@3apets.ie");
		loginBean.setPassword("user123");
		String outcome = loginBean.login();
		assertEquals("userDashboard?faces-redirect=true", outcome);
		boolean loggedInCheck = loginBean.isLoggedIn();
		boolean adminCheck = loginBean.isAdmin();
		boolean attendantCheck =loginBean.isAttendant();
		boolean customerCheck =loginBean.isCustomer();
		assertTrue(loggedInCheck);
		assertFalse(adminCheck);					//admin will fail
		assertFalse(attendantCheck);				//attendant will fail
		assertTrue(customerCheck);					//will return true
		
	}
	@Test
	@DisplayName("Test: User Profile fails admin checks")
	void testUserAsAttendant() {
		loginBean.setEmail("attendant@3apets.ie");
		loginBean.setPassword("attendant123");
		String outcome = loginBean.login();
		assertEquals("petAttendantDashboard?faces-redirect=true", outcome);
		boolean loggedInCheck = loginBean.isLoggedIn();
		boolean adminCheck = loginBean.isAdmin();
		boolean attendantCheck =loginBean.isAttendant();
		boolean customerCheck =loginBean.isCustomer();
		assertTrue(loggedInCheck);
		assertFalse(adminCheck);
		assertTrue(attendantCheck);
		assertTrue(customerCheck);
	}

}
