package com.revplay.dao.impl;

import com.revplay.dao.PlaylistDAO;
import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.util.DBUtil;
import com.revplay.util.LogUtil;
import com.revplay.exception.CustomException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAOImpl implements PlaylistDAO {

    @Override
    public Playlist createPlaylist(Playlist playlist) throws CustomException {
        String sql = "INSERT INTO playlists (name, user_id, description, is_public, total_songs, total_duration) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"playlist_id"})) {

            pstmt.setString(1, playlist.getName());
            pstmt.setInt(2, playlist.getUserId());
            pstmt.setString(3, playlist.getDescription());
            pstmt.setString(4, playlist.isPublic() ? "Y" : "N");
            pstmt.setInt(5, playlist.getTotalSongs());
            pstmt.setInt(6, playlist.getTotalDuration());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        playlist.setPlaylistId(rs.getInt(1));
                    }
                }
                LogUtil.logInfo("Playlist created successfully: " + playlist.getName());
                return playlist;
            }

            throw new CustomException("PLAYLIST_CREATE_001", "Failed to create playlist",
                    "Playlist could not be created.");

        } catch (SQLException e) {
            LogUtil.logError("Error creating playlist: " + playlist.getName(), e);

            if (e.getErrorCode() == 2291) {
                throw new CustomException("PLAYLIST_CREATE_002", "User not found",
                        "Associated user account not found.");
            }
            throw new CustomException("PLAYLIST_CREATE_003", "Database error",
                    "Playlist creation failed due to system error.");
        }
    }

    @Override
    public Playlist getPlaylistById(int playlistId) throws CustomException {
        String sql = "SELECT p.*, u.username " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.playlist_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Playlist playlist = extractPlaylistFromResultSet(rs);

                    // Get songs in the playlist
                    playlist.setSongs(getSongsInPlaylist(playlistId));

                    return playlist;
                } else {
                    throw new CustomException("PLAYLIST_GET_001", "Playlist not found",
                            "Playlist with ID " + playlistId + " not found.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting playlist by ID: " + playlistId, e);
            throw new CustomException("PLAYLIST_GET_002", "Database error",
                    "Failed to retrieve playlist information.");
        }
    }

    @Override
    public boolean updatePlaylist(Playlist playlist) throws CustomException {
        String sql = "UPDATE playlists SET name = ?, description = ?, is_public = ? " +
                "WHERE playlist_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playlist.getName());
            pstmt.setString(2, playlist.getDescription());
            pstmt.setString(3, playlist.isPublic() ? "Y" : "N");
            pstmt.setInt(4, playlist.getPlaylistId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Playlist updated successfully: " + playlist.getPlaylistId());
                return true;
            }

            throw new CustomException("PLAYLIST_UPDATE_001", "Playlist not found",
                    "Playlist information could not be updated.");

        } catch (SQLException e) {
            LogUtil.logError("Error updating playlist: " + playlist.getPlaylistId(), e);
            throw new CustomException("PLAYLIST_UPDATE_002", "Database error",
                    "Playlist update failed due to system error.");
        }
    }

    @Override
    public boolean deletePlaylist(int playlistId) throws CustomException {
        String sql = "DELETE FROM playlists WHERE playlist_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Playlist deleted successfully: " + playlistId);
                return true;
            }

            throw new CustomException("PLAYLIST_DELETE_001", "Playlist not found",
                    "Playlist could not be deleted.");

        } catch (SQLException e) {
            LogUtil.logError("Error deleting playlist: " + playlistId, e);
            throw new CustomException("PLAYLIST_DELETE_002", "Database error",
                    "Playlist deletion failed due to system error.");
        }
    }

    @Override
    public List<Playlist> getPlaylistsByUser(int userId) throws CustomException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT p.*, u.username " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.user_id = ? " +
                "ORDER BY p.created_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(extractPlaylistFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved " + playlists.size() + " playlists for user: " + userId);
            return playlists;

        } catch (SQLException e) {
            LogUtil.logError("Error getting playlists by user: " + userId, e);
            throw new CustomException("PLAYLIST_USER_001", "Database error",
                    "Failed to retrieve user's playlists.");
        }
    }

    @Override
    public List<Playlist> getPublicPlaylists() throws CustomException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT p.*, u.username " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.is_public = 'Y' " +
                "ORDER BY p.created_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                playlists.add(extractPlaylistFromResultSet(rs));
            }

            LogUtil.logDebug("Retrieved " + playlists.size() + " public playlists");
            return playlists;

        } catch (SQLException e) {
            LogUtil.logError("Error getting public playlists", e);
            throw new CustomException("PLAYLIST_PUBLIC_001", "Database error",
                    "Failed to retrieve public playlists.");
        }
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId) throws CustomException {
        String checkSql = "SELECT COUNT(*) FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        String insertSql = "INSERT INTO playlist_songs (playlist_id, song_id, position_number) " +
                "VALUES (?, ?, (SELECT NVL(MAX(position_number), 0) + 1 FROM playlist_songs WHERE playlist_id = ?))";
        String updateSql = "UPDATE playlists SET total_songs = total_songs + 1, " +
                "total_duration = total_duration + (SELECT duration_seconds FROM songs WHERE song_id = ?) " +
                "WHERE playlist_id = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            DBUtil.setAutoCommit(conn, false);

            // Check if song already exists in playlist
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, playlistId);
                checkStmt.setInt(2, songId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new CustomException("PLAYLIST_SONG_001", "Song already in playlist",
                                "This song is already in the playlist.");
                    }
                }
            }

            // Add song to playlist
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, playlistId);
                insertStmt.setInt(2, songId);
                insertStmt.setInt(3, playlistId);
                insertStmt.executeUpdate();
            }

            // Update playlist statistics
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, songId);
                updateStmt.setInt(2, playlistId);
                updateStmt.executeUpdate();
            }

            DBUtil.commitTransaction(conn);
            LogUtil.logInfo("Song " + songId + " added to playlist " + playlistId);
            return true;

        } catch (SQLException | CustomException e) {
            if (conn != null) {
                DBUtil.rollbackTransaction(conn);
            }

            if (e instanceof CustomException) {
                throw (CustomException) e;
            }

            LogUtil.logError("Error adding song to playlist", e);

            if (((SQLException) e).getErrorCode() == 2291) {
                throw new CustomException("PLAYLIST_SONG_002", "Invalid song or playlist",
                        "The song or playlist does not exist.");
            }
            throw new CustomException("PLAYLIST_SONG_003", "Database error",
                    "Failed to add song to playlist.");
        } finally {
            if (conn != null) {
                DBUtil.setAutoCommit(conn, true);
            }
        }
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId) throws CustomException {
        String deleteSql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        String updateSql = "UPDATE playlists SET total_songs = total_songs - 1, " +
                "total_duration = total_duration - (SELECT duration_seconds FROM songs WHERE song_id = ?) " +
                "WHERE playlist_id = ?";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            DBUtil.setAutoCommit(conn, false);

            // Remove song from playlist
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, playlistId);
                deleteStmt.setInt(2, songId);

                int affectedRows = deleteStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new CustomException("PLAYLIST_REMOVE_001", "Song not in playlist",
                            "This song is not in the playlist.");
                }
            }

            // Update playlist statistics
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, songId);
                updateStmt.setInt(2, playlistId);
                updateStmt.executeUpdate();
            }

            // Reorder positions
            reorderPlaylistPositions(conn, playlistId);

            DBUtil.commitTransaction(conn);
            LogUtil.logInfo("Song " + songId + " removed from playlist " + playlistId);
            return true;

        } catch (SQLException | CustomException e) {
            if (conn != null) {
                DBUtil.rollbackTransaction(conn);
            }

            if (e instanceof CustomException) {
                throw (CustomException) e;
            }

            LogUtil.logError("Error removing song from playlist", e);
            throw new CustomException("PLAYLIST_REMOVE_002", "Database error",
                    "Failed to remove song from playlist.");
        } finally {
            if (conn != null) {
                DBUtil.setAutoCommit(conn, true);
            }
        }
    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId) throws CustomException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.*, a.stage_name as artist_name " +
                "FROM songs s " +
                "JOIN playlist_songs ps ON s.song_id = ps.song_id " +
                "LEFT JOIN artists a ON s.artist_id = a.artist_id " +
                "WHERE ps.playlist_id = ? AND s.is_active = 'Y' " +
                "ORDER BY ps.position_number";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Song song = new Song();
                    song.setSongId(rs.getInt("song_id"));
                    song.setTitle(rs.getString("title"));
                    song.setArtistId(rs.getInt("artist_id"));
                    song.setDurationSeconds(rs.getInt("duration_seconds"));
                    song.setGenre(rs.getString("genre"));
                    song.setPlayCount(rs.getInt("play_count"));

                    songs.add(song);
                }
            }

            return songs;

        } catch (SQLException e) {
            LogUtil.logError("Error getting songs in playlist: " + playlistId, e);
            throw new CustomException("PLAYLIST_SONGS_001", "Database error",
                    "Failed to retrieve playlist songs.");
        }
    }

    @Override
    public boolean isSongInPlaylist(int playlistId, int songId) throws CustomException {
        String sql = "SELECT COUNT(*) FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, songId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }

        } catch (SQLException e) {
            LogUtil.logError("Error checking if song is in playlist", e);
            throw new CustomException("PLAYLIST_CHECK_001", "Database error",
                    "Failed to check playlist membership.");
        }
    }

    @Override
    public List<Playlist> searchPlaylists(String keyword) throws CustomException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT p.*, u.username " +
                "FROM playlists p JOIN users u ON p.user_id = u.user_id " +
                "WHERE (p.name LIKE ? OR p.description LIKE ? OR u.username LIKE ?) " +
                "AND p.is_public = 'Y' " +
                "ORDER BY p.created_date DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(extractPlaylistFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Found " + playlists.size() + " playlists matching: " + keyword);
            return playlists;

        } catch (SQLException e) {
            LogUtil.logError("Error searching playlists with keyword: " + keyword, e);
            throw new CustomException("PLAYLIST_SEARCH_001", "Database error",
                    "Playlist search failed due to system error.");
        }
    }

    private Playlist extractPlaylistFromResultSet(ResultSet rs) throws SQLException {
        Playlist playlist = new Playlist();
        playlist.setPlaylistId(rs.getInt("playlist_id"));
        playlist.setName(rs.getString("name"));
        playlist.setUserId(rs.getInt("user_id"));
        playlist.setDescription(rs.getString("description"));

        String isPublic = rs.getString("is_public");
        playlist.setPublic("Y".equals(isPublic));

        Timestamp createdDate = rs.getTimestamp("created_date");
        playlist.setCreatedDate(createdDate != null ? createdDate.toLocalDateTime() : null);

        playlist.setTotalSongs(rs.getInt("total_songs"));
        playlist.setTotalDuration(rs.getInt("total_duration"));

        return playlist;
    }

    private void reorderPlaylistPositions(Connection conn, int playlistId) throws SQLException {
        String sql = "UPDATE playlist_songs ps1 " +
                "SET position_number = (" +
                "  SELECT COUNT(*) FROM playlist_songs ps2 " +
                "  WHERE ps2.playlist_id = ps1.playlist_id " +
                "  AND ps2.position_number <= ps1.position_number" +
                ") WHERE ps1.playlist_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playlistId);
            pstmt.executeUpdate();
        }
    }
}