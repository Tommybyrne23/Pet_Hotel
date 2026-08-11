package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

class RegistrationBeanTest {

	RegistrationBeanTest bean;
	UserList userList;
	
	
	@BeforeEach
	void setUp() throws Exception {
		userList = new Userlist();
		bean = new RegistrationBeanTest();
		bean.setUserList(userList);
		
		
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
