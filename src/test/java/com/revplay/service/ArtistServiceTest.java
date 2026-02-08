package com.revplay.service;

import com.revplay.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ArtistServiceTest {

    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        artistService = new ArtistService();
    }

    @Test
    @DisplayName("Test register artist validation - empty stage name")
    void testRegisterArtistValidationEmptyStageName() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.registerArtist(1, "", "Pop", "Label", "social");
        });

        assertEquals("VALIDATION_021", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Stage name is required"));
    }

    @Test
    @DisplayName("Test register artist validation - short stage name")
    void testRegisterArtistValidationShortStageName() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.registerArtist(1, "A", "Pop", "Label", "social");
        });

        assertEquals("VALIDATION_022", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Stage name must be at least 2 characters"));
    }

    @Test
    @DisplayName("Test register artist validation - empty genre")
    void testRegisterArtistValidationEmptyGenre() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.registerArtist(1, "Artist Name", "", "Label", "social");
        });

        assertEquals("VALIDATION_023", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Genre is required"));
    }

    @Test
    @DisplayName("Test upload song validation - empty title")
    void testUploadSongValidationEmptyTitle() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.uploadSong(1, "", null, 180, "Pop", "/path/to/song.mp3");
        });

        assertEquals("VALIDATION_017", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Song title is required"));
    }

    @Test
    @DisplayName("Test upload song validation - invalid duration")
    void testUploadSongValidationInvalidDuration() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.uploadSong(1, "Song Title", null, 0, "Pop", "/path/to/song.mp3");
        });

        assertEquals("VALIDATION_018", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Song duration must be a positive number"));
    }

    @Test
    @DisplayName("Test upload song validation - empty file path")
    void testUploadSongValidationEmptyFilePath() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.uploadSong(1, "Song Title", null, 180, "Pop", "");
        });

        assertEquals("VALIDATION_019", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("File path is required"));
    }

    @Test
    @DisplayName("Test get artist profile validation - invalid artist ID")
    void testGetArtistProfileValidationInvalidId() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.getArtistProfile(0);
        });

        assertEquals("VALIDATION_015", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Artist ID must be a positive number"));
    }

    @Test
    @DisplayName("Test search artists validation - empty keyword")
    void testSearchArtistsValidationEmptyKeyword() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.searchArtists("");
        });

        assertEquals("VALIDATION_010", exception.getErrorCode());
//        assertTrue(exception.getUserMessage().contains("Search keyword is required"));
    }

    @Test
    @DisplayName("Test get top artists validation - invalid limit")
    void testGetTopArtistsValidationInvalidLimit() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            artistService.getTopArtists(0);
        });

        assertEquals("VALIDATION_016", exception.getErrorCode());
        assertTrue(exception.getUserMessage().contains("Limit must be a positive number"));
    }
}