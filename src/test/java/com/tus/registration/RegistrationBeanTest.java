package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.tus.pethotel.User;
import com.tus.pethotel.UserList;


class RegistrationBeanTest {

	RegistrationBean bean;
	UserList userList;
	
	
	@BeforeEach
	void setUp() throws Exception {
		userList = new UserList();
		bean = new RegistrationBean();
		bean.setUserList(userList);
		bean.init();
		
	}

	@Test
	@DisplayName("Test: init creates a blank user to bind the form")
	void testInit() {
		assertNotNull(bean.getUser());	
	}
	@Test
	@DisplayName("Test: registerring a new accounts is successfull and redirects to login")
	void testAccountRegistrationSuccess() {
		bean.getUser().setName("Mary");
		bean.getUser().setEmail("mary@gmail.com");
		bean.getUser().setPassword("pass123");
		bean.setConfirmPassword("pass123");				//had to add this function to create the user
		String outcome = bean.register();
		assertEquals("login?faces-redirect=true", outcome);
		assertNotNull(userList.findByEmail("mary@gmail.com"));	//check to see if the email has been stored in the array. If it hasn't then the usser hasn't been added.
		
	
	}
	
	@Test
	@DisplayName("Test: Signup fails because email is already registered ")
	void testDuplicateEmailSignup() {
		assertNotNull(userList.findByEmail("admin@3apets.ie"));	//confirming the email address is already in the list 
		bean.getUser().setName("Admin");				
		bean.getUser().setEmail("admin@3apets.ie");
		bean.getUser().setPassword("pass123");
		bean.setConfirmPassword("pass123");	
		String outcome = bean.register(); 						//test the register bean function, return the value as a string for testing. 
		assertNull(outcome);									// if successful this would be a redirection link
		 
	}
	@Test
	@DisplayName("Test: Signup fails because passwords don't match")
	void testPasswordCheckFail() {
		bean.getUser().setName("Thiago");				
		bean.getUser().setEmail("thiago@tus.ie");
		bean.getUser().setPassword("pass123");
		bean.setConfirmPassword("pass124");	
		String outcome = bean.register(); 						//test the register bean function, return the value as a string for testing. 
		assertNull(outcome);									// if successful this would be a redirection link
		assertNull(userList.findByEmail("thiago@tus.ie"));			//confirms user hasn't been added to the list 
	}
	@Test
	@DisplayName("Test: Blank Name Field")
	void testBlankNameField() {
		bean.getUser().setName("");				
		bean.getUser().setEmail("thiago@tus.ie");
		bean.getUser().setPassword("pass123");
		bean.setConfirmPassword("pass123");	
		String outcome = bean.register(); 						//test the register bean function, return the value as a string for testing. 
		assertNull(outcome);									// if successful this would be a redirection link
		assertNull(userList.findByEmail("thiago@tus.ie"));			//confirms user hasn't been added to the list 
	}
	@Test
	@DisplayName("Test: Blank Email Field")
	void testBlankEmailField() {
		bean.getUser().setName("Thiago");				
		bean.getUser().setEmail("");							
		bean.getUser().setPassword("pass123");				
		bean.setConfirmPassword("pass123");	
		String outcome = bean.register(); 						//test the register bean function, return the value as a string for testing. 
		assertNull(outcome);									// if successful this would be a redirection link
		assertNull(userList.findByEmail(""));			//confirms user hasn't been added to the list 
	}
	
	@Test
	@DisplayName("Test: Text in Email Field but no @")
	void testEmailHasTextNoAT() {
		bean.getUser().setName("Thiago");				
		bean.getUser().setEmail("thiago.tus.ie");						
		bean.getUser().setPassword("pass123");				
		bean.setConfirmPassword("pass123");	
		String outcome = bean.register(); 						//test the register bean function, return the value as a string for testing. 
		assertNull(outcome);									// if successful this would be a redirection link
		assertNull(userList.findByEmail(""));			//confirms user hasn't been added to the list 
	}
	
	@Test
	@DisplayName("Test: Reset Form Functionality")
	void testResetForm(){
		bean.getUser().setName("Tommy");
		bean.getUser().setEmail("tommy@birr.com");
		bean.getUser().setPassword("birrisgreat");
		bean.setConfirmPassword("birrisgreat");
		
		User firstEntry = bean.getUser();					//record the entries we're putting in the bean on the form 
		
		//reset the form 
		String outcome = bean.reset();
		
		//results
		assertEquals("registration?faces-redirect=true", outcome);
		assertNull(bean.getConfirmPassword());//confirm the password has been reset 
		assertNull(bean.getUser().getName());
		assertNull(bean.getUser().getEmail());
		assertNull(bean.getUser().getPassword());
		assertNotSame(firstEntry, bean.getUser());			//confirming the reset values have wiped the entries we put in earlier 

	}
	
}
