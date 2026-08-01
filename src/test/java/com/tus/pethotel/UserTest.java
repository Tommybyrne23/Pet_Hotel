package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

	User user; 
	
	@BeforeEach
	void setUp() throws Exception {
		user = new User("jonathn"); 
	}


	/* Test case to create a new user account, 
	 * success if an account with new details is created
	 * 
	 * All tests have been made from the user story tasks and acceptence criteria
	 */
	@Test
	@DisplayName("Test: Create a new account")
	void createUser() {
		user("Jonathan", String "a00347373@student.tus.ie", String "087654321", "3 Shiny Shell, Puffin Rock, Dublin", "P113R0CK", false);
		fail("Not yet implemented");
	}

	/*
	 * Test to check whether an error is thrown when 
	 */
	@Test
	@DisplayName("Test: Duplicate email ")
	void registeredEmail() {
		fail("Not yet implemented");
	}

	/*
	 * Input field validation
	 */
	
	@Test
	@DisplayName("Test: Input Name missing validation")
	void inputValidation() {
		fail("Not yet implemented");
	}
	
	
	
	/*
	 * User redirection test
	 */
	
	@Test
	@DisplayName("Test: Redirection function after successful account creation")
	void accountRedirect() {
		fail("Not yet implemented");
	}
	
}// class closure
/*
 * Lambda exception example https://www.youtube.com/watch?v=vZm0lHciFsQ
 * 
 * using letter grade. 
 * 
 * var grader = new Grader();
 * assertThrows(IllegalArgumentException.class, 
 * 			() -> {
 * 					grader.determineLetterGrade(-1);
 * 			});
 * 
 */

