package com.revplay.dao.impl;

import com.revplay.dao.UserDAO;
import com.revplay.model.User;
import com.revplay.util.DBUtil;
import com.revplay.util.LogUtil;
import com.revplay.exception.CustomException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User registerUser(User user) throws CustomException {
        String sql = "INSERT INTO users (username, password, email, full_name, user_type, profile_image, bio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"user_id"})) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getUserType());
            pstmt.setString(6, user.getProfileImage());
            pstmt.setString(7, user.getBio());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                LogUtil.logInfo("User registered successfully: " + user.getUsername());
                return user;
            }

            throw new CustomException("USER_REG_001", "Failed to register user",
                    "Registration failed. Please try again.");

        } catch (SQLException e) {
            LogUtil.logError("Error registering user: " + user.getUsername(), e);

            if (e.getErrorCode() == 1) { // Unique constraint violation
                throw new CustomException("USER_REG_002", "Username or email already exists",
                        "Username or email is already taken.");
            }
            throw new CustomException("USER_REG_003", "Database error during registration",
                    "Registration failed due to system error.");
        }
    }

    @Override
    public User loginUser(String username, String password) throws CustomException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = 'Y'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = extractUserFromResultSet(rs);
                    LogUtil.logInfo("User logged in: " + username);
                    return user;
                } else {
                    throw new CustomException("USER_LOGIN_001", "Invalid credentials",
                            "Invalid username or password.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error during login for user: " + username, e);
            throw new CustomException("USER_LOGIN_002", "Database error during login",
                    "Login failed due to system error.");
        }
    }

    @Override
    public User getUserById(int userId) throws CustomException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                } else {
                    throw new CustomException("USER_GET_001", "User not found",
                            "User with ID " + userId + " not found.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting user by ID: " + userId, e);
            throw new CustomException("USER_GET_002", "Database error",
                    "Failed to retrieve user information.");
        }
    }

    @Override
    public User getUserByUsername(String username) throws CustomException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                } else {
                    throw new CustomException("USER_GET_003", "User not found",
                            "Username '" + username + "' not found.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting user by username: " + username, e);
            throw new CustomException("USER_GET_004", "Database error",
                    "Failed to retrieve user information.");
        }
    }

    @Override
    public User getUserByEmail(String email) throws CustomException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting user by email: " + email, e);
            throw new CustomException("USER_GET_005", "Database error",
                    "Failed to retrieve user information.");
        }
    }

    @Override
    public boolean updateUser(User user) throws CustomException {
        String sql = "UPDATE users SET email = ?, full_name = ?, profile_image = ?, bio = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getProfileImage());
            pstmt.setString(4, user.getBio());
            pstmt.setInt(5, user.getUserId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("User updated successfully: " + user.getUserId());
                return true;
            }

            throw new CustomException("USER_UPDATE_001", "User not found for update",
                    "User information could not be updated.");

        } catch (SQLException e) {
            LogUtil.logError("Error updating user: " + user.getUserId(), e);

            if (e.getErrorCode() == 1) {
                throw new CustomException("USER_UPDATE_002", "Email already exists",
                        "Email is already in use by another account.");
            }
            throw new CustomException("USER_UPDATE_003", "Database error during update",
                    "Update failed due to system error.");
        }
    }

    @Override
    public boolean deleteUser(int userId) throws CustomException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("User deleted successfully: " + userId);
                return true;
            }

            throw new CustomException("USER_DELETE_001", "User not found for deletion",
                    "User account could not be deleted.");

        } catch (SQLException e) {
            LogUtil.logError("Error deleting user: " + userId, e);
            throw new CustomException("USER_DELETE_002", "Database error during deletion",
                    "Deletion failed due to system error.");
        }
    }

    @Override
    public boolean changePassword(int userId, String newPassword) throws CustomException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Password changed for user: " + userId);
                return true;
            }

            throw new CustomException("USER_PASS_001", "User not found for password change",
                    "Password could not be changed.");

        } catch (SQLException e) {
            LogUtil.logError("Error changing password for user: " + userId, e);
            throw new CustomException("USER_PASS_002", "Database error during password change",
                    "Password change failed due to system error.");
        }
    }

    @Override
    public List<User> getAllUsers() throws CustomException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY registration_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }

            LogUtil.logDebug("Retrieved " + users.size() + " users");
            return users;

        } catch (SQLException e) {
            LogUtil.logError("Error getting all users", e);
            throw new CustomException("USER_GET_ALL_001", "Database error",
                    "Failed to retrieve users list.");
        }
    }

    @Override
    public List<User> searchUsers(String keyword) throws CustomException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR email LIKE ? OR full_name LIKE ? " +
                "ORDER BY username";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Found " + users.size() + " users matching: " + keyword);
            return users;

        } catch (SQLException e) {
            LogUtil.logError("Error searching users with keyword: " + keyword, e);
            throw new CustomException("USER_SEARCH_001", "Database error during search",
                    "Search failed due to system error.");
        }
    }

    @Override
    public boolean deactivateUser(int userId) throws CustomException {
        String sql = "UPDATE users SET is_active = 'N' WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("User deactivated: " + userId);
                return true;
            }

            throw new CustomException("USER_DEACTIVATE_001", "User not found",
                    "User account could not be deactivated.");

        } catch (SQLException e) {
            LogUtil.logError("Error deactivating user: " + userId, e);
            throw new CustomException("USER_DEACTIVATE_002", "Database error",
                    "Deactivation failed due to system error.");
        }
    }

    @Override
    public boolean activateUser(int userId) throws CustomException {
        String sql = "UPDATE users SET is_active = 'Y' WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("User activated: " + userId);
                return true;
            }

            throw new CustomException("USER_ACTIVATE_001", "User not found",
                    "User account could not be activated.");

        } catch (SQLException e) {
            LogUtil.logError("Error activating user: " + userId, e);
            throw new CustomException("USER_ACTIVATE_002", "Database error",
                    "Activation failed due to system error.");
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setUserType(rs.getString("user_type"));
        user.setProfileImage(rs.getString("profile_image"));
        user.setBio(rs.getString("bio"));

        Timestamp regDate = rs.getTimestamp("registration_date");
        user.setRegistrationDate(regDate != null ? regDate.toLocalDateTime() : null);

        String isActive = rs.getString("is_active");
        user.setActive("Y".equals(isActive));

        return user;
    }
}