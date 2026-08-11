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
	
	@Test
	void testCustomerLoginfailure() {
		loginBean.setEmail("a00347373@student.tus.ie");
		loginBean.setPassword("wrong");
		String outcome = loginBean.login();
		assertNull(outcome);
		assertNotEquals("a00347373@student.tus.ie", loginBean.getLoggedInUser().getEmail());
	}
	
	@Test
	@DisplayName("Test: Admin Logs in")
	void testAdminLogin() {
		loginBean.setEmail("admin@3apets.ie");
		loginBean.setPassword("admin123");
		String outcome = loginBean.login();
		assertEquals("adminDashboard?faces-redirect=true", outcome);
		String isAdmin = loginBean.checkAdminAccess();
		assertNull(isAdmin);
		}
	
	@Test
	@DisplayName("Test: User Profile fails admin checks")
	void testUserAsAdmin() {
		loginBean.setEmail("user@3apets.ie");
		loginBean.setPassword("user123");
		String outcome = loginBean.login();
		assertEquals("userDashboard?faces-redirect=true", outcome);
		boolean loggedInCheck = longBean.isLoggedin();
		boolean AdminCheck = longBean.isLoggedin();
		
	}
	

}
