package com.revplay.controller;

import com.revplay.model.User;
import com.revplay.service.UserService;
import com.revplay.exception.CustomException;
import com.revplay.util.LogUtil;
import java.util.List;
import java.util.Scanner;

public class UserController {
    private final UserService userService;
    private final Scanner scanner;
    private User currentUser;

    public UserController() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }

    public void showMainMenu() {
        while (true) {
            if (currentUser == null) {
                showUnauthenticatedMenu();
            } else {
                showAuthenticatedMenu();
            }
        }
    }

    private void showUnauthenticatedMenu() {
        System.out.println("\n=== RevPlay - Music Streaming ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Search Users");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    searchUsers();
                    break;
                case 4:
                    System.out.println("Thank you for using RevPlay. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (CustomException e) {
            System.out.println("Error: " + e.getUserMessage());
            LogUtil.logError(e.getMessage(), e);
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            LogUtil.logError("Unexpected error in user controller", e);
        }
    }

    private void showAuthenticatedMenu() {
        System.out.println("\n=== RevPlay - Welcome " + currentUser.getUsername() + " ===");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Change Password");
        System.out.println("4. Search Users");
        System.out.println("5. Deactivate Account");
        System.out.println("6. Logout");
        System.out.println("7. Exit");

        if ("ARTIST".equals(currentUser.getUserType())) {
            System.out.println("8. Switch to Artist Dashboard");
        }

        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewProfile();
                    break;
                case 2:
                    updateProfile();
                    break;
                case 3:
                    changePassword();
                    break;
                case 4:
                    searchUsers();
                    break;
                case 5:
                    deactivateAccount();
                    break;
                case 6:
                    logout();
                    return;
                case 7:
                    System.out.println("Thank you for using RevPlay. Goodbye!");
                    System.exit(0);
                    break;
                case 8:
                    if ("ARTIST".equals(currentUser.getUserType())) {
                        return; // Let main method handle switching
                    }
                    // fall through
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (CustomException e) {
            System.out.println("Error: " + e.getUserMessage());
            LogUtil.logError(e.getMessage(), e);
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            LogUtil.logError("Unexpected error in user controller", e);
        }
    }

    private void registerUser() throws CustomException {
        System.out.println("\n=== User Registration ===");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        System.out.print("User type (USER/ARTIST): ");
        String userType = scanner.nextLine().toUpperCase();

        User user = userService.registerUser(username, password, email, fullName, userType);
        System.out.println("Registration successful! User ID: " + user.getUserId());

        if ("ARTIST".equals(userType)) {
            System.out.println("Please complete your artist profile in the Artist Dashboard.");
        }
    }

    private void loginUser() throws CustomException {
        System.out.println("\n=== User Login ===");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        currentUser = userService.loginUser(username, password);
        System.out.println("Login successful! Welcome, " + currentUser.getUsername());
    }

    private void viewProfile() {
        System.out.println("\n=== Your Profile ===");
        System.out.println("User ID: " + currentUser.getUserId());
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Full Name: " + currentUser.getFullName());
        System.out.println("User Type: " + currentUser.getUserType());
        System.out.println("Registration Date: " + currentUser.getRegistrationDate());
        System.out.println("Status: " + (currentUser.isActive() ? "Active" : "Inactive"));

        if (currentUser.getBio() != null && !currentUser.getBio().isEmpty()) {
            System.out.println("Bio: " + currentUser.getBio());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateProfile() throws CustomException {
        System.out.println("\n=== Update Profile ===");

        System.out.print("Enter new email (current: " + currentUser.getEmail() + "): ");
        String email = scanner.nextLine();

        System.out.print("Enter new full name (current: " + currentUser.getFullName() + "): ");
        String fullName = scanner.nextLine();

        System.out.print("Enter profile image URL (optional): ");
        String profileImage = scanner.nextLine();

        System.out.print("Enter bio (optional): ");
        String bio = scanner.nextLine();

        boolean success = userService.updateUserProfile(
                currentUser.getUserId(), email, fullName, profileImage, bio);

        if (success) {
            // Update current user object
            currentUser.setEmail(email);
            currentUser.setFullName(fullName);
            currentUser.setProfileImage(profileImage);
            currentUser.setBio(bio);

            System.out.println("Profile updated successfully!");
        }
    }

    private void changePassword() throws CustomException {
        System.out.println("\n=== Change Password ===");

        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        boolean success = userService.changePassword(
                currentUser.getUserId(), currentPassword, newPassword, confirmPassword);

        if (success) {
            currentUser.setPassword(newPassword);
            System.out.println("Password changed successfully!");
        }
    }

    private void searchUsers() throws CustomException {
        System.out.println("\n=== Search Users ===");

        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<User> users = userService.searchUsers(keyword);

        if (users.isEmpty()) {
            System.out.println("No users found matching your search.");
        } else {
            System.out.println("\nFound " + users.size() + " user(s):");
            System.out.println("ID\tUsername\tEmail\t\t\tType\tStatus");
            System.out.println("------------------------------------------------------------");

            for (User user : users) {
                System.out.printf("%d\t%s\t\t%s\t%s\t%s\n",
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail().length() > 15 ? user.getEmail().substring(0, 15) + "..." : user.getEmail(),
                        user.getUserType(),
                        user.isActive() ? "Active" : "Inactive"
                );
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deactivateAccount() throws CustomException {
        System.out.println("\n=== Deactivate Account ===");

        System.out.print("Are you sure you want to deactivate your account? (yes/no): ");
        String confirmation = scanner.nextLine().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            boolean success = userService.deactivateAccount(currentUser.getUserId());

            if (success) {
                System.out.println("Account deactivated successfully.");
                currentUser = null;
            }
        } else {
            System.out.println("Account deactivation cancelled.");
        }
    }

    private void logout() {
        System.out.println("Logging out... Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
    // Add these methods to your existing UserController class

    public void loginUserDirect(String username, String password) throws CustomException {
        currentUser = userService.loginUser(username, password);
        System.out.println("Login successful! Welcome, " + currentUser.getUsername());
    }

    public void registerUserDirect(String username, String password, String email,
                                   String fullName, String userType) throws CustomException {
        User user = userService.registerUser(username, password, email, fullName, userType);
        currentUser = user; // Auto-login after registration
        System.out.println("Registration successful! Welcome, " + currentUser.getUsername());

        if ("ARTIST".equals(userType)) {
            System.out.println("Please visit Artist Dashboard to complete your profile.");
        }
    }
}