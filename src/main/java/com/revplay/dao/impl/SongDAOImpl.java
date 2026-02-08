package com.revplay.dao.impl;

import com.revplay.dao.SongDAO;
import com.revplay.model.Song;
import com.revplay.util.DBUtil;
import com.revplay.util.LogUtil;
import com.revplay.exception.CustomException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SongDAOImpl implements SongDAO {

    @Override
    public Song addSong(Song song) throws CustomException {
        String sql = "INSERT INTO songs (title, artist_id, album_id, duration_seconds, " +
                "genre, file_path, release_date, play_count, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"song_id"})) {

            pstmt.setString(1, song.getTitle());
            pstmt.setInt(2, song.getArtistId());

            if (song.getAlbumId() != null) {
                pstmt.setInt(3, song.getAlbumId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, song.getDurationSeconds());
            pstmt.setString(5, song.getGenre());
            pstmt.setString(6, song.getFilePath());

            if (song.getReleaseDate() != null) {
                pstmt.setDate(7, Date.valueOf(song.getReleaseDate()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }

            pstmt.setInt(8, song.getPlayCount());
            pstmt.setString(9, song.isActive() ? "Y" : "N");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        song.setSongId(rs.getInt(1));
                    }
                }
                LogUtil.logInfo("Song added successfully: " + song.getTitle());
                return song;
            }

            throw new CustomException("SONG_ADD_001", "Failed to add song",
                    "Song could not be added.");

        } catch (SQLException e) {
            LogUtil.logError("Error adding song: " + song.getTitle(), e);

            if (e.getErrorCode() == 2291) {
                throw new CustomException("SONG_ADD_002", "Artist not found",
                        "Associated artist not found.");
            }
            if (e.getErrorCode() == 2292) {
                throw new CustomException("SONG_ADD_003", "Album not found",
                        "Associated album not found.");
            }
            throw new CustomException("SONG_ADD_004", "Database error",
                    "Song addition failed due to system error.");
        }
    }

    @Override
    public Song getSongById(int songId) throws CustomException {
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.song_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, songId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractSongWithDetailsFromResultSet(rs);
                } else {
                    throw new CustomException("SONG_GET_001", "Song not found",
                            "Song with ID " + songId + " not found.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting song by ID: " + songId, e);
            throw new CustomException("SONG_GET_002", "Database error",
                    "Failed to retrieve song information.");
        }
    }

    @Override
    public boolean updateSong(Song song) throws CustomException {
        String sql = "UPDATE songs SET title = ?, album_id = ?, duration_seconds = ?, " +
                "genre = ?, file_path = ?, release_date = ?, is_active = ? " +
                "WHERE song_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, song.getTitle());

            if (song.getAlbumId() != null) {
                pstmt.setInt(2, song.getAlbumId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setInt(3, song.getDurationSeconds());
            pstmt.setString(4, song.getGenre());
            pstmt.setString(5, song.getFilePath());

            if (song.getReleaseDate() != null) {
                pstmt.setDate(6, Date.valueOf(song.getReleaseDate()));
            } else {
                pstmt.setNull(6, Types.DATE);
            }

            pstmt.setString(7, song.isActive() ? "Y" : "N");
            pstmt.setInt(8, song.getSongId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Song updated successfully: " + song.getSongId());
                return true;
            }

            throw new CustomException("SONG_UPDATE_001", "Song not found",
                    "Song information could not be updated.");

        } catch (SQLException e) {
            LogUtil.logError("Error updating song: " + song.getSongId(), e);
            throw new CustomException("SONG_UPDATE_002", "Database error",
                    "Song update failed due to system error.");
        }
    }

    @Override
    public boolean deleteSong(int songId) throws CustomException {
        String sql = "DELETE FROM songs WHERE song_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, songId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Song deleted successfully: " + songId);
                return true;
            }

            throw new CustomException("SONG_DELETE_001", "Song not found",
                    "Song could not be deleted.");

        } catch (SQLException e) {
            LogUtil.logError("Error deleting song: " + songId, e);
            throw new CustomException("SONG_DELETE_002", "Database error",
                    "Song deletion failed due to system error.");
        }
    }

    @Override
    public List<Song> getAllSongs() throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.is_active = 'Y' " +
                "ORDER BY s.play_count DESC, s.title";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                songs.add(extractSongWithDetailsFromResultSet(rs));
            }

            LogUtil.logDebug("Retrieved " + songs.size() + " songs");
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting all songs", e);
            throw new CustomException("SONG_GET_ALL_001", "Database error",
                    "Failed to retrieve songs list.");
        }
    }

    @Override
    public List<Song> getSongsByArtist(int artistId) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.artist_id = ? AND s.is_active = 'Y' " +
                "ORDER BY s.release_date DESC, s.title";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artistId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongWithDetailsFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved " + songs.size() + " songs for artist: " + artistId);
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting songs by artist: " + artistId, e);
            throw new CustomException("SONG_ARTIST_001", "Database error",
                    "Failed to retrieve artist's songs.");
        }
    }

    @Override
    public List<Song> getSongsByAlbum(int albumId) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.album_id = ? AND s.is_active = 'Y' " +
                "ORDER BY s.duration_seconds";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, albumId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongWithDetailsFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved " + songs.size() + " songs for album: " + albumId);
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting songs by album: " + albumId, e);
            throw new CustomException("SONG_ALBUM_001", "Database error",
                    "Failed to retrieve album's songs.");
        }
    }

    @Override
    public List<Song> searchSongs(String keyword) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE (s.title LIKE ? OR a.stage_name LIKE ? OR al.title LIKE ?) " +
                "AND s.is_active = 'Y' " +
                "ORDER BY s.play_count DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongWithDetailsFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Found " + songs.size() + " songs matching: " + keyword);
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error searching songs with keyword: " + keyword, e);
            throw new CustomException("SONG_SEARCH_001", "Database error",
                    "Song search failed due to system error.");
        }
    }

    @Override
    public List<Song> getSongsByGenre(String genre) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.genre = ? AND s.is_active = 'Y' " +
                "ORDER BY s.play_count DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genre);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongWithDetailsFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved " + songs.size() + " songs in genre: " + genre);
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting songs by genre: " + genre, e);
            throw new CustomException("SONG_GENRE_001", "Database error",
                    "Failed to retrieve songs by genre.");
        }
    }

    @Override
    public boolean incrementPlayCount(int songId) throws CustomException {
        String sql = "UPDATE songs SET play_count = play_count + 1 WHERE song_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, songId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logDebug("Incremented play count for song: " + songId);
                return true;
            }

            throw new CustomException("SONG_PLAY_001", "Song not found",
                    "Failed to increment play count.");

        } catch (SQLException e) {
            LogUtil.logError("Error incrementing play count for song: " + songId, e);
            throw new CustomException("SONG_PLAY_002", "Database error",
                    "Failed to update play count.");
        }
    }

    @Override
    public List<Song> getTopSongs(int limit) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.is_active = 'Y' " +
                "ORDER BY s.play_count DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongWithDetailsFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved top " + songs.size() + " songs");
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting top songs", e);
            throw new CustomException("SONG_TOP_001", "Database error",
                    "Failed to retrieve top songs.");
        }
    }

    @Override
    public List<Song> getRecentlyAddedSongs(int limit) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name, al.title as album_title " +
                "FROM songs s " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "LEFT JOIN albums al ON s.album_id = al.album_id " +
                "WHERE s.is_active = 'Y' " +
                "ORDER BY s.release_date DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongWithDetailsFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved " + songs.size() + " recently added songs");
            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting recently added songs", e);
            throw new CustomException("SONG_RECENT_001", "Database error",
                    "Failed to retrieve recently added songs.");
        }
    }

    private Song extractSongWithDetailsFromResultSet(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setSongId(rs.getInt("song_id"));
        song.setTitle(rs.getString("title"));
        song.setArtistId(rs.getInt("artist_id"));

        int albumId = rs.getInt("album_id");
        if (!rs.wasNull()) {
            song.setAlbumId(albumId);
        }

        song.setDurationSeconds(rs.getInt("duration_seconds"));
        song.setGenre(rs.getString("genre"));
        song.setFilePath(rs.getString("file_path"));

        Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            song.setReleaseDate(releaseDate.toLocalDate());
        }

        song.setPlayCount(rs.getInt("play_count"));

        String isActive = rs.getString("is_active");
        song.setActive("Y".equals(isActive));

        // Set additional details if available
        try {
            String artistName = rs.getString("artist_name");
            if (artistName != null) {
                com.revplay.model.Artist artist = new com.revplay.model.Artist();
                artist.setStageName(artistName);
                song.setArtist(artist);
            }

            String albumTitle = rs.getString("album_title");
            if (albumTitle != null) {
                com.revplay.model.Album album = new com.revplay.model.Album();
                album.setTitle(albumTitle);
                song.setAlbum(album);
            }
        } catch (SQLException e) {
            // Ignore if these columns don't exist in the result set
        }

        return song;
    }
}