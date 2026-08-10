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
		assertEquals("userHomepage?faces-redirect=true", outcome);
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

}
