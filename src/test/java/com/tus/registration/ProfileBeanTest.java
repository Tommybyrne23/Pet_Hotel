package com.tus.registration;
 
import static org.junit.jupiter.api.Assertions.*;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
 
import com.tus.pethotel.User;
import com.tus.pethotel.UserList;
 
class ProfileBeanTest {
 
    ProfileBean profileBean;
    LoginBean loginBean;
    UserList userList;
    User loggedInUser;
 
    @BeforeEach
    void setUp() throws Exception {
        userList = new UserList();
        User customer = new User("Rupali", "rupali@tus.ie", "password");
        userList.addUser(customer);
 
        loginBean = new LoginBean();
        loginBean.setUserList(userList);
        loginBean.setEmail("rupali@tus.ie");
        loginBean.setPassword("password");
        loginBean.login();
        loggedInUser = loginBean.getLoggedInUser();
 
        profileBean = new ProfileBean();
        profileBean.setLoginBean(loginBean);
    }
 
    @Test
    @DisplayName("Test the user information visible")
    void loadProfileShowsCurrentSavedDetails() {
        profileBean.loadProfile();
        assertEquals("Rupali", profileBean.getEditName());
        assertEquals("rupali@tus.ie", profileBean.getEditEmail());
    }
 
    @Test
    @DisplayName("details updated after user enter new details")
    void savingValidDetailsUpdatesProfileImmediately() {
        profileBean.loadProfile();
        profileBean.setEditName("Rupali Motwani");
        profileBean.setEditEmail("rupali.motwani@tus.ie");
        String outcome = profileBean.saveProfile();
        assertNull(outcome); // stays on the same page, no redirect needed
        assertEquals("Rupali Motwani", loggedInUser.getName());
        assertEquals("rupali.motwani@tus.ie", loggedInUser.getEmail());
    }
 
    @Test
    @DisplayName("invalid email format")
    void savingInvalidEmailFormatBlocksTheUpdate() {
        profileBean.loadProfile();
        String originalEmail = loggedInUser.getEmail();
        profileBean.setEditName("Rupali");
        profileBean.setEditEmail("rupali.tus.ie"); // missing "@" - invalid format
        String outcome = profileBean.saveProfile();
        assertNull(outcome);
        assertEquals(originalEmail, loggedInUser.getEmail());
           }
 
    @Test
    @DisplayName("Given a blank name, when the customer saves, then the update is blocked")
    void savingBlankNameBlocksTheUpdate() {
        profileBean.loadProfile();
        String originalName = loggedInUser.getName();
        profileBean.setEditName("   ");
        profileBean.setEditEmail("rupali@tus.ie");
        String outcome = profileBean.saveProfile();
        assertNull(outcome);
        assertEquals(originalName, loggedInUser.getName());
    }
}
 
