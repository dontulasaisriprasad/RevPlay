package com.revplay.dao;

import com.revplay.dao.impl.UserDAOImpl;
import com.revplay.model.User;
import com.revplay.exception.CustomException;
import com.revplay.util.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {
    private UserDAOImpl userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        userDAO = new UserDAOImpl();
        connection = DBUtil.getConnection();

        // Clear ALL test data to prevent conflicts
        try (Statement stmt = connection.createStatement()) {
            // Delete in correct order due to foreign key constraints
            stmt.execute("DELETE FROM favorites");
            stmt.execute("DELETE FROM listening_history");
            stmt.execute("DELETE FROM playlist_songs");
            stmt.execute("DELETE FROM playlists");
            stmt.execute("DELETE FROM songs");
            stmt.execute("DELETE FROM artists");
            // Delete all test users with various patterns used in tests
            stmt.execute("DELETE FROM users WHERE username LIKE 'test%' OR " +
                    "username LIKE 'search%' OR " +
                    "username LIKE 'all%' OR " +
                    "username LIKE 'update%' OR " +
                    "username LIKE 'pass%' OR " +
                    "username LIKE 'login%' OR " +
                    "username LIKE 'active%' OR " +
                    "username LIKE 'byid%' OR " +
                    "username LIKE 'other%' OR " +
                    "username LIKE 'topartist%' OR " +
                    "username LIKE 'allartist%' OR " +
                    "username LIKE 'testartist%' OR " +
                    "username LIKE 'testlisteners%'");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Test user registration - success")
    void testRegisterUserSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testuser1");
        user.setPassword("password123");
        user.setEmail("test1@example.com");
        user.setFullName("Test User One");
        user.setUserType("USER");
        user.setActive(true);

        // Act
        User registeredUser = userDAO.registerUser(user);

        // Assert
        assertNotNull(registeredUser);
        assertTrue(registeredUser.getUserId() > 0);
        assertEquals("testuser1", registeredUser.getUsername());
        assertEquals("test1@example.com", registeredUser.getEmail());
        assertEquals("Test User One", registeredUser.getFullName());
        assertEquals("USER", registeredUser.getUserType());
        assertTrue(registeredUser.isActive());
    }

    @Test
    @DisplayName("Test user registration - duplicate username")
    void testRegisterUserDuplicateUsername() throws CustomException {
        // Arrange
        User user1 = new User();
        user1.setUsername("testuser2");
        user1.setPassword("password123");
        user1.setEmail("test2@example.com");
        user1.setFullName("Test User Two");
        user1.setUserType("USER");

        User user2 = new User();
        user2.setUsername("testuser2"); // Same username
        user2.setPassword("password456");
        user2.setEmail("test3@example.com");
        user2.setFullName("Test User Three");
        user2.setUserType("USER");

        // Act & Assert
        userDAO.registerUser(user1);
        CustomException exception = assertThrows(CustomException.class, () -> {
            userDAO.registerUser(user2);
        });

        assertEquals("USER_REG_002", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("already taken"));
    }

    @Test
    @DisplayName("Test user login - success")
    void testLoginUserSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testlogin");
        user.setPassword("loginpass");
        user.setEmail("login@example.com");
        user.setFullName("Login Test");
        user.setUserType("USER");
        user.setActive(true);

        userDAO.registerUser(user);

        // Act
        User loggedInUser = userDAO.loginUser("testlogin", "loginpass");

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("testlogin", loggedInUser.getUsername());
        assertEquals("login@example.com", loggedInUser.getEmail());
    }

    @Test
    @DisplayName("Test user login - invalid credentials")
    void testLoginUserInvalidCredentials() {
        // Arrange
        CustomException exception = assertThrows(CustomException.class, () -> {
            // Act
            userDAO.loginUser("nonexistent", "wrongpass");
        });

        // Assert
        assertEquals("USER_LOGIN_001", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Invalid username or password"));
    }

    @Test
    @DisplayName("Test get user by ID - success")
    void testGetUserByIdSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testbyid");
        user.setPassword("password123");
        user.setEmail("byid@example.com");
        user.setFullName("Get By ID");
        user.setUserType("USER");

        User registeredUser = userDAO.registerUser(user);
        int userId = registeredUser.getUserId();

        // Act
        User retrievedUser = userDAO.getUserById(userId);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getUserId());
        assertEquals("testbyid", retrievedUser.getUsername());
        assertEquals("byid@example.com", retrievedUser.getEmail());
    }

    @Test
    @DisplayName("Test get user by ID - not found")
    void testGetUserByIdNotFound() {
        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            userDAO.getUserById(99999); // Non-existent ID
        });

        assertEquals("USER_GET_001", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("not found"));
    }

    @Test
    @DisplayName("Test update user - success")
    void testUpdateUserSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testupdate");
        user.setPassword("password123");
        user.setEmail("update@example.com");
        user.setFullName("Update Test");
        user.setUserType("USER");

        User registeredUser = userDAO.registerUser(user);

        // Update user information
        registeredUser.setEmail("updated@example.com");
        registeredUser.setFullName("Updated Name");
        registeredUser.setProfileImage("new_image.jpg");
        registeredUser.setBio("Updated bio information");

        // Act
        boolean result = userDAO.updateUser(registeredUser);

        // Assert
        assertTrue(result);

        // Verify update
        User updatedUser = userDAO.getUserById(registeredUser.getUserId());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated Name", updatedUser.getFullName());
        assertEquals("new_image.jpg", updatedUser.getProfileImage());
        assertEquals("Updated bio information", updatedUser.getBio());
    }

    @Test
    @DisplayName("Test change password - success")
    void testChangePasswordSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testpass");
        user.setPassword("oldpassword");
        user.setEmail("pass@example.com");
        user.setFullName("Password Test");
        user.setUserType("USER");

        User registeredUser = userDAO.registerUser(user);

        // Act
        boolean result = userDAO.changePassword(registeredUser.getUserId(), "newpassword");

        // Assert
        assertTrue(result);

        // Verify new password works
        User loggedInUser = userDAO.loginUser("testpass", "newpassword");
        assertNotNull(loggedInUser);
        assertEquals(registeredUser.getUserId(), loggedInUser.getUserId());
    }

    @Test
    @DisplayName("Test search users - success")
    void testSearchUsers() throws CustomException {
        // Arrange - create test users
        User user1 = new User();
        user1.setUsername("testsearch1");
        user1.setPassword("pass1");
        user1.setEmail("search1@example.com");
        user1.setFullName("Search User One");
        user1.setUserType("USER");
        userDAO.registerUser(user1);

        User user2 = new User();
        user2.setUsername("testsearch2");
        user2.setPassword("pass2");
        user2.setEmail("search2@example.com");
        user2.setFullName("Search User Two");
        user2.setUserType("USER");
        userDAO.registerUser(user2);

        User user3 = new User();
        user3.setUsername("otheruser");
        user3.setPassword("pass3");
        user3.setEmail("other@example.com");
        user3.setFullName("Other User");
        user3.setUserType("USER");
        userDAO.registerUser(user3);

        // Act
        List<User> results = userDAO.searchUsers("search");

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());

        // Verify usernames
        List<String> usernames = results.stream()
                .map(User::getUsername)
                .toList();
        assertTrue(usernames.contains("testsearch1"));
        assertTrue(usernames.contains("testsearch2"));
        assertFalse(usernames.contains("otheruser"));
    }

    @Test
    @DisplayName("Test deactivate and activate user")
    void testDeactivateActivateUser() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testactive");
        user.setPassword("password123");
        user.setEmail("active@example.com");
        user.setFullName("Active Test");
        user.setUserType("USER");

        User registeredUser = userDAO.registerUser(user);
        int userId = registeredUser.getUserId();

        // Act & Assert - deactivate
        boolean deactivated = userDAO.deactivateUser(userId);
        assertTrue(deactivated);

        User deactivatedUser = userDAO.getUserById(userId);
        assertFalse(deactivatedUser.isActive());

        // Try to login with deactivated account
        CustomException loginException = assertThrows(CustomException.class, () -> {
            userDAO.loginUser("testactive", "password123");
        });
        assertEquals("USER_LOGIN_001", loginException.getErrorCode());

        // Act & Assert - activate
        boolean activated = userDAO.activateUser(userId);
        assertTrue(activated);

        User activatedUser = userDAO.getUserById(userId);
        assertTrue(activatedUser.isActive());

        // Should be able to login now
        User loggedInUser = userDAO.loginUser("testactive", "password123");
        assertNotNull(loggedInUser);
    }

    @Test
    @DisplayName("Test get all users")
    void testGetAllUsers() throws CustomException {
        // Arrange - create multiple users
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("alltest" + i);
            user.setPassword("pass" + i);
            user.setEmail("all" + i + "@example.com");
            user.setFullName("All Test " + i);
            user.setUserType("USER");
            userDAO.registerUser(user);
        }

        // Act
        List<User> allUsers = userDAO.getAllUsers();

        // Assert
        assertNotNull(allUsers);
        assertTrue(allUsers.size() >= 3); // Might include other test users

        // Verify our test users are in the list
        List<String> testUsernames = allUsers.stream()
                .map(User::getUsername)
                .filter(name -> name.startsWith("alltest"))
                .toList();
        assertEquals(3, testUsernames.size());
    }
}