package com.revplay.dao;

import com.revplay.dao.impl.ArtistDAOImpl;
import com.revplay.dao.impl.UserDAOImpl;
import com.revplay.model.Artist;
import com.revplay.model.User;
import com.revplay.exception.CustomException;
import com.revplay.util.DBUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistDAOImplTest {
    private ArtistDAOImpl artistDAO;
    private UserDAOImpl userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        artistDAO = new ArtistDAOImpl();
        userDAO = new UserDAOImpl();
        connection = DBUtil.getConnection();

        // Clear ALL test data to prevent conflicts
        try (Statement stmt = connection.createStatement()) {
            // Delete in correct order due to foreign key constraints
            stmt.execute("DELETE FROM favorites");
            stmt.execute("DELETE FROM listening_history");
            stmt.execute("DELETE FROM playlist_songs");
            stmt.execute("DELETE FROM playlists");
            stmt.execute("DELETE FROM songs");
            stmt.execute("DELETE FROM artists");
            // Delete all test users with various patterns used in tests
            stmt.execute("DELETE FROM users WHERE username LIKE 'testartist%' OR " +
                    "username LIKE 'searchartist%' OR " +
                    "username LIKE 'topartist%' OR " +
                    "username LIKE 'allartist%' OR " +
                    "username LIKE 'testlisteners%' OR " +
                    "username LIKE 'testuser%' OR " +
                    "username LIKE 'testlogin%' OR " +
                    "username LIKE 'testbyid%' OR " +
                    "username LIKE 'testupdate%' OR " +
                    "username LIKE 'testpass%' OR " +
                    "username LIKE 'testsearch%' OR " +
                    "username LIKE 'testactive%' OR " +
                    "username LIKE 'alltest%' OR " +
                    "username LIKE 'otheruser%'");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Test artist registration - success")
    void testRegisterArtistSuccess() throws CustomException {
        // Arrange - first create a user
        User user = new User();
        user.setUsername("testartist1");
        user.setPassword("artistpass");
        user.setEmail("artist1@example.com");
        user.setFullName("Test Artist One");
        user.setUserType("ARTIST");

        User registeredUser = userDAO.registerUser(user);

        Artist artist = new Artist();
        artist.setArtistId(registeredUser.getUserId());
        artist.setStageName("Test Artist One");
        artist.setGenre("Pop");
        artist.setRecordLabel("Test Records");
        artist.setMonthlyListeners(1000);
        artist.setSocialMediaLinks("@testartist1");

        // Act
        Artist registeredArtist = artistDAO.registerArtist(artist);

        // Assert
        assertNotNull(registeredArtist);
        assertEquals(registeredUser.getUserId(), registeredArtist.getArtistId());
        assertEquals("Test Artist One", registeredArtist.getStageName());
        assertEquals("Pop", registeredArtist.getGenre());
        assertEquals("Test Records", registeredArtist.getRecordLabel());
        assertEquals(1000, registeredArtist.getMonthlyListeners());
    }

    @Test
    @DisplayName("Test get artist by ID - success")
    void testGetArtistByIdSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testartist2");
        user.setPassword("artistpass");
        user.setEmail("artist2@example.com");
        user.setFullName("Test Artist Two");
        user.setUserType("ARTIST");

        User registeredUser = userDAO.registerUser(user);

        Artist artist = new Artist();
        artist.setArtistId(registeredUser.getUserId());
        artist.setStageName("Test Artist Two");
        artist.setGenre("Rock");
        artist.setRecordLabel("Rock Records");

        artistDAO.registerArtist(artist);

        // Act
        Artist retrievedArtist = artistDAO.getArtistById(registeredUser.getUserId());

        // Assert
        assertNotNull(retrievedArtist);
        assertEquals(registeredUser.getUserId(), retrievedArtist.getArtistId());
        assertEquals("Test Artist Two", retrievedArtist.getStageName());
        assertEquals("Rock", retrievedArtist.getGenre());
        assertNotNull(retrievedArtist.getUserDetails());
        assertEquals("testartist2", retrievedArtist.getUserDetails().getUsername());
    }

    @Test
    @DisplayName("Test update artist - success")
    void testUpdateArtistSuccess() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testartist3");
        user.setPassword("artistpass");
        user.setEmail("artist3@example.com");
        user.setFullName("Test Artist Three");
        user.setUserType("ARTIST");

        User registeredUser = userDAO.registerUser(user);

        Artist artist = new Artist();
        artist.setArtistId(registeredUser.getUserId());
        artist.setStageName("Test Artist Three");
        artist.setGenre("Hip Hop");
        artist.setRecordLabel("Hip Hop Records");

        artistDAO.registerArtist(artist);

        // Update artist
        artist.setStageName("Updated Artist Name");
        artist.setGenre("R&B");
        artist.setRecordLabel("Updated Records");
        artist.setSocialMediaLinks("@updatedartist");

        // Act
        boolean result = artistDAO.updateArtist(artist);

        // Assert
        assertTrue(result);

        // Verify update
        Artist updatedArtist = artistDAO.getArtistById(registeredUser.getUserId());
        assertEquals("Updated Artist Name", updatedArtist.getStageName());
        assertEquals("R&B", updatedArtist.getGenre());
        assertEquals("Updated Records", updatedArtist.getRecordLabel());
        assertEquals("@updatedartist", updatedArtist.getSocialMediaLinks());
    }

    @Test
    @DisplayName("Test search artists - success")
    void testSearchArtists() throws CustomException {
        // Arrange - create multiple artists
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("searchartist" + i);
            user.setPassword("pass" + i);
            user.setEmail("search" + i + "@example.com");
            user.setFullName("Search Artist " + i);
            user.setUserType("ARTIST");

            User registeredUser = userDAO.registerUser(user);

            Artist artist = new Artist();
            artist.setArtistId(registeredUser.getUserId());
            artist.setStageName("Search Artist " + i);
            artist.setGenre(i % 2 == 0 ? "Pop" : "Rock");
            artist.setRecordLabel("Test Records");

            artistDAO.registerArtist(artist);
        }

        // Act
        List<Artist> results = artistDAO.searchArtists("Search");

        // Assert
        assertNotNull(results);
        assertEquals(3, results.size());

        // Verify stage names
        List<String> stageNames = results.stream()
                .map(Artist::getStageName)
                .toList();
        assertTrue(stageNames.contains("Search Artist 1"));
        assertTrue(stageNames.contains("Search Artist 2"));
        assertTrue(stageNames.contains("Search Artist 3"));
    }

    @Test
    @DisplayName("Test update monthly listeners - success")
    void testUpdateMonthlyListeners() throws CustomException {
        // Arrange
        User user = new User();
        user.setUsername("testlisteners");
        user.setPassword("artistpass");
        user.setEmail("listeners@example.com");
        user.setFullName("Listeners Test");
        user.setUserType("ARTIST");

        User registeredUser = userDAO.registerUser(user);

        Artist artist = new Artist();
        artist.setArtistId(registeredUser.getUserId());
        artist.setStageName("Listeners Test");
        artist.setGenre("Pop");

        artistDAO.registerArtist(artist);

        // Act
        boolean result = artistDAO.updateMonthlyListeners(registeredUser.getUserId(), 50000);

        // Assert
        assertTrue(result);

        // Verify update
        Artist updatedArtist = artistDAO.getArtistById(registeredUser.getUserId());
        assertEquals(50000, updatedArtist.getMonthlyListeners());
    }

    @Test
    @DisplayName("Test get top artists")
    void testGetTopArtists() throws CustomException {
        // Arrange - create artists with different listener counts
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("topartist" + i);
            user.setPassword("pass" + i);
            user.setEmail("top" + i + "@example.com");
            user.setFullName("Top Artist " + i);
            user.setUserType("ARTIST");

            User registeredUser = userDAO.registerUser(user);

            Artist artist = new Artist();
            artist.setArtistId(registeredUser.getUserId());
            artist.setStageName("Top Artist " + i);
            artist.setGenre("Pop");
            artist.setMonthlyListeners(i * 10000); // Increasing listeners

            artistDAO.registerArtist(artist);
        }

        // Act
        List<Artist> topArtists = artistDAO.getTopArtists(3);

        // Assert
        assertNotNull(topArtists);
        assertEquals(3, topArtists.size());

        // Verify they're ordered by monthly listeners (descending)
        assertTrue(topArtists.get(0).getMonthlyListeners() >= topArtists.get(1).getMonthlyListeners());
        assertTrue(topArtists.get(1).getMonthlyListeners() >= topArtists.get(2).getMonthlyListeners());

        // Top artist should have 50000 listeners
        assertEquals(50000, topArtists.get(0).getMonthlyListeners());
    }

    @Test
    @DisplayName("Test get all artists")
    void testGetAllArtists() throws CustomException {
        // Arrange - create multiple artists
        for (int i = 1; i <= 4; i++) {
            User user = new User();
            user.setUsername("allartist" + i);
            user.setPassword("pass" + i);
            user.setEmail("allartist" + i + "@example.com");
            user.setFullName("All Artist " + i);
            user.setUserType("ARTIST");

            User registeredUser = userDAO.registerUser(user);

            Artist artist = new Artist();
            artist.setArtistId(registeredUser.getUserId());
            artist.setStageName("All Artist " + i);
            artist.setGenre(i % 2 == 0 ? "Pop" : "Rock");

            artistDAO.registerArtist(artist);
        }

        // Act
        List<Artist> allArtists = artistDAO.getAllArtists();

        // Assert
        assertNotNull(allArtists);
        assertTrue(allArtists.size() >= 4); // Might include other test artists

        // Verify our test artists are in the list
        List<String> stageNames = allArtists.stream()
                .map(Artist::getStageName)
                .filter(name -> name.startsWith("All Artist"))
                .toList();
        assertEquals(4, stageNames.size());
    }
}