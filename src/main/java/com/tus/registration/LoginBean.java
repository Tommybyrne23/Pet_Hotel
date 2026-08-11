package com.tus.registration;

import java.io.Serializable;

import com.tus.pethotel.Role;
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("LoginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private User loggedInUser;

    @Inject
    private UserList userList;


    public String login() {

        // Loop through all registered users
        for (User user : userList.getUsers()) {

            // Check email and password match
            if (user.getEmail().equalsIgnoreCase(email)
                    && user.getPassword().equals(password)) {

                loggedInUser = user;
                
                Role role = user.getRole();

               
                if (user.getRole().equals(Role.ADMIN)){
                	return "adminDashboard?faces-redirect=true";
                } else if (user.getRole().equals(Role.CUSTOMER)){
                	return "userDashboard?faces-redirect=true";
                } else  {
                	return "petAttendantDashboard?faces-redirect=true";
                }

			}
		} //end of for enhanced loop 

        // If no match found
        FacesContext.getCurrentInstance().addMessage(
                "loginForm:emailInput",
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Invalid email or password.",
                        null
                )   );

        return null; // stay on login page
    }


    public String logout() {
    	//use jakarta faces to inavlidate the session
    	 FacesContext.getCurrentInstance().getExternalContext().invalidateSession();


        return "login?faces-redirect=true";
    }


 /*
  * Tests to keep track of the users status (logged in or not and user role) across the website, 
  */

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && Role.ADMIN.equals(loggedInUser.getRole());
    }

    public boolean isAttendant() {
        // Admins can also access Attendant features
        return isAdmin() || (isLoggedIn() && Role.ATTENDANT.equals(loggedInUser.getRole()));
    }

    public boolean isCustomer() {
        // All logged-in accounts have customer-level access to their own data
        return isLoggedIn();
    }


    // Page restriction methods for user account types  <f:viewAction> 

    /**
     * Tier 1: Admin Only
     */
    public String checkAdminAccess() {
        if (!isAdmin()) {
            return handleUnauthorizedAccess("Admin privileges required.");
        }
        return null;
    }

    /**
     * Tier 2: Attendants and Admins (admins can access pet attendant files 
     */
    public String checkAttendantAccess() {
        if (!isAttendant()) {
            return handleUnauthorizedAccess("Pet Attendant or Admin privileges required.");
        }
        return null;
    }

    /**
     * Tier 3: Any Logged-in User (Customer, Attendant, or Admin)
     */
    public String checkCustomerAccess() {
        if (!isCustomer()) {
            return handleUnauthorizedAccess("Please log in to access this page.");
        }
        return null;
    }

    /**
     * Helper to handle unauthorized redirects gracefully
     */
    private String handleUnauthorizedAccess(String messageText) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, messageText, null));
        context.getExternalContext().getFlash().setKeepMessages(true);
        return "login?faces-redirect=true";						//returns the user to the login page as they won't have acces
    }
    
    //setters and getters

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public User getLoggedInUser() {
        return loggedInUser;
    }


    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    
    public void setUserList(UserList userList) {
        this.userList = userList;
    }
}
