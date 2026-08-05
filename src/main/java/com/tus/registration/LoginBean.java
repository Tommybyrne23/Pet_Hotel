package com.tus.registration;

import java.io.Serializable;

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

                return "index?faces-redirect=true";
            }
        }


        // If no match found
        FacesContext.getCurrentInstance().addMessage(
                "loginForm:emailInput",
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Invalid email or password.",
                        null
                )
        );

        return null; // stay on login page
    }


    public String logout() {

        loggedInUser = null;
        email = null;
        password = null;

        return "login?faces-redirect=true";
    }


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
}
