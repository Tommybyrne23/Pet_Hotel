package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
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
	UserList userList;
	LoginBean loginBean;

	@BeforeEach
	void setUp() {
		userList = new UserList();
		loginBean = new LoginBean();
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
		List<ContactMessage> messages = contactMessageList.getMessages();
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
	@DisplayName("A fully completed message when added directly then it is stored")
	void addingCompleteMessageSucceeds() {
		ContactMessage message = new ContactMessage("Mary Byrne", "mary@byrne.com","Boarding availability", "Message body");
		boolean added = contactMessageList.addMessage(message);
		assertTrue(added);
		assertEquals(1, contactMessageList.getNumberOfMessages());
		assertEquals(message, contactMessageList.getMessages().get(0));
	}

	@Test
	@DisplayName("name is missing then the message is rejected")
	void addingMessageWithBlankNameIsRejected() {
		ContactMessage message = new ContactMessage("", "mary@byrne.com","Boarding availability", "Message body");
		assertFalse(contactMessageList.addMessage(message));
		assertEquals(0, contactMessageList.getNumberOfMessages());
	}

	@Test
	@DisplayName("the email is missing (blank/whitespace), then the message is rejected")
	void addingMessageWithBlankEmailIsRejected() {
		ContactMessage message = new ContactMessage("Mary Byrne", "   ",	"Boarding availability", "Message body");
		assertFalse(contactMessageList.addMessage(message));
		assertEquals(0, contactMessageList.getNumberOfMessages());
	}

	@Test
	@DisplayName("subject is missing (null), then the message is rejected")
	void addingMessageWithNullSubjectIsRejected() {
		ContactMessage message = new ContactMessage("Mary Byrne", "mary@byrne.com",null, "Message body");
		assertFalse(contactMessageList.addMessage(message));
		assertEquals(0, contactMessageList.getNumberOfMessages());
	}

	@Test
	@DisplayName("the message body is missing (blank/whitespace), then the message is rejected")
	void addingMessageWithBlankBodyIsRejected() {
		ContactMessage message = new ContactMessage("Mary Byrne", "mary@byrne.com","Boarding availability", "   ");
		assertFalse(contactMessageList.addMessage(message));
		assertEquals(0, contactMessageList.getNumberOfMessages());
	}

	@Test
	@DisplayName("a null message object, then it is rejected without error")
	void addingNullMessageIsRejected() {
		assertFalse(contactMessageList.addMessage(null));
		assertEquals(0, contactMessageList.getNumberOfMessages());
	}

	@Test
	@DisplayName(" no enquiries have been submitted, then the admin view would show an empty list")
	void newContactMessageListStartsEmpty() {
		assertEquals(0, contactMessageList.getNumberOfMessages());
		assertTrue(contactMessageList.getMessages().isEmpty());
	}

		@Test
		@DisplayName("several valid enquiries were added, when viewed by an admin, then all are returned in order")
		void multipleMessagesAreAllReturnedForAdminView() {
			contactMessageList.addMessage(new ContactMessage("Mary Byrne", "mary@byrne.com","Subject 1", "Message 1"));
			contactMessageList.addMessage(new ContactMessage("John Doe", "john@doe.com","Subject 2", "Message 2"));
	 		assertEquals(2, contactMessageList.getNumberOfMessages());
			assertEquals("Mary Byrne", contactMessageList.getMessages().get(0).getName());
			assertEquals("John Doe", contactMessageList.getMessages().get(1).getName());
		}
	 
		@Test
		@DisplayName("the list returned to a caller is modified, then the internal store is unaffected")
		void getMessagesReturnsADefensiveCopy() {
			contactMessageList.addMessage(new ContactMessage("Mary Byrne", "mary@byrne.com","Subject 1", "Message 1"));
	 		contactMessageList.getMessages().clear(); 
	 		assertEquals(1, contactMessageList.getNumberOfMessages()); 
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
	 
		@Test
		@DisplayName("parameterised constructor is used, then receivedAt is stamped automatically")
		void parameterisedConstructorStampsReceivedAt() {
			ContactMessage message = new ContactMessage("Mary Byrne", "mary@byrne.com",
					"Subject", "Body");
	 
			assertNotNull(message.getReceivedAt());
		}
	 
		@Test
		@DisplayName("the no-arg constructor is used, then all fields start blank")
		void noArgConstructorStartsBlank() {
			ContactMessage message = new ContactMessage();
	 
			assertNull(message.getName());
			assertNull(message.getEmail());
			assertNull(message.getSubject());
			assertNull(message.getMessage());
			assertNull(message.getReceivedAt());
		}
	 
		@Test
		@DisplayName("each setter is used, then the corresponding getter reflects the new value")
		void settersUpdateTheCorrespondingFields() {
			ContactMessage message = new ContactMessage();
			LocalDateTime fixedTime = LocalDateTime.of(2026, 8, 17, 10, 30);
	 
			message.setName("Mary Byrne");
			message.setEmail("mary@byrne.com");
			message.setSubject("Boarding availability");
			message.setMessage("Message body");
			message.setReceivedAt(fixedTime);
	 
			assertEquals("Mary Byrne", message.getName());
			assertEquals("mary@byrne.com", message.getEmail());
			assertEquals("Boarding availability", message.getSubject());
			assertEquals("Message body", message.getMessage());
			assertEquals(fixedTime, message.getReceivedAt());
		}
}


