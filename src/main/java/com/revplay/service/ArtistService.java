package com.revplay.service;

import com.revplay.dao.ArtistDAO;
import com.revplay.dao.SongDAO;
import com.revplay.dao.impl.ArtistDAOImpl;
import com.revplay.dao.impl.SongDAOImpl;
import com.revplay.model.Artist;
import com.revplay.model.Song;
import com.revplay.exception.CustomException;
import java.util.List;

public class ArtistService {
    private final ArtistDAO artistDAO;
    private final SongDAO songDAO;

    public ArtistService() {
        this.artistDAO = new ArtistDAOImpl();
        this.songDAO = new SongDAOImpl();
    }

    public Artist registerArtist(int userId, String stageName, String genre,
                                 String recordLabel, String socialMediaLinks) throws CustomException {
        validateArtistInput(stageName, genre);

        Artist artist = new Artist();
        artist.setArtistId(userId);
        artist.setStageName(stageName);
        artist.setGenre(genre);
        artist.setRecordLabel(recordLabel);
        artist.setMonthlyListeners(0);
        artist.setSocialMediaLinks(socialMediaLinks);

        return artistDAO.registerArtist(artist);
    }

    public Artist getArtistProfile(int artistId) throws CustomException {
        if (artistId <= 0) {
            throw new CustomException("VALIDATION_015", "Invalid artist ID",
                    "Artist ID must be a positive number.");
        }
        return artistDAO.getArtistById(artistId);
    }

    public boolean updateArtistProfile(int artistId, String stageName, String genre,
                                       String recordLabel, String socialMediaLinks) throws CustomException {
        if (artistId <= 0) {
            throw new CustomException("VALIDATION_015", "Invalid artist ID",
                    "Artist ID must be a positive number.");
        }
        validateArtistInput(stageName, genre);

        Artist artist = artistDAO.getArtistById(artistId);
        artist.setStageName(stageName);
        artist.setGenre(genre);
        artist.setRecordLabel(recordLabel);
        artist.setSocialMediaLinks(socialMediaLinks);

        return artistDAO.updateArtist(artist);
    }

    public List<Artist> searchArtists(String keyword) throws CustomException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException("VALIDATION_010", "Search keyword is required",
                    "Please enter a search keyword.");
        }
        return artistDAO.searchArtists(keyword.trim());
    }

    public List<Artist> getTopArtists(int limit) throws CustomException {
        if (limit <= 0) {
            throw new CustomException("VALIDATION_016", "Invalid limit",
                    "Limit must be a positive number.");
        }
        return artistDAO.getTopArtists(limit);
    }

    public Song uploadSong(int artistId, String title, Integer albumId,
                           int durationSeconds, String genre, String filePath) throws CustomException {
        if (artistId <= 0) {
            throw new CustomException("VALIDATION_015", "Invalid artist ID",
                    "Artist ID must be a positive number.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new CustomException("VALIDATION_017", "Song title is required",
                    "Please enter a song title.");
        }
        if (durationSeconds <= 0) {
            throw new CustomException("VALIDATION_018", "Invalid duration",
                    "Song duration must be a positive number.");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CustomException("VALIDATION_019", "File path is required",
                    "Please provide a file path for the song.");
        }

        Song song = new Song();
        song.setTitle(title.trim());
        song.setArtistId(artistId);
        song.setAlbumId(albumId);
        song.setDurationSeconds(durationSeconds);
        song.setGenre(genre);
        song.setFilePath(filePath.trim());
        song.setPlayCount(0);
        song.setActive(true);

        Song uploadedSong = songDAO.addSong(song);

        // Update artist's monthly listeners (simplified - in real app, this would be based on actual plays)
        Artist artist = artistDAO.getArtistById(artistId);
        artistDAO.updateMonthlyListeners(artistId, artist.getMonthlyListeners() + 100); // Example increment

        return uploadedSong;
    }

    public List<Song> getArtistSongs(int artistId) throws CustomException {
        if (artistId <= 0) {
            throw new CustomException("VALIDATION_015", "Invalid artist ID",
                    "Artist ID must be a positive number.");
        }
        return songDAO.getSongsByArtist(artistId);
    }

    public List<Song> getArtistTopSongs(int artistId, int limit) throws CustomException {
        if (artistId <= 0) {
            throw new CustomException("VALIDATION_015", "Invalid artist ID",
                    "Artist ID must be a positive number.");
        }
        if (limit <= 0) {
            throw new CustomException("VALIDATION_016", "Invalid limit",
                    "Limit must be a positive number.");
        }

        List<Song> allSongs = songDAO.getSongsByArtist(artistId);
        return allSongs.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getPlayCount(), s1.getPlayCount()))
                .limit(limit)
                .toList();
    }

    public boolean updateSong(int songId, String title, Integer albumId,
                              int durationSeconds, String genre, String filePath) throws CustomException {
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new CustomException("VALIDATION_017", "Song title is required",
                    "Please enter a song title.");
        }
        if (durationSeconds <= 0) {
            throw new CustomException("VALIDATION_018", "Invalid duration",
                    "Song duration must be a positive number.");
        }

        Song song = songDAO.getSongById(songId);
        song.setTitle(title.trim());
        song.setAlbumId(albumId);
        song.setDurationSeconds(durationSeconds);
        song.setGenre(genre);

        if (filePath != null && !filePath.trim().isEmpty()) {
            song.setFilePath(filePath.trim());
        }

        return songDAO.updateSong(song);
    }

    public boolean deleteSong(int songId) throws CustomException {
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }
        return songDAO.deleteSong(songId);
    }

    public List<Artist> getAllArtists() throws CustomException {
        return artistDAO.getAllArtists();
    }

    private void validateArtistInput(String stageName, String genre) throws CustomException {
        if (stageName == null || stageName.trim().isEmpty()) {
            throw new CustomException("VALIDATION_021", "Stage name is required",
                    "Please enter a stage name.");
        }
        if (stageName.length() < 2) {
            throw new CustomException("VALIDATION_022", "Stage name too short",
                    "Stage name must be at least 2 characters long.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new CustomException("VALIDATION_023", "Genre is required",
                    "Please enter a music genre.");
        }
    }
}