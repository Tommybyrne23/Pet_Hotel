package com.tus.pethotel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reservation Search Bean Acceptance Tests")
public class ReservationSearchBeanTest {

    private ReservationSearchBean searchBean;
    private ReservationList reservationList;
    private UserList userList;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize real backend beans
        reservationList = new ReservationList();
        reservationList.init(); // Populate mock data (5 reservations)

        userList = new UserList();

        // Use a unique email so UserList.addUser() won't reject it as a duplicate
        User customerUser = new User("Customer User", "testuser6@3apets.ie", "user123");
        injectUserId(customerUser, 6);
        userList.addUser(customerUser);

        // Instantiate the bean under test
        searchBean = new ReservationSearchBean();

        // Inject dependencies using reflection
        injectDependency(searchBean, "reservationList", reservationList);
        injectDependency(searchBean, "userList", userList);

        // Run post-construct initialization
        searchBean.init();
    }

    // Helper method to set private injected fields
    private void injectDependency(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Helper method to force setting private userID for mock user
    private void injectUserId(User user, int id) throws Exception {
        Field field = user.getClass().getDeclaredField("userID");
        field.setAccessible(true);
        field.set(user, id);
    }

    // --- ACCEPTANCE CRITERIA 1: View all reservations ---

    @Test
    @DisplayName("Should load all past, current, and upcoming reservations on page init")
    void testAllReservations() {
        List<Reservation> results = searchBean.getFilteredReservations();

        assertNotNull(results, "Filtered reservations list should not be null");
        assertEquals(5, results.size(), "Should load all 5 system reservations initially");
    }

    @Test
    @DisplayName("Each entry should expose correct reservation details and formatted dates")
    void testReservationDetailsFormat() {
        Reservation first = searchBean.getFilteredReservations().get(0);

        assertEquals("Buddy", first.getPetName());
        assertEquals("Dog Boarding", first.getPodName());
        assertEquals("Confirmed", first.getStatus());
        assertEquals("1st Aug 2026", first.getFormattedCheckInDate());
        assertEquals("7th Aug 2026", first.getFormattedCheckOutDate());
    }

    // --- ACCEPTANCE CRITERIA 2: Filtering or searching reservations ---

    @Test
    @DisplayName("Filter reservations by pet name (case-insensitive & partial match)")
    void testFilterByPetName() {
        searchBean.setSearchPetName("bud"); // Partial match for "Buddy"
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(2, results.size(), "Should find 2 bookings for Buddy");
        assertTrue(results.stream().allMatch(r -> r.getPetName().equalsIgnoreCase("Buddy")));
    }

    @Test
    @DisplayName("Filter reservations by petID")
    void testFilterByPetID() {
        searchBean.setSearchPetID("1");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(2, results.size(), "Should find 2 bookings for pet ID 1");
    }

    @Test
    @DisplayName("Filter reservations by pod type")
    void testFilterByPodName() {
        searchBean.setSearchPodName("Cat Boarding");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(2, results.size(), "Should find 2 Cat Boarding bookings");
        assertTrue(results.stream().allMatch(r -> r.getPodName().equals("Cat Boarding")));
    }

    @Test
    @DisplayName("Filter reservations by Customer ID")
    void testFilterByCustomerID() {
        searchBean.setSearchCustomerID("6");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(5, results.size(), "Should match all 5 bookings assigned to Customer ID 6");
    }

    @Test
    @DisplayName("Filter reservations by Customer Name")
    void testFilterByCustomerName() {
        searchBean.setSearchCustomerName("Customer");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(5, results.size(), "Should match all 5 bookings assigned to Customer User");
    }

    @Test
    @DisplayName("Filter reservations by Customer Email")
    void testFilterByCustomerEmail() {
        searchBean.setSearchCustomerEmail("testuser6@3apets.ie");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(5, results.size(), "Should match all 5 bookings assigned to user email");
    }

    @Test
    @DisplayName("Filter reservations by exact Check-In date")
    void testFilterByCheckInDate() {
        searchBean.setSearchCheckInDate(LocalDate.of(2026, 8, 10));
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(1, results.size(), "Should find exactly 1 booking checking in on 2026-08-10");
        assertEquals("Buddy", results.get(0).getPetName());
    }

    @Test
    @DisplayName("Filter reservations by exact Check-Out date")
    void testFilterByCheckOutDate() {
        searchBean.setSearchCheckOutDate(LocalDate.of(2026, 8, 16));
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(1, results.size(), "Should find exactly 1 booking checking out on 2026-08-16");
        assertEquals("Buddy", results.get(0).getPetName());
    }

    @Test
    @DisplayName("Filter using combined search criteria (Pet Name AND Pod Type)")
    void testFilterByMultipleCriteria() {
        searchBean.setSearchPetName("Luna");
        searchBean.setSearchPodName("Cat Boarding");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getPetName().equals("Luna")));
    }

    @Test
    @DisplayName("Filtering with blank/whitespace search parameters returns full list")
    void testFilterWithBlankSearchStrings() {
        searchBean.setSearchPetName("   ");
        searchBean.setSearchPodName("");
        searchBean.setSearchCustomerID(null);
        searchBean.filter();

        assertEquals(5, searchBean.getFilteredReservations().size());
    }

    // --- ACCEPTANCE CRITERIA 3: Empty state when no reservations exist ---

    @Test
    @DisplayName("Should return empty list when filter criteria matches no bookings")
    void testNoPetFoundinSearch() {
        searchBean.setSearchPetName("Frankie McWoofWoof");
        searchBean.filter();

        List<Reservation> results = searchBean.getFilteredReservations();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle null reservation list safely without throwing exceptions")
    void testFilterNullReservationList() throws Exception {
        injectDependency(searchBean, "reservationList", null);
        
        searchBean.init();
        assertTrue(searchBean.getFilteredReservations().isEmpty());

        searchBean.filter();
        assertTrue(searchBean.getFilteredReservations().isEmpty());
    }



    @Test
    @DisplayName("TestCoverage: Verify all getters and setters operate correctly")
    void testGettersAndSetters() {
        searchBean.setSearchCustomerID("10");
        assertEquals("10", searchBean.getSearchCustomerID());

        searchBean.setSearchCustomerName("John");
        assertEquals("John", searchBean.getSearchCustomerName());

        searchBean.setSearchCustomerEmail("john@test.com");
        assertEquals("john@test.com", searchBean.getSearchCustomerEmail());

        searchBean.setSearchPetID("5");
        assertEquals("5", searchBean.getSearchPetID());

        searchBean.setSearchPetName("Rex");
        assertEquals("Rex", searchBean.getSearchPetName());

        searchBean.setSearchPodName("VIP Pod");
        assertEquals("VIP Pod", searchBean.getSearchPodName());

        LocalDate dateIn = LocalDate.of(2026, 9, 1);
        searchBean.setSearchCheckInDate(dateIn);
        assertEquals(dateIn, searchBean.getSearchCheckInDate());

        LocalDate dateOut = LocalDate.of(2026, 9, 5);
        searchBean.setSearchCheckOutDate(dateOut);
        assertEquals(dateOut, searchBean.getSearchCheckOutDate());

        List<Reservation> customList = new ArrayList<>();
        searchBean.setFilteredReservations(customList);
        assertEquals(customList, searchBean.getFilteredReservations());
    }
}
