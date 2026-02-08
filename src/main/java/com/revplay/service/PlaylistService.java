package com.revplay.service;

import com.revplay.dao.PlaylistDAO;
import com.revplay.dao.SongDAO;
import com.revplay.dao.impl.PlaylistDAOImpl;
import com.revplay.dao.impl.SongDAOImpl;
import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.exception.CustomException;
import java.util.List;

public class PlaylistService {
    private final PlaylistDAO playlistDAO;
    private final SongDAO songDAO;

    public PlaylistService() {
        this.playlistDAO = new PlaylistDAOImpl();
        this.songDAO = new SongDAOImpl();
    }

    public Playlist createPlaylist(int userId, String name, String description,
                                   boolean isPublic) throws CustomException {
        validatePlaylistInput(name);

        Playlist playlist = new Playlist();
        playlist.setUserId(userId);
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);
        playlist.setTotalSongs(0);
        playlist.setTotalDuration(0);

        return playlistDAO.createPlaylist(playlist);
    }

    public Playlist getPlaylistDetails(int playlistId) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        return playlistDAO.getPlaylistById(playlistId);
    }

    public boolean updatePlaylist(int playlistId, String name, String description,
                                  boolean isPublic) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        validatePlaylistInput(name);

        Playlist playlist = playlistDAO.getPlaylistById(playlistId);
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);

        return playlistDAO.updatePlaylist(playlist);
    }

    public boolean deletePlaylist(int playlistId) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        return playlistDAO.deletePlaylist(playlistId);
    }

    public List<Playlist> getUserPlaylists(int userId) throws CustomException {
        if (userId <= 0) {
            throw new CustomException("VALIDATION_003", "Invalid user ID",
                    "User ID must be a positive number.");
        }
        return playlistDAO.getPlaylistsByUser(userId);
    }

    public List<Playlist> getPublicPlaylists() throws CustomException {
        return playlistDAO.getPublicPlaylists();
    }

    public boolean addSongToPlaylist(int playlistId, int songId) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }

        // Check if song exists
        Song song = songDAO.getSongById(songId);
        if (!song.isActive()) {
            throw new CustomException("PLAYLIST_SONG_004", "Song is not active",
                    "This song is no longer available.");
        }

        return playlistDAO.addSongToPlaylist(playlistId, songId);
    }

    public boolean removeSongFromPlaylist(int playlistId, int songId) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }

        return playlistDAO.removeSongFromPlaylist(playlistId, songId);
    }

    public List<Song> getPlaylistSongs(int playlistId) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        return playlistDAO.getSongsInPlaylist(playlistId);
    }

    public List<Playlist> searchPublicPlaylists(String keyword) throws CustomException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException("VALIDATION_010", "Search keyword is required",
                    "Please enter a search keyword.");
        }
        return playlistDAO.searchPlaylists(keyword.trim());
    }

    public boolean isSongInPlaylist(int playlistId, int songId) throws CustomException {
        if (playlistId <= 0) {
            throw new CustomException("VALIDATION_024", "Invalid playlist ID",
                    "Playlist ID must be a positive number.");
        }
        if (songId <= 0) {
            throw new CustomException("VALIDATION_020", "Invalid song ID",
                    "Song ID must be a positive number.");
        }

        return playlistDAO.isSongInPlaylist(playlistId, songId);
    }

    private void validatePlaylistInput(String name) throws CustomException {
        if (name == null || name.trim().isEmpty()) {
            throw new CustomException("VALIDATION_025", "Playlist name is required",
                    "Please enter a playlist name.");
        }
        if (name.length() < 2) {
            throw new CustomException("VALIDATION_026", "Playlist name too short",
                    "Playlist name must be at least 2 characters long.");
        }
        if (name.length() > 100) {
            throw new CustomException("VALIDATION_027", "Playlist name too long",
                    "Playlist name cannot exceed 100 characters.");
        }
    }
}