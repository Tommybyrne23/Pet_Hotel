package com.tus.registration;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tus.pethotel.*;

@DisplayName("Dashboard Bean Navigation Tests")
class DashboardBeanTest {

    // 1. Declare bean and user fields at the class level so all @Test methods can access them
    DashboardBean dashboardBean;
    LoginBean loginBean;
    UserList userList;
    
    User customer;
    User attendant;
    User admin;

    @BeforeEach
    void setUp() throws Exception {
        loginBean = new LoginBean();
        dashboardBean = new DashboardBean();
        userList = new UserList();

        // 2. Assign values to the class-level user fields (do NOT re-declare with "User customer = ...")
        customer = new User("Jonathan", "a00347373@student.tus.ie", "password", Role.CUSTOMER);
        attendant = new User("Rupali", "rupali@student.tus.ie", "password", Role.ATTENDANT);
        admin = new User("TOMMY", "TOMMY@student.tus.ie", "password", Role.ADMIN);

        // Add users to userList
        userList.addUser(customer);
        userList.addUser(attendant);
        userList.addUser(admin);

        // Inject dependencies into DashboardBean
        injectDependency(dashboardBean, "loginBean", loginBean);
    }

    // Helper method to inject private fields without CDI container
    private void injectDependency(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Should redirect CUSTOMER role to /userDashboard")
    void testCustomerRedirect() {
        loginBean.setLoggedInUser(customer);

        String outcome = dashboardBean.dashboard();

        assertEquals("/userDashboard?faces-redirect=true", outcome);
    }

    @Test
    @DisplayName("Should redirect ATTENDANT role to /attendantDashboard")
    void testAttendantRedirect() {
        loginBean.setLoggedInUser(attendant);

        String outcome = dashboardBean.dashboard();

        assertEquals("/attendantDashboard?faces-redirect=true", outcome);
    }

    @Test
    @DisplayName("Should redirect ADMIN role to /adminDashboard")
    void testAdminRedirect() {
        loginBean.setLoggedInUser(admin);

        String outcome = dashboardBean.dashboard();

        assertEquals("/adminDashboard?faces-redirect=true", outcome);
    }

    @Test
    @DisplayName("Should redirect logged-out users to index page")
    void testLoggedOutRedirect() {
        loginBean.setLoggedInUser(null); // Ensure no user is logged in

        String outcome = dashboardBean.dashboard();

        assertEquals("/index?faces-redirect=true", outcome);
    }
}
