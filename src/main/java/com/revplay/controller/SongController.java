package com.revplay.controller;

import com.revplay.service.SongService;
import com.revplay.model.Song;
import com.revplay.exception.CustomException;
import com.revplay.util.LogUtil;
import java.util.List;
import java.util.Scanner;

public class SongController {
    private final SongService songService;
    private final Scanner scanner;
    private int currentUserId;

    public SongController() {
        this.songService = new SongService();
        this.scanner = new Scanner(System.in);
        this.currentUserId = 0;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public void showMusicPlayer() {
        while (true) {
            System.out.println("\n=== Music Player ===");
            System.out.println("1. Browse All Songs");
            System.out.println("2. Search Songs");
            System.out.println("3. Browse by Genre");
            System.out.println("4. Top Songs");
            System.out.println("5. Recently Added");
            System.out.println("6. Play Song");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        browseAllSongs();
                        break;
                    case 2:
                        searchSongs();
                        break;
                    case 3:
                        browseByGenre();
                        break;
                    case 4:
                        viewTopSongs();
                        break;
                    case 5:
                        viewRecentlyAdded();
                        break;
                    case 6:
                        playSong();
                        break;
                    case 7:
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
                LogUtil.logError("Unexpected error in song controller", e);
            }
        }
    }

    public void showSearchMenu() {
        while (true) {
            System.out.println("\n=== Song Search ===");
            System.out.println("1. Search Songs");
            System.out.println("2. Browse by Genre");
            System.out.println("3. Top Songs");
            System.out.println("4. Recently Added");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        searchSongs();
                        break;
                    case 2:
                        browseByGenre();
                        break;
                    case 3:
                        viewTopSongs();
                        break;
                    case 4:
                        viewRecentlyAdded();
                        break;
                    case 5:
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
                LogUtil.logError("Unexpected error in search menu", e);
            }
        }
    }

    private void browseAllSongs() throws CustomException {
        System.out.println("\n=== All Songs ===");

        List<Song> songs = songService.getAllSongs();

        if (songs.isEmpty()) {
            System.out.println("No songs available in the system.");
        } else {
            System.out.println("Total songs: " + songs.size());
            displaySongsTable(songs);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchSongs() throws CustomException {
        System.out.println("\n=== Search Songs ===");

        System.out.print("Enter search keyword (title, artist, album): ");
        String keyword = scanner.nextLine();

        List<Song> songs = songService.searchSongs(keyword);

        if (songs.isEmpty()) {
            System.out.println("No songs found matching your search.");
        } else {
            System.out.println("\nFound " + songs.size() + " song(s):");
            displaySongsTable(songs);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void browseByGenre() throws CustomException {
        System.out.println("\n=== Browse by Genre ===");

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        List<Song> songs = songService.getSongsByGenre(genre);

        if (songs.isEmpty()) {
            System.out.println("No songs found in genre: " + genre);
        } else {
            System.out.println("\nFound " + songs.size() + " song(s) in genre '" + genre + "':");
            displaySongsTable(songs);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewTopSongs() throws CustomException {
        System.out.println("\n=== Top Songs ===");

        System.out.print("Enter number of top songs to view: ");
        int limit = Integer.parseInt(scanner.nextLine());

        List<Song> topSongs = songService.getTopSongs(limit);

        if (topSongs.isEmpty()) {
            System.out.println("No songs found.");
        } else {
            System.out.println("\nTop " + topSongs.size() + " song(s):");
            System.out.println("Rank\tID\tTitle\t\t\tArtist\tDuration\tPlays\tGenre");
            System.out.println("-------------------------------------------------------------------");

            int rank = 1;
            for (Song song : topSongs) {
                System.out.printf("%d\t%d\t%-20s\t%d\t%s\t\t%d\t%s\n",
                        rank++,
                        song.getSongId(),
                        song.getTitle().length() > 20 ? song.getTitle().substring(0, 17) + "..." : song.getTitle(),
                        song.getArtistId(),
                        song.getFormattedDuration(),
                        song.getPlayCount(),
                        song.getGenre()
                );
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewRecentlyAdded() throws CustomException {
        System.out.println("\n=== Recently Added Songs ===");

        System.out.print("Enter number of recent songs to view: ");
        int limit = Integer.parseInt(scanner.nextLine());

        List<Song> recentSongs = songService.getRecentlyAddedSongs(limit);

        if (recentSongs.isEmpty()) {
            System.out.println("No songs found.");
        } else {
            System.out.println("\nRecently added " + recentSongs.size() + " song(s):");
            displaySongsTable(recentSongs);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void playSong() throws CustomException {
        System.out.println("\n=== Play Song ===");

        System.out.print("Enter song ID: ");
        int songId = Integer.parseInt(scanner.nextLine());

        songService.playSongSimulation(songId);

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displaySongsTable(List<Song> songs) {
        System.out.println("ID\tTitle\t\t\tArtist\tDuration\tPlays\tGenre");
        System.out.println("------------------------------------------------------------");

        for (Song song : songs) {
            System.out.printf("%d\t%-20s\t%d\t%s\t\t%d\t%s\n",
                    song.getSongId(),
                    song.getTitle().length() > 20 ? song.getTitle().substring(0, 17) + "..." : song.getTitle(),
                    song.getArtistId(),
                    song.getFormattedDuration(),
                    song.getPlayCount(),
                    song.getGenre()
            );
        }
    }
}