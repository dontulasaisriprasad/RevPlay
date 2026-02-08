package com.revplay;

import com.revplay.controller.UserController;
import com.revplay.controller.ArtistController;
import com.revplay.controller.PlaylistController;
import com.revplay.controller.SongController;
import com.revplay.model.User;
import com.revplay.exception.CustomException;
import com.revplay.util.LogUtil;
import com.revplay.service.UserService;
import java.util.Scanner;

public class Main {
    private static final UserService userService = new UserService();
    private static final UserController userController = new UserController();
    private static ArtistController artistController = new ArtistController();
    private static final PlaylistController playlistController = new PlaylistController();
    private static final SongController songController = new SongController();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        LogUtil.logInfo("RevPlay Application Started");

        try {
            System.out.println("=========================================");
            System.out.println("    Welcome to RevPlay Music Streaming   ");
            System.out.println("=========================================");

            boolean running = true;
            while (running) {
                if (!userController.isAuthenticated()) {
                    // Show login/register menu
                    showLoginMenu();
                } else {
                    // Show MUSIC FEATURES menu immediately after login
                    showMusicFeaturesMenu();
                }
            }
        } catch (Exception e) {
            System.out.println("A critical error occurred: " + e.getMessage());
            LogUtil.logError("Critical error in main application", e);
        } finally {
            scanner.close();
            LogUtil.logInfo("RevPlay Application Shutdown");
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== RevPlay Login ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    loginUser();
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
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
            LogUtil.logError("Unexpected error", e);
        }
    }

    private static void showMusicFeaturesMenu() {
        User currentUser = userController.getCurrentUser();

        System.out.println("\n=== RevPlay Music Streamer ===");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("What would you like to do?");
        System.out.println("=== MUSIC FEATURES ===");
        System.out.println("1. Browse & Play Music");
        System.out.println("2. Search Songs");
        System.out.println("3. Manage Playlists");
        System.out.println("4. View Public Playlists");
        System.out.println("=== ACCOUNT ===");
        System.out.println("5. User Dashboard");

        if ("ARTIST".equals(currentUser.getUserType())) {
            System.out.println("6. Artist Dashboard");
        }

        System.out.println("7. Logout");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    // Browse & Play Music
                    songController.setCurrentUserId(currentUser.getUserId());
                    songController.showMusicPlayer();
                    break;
                case 2:
                    // Search Songs
                    songController.showSearchMenu();
                    break;
                case 3:
                    // Manage Playlists
                    playlistController.setCurrentUserId(currentUser.getUserId());
                    playlistController.showPlaylistMenu();
                    break;
                case 4:
                    // View Public Playlists
                    playlistController.showPlaylistMenu();
                    break;
                case 5:
                    // User Dashboard
                    userController.showMainMenu();
                    break;
                case 6:
                    if ("ARTIST".equals(currentUser.getUserType())) {
                        handleArtistDashboard(currentUser);
                    } else {
                        System.out.println("Access denied. Artist features only available for artists.");
                    }
                    break;
                case 7:
                    // Logout
                    userController.setCurrentUser(null);
                    artistController = new ArtistController();
                    System.out.println("Logged out successfully.");
                    break;
                case 8:
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
            LogUtil.logError("Unexpected error", e);
        }
    }

    private static void loginUser() throws CustomException {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Use UserService to authenticate
        User user = userService.loginUser(username, password);
        // Set the authenticated user in controller
        userController.setCurrentUser(user);
        System.out.println("Login successful! Welcome, " + user.getUsername());
    }

    private static void registerUser() throws CustomException {
        System.out.println("\n=== Register ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("User Type (USER/ARTIST): ");
        String userType = scanner.nextLine().toUpperCase();

        // Use UserService to register
        User user = userService.registerUser(username, password, email, fullName, userType);
        // Auto-login after registration
        userController.setCurrentUser(user);
        System.out.println("Registration successful! User ID: " + user.getUserId());

        if ("ARTIST".equals(userType)) {
            System.out.println("Please complete your artist profile in the Artist Dashboard.");
        }
    }

    private static void handleArtistDashboard(User currentUser) throws CustomException {
        if (!artistController.isArtistRegistered(currentUser.getUserId())) {
            System.out.println("\n=== Complete Artist Profile ===");
            System.out.print("Do you want to complete your artist profile? (yes/no): ");
            String response = scanner.nextLine().toLowerCase();

            if (response.equals("yes") || response.equals("y")) {
                // The ArtistController.registerArtist() method takes only 1 parameter (userId)
                // and handles the input collection internally
                artistController.registerArtist(currentUser.getUserId());
            }
        }

        artistController.showArtistDashboard(currentUser.getUserId());
    }
}