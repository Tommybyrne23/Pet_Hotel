/*
 * think i may not need this, 
 */

package com.tus.pethotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.registration.RegistrationBean;

class UserTest {

	User user1; 

	UserList userList;
	RegistrationBean bean;
	
	@BeforeEach
	void setUp() {
		bean = new RegistrationBean();
		userList  = new UserList();
		user1 = new User("Jonathan", "a00347373@student.tus.ie", "CSE2026" ); 			// currently not added to the array
	}


	@Test
	@DisplayName("Test: Adding a new user succeeds")
	void testAddUser(){
		boolean added = userList.addUser(user1);
		assertTrue(added, "A new email should be added");
		assertEquals(4, userList.getNumberOfUsers());
	}
	

	@Test
	@DisplayName("Test: Duplicate email is rejected")
	void testDuplicateEmail() {
		userList.addUser(user1);
		boolean addedAgain = userList.addUser(new User("Jon", "a00347373@student.tus.ie", "pw2"));
		assertFalse(addedAgain, "A duplicate (case-insensitive) email must be rejected");
		assertEquals(4, userList.getNumberOfUsers());
	}
	

	@Test
	@DisplayName("Test: New users default to the CUSTOMER role")
	void testDefaultRoleIsCustomer() {
		userList.addUser(user1);
	assertEquals(Role.CUSTOMER, user1.getRole(), "A new user should be a CUSTOMER by default");

	}


	@Test
	@DisplayName("Test: Setters update the user's fields")
	void testSetters() {
		user1.setName("Tommy");
		user1.setEmail("tommy@student.tus.ie");
		user1.setPassword("newpass");
		assertEquals("Tommy", user1.getName());
		assertEquals("tommy@student.tus.ie", user1.getEmail());
		assertEquals("newpass", user1.getPassword());
	}

	@Test
	@DisplayName("Test: Unique IDs increment between users")
	void testUniqueIds() {
		User user2 = new User("Rupali", "rupali@student.tus.ie", "dev");
		userList.addUser(user2);
		assertTrue(user2.getUserID() > user1.getUserID(), "Each new user gets a higher id");
	}
	
	@Test
	@DisplayName("Test: Create an Admin account")
	void testAdminAccount() {
	User user2 = new User ("Markus", "markus@gmail.com", "Zaks", Role.ADMIN);
	assertEquals(Role.ADMIN, user2.getRole());	
	}
	
	@Test
	@DisplayName("Test: Create a Pet Attendant account")
	void testAttendantAccount() {
		User user2 = new User ("Markus", "markus@gmail.com", "Zaks", Role.ATTENDANT);
		assertEquals(Role.ATTENDANT, user2.getRole());	
	}
	
	@Test
	@DisplayName("Test: Change user account type from customer to Admin")
	void testChangeUserRole() {
		assertEquals(Role.CUSTOMER,user1.getRole());
		user1.setRole(Role.ADMIN);
		assertEquals(Role.ADMIN,user1.getRole());
	}
	
}