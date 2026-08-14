package com.tus.registration;

import java.io.Serializable;
import com.tus.pethotel.Role;
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;

import jakarta.enterprise.context.RequestScoped; // MUST be jakarta.enterprise.context
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;                    // MUST be jakarta.inject

@Named("dashboardBean")
@RequestScoped
public class DashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private LoginBean loginBean;

    @Inject
    private UserList userList;

    public String dashboard() {
        if (loginBean != null && loginBean.isLoggedIn()) {
            User loggedInUser = loginBean.getLoggedInUser();
            
            if (loggedInUser != null && loggedInUser.getRole() != null) {
                Role role = loggedInUser.getRole();
                
                if (role == Role.ADMIN) {
                    return "/adminDashboard?faces-redirect=true";
                } else if (role == Role.CUSTOMER) {
                    return "/userDashboard?faces-redirect=true";
                } else {
                    return "/attendantDashboard?faces-redirect=true";
                }
            }
        }
        return "/index?faces-redirect=true";
    }
}
