package com.revplay.service;

import com.revplay.dao.UserDAO;
import com.revplay.dao.impl.UserDAOImpl;
import com.revplay.model.User;
import com.revplay.exception.CustomException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    public User registerUser(String username, String password, String email,
                             String fullName, String userType) throws CustomException {
        validateUserInput(username, password, email, fullName, userType);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setUserType(userType);
        user.setActive(true);

        return userDAO.registerUser(user);
    }

    public User loginUser(String username, String password) throws CustomException {
        if (username == null || username.trim().isEmpty()) {
            throw new CustomException("VALIDATION_001", "Username is required",
                    "Please enter your username.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new CustomException("VALIDATION_002", "Password is required",
                    "Please enter your password.");
        }

        return userDAO.loginUser(username.trim(), password.trim());
    }

    public User getUserById(int userId) throws CustomException {
        if (userId <= 0) {
            throw new CustomException("VALIDATION_003", "Invalid user ID",
                    "User ID must be a positive number.");
        }
        return userDAO.getUserById(userId);
    }

    public boolean updateUserProfile(int userId, String email, String fullName,
                                     String profileImage, String bio) throws CustomException {
        if (userId <= 0) {
            throw new CustomException("VALIDATION_003", "Invalid user ID",
                    "User ID must be a positive number.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new CustomException("VALIDATION_004", "Email is required",
                    "Please enter your email address.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new CustomException("VALIDATION_005", "Full name is required",
                    "Please enter your full name.");
        }

        User user = userDAO.getUserById(userId);
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setProfileImage(profileImage);
        user.setBio(bio);

        return userDAO.updateUser(user);
    }

    public boolean changePassword(int userId, String currentPassword,
                                  String newPassword, String confirmPassword) throws CustomException {
        if (userId <= 0) {
            throw new CustomException("VALIDATION_003", "Invalid user ID",
                    "User ID must be a positive number.");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new CustomException("VALIDATION_006", "New password is required",
                    "Please enter a new password.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new CustomException("VALIDATION_007", "Passwords do not match",
                    "New password and confirmation do not match.");
        }
        if (newPassword.length() < 6) {
            throw new CustomException("VALIDATION_008", "Password too short",
                    "Password must be at least 6 characters long.");
        }

        // Verify current password
        User user = userDAO.getUserById(userId);
        if (!user.getPassword().equals(currentPassword)) {
            throw new CustomException("VALIDATION_009", "Current password is incorrect",
                    "The current password you entered is incorrect.");
        }

        return userDAO.changePassword(userId, newPassword);
    }

    public List<User> searchUsers(String keyword) throws CustomException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException("VALIDATION_010", "Search keyword is required",
                    "Please enter a search keyword.");
        }
        return userDAO.searchUsers(keyword.trim());
    }

    public boolean deactivateAccount(int userId) throws CustomException {
        if (userId <= 0) {
            throw new CustomException("VALIDATION_003", "Invalid user ID",
                    "User ID must be a positive number.");
        }
        return userDAO.deactivateUser(userId);
    }

    public boolean activateAccount(int userId) throws CustomException {
        if (userId <= 0) {
            throw new CustomException("VALIDATION_003", "Invalid user ID",
                    "User ID must be a positive number.");
        }
        return userDAO.activateUser(userId);
    }

    public List<User> getAllUsers() throws CustomException {
        return userDAO.getAllUsers();
    }

    private void validateUserInput(String username, String password,
                                   String email, String fullName, String userType) throws CustomException {
        if (username == null || username.trim().isEmpty()) {
            throw new CustomException("VALIDATION_011", "Username is required",
                    "Please enter a username.");
        }
        if (username.length() < 3) {
            throw new CustomException("VALIDATION_012", "Username too short",
                    "Username must be at least 3 characters long.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new CustomException("VALIDATION_002", "Password is required",
                    "Please enter a password.");
        }
        if (password.length() < 6) {
            throw new CustomException("VALIDATION_008", "Password too short",
                    "Password must be at least 6 characters long.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new CustomException("VALIDATION_004", "Email is required",
                    "Please enter your email address.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new CustomException("VALIDATION_013", "Invalid email format",
                    "Please enter a valid email address.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new CustomException("VALIDATION_005", "Full name is required",
                    "Please enter your full name.");
        }
        if (userType == null || (!userType.equals("USER") && !userType.equals("ARTIST"))) {
            throw new CustomException("VALIDATION_014", "Invalid user type",
                    "User type must be either USER or ARTIST.");
        }
    }
}