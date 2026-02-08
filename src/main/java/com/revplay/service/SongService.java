package com.revplay.service;

import com.revplay.dao.SongDAO;
import com.revplay.dao.impl.SongDAOImpl;
import com.revplay.model.Song;
import com.revplay.exception.CustomException;
import java.util.List;

public class SongService {
    private final SongDAO songDAO;

    public SongService() {
        this.songDAO = new SongDAOImpl();
    }

    public List<Song> getAllSongs() throws CustomException {
        return songDAO.getAllSongs();
    }

    public List<Song> searchSongs(String keyword) throws CustomException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException("VALIDATION_010", "Search keyword is required",
                    "Please enter a search keyword.");
        }
        return songDAO.searchSongs(keyword.trim());
    }

    public List<Song> getSongsByGenre(String genre) throws CustomException {
        if (genre == null || genre.trim().isEmpty()) {
            throw new CustomException("VALIDATION_023", "Genre is required",
                    "Please enter a music genre.");
        }
        return songDAO.getSongsByGenre(genre.trim());
    }

    public List<Song> getTopSongs(int limit) throws CustomException {
        if (limit <= 0) {
            throw new CustomException("VALIDATION_016", "Invalid limit",
                    "Limit must be a positive number.");
        }
        return songDAO.getTopSongs(limit);
    }

    public List<Song> getRecentlyAddedSongs(int limit) throws CustomException {
        if (limit <= 0) {
            throw new CustomException("VALIDATION_016", "Invalid limit",
                    "Limit must be a positive number.");
        }
        return songDAO.getRecentlyAddedSongs(limit);
    }

    public Song getSongById(int songId) throws CustomException {
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }
        return songDAO.getSongById(songId);
    }

    public boolean incrementPlayCount(int songId) throws CustomException {
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }
        return songDAO.incrementPlayCount(songId);
    }

    public void playSongSimulation(int songId) throws CustomException {
        Song song = getSongById(songId);

        System.out.println("\n=== Now Playing ===");
        System.out.println("Title: " + song.getTitle());
        System.out.println("Artist ID: " + song.getArtistId());
        System.out.println("Duration: " + song.getFormattedDuration());
        System.out.println("Genre: " + song.getGenre());
        System.out.println("Play count: " + (song.getPlayCount() + 1));

        // Simulate play progress
        System.out.print("Playing: [");
        int duration = Math.min(song.getDurationSeconds(), 30); // Show max 30 seconds in simulation
        for (int i = 0; i < duration; i++) {
            System.out.print("=");
            try {
                Thread.sleep(100); // Simulate time passing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("]");

        System.out.println("Song completed!");
    }
    }
