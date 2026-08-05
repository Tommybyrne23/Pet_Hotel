/*
 * think i may not need this, 
 */

package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

	/* 
	 * Delare 3 users as part of the test create a Userlist Array 
	 */
	User user1; 
//	User user2; 
//	User user3; 
	UserList userlist;
	
	@BeforeEach
	void setUp() throws Exception {
		user1 = new User("Jonathan", "a00347373@student.tus.ie", "CSE2026" ); 			// currently not added to the array
//		user2 = new User("Tommy", "a00347372@student.tus.ie", "Software" ); 			// not sure if we need these 
//		user3 = new User("Rupali", "a00347380@student.tus.ie", "Development" ); 
	}


	/* Test case to create a new user account, 
	 * success if an account with new details is created
	 * 
	 * All tests have been made from the user story tasks and acceptence criteria
	 */
	
	@Test
	@DisplayName("Test: Create a new account")
	void testCreateNewUser() {
	assertNotNull(user1, "User object should not be null after creation");
	assertEquals("Jonathan", user1.getName(), "Name Should match constructor input");
	assertEquals("a00347373@student.tus.ie", user1.getEmail(), "Email should match constructor input");
	assertEquals("CSE2026", user1.getPassword(), "Password should match constructor input");
	
	// Assert that userID was auto-generated and assigned
			assertTrue(user1.getUserID() > 0, "User ID should be greater than 0");	
	}

	/*
	 * Test to check whether an error is thrown when a user registers with an existing 
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

