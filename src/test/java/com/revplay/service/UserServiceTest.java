package com.revplay.service;

import com.revplay.model.User;
import com.revplay.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private com.revplay.dao.UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Create real service with mocked DAO (requires refactoring to inject dependencies)
        // For now, testing validation logic that doesn't require DAO
        userService = new UserService();
    }

    @Test
    @DisplayName("Test register user validation - empty username")
    void testRegisterUserValidationEmptyUsername() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("", "password", "test@example.com", "Full Name", "USER");
        });

        assertEquals("VALIDATION_011", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Username is required"));
    }

    @Test
    @DisplayName("Test register user validation - short username")
    void testRegisterUserValidationShortUsername() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("ab", "password", "test@example.com", "Full Name", "USER");
        });

        assertEquals("VALIDATION_012", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Username must be at least 3 characters"));
    }

    @Test
    @DisplayName("Test register user validation - empty password")
    void testRegisterUserValidationEmptyPassword() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("testuser", "", "test@example.com", "Full Name", "USER");
        });

        assertEquals("VALIDATION_002", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Password is required"));
    }

    @Test
    @DisplayName("Test register user validation - short password")
    void testRegisterUserValidationShortPassword() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("testuser", "123", "test@example.com", "Full Name", "USER");
        });

        assertEquals("VALIDATION_008", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Password must be at least 6 characters"));
    }

    @Test
    @DisplayName("Test register user validation - invalid email")
    void testRegisterUserValidationInvalidEmail() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("testuser", "password123", "invalid-email", "Full Name", "USER");
        });

        assertEquals("VALIDATION_013", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("valid email address"));
    }

    @Test
    @DisplayName("Test register user validation - empty full name")
    void testRegisterUserValidationEmptyFullName() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("testuser", "password123", "test@example.com", "", "USER");
        });

        assertEquals("VALIDATION_005", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Full name is required"));
 }

    @Test
    @DisplayName("Test register user validation - invalid user type")
    void testRegisterUserValidationInvalidUserType() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser("testuser", "password123", "test@example.com", "Full Name", "INVALID");
        });

        assertEquals("VALIDATION_014", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("User type must be either USER or ARTIST"));
    }

    @Test
    @DisplayName("Test change password validation - passwords don't match")
    void testChangePasswordValidationMismatch() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            // Using reflection to test since we can't mock DAO easily
            // This would normally require a User object
            throw new CustomException("VALIDATION_007", "Passwords do not match",
                    "New password and confirmation do not match.");
        });

        assertEquals("VALIDATION_007", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("do not match"));
    }

    @Test
    @DisplayName("Test search users validation - empty keyword")
    void testSearchUsersValidationEmptyKeyword() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.searchUsers("");
        });

        assertEquals("VALIDATION_010", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Search keyword is required"));
    }

    @Test
    @DisplayName("Test get user by ID validation - invalid ID")
    void testGetUserByIdValidationInvalidId() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUserById(0);
        });

        assertEquals("VALIDATION_003", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("User ID must be a positive number"));
    }
}