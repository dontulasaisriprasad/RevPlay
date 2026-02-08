package com.revplay.dao.impl;

import com.revplay.dao.ArtistDAO;
import com.revplay.model.Artist;
import com.revplay.util.DBUtil;
import com.revplay.util.LogUtil;
import com.revplay.exception.CustomException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAOImpl implements ArtistDAO {

    @Override
    public Artist registerArtist(Artist artist) throws CustomException {
        String sql = "INSERT INTO artists (artist_id, stage_name, genre, record_label, monthly_listeners, social_media_links) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artist.getArtistId());
            pstmt.setString(2, artist.getStageName());
            pstmt.setString(3, artist.getGenre());
            pstmt.setString(4, artist.getRecordLabel());
            pstmt.setInt(5, artist.getMonthlyListeners());
            pstmt.setString(6, artist.getSocialMediaLinks());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Artist registered successfully: " + artist.getStageName());
                return artist;
            }

            throw new CustomException("ARTIST_REG_001", "Failed to register artist",
                    "Artist registration failed.");

        } catch (SQLException e) {
            LogUtil.logError("Error registering artist: " + artist.getStageName(), e);

            if (e.getErrorCode() == 1) {
                throw new CustomException("ARTIST_REG_002", "Stage name already exists",
                        "This stage name is already taken.");
            }
            if (e.getErrorCode() == 2291) { // Foreign key violation
                throw new CustomException("ARTIST_REG_003", "User not found",
                        "Associated user account not found.");
            }
            throw new CustomException("ARTIST_REG_004", "Database error",
                    "Artist registration failed due to system error.");
        }
    }

    @Override
    public Artist getArtistById(int artistId) throws CustomException {
        String sql = "SELECT a.*, u.username, u.email, u.full_name, u.profile_image, u.bio, " +
                "u.registration_date, u.is_active " +
                "FROM artists a JOIN users u ON a.artist_id = u.user_id " +
                "WHERE a.artist_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, artistId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractArtistWithUserFromResultSet(rs);
                } else {
                    throw new CustomException("ARTIST_GET_001", "Artist not found",
                            "Artist with ID " + artistId + " not found.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting artist by ID: " + artistId, e);
            throw new CustomException("ARTIST_GET_002", "Database error",
                    "Failed to retrieve artist information.");
        }
    }

    @Override
    public Artist getArtistByStageName(String stageName) throws CustomException {
        String sql = "SELECT a.*, u.username, u.email, u.full_name, u.profile_image, u.bio, " +
                "u.registration_date, u.is_active " +
                "FROM artists a JOIN users u ON a.artist_id = u.user_id " +
                "WHERE a.stage_name = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stageName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractArtistWithUserFromResultSet(rs);
                } else {
                    throw new CustomException("ARTIST_GET_003", "Artist not found",
                            "Artist '" + stageName + "' not found.");
                }
            }

        } catch (SQLException e) {
            LogUtil.logError("Error getting artist by stage name: " + stageName, e);
            throw new CustomException("ARTIST_GET_004", "Database error",
                    "Failed to retrieve artist information.");
        }
    }

    @Override
    public boolean updateArtist(Artist artist) throws CustomException {
        String sql = "UPDATE artists SET stage_name = ?, genre = ?, record_label = ?, " +
                "social_media_links = ? WHERE artist_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artist.getStageName());
            pstmt.setString(2, artist.getGenre());
            pstmt.setString(3, artist.getRecordLabel());
            pstmt.setString(4, artist.getSocialMediaLinks());
            pstmt.setInt(5, artist.getArtistId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logInfo("Artist updated successfully: " + artist.getArtistId());
                return true;
            }

            throw new CustomException("ARTIST_UPDATE_001", "Artist not found",
                    "Artist information could not be updated.");

        } catch (SQLException e) {
            LogUtil.logError("Error updating artist: " + artist.getArtistId(), e);

            if (e.getErrorCode() == 1) {
                throw new CustomException("ARTIST_UPDATE_002", "Stage name already exists",
                        "This stage name is already taken.");
            }
            throw new CustomException("ARTIST_UPDATE_003", "Database error",
                    "Artist update failed due to system error.");
        }
    }

    @Override
    public List<Artist> getAllArtists() throws CustomException {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT a.*, u.username, u.email, u.full_name, u.profile_image, u.bio, " +
                "u.registration_date, u.is_active " +
                "FROM artists a JOIN users u ON a.artist_id = u.user_id " +
                "ORDER BY a.stage_name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                artists.add(extractArtistWithUserFromResultSet(rs));
            }

            LogUtil.logDebug("Retrieved " + artists.size() + " artists");
            return artists;

        } catch (SQLException e) {
            LogUtil.logError("Error getting all artists", e);
            throw new CustomException("ARTIST_GET_ALL_001", "Database error",
                    "Failed to retrieve artists list.");
        }
    }

    @Override
    public List<Artist> searchArtists(String keyword) throws CustomException {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT a.*, u.username, u.email, u.full_name, u.profile_image, u.bio, " +
                "u.registration_date, u.is_active " +
                "FROM artists a JOIN users u ON a.artist_id = u.user_id " +
                "WHERE a.stage_name LIKE ? OR a.genre LIKE ? OR u.full_name LIKE ? " +
                "ORDER BY a.stage_name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    artists.add(extractArtistWithUserFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Found " + artists.size() + " artists matching: " + keyword);
            return artists;

        } catch (SQLException e) {
            LogUtil.logError("Error searching artists with keyword: " + keyword, e);
            throw new CustomException("ARTIST_SEARCH_001", "Database error",
                    "Artist search failed due to system error.");
        }
    }

    @Override
    public boolean updateMonthlyListeners(int artistId, int listenerCount) throws CustomException {
        String sql = "UPDATE artists SET monthly_listeners = ? WHERE artist_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, listenerCount);
            pstmt.setInt(2, artistId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LogUtil.logDebug("Updated monthly listeners for artist: " + artistId + " to " + listenerCount);
                return true;
            }

            throw new CustomException("ARTIST_LISTENERS_001", "Artist not found",
                    "Failed to update listener count.");

        } catch (SQLException e) {
            LogUtil.logError("Error updating monthly listeners for artist: " + artistId, e);
            throw new CustomException("ARTIST_LISTENERS_002", "Database error",
                    "Failed to update listener count due to system error.");
        }
    }

    @Override
    public List<Artist> getTopArtists(int limit) throws CustomException {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT a.*, u.username, u.email, u.full_name, u.profile_image, u.bio, " +
                "u.registration_date, u.is_active " +
                "FROM artists a JOIN users u ON a.artist_id = u.user_id " +
                "ORDER BY a.monthly_listeners DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    artists.add(extractArtistWithUserFromResultSet(rs));
                }
            }

            LogUtil.logDebug("Retrieved top " + artists.size() + " artists");
            return artists;

        } catch (SQLException e) {
            LogUtil.logError("Error getting top artists", e);
            throw new CustomException("ARTIST_TOP_001", "Database error",
                    "Failed to retrieve top artists.");
        }
    }

    private Artist extractArtistWithUserFromResultSet(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setArtistId(rs.getInt("artist_id"));
        artist.setStageName(rs.getString("stage_name"));
        artist.setGenre(rs.getString("genre"));
        artist.setRecordLabel(rs.getString("record_label"));
        artist.setMonthlyListeners(rs.getInt("monthly_listeners"));
        artist.setSocialMediaLinks(rs.getString("social_media_links"));

        // Create and populate User object
        com.revplay.model.User user = new com.revplay.model.User();
        user.setUserId(rs.getInt("artist_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setUserType("ARTIST");
        user.setProfileImage(rs.getString("profile_image"));
        user.setBio(rs.getString("bio"));

        Timestamp regDate = rs.getTimestamp("registration_date");
        user.setRegistrationDate(regDate != null ? regDate.toLocalDateTime() : null);

        String isActive = rs.getString("is_active");
        user.setActive("Y".equals(isActive));

        artist.setUserDetails(user);
        return artist;
    }
}