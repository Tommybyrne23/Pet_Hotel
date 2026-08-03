/*
 * think i may not need this, 
 */

package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

	User user1; 
	User user2; 
	User user3; 
	
	@BeforeEach
	void setUp() throws Exception {
		user1 = new User("Jonathan", "a00347373@student.tus.ie", "CSE2026" ); 
		user2 = new User("Tommy", "a00347372@student.tus.ie", "Software" ); 
		user3 = new User("Rupali", "a00347380@student.tus.ie", "Development" ); 
	}


	/* Test case to create a new user account, 
	 * success if an account with new details is created
	 * 
	 * All tests have been made from the user story tasks and acceptence criteria
	 */
	@Test
	@DisplayName("Test: Create a new account")
	void testCreateNewUser() {
		user4 = new User("Markus","a00311226@student.tus.ie", "AthloneTUS" );
		
	}

	/*
	 * Test to check whether an error is thrown when 
	 */
	@Test
	@DisplayName("Test: Duplicate email ")
	void testDuplicateEmail() {
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

