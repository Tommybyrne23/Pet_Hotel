package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountRegistrationTest {

	AccountRegistration accountRegistration; 
	
	@BeforeEach
	void setUp() throws Exception {
		accountRegistration = new AccountRegistration(); 
	}


	/* Test case to create a new user account, success if an account wiht new details is created
	 */
	@Test
	@DisplayName("Test: Create a new account")
	void createUser() {
		fail("Not yet implemented");
	}

	/*
	 * Test to check whether an error is thrown when 
	 */
	@Test
	@DisplayName("Test: Duplicate email ")
	void registeredEmail() {

	}
	
	/*
	 * User redirection test
	 */
	
	@Test
	@DisplayName("Test: Redirection function after successful accoutn creation")
	void accountRedirect() {

	}

	/*
	 * Input field validation
	 */
	
	@Test
	@DisplayName("Test: Input field validation")
	void inputValidation() {

	}
	
	
}// class closure
