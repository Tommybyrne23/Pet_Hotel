package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.ContactMessage;
import com.tus.pethotel.ContactMessageList;
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

import jakarta.faces.application.FacesMessage;

class ContactBeanTest {


	static class TestableContactBean extends ContactBean {
		FacesMessage.Severity capturedSeverity;
		String capturedText;

		@Override
		protected void addFacesMessage(FacesMessage.Severity severity, String text) {
			this.capturedSeverity = severity;
			this.capturedText = text;
		}
	}

	TestableContactBean contactBean;
	ContactMessageList contactMessageList;
	UserList userList = new UserList();
	LoginBean loginBean = new LoginBean();
	List<ContactMessage> messages = contactMessageList.getMessages();

	@BeforeEach
	void setUp() {
		contactMessageList = new ContactMessageList();
		contactBean = new TestableContactBean();
		contactBean.setContactMessageList(contactMessageList);
	}

	@Test
	@DisplayName("form is filled out  the enquiry is sent to the business and a confirmation message is shown")
	void submittingCompleteFormSendsEnquiryAndConfirms() {
		contactBean.setName("Mary Byrne");
		contactBean.setEmail("mary@byrne.com");
		contactBean.setSubject("Boarding availability");
		contactBean.setMessage("Do you have space for two cats next weekend?");
		contactBean.submit();
		assertEquals(1, messages.size());
		assertEquals("Mary Byrne", messages.get(0).getName());
		assertEquals("mary@byrne.com", messages.get(0).getEmail());
		assertEquals("Boarding availability", messages.get(0).getSubject());
		assertEquals("Do you have space for two cats next weekend?", messages.get(0).getMessage());
		assertEquals(FacesMessage.SEVERITY_INFO, contactBean.capturedSeverity);
		assertTrue(contactBean.capturedText.contains("Mary Byrne"));
		assertTrue(contactBean.capturedText.toLowerCase().contains("thank you"));
	}

	@Test
	@DisplayName("After successful submission the form is cleared ")
	void formFieldsAreClearedAfterSuccessfulSubmission() {

		contactBean.setName("Mary Byrne");
		contactBean.setEmail("mary@byrne.com");
		contactBean.setSubject("Boarding availability");
		contactBean.setMessage("Do you have space for two cats next weekend?");
		contactBean.submit();
		assertNull(contactBean.getName());
		assertNull(contactBean.getEmail());
		assertNull(contactBean.getSubject());
		assertNull(contactBean.getMessage());
	}

	@Test
	@DisplayName("For a logged in user the name and email fields are preloaded ")
	void prefillFromLoginPopulatesNameAndEmail() {
		User customer = new User("Rupali", "rupali@tus.ie", "password");
		userList.addUser(customer);
		loginBean.setUserList(userList);
		loginBean.setEmail("rupali@tus.ie");
		loginBean.setPassword("password");
		loginBean.login();
		contactBean.setLoginBean(loginBean);
		contactBean.prefillFromLogin();
		assertEquals("Rupali", contactBean.getName());
		assertEquals("rupali@tus.ie", contactBean.getEmail());
	}

	@Test
	@DisplayName("User is not logged in, when the contact page loads, then the name and email fields are  blank for the visitor to fill in")
	void prefillFromLoginDoesNothingForAnonymousVisitor() {
		loginBean.setUserList(userList);
		contactBean.setLoginBean(loginBean);
		contactBean.prefillFromLogin();
		assertNull(contactBean.getName());
		assertNull(contactBean.getEmail());
	}

	@Test
	@DisplayName("the  message is incomplete when trying to submit then the enquiry is not stored and an error is shown")
	void submittingIncompleteMessageIsRejectedAtBeanLevel() {
		contactBean.setName("Mary Byrne");
		contactBean.setEmail("mary@byrne.com");
		contactBean.setSubject(""); // missing
		contactBean.setMessage("Do you have space for two cats next weekend?");
		contactBean.submit();
		assertTrue(contactMessageList.getMessages().isEmpty());
		assertEquals(FacesMessage.SEVERITY_ERROR, contactBean.capturedSeverity);
		assertNotNull(contactBean.capturedText);
	}
}

