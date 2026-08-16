package com.tus.registration;

import com.tus.pethotel.ContactMessage;
import com.tus.pethotel.ContactMessageList;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;


@Named
@RequestScoped
public class ContactBean {

    private ContactMessageList contactMessageList;

    private String name;
    private String email;
    private String subject;
    private String message;
    private LoginBean loginBean;

    @Inject
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    @PostConstruct
    public void prefillFromLogin() {
        if (loginBean != null && loginBean.isLoggedIn()) {
            name = loginBean.getLoggedInUser().getName();
            email = loginBean.getLoggedInUser().getEmail();
        }
    }
    
    @Inject
    public void setContactMessageList(ContactMessageList contactMessageList) {
        this.contactMessageList = contactMessageList;
    }

    public void submit() {
        ContactMessage enquiry = new ContactMessage(name, email, subject, message);

        if (contactMessageList.addMessage(enquiry)) {
            addFacesMessage(FacesMessage.SEVERITY_INFO,
                    "Thank you " + name + ", your enquiry has been sent. We will be in touch shortly.");
            clearForm();
        } else {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Sorry, your enquiry could not be sent. Please try again.");
        }
    }

    // Overridden in tests (self-shunt) as FacesContext is unavailable outside a request
    protected void addFacesMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }

    private void clearForm() {
        subject = null;
        message = null;
        name = null;
        email = null;
        prefillFromLogin();
    }

    public String getName() { 
    	return name; 
    	}
    public void setName(String name) {
    	this.name = name; 
    	}

    public String getEmail() {
    	return email; 
    	}
    public void setEmail(String email) {
    	this.email = email; 
    	}

    public String getSubject() {
    	return subject; 
    	}
    public void setSubject(String subject) {
    	this.subject = subject; 
    	}

    public String getMessage() {
    	return message; 
    	}
    public void setMessage(String message) {
    	this.message = message; 
    	}
}