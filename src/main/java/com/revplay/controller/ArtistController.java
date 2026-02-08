package com.revplay.controller;

import com.revplay.model.Artist;
import com.revplay.model.Song;
import com.revplay.service.ArtistService;
import com.revplay.exception.CustomException;
import com.revplay.util.LogUtil;
import java.util.List;
import java.util.Scanner;

public class ArtistController {
    private final ArtistService artistService;
    private final Scanner scanner;
    private Artist currentArtist;

    public ArtistController() {
        this.artistService = new ArtistService();
        this.scanner = new Scanner(System.in);
        this.currentArtist = null;
    }

    public void showArtistDashboard(int userId) throws CustomException {
        // Load artist profile
        currentArtist = artistService.getArtistProfile(userId);

        while (true) {
            showArtistMenu();
        }
    }

    private void showArtistMenu() {
        System.out.println("\n=== Artist Dashboard - " + currentArtist.getStageName() + " ===");
        System.out.println("1. View Artist Profile");
        System.out.println("2. Update Artist Profile");
        System.out.println("3. Upload Song");
        System.out.println("4. View My Songs");
        System.out.println("5. Update Song");
        System.out.println("6. Delete Song");
        System.out.println("7. View Top Songs");
        System.out.println("8. Search Artists");
        System.out.println("9. View Top Artists");
        System.out.println("10. Switch to User Dashboard");
        System.out.println("11. Logout");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewArtistProfile();
                    break;
                case 2:
                    updateArtistProfile();
                    break;
                case 3:
                    uploadSong();
                    break;
                case 4:
                    viewArtistSongs();
                    break;
                case 5:
                    updateSong();
                    break;
                case 6:
                    deleteSong();
                    break;
                case 7:
                    viewTopSongs();
                    break;
                case 8:
                    searchArtists();
                    break;
                case 9:
                    viewTopArtists();
                    break;
                case 10:
                    return; // Return to main menu for dashboard switch
                case 11:
                    logout();
                    return;
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
            LogUtil.logError("Unexpected error in artist controller", e);
        }
    }

    public void registerArtist(int userId) throws CustomException {
        System.out.println("\n=== Complete Artist Registration ===");

        System.out.print("Enter stage name: ");
        String stageName = scanner.nextLine();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter record label (optional): ");
        String recordLabel = scanner.nextLine();

        System.out.print("Enter social media links (optional): ");
        String socialMediaLinks = scanner.nextLine();

        Artist artist = artistService.registerArtist(userId, stageName, genre, recordLabel, socialMediaLinks);
        System.out.println("Artist registration completed successfully!");
        System.out.println("Stage Name: " + artist.getStageName());
        System.out.println("Genre: " + artist.getGenre());
    }

    private void viewArtistProfile() {
        System.out.println("\n=== Artist Profile ===");
        System.out.println("Artist ID: " + currentArtist.getArtistId());
        System.out.println("Stage Name: " + currentArtist.getStageName());
        System.out.println("Genre: " + currentArtist.getGenre());
        System.out.println("Record Label: " +
                (currentArtist.getRecordLabel() != null ? currentArtist.getRecordLabel() : "Not specified"));
        System.out.println("Monthly Listeners: " + currentArtist.getMonthlyListeners());

        if (currentArtist.getSocialMediaLinks() != null && !currentArtist.getSocialMediaLinks().isEmpty()) {
            System.out.println("Social Media: " + currentArtist.getSocialMediaLinks());
        }

        if (currentArtist.getUserDetails() != null) {
            System.out.println("\nUser Details:");
            System.out.println("Email: " + currentArtist.getUserDetails().getEmail());
            System.out.println("Full Name: " + currentArtist.getUserDetails().getFullName());

            if (currentArtist.getUserDetails().getBio() != null &&
                    !currentArtist.getUserDetails().getBio().isEmpty()) {
                System.out.println("Bio: " + currentArtist.getUserDetails().getBio());
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateArtistProfile() throws CustomException {
        System.out.println("\n=== Update Artist Profile ===");

        System.out.print("Enter new stage name (current: " + currentArtist.getStageName() + "): ");
        String stageName = scanner.nextLine();

        System.out.print("Enter new genre (current: " + currentArtist.getGenre() + "): ");
        String genre = scanner.nextLine();

        System.out.print("Enter record label (current: " +
                (currentArtist.getRecordLabel() != null ? currentArtist.getRecordLabel() : "None") + "): ");
        String recordLabel = scanner.nextLine();

        System.out.print("Enter social media links (current: " +
                (currentArtist.getSocialMediaLinks() != null ? currentArtist.getSocialMediaLinks() : "None") + "): ");
        String socialMediaLinks = scanner.nextLine();

        boolean success = artistService.updateArtistProfile(
                currentArtist.getArtistId(), stageName, genre, recordLabel, socialMediaLinks);

        if (success) {
            // Update current artist object
            currentArtist.setStageName(stageName);
            currentArtist.setGenre(genre);
            currentArtist.setRecordLabel(recordLabel.isEmpty() ? null : recordLabel);
            currentArtist.setSocialMediaLinks(socialMediaLinks.isEmpty() ? null : socialMediaLinks);

            System.out.println("Artist profile updated successfully!");
        }
    }

    private void uploadSong() throws CustomException {
        System.out.println("\n=== Upload Song ===");

        System.out.print("Enter song title: ");
        String title = scanner.nextLine();

        System.out.print("Enter album ID (optional, press Enter to skip): ");
        String albumIdInput = scanner.nextLine();
        Integer albumId = null;
        if (!albumIdInput.isEmpty()) {
            try {
                albumId = Integer.parseInt(albumIdInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid album ID. Skipping album association.");
            }
        }

        System.out.print("Enter duration in seconds: ");
        int durationSeconds = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine();

        Song song = artistService.uploadSong(
                currentArtist.getArtistId(), title, albumId, durationSeconds, genre, filePath);

        System.out.println("Song uploaded successfully!");
        System.out.println("Song ID: " + song.getSongId());
        System.out.println("Title: " + song.getTitle());
        System.out.println("Duration: " + song.getFormattedDuration());
    }

    private void viewArtistSongs() throws CustomException {
        System.out.println("\n=== My Songs ===");

        List<Song> songs = artistService.getArtistSongs(currentArtist.getArtistId());

        if (songs.isEmpty()) {
            System.out.println("You haven't uploaded any songs yet.");
        } else {
            System.out.println("You have " + songs.size() + " song(s):");
            System.out.println("ID\tTitle\t\t\tDuration\tPlays\tGenre");
            System.out.println("------------------------------------------------------------");

            for (Song song : songs) {
                System.out.printf("%d\t%-20s\t%s\t\t%d\t%s\n",
                        song.getSongId(),
                        song.getTitle().length() > 20 ? song.getTitle().substring(0, 17) + "..." : song.getTitle(),
                        song.getFormattedDuration(),
                        song.getPlayCount(),
                        song.getGenre()
                );
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateSong() throws CustomException {
        System.out.println("\n=== Update Song ===");

        System.out.print("Enter song ID to update: ");
        int songId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new title: ");
        String title = scanner.nextLine();

        System.out.print("Enter album ID (optional, press Enter to keep current): ");
        String albumIdInput = scanner.nextLine();
        Integer albumId = null;
        if (!albumIdInput.isEmpty()) {
            try {
                albumId = Integer.parseInt(albumIdInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid album ID. Keeping current album association.");
            }
        }

        System.out.print("Enter new duration in seconds: ");
        int durationSeconds = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter new file path (optional, press Enter to keep current): ");
        String filePath = scanner.nextLine();

        boolean success = artistService.updateSong(songId, title, albumId, durationSeconds, genre,
                filePath.isEmpty() ? null : filePath);

        if (success) {
            System.out.println("Song updated successfully!");
        }
    }

    private void deleteSong() throws CustomException {
        System.out.println("\n=== Delete Song ===");

        System.out.print("Enter song ID to delete: ");
        int songId = Integer.parseInt(scanner.nextLine());

        System.out.print("Are you sure you want to delete this song? (yes/no): ");
        String confirmation = scanner.nextLine().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            boolean success = artistService.deleteSong(songId);

            if (success) {
                System.out.println("Song deleted successfully!");
            }
        } else {
            System.out.println("Song deletion cancelled.");
        }
    }

    private void viewTopSongs() throws CustomException {
        System.out.println("\n=== My Top Songs ===");

        System.out.print("Enter number of top songs to view: ");
        int limit = Integer.parseInt(scanner.nextLine());

        List<Song> topSongs = artistService.getArtistTopSongs(currentArtist.getArtistId(), limit);

        if (topSongs.isEmpty()) {
            System.out.println("No songs found.");
        } else {
            System.out.println("Top " + topSongs.size() + " song(s):");
            System.out.println("Rank\tID\tTitle\t\t\tPlays\tDuration");
            System.out.println("------------------------------------------------");

            int rank = 1;
            for (Song song : topSongs) {
                System.out.printf("%d\t%d\t%-20s\t%d\t%s\n",
                        rank++,
                        song.getSongId(),
                        song.getTitle().length() > 20 ? song.getTitle().substring(0, 17) + "..." : song.getTitle(),
                        song.getPlayCount(),
                        song.getFormattedDuration()
                );
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchArtists() throws CustomException {
        System.out.println("\n=== Search Artists ===");

        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<Artist> artists = artistService.searchArtists(keyword);

        if (artists.isEmpty()) {
            System.out.println("No artists found matching your search.");
        } else {
            System.out.println("\nFound " + artists.size() + " artist(s):");
            System.out.println("ID\tStage Name\t\tGenre\t\tListeners");
            System.out.println("------------------------------------------------------------");

            for (Artist artist : artists) {
                System.out.printf("%d\t%-20s\t%-15s\t%d\n",
                        artist.getArtistId(),
                        artist.getStageName().length() > 20 ? artist.getStageName().substring(0, 17) + "..." : artist.getStageName(),
                        artist.getGenre().length() > 15 ? artist.getGenre().substring(0, 12) + "..." : artist.getGenre(),
                        artist.getMonthlyListeners()
                );
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewTopArtists() throws CustomException {
        System.out.println("\n=== Top Artists ===");

        System.out.print("Enter number of top artists to view: ");
        int limit = Integer.parseInt(scanner.nextLine());

        List<Artist> topArtists = artistService.getTopArtists(limit);

        if (topArtists.isEmpty()) {
            System.out.println("No artists found.");
        } else {
            System.out.println("Top " + topArtists.size() + " artist(s):");
            System.out.println("Rank\tID\tStage Name\t\tGenre\t\tListeners");
            System.out.println("------------------------------------------------------------");

            int rank = 1;
            for (Artist artist : topArtists) {
                System.out.printf("%d\t%d\t%-20s\t%-15s\t%d\n",
                        rank++,
                        artist.getArtistId(),
                        artist.getStageName().length() > 20 ? artist.getStageName().substring(0, 17) + "..." : artist.getStageName(),
                        artist.getGenre().length() > 15 ? artist.getGenre().substring(0, 12) + "..." : artist.getGenre(),
                        artist.getMonthlyListeners()
                );
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void logout() {
        System.out.println("Logging out from Artist Dashboard...");
        currentArtist = null;
    }

    // Getters
    public Artist getCurrentArtist() {
        return currentArtist;
    }

    public boolean isArtistRegistered(int userId) {
        try {
            artistService.getArtistProfile(userId);
            return true;
        } catch (CustomException e) {
            return false;
        }
    }
}