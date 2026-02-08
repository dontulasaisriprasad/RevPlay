package com.revplay.controller;

import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.service.PlaylistService;
import com.revplay.service.SongService;
import com.revplay.exception.CustomException;
import com.revplay.util.LogUtil;
import java.util.List;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class PlaylistController {
    private final PlaylistService playlistService;
    private final SongService songService;
    private final Scanner scanner;
    private int currentUserId;

    public PlaylistController() {
        this.playlistService = new PlaylistService();
        this.songService = new SongService();
        this.scanner = new Scanner(System.in);
        this.currentUserId = 0;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    // This is the missing method that Main.java is calling
    public void showPublicPlaylists() {
        try {
            viewPublicPlaylists();
        } catch (CustomException e) {
            System.out.println("Error: " + e.getUserMessage());
            LogUtil.logError(e.getMessage(), e);
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            LogUtil.logError("Unexpected error in showPublicPlaylists", e);
        }

        waitForEnterKey();
    }

    public void showPlaylistMenu() {
        while (true) {
            System.out.println("\n=== Playlist Management ===");
            System.out.println("1. Create Playlist");
            System.out.println("2. View My Playlists");
            System.out.println("3. View Playlist Details");
            System.out.println("4. Update Playlist");
            System.out.println("5. Delete Playlist");
            System.out.println("6. Add Song to Playlist");
            System.out.println("7. Remove Song from Playlist");
            System.out.println("8. View Public Playlists");
            System.out.println("9. Search Public Playlists");
            System.out.println("10. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Please enter a choice.");
                    continue;
                }

                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        createPlaylist();
                        break;
                    case 2:
                        viewMyPlaylists();
                        break;
                    case 3:
                        viewPlaylistDetails();
                        break;
                    case 4:
                        updatePlaylist();
                        break;
                    case 5:
                        deletePlaylist();
                        break;
                    case 6:
                        addSongToPlaylist();
                        break;
                    case 7:
                        removeSongFromPlaylist();
                        break;
                    case 8:
                        viewPublicPlaylists();
                        break;
                    case 9:
                        searchPublicPlaylists();
                        break;
                    case 10:
                        System.out.println("Returning to Main Menu...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1-10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (1-10).");
            } catch (CustomException e) {
                System.out.println("Error: " + e.getUserMessage());
                LogUtil.logError(e.getMessage(), e);
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                LogUtil.logError("Unexpected error in playlist controller", e);
            }
        }
    }

    private void createPlaylist() throws CustomException {
        System.out.println("\n=== Create Playlist ===");

        System.out.print("Enter playlist name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Playlist name cannot be empty.");
            waitForEnterKey();
            return;
        }

        System.out.print("Enter description (optional): ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            description = null;
        }

        System.out.print("Make playlist public? (yes/no): ");
        String publicInput = scanner.nextLine().trim().toLowerCase();
        boolean isPublic = publicInput.equals("yes") || publicInput.equals("y");

        Playlist playlist = playlistService.createPlaylist(currentUserId, name, description, isPublic);
        System.out.println("\n✓ Playlist created successfully!");
        System.out.println("Playlist ID: " + playlist.getPlaylistId());
        System.out.println("Name: " + playlist.getName());
        System.out.println("Visibility: " + (playlist.isPublic() ? "Public" : "Private"));

        waitForEnterKey();
    }

    private void viewMyPlaylists() throws CustomException {
        System.out.println("\n=== My Playlists ===");

        List<Playlist> playlists = playlistService.getUserPlaylists(currentUserId);

        if (playlists.isEmpty()) {
            System.out.println("You haven't created any playlists yet.");
        } else {
            System.out.println("You have " + playlists.size() + " playlist(s):");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("ID      Name                    Songs   Duration    Visibility  Created");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");

            for (Playlist playlist : playlists) {
                String playlistName = playlist.getName();
                if (playlistName.length() > 20) {
                    playlistName = playlistName.substring(0, 17) + "...";
                }

                System.out.printf("%-7d %-23s %-7d %-11s %-11s %-10s\n",
                        playlist.getPlaylistId(),
                        playlistName,
                        playlist.getTotalSongs(),
                        playlist.getFormattedDuration(),
                        playlist.isPublic() ? "Public" : "Private",
                        playlist.getCreatedDate().toLocalDate()
                );
            }
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");
        }

        waitForEnterKey();
    }

    private void viewPlaylistDetails() throws CustomException {
        System.out.println("\n=== Playlist Details ===");

        System.out.print("Enter playlist ID (or '0' to cancel): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0") || input.isEmpty()) {
            System.out.println("Operation cancelled.");
            waitForEnterKey();
            return;
        }

        int playlistId;
        try {
            playlistId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid playlist ID. Please enter a valid number.");
            waitForEnterKey();
            return;
        }

        Playlist playlist = playlistService.getPlaylistDetails(playlistId);

        System.out.println("\n══════════════════════════════════════════════════════════════");
        System.out.println("Playlist: " + playlist.getName());
        System.out.println("══════════════════════════════════════════════════════════════");
        System.out.println("Description: " +
                (playlist.getDescription() != null && !playlist.getDescription().isEmpty()
                        ? playlist.getDescription() : "No description"));
        System.out.println("Owner: User ID " + playlist.getUserId());
        System.out.println("Visibility: " + (playlist.isPublic() ? "Public" : "Private"));
        System.out.println("Created: " + playlist.getCreatedDate());
        System.out.println("Total Songs: " + playlist.getTotalSongs());
        System.out.println("Total Duration: " + playlist.getFormattedDuration());

        List<Song> songs = playlistService.getPlaylistSongs(playlistId);
        if (songs.isEmpty()) {
            System.out.println("\nThis playlist is empty.");
        } else {
            System.out.println("\nSongs in this playlist:");
            System.out.println("══════════════════════════════════════════════════════════════");
            System.out.println("No.  ID      Title                    Artist  Duration");
            System.out.println("══════════════════════════════════════════════════════════════");

            int position = 1;
            for (Song song : songs) {
                String title = song.getTitle();
                if (title.length() > 20) {
                    title = title.substring(0, 17) + "...";
                }

                System.out.printf("%-4d %-7d %-23s %-7d %-10s\n",
                        position++,
                        song.getSongId(),
                        title,
                        song.getArtistId(),
                        song.getFormattedDuration()
                );
            }
            System.out.println("══════════════════════════════════════════════════════════════");
        }

        waitForEnterKey();
    }

    private void updatePlaylist() throws CustomException {
        System.out.println("\n=== Update Playlist ===");

        System.out.print("Enter playlist ID to update (or '0' to cancel): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0") || input.isEmpty()) {
            System.out.println("Operation cancelled.");
            waitForEnterKey();
            return;
        }

        int playlistId;
        try {
            playlistId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid playlist ID. Please enter a valid number.");
            waitForEnterKey();
            return;
        }

        // First, show current playlist details
        Playlist playlist = playlistService.getPlaylistDetails(playlistId);

        System.out.println("\nCurrent playlist details:");
        System.out.println("Name: " + playlist.getName());
        System.out.println("Description: " +
                (playlist.getDescription() != null ? playlist.getDescription() : "None"));
        System.out.println("Visibility: " + (playlist.isPublic() ? "Public" : "Private"));
        System.out.println();

        System.out.print("Enter new name (press Enter to keep current): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = playlist.getName();
        }

        System.out.print("Enter new description (press Enter to keep current): ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            description = playlist.getDescription();
        }

        System.out.print("Change visibility? (public/private/keep): ");
        String visibilityInput = scanner.nextLine().trim().toLowerCase();
        boolean isPublic = playlist.isPublic();

        if (visibilityInput.equals("public")) {
            isPublic = true;
        } else if (visibilityInput.equals("private")) {
            isPublic = false;
        }

        boolean success = playlistService.updatePlaylist(playlistId, name, description, isPublic);

        if (success) {
            System.out.println("\n✓ Playlist updated successfully!");
        } else {
            System.out.println("Failed to update playlist.");
        }

        waitForEnterKey();
    }

    private void deletePlaylist() throws CustomException {
        System.out.println("\n=== Delete Playlist ===");

        System.out.print("Enter playlist ID to delete (or '0' to cancel): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0") || input.isEmpty()) {
            System.out.println("Operation cancelled.");
            waitForEnterKey();
            return;
        }

        int playlistId;
        try {
            playlistId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid playlist ID. Please enter a valid number.");
            waitForEnterKey();
            return;
        }

        // Show playlist details before deletion
        try {
            Playlist playlist = playlistService.getPlaylistDetails(playlistId);
            System.out.println("\nPlaylist to delete: " + playlist.getName());
            System.out.println("Contains " + playlist.getTotalSongs() + " songs");
            System.out.println("Created: " + playlist.getCreatedDate().toLocalDate());
        } catch (CustomException e) {
            System.out.println("Playlist not found.");
            waitForEnterKey();
            return;
        }

        System.out.print("\nAre you sure you want to delete this playlist? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            boolean success = playlistService.deletePlaylist(playlistId);

            if (success) {
                System.out.println("\n✓ Playlist deleted successfully!");
            } else {
                System.out.println("Failed to delete playlist.");
            }
        } else {
            System.out.println("Playlist deletion cancelled.");
        }

        waitForEnterKey();
    }

    private void addSongToPlaylist() throws CustomException {
        System.out.println("\n=== Add Song to Playlist ===");

        System.out.print("Enter playlist ID: ");
        int playlistId = Integer.parseInt(scanner.nextLine());

        // Show available songs
        System.out.println("\n=== Available Songs ===");
        List<Song> allSongs = songService.getAllSongs();

        if (allSongs.isEmpty()) {
            System.out.println("No songs available in the system.");
            waitForEnterKey();
            return;
        }

        System.out.println("ID      Title                    Artist  Duration    Plays");
        System.out.println("══════════════════════════════════════════════════════════════");

        for (Song song : allSongs) {
            String title = song.getTitle();
            if (title.length() > 20) {
                title = title.substring(0, 17) + "...";
            }

            System.out.printf("%-7d %-23s %-7d %-11s %-7d\n",
                    song.getSongId(),
                    title,
                    song.getArtistId(),
                    song.getFormattedDuration(),
                    song.getPlayCount()
            );
        }

        System.out.print("\nEnter song ID to add (or '0' to cancel): ");
        String songInput = scanner.nextLine().trim();

        if (songInput.equals("0") || songInput.isEmpty()) {
            System.out.println("Operation cancelled.");
            waitForEnterKey();
            return;
        }

        int songId;
        try {
            songId = Integer.parseInt(songInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid song ID. Please enter a valid number.");
            waitForEnterKey();
            return;
        }

        // Check if song already in playlist
        if (playlistService.isSongInPlaylist(playlistId, songId)) {
            System.out.println("This song is already in the playlist.");
            waitForEnterKey();
            return;
        }

        boolean success = playlistService.addSongToPlaylist(playlistId, songId);

        if (success) {
            System.out.println("\n✓ Song added to playlist successfully!");
        } else {
            System.out.println("Failed to add song to playlist.");
        }

        waitForEnterKey();
    }

    private void removeSongFromPlaylist() throws CustomException {
        System.out.println("\n=== Remove Song from Playlist ===");

        System.out.print("Enter playlist ID: ");
        int playlistId = Integer.parseInt(scanner.nextLine());

        // Show songs in playlist
        List<Song> playlistSongs = playlistService.getPlaylistSongs(playlistId);

        if (playlistSongs.isEmpty()) {
            System.out.println("This playlist is empty.");
            waitForEnterKey();
            return;
        }

        System.out.println("\nSongs in playlist:");
        System.out.println("══════════════════════════════════════════════════════════════");
        System.out.println("ID      Title                    Artist  Duration");
        System.out.println("══════════════════════════════════════════════════════════════");

        for (Song song : playlistSongs) {
            String title = song.getTitle();
            if (title.length() > 20) {
                title = title.substring(0, 17) + "...";
            }

            System.out.printf("%-7d %-23s %-7d %-10s\n",
                    song.getSongId(),
                    title,
                    song.getArtistId(),
                    song.getFormattedDuration()
            );
        }

        System.out.print("\nEnter song ID to remove (or '0' to cancel): ");
        String songInput = scanner.nextLine().trim();

        if (songInput.equals("0") || songInput.isEmpty()) {
            System.out.println("Operation cancelled.");
            waitForEnterKey();
            return;
        }

        int songId;
        try {
            songId = Integer.parseInt(songInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid song ID. Please enter a valid number.");
            waitForEnterKey();
            return;
        }

        boolean success = playlistService.removeSongFromPlaylist(playlistId, songId);

        if (success) {
            System.out.println("\n✓ Song removed from playlist successfully!");
        } else {
            System.out.println("Failed to remove song from playlist.");
        }

        waitForEnterKey();
    }

    private void viewPublicPlaylists() throws CustomException {
        System.out.println("\n=== Public Playlists ===");

        List<Playlist> publicPlaylists = playlistService.getPublicPlaylists();

        if (publicPlaylists.isEmpty()) {
            System.out.println("No public playlists available.");
        } else {
            System.out.println("Found " + publicPlaylists.size() + " public playlist(s):");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("ID      Name                    Owner   Songs   Duration    Created");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");

            for (Playlist playlist : publicPlaylists) {
                String playlistName = playlist.getName();
                if (playlistName.length() > 20) {
                    playlistName = playlistName.substring(0, 17) + "...";
                }

                System.out.printf("%-7d %-23s %-7d %-7d %-11s %-10s\n",
                        playlist.getPlaylistId(),
                        playlistName,
                        playlist.getUserId(),
                        playlist.getTotalSongs(),
                        playlist.getFormattedDuration(),
                        playlist.getCreatedDate().toLocalDate()
                );
            }
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    private void searchPublicPlaylists() throws CustomException {
        System.out.println("\n=== Search Public Playlists ===");

        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            waitForEnterKey();
            return;
        }

        List<Playlist> playlists = playlistService.searchPublicPlaylists(keyword);

        if (playlists.isEmpty()) {
            System.out.println("No public playlists found matching your search.");
        } else {
            System.out.println("\nFound " + playlists.size() + " playlist(s):");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("ID      Name                    Owner   Songs   Duration    Created");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");

            for (Playlist playlist : playlists) {
                String playlistName = playlist.getName();
                if (playlistName.length() > 20) {
                    playlistName = playlistName.substring(0, 17) + "...";
                }

                System.out.printf("%-7d %-23s %-7d %-7d %-11s %-10s\n",
                        playlist.getPlaylistId(),
                        playlistName,
                        playlist.getUserId(),
                        playlist.getTotalSongs(),
                        playlist.getFormattedDuration(),
                        playlist.getCreatedDate().toLocalDate()
                );
            }
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════");
        }

        waitForEnterKey();
    }

    private void waitForEnterKey() {
        System.out.print("\nPress Enter to continue...");
        try {
            scanner.nextLine();
        } catch (NoSuchElementException e) {
            LogUtil.logError("Scanner issue in waitForEnterKey", e);
        }
    }

    // Cleanup method
    public void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
    }
}