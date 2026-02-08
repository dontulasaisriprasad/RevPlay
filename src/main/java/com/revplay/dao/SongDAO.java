package com.revplay.dao;

import com.revplay.model.Song;
import com.revplay.exception.CustomException;
import java.util.List;

public interface SongDAO {
    Song addSong(Song song) throws CustomException;
    Song getSongById(int songId) throws CustomException;
    boolean updateSong(Song song) throws CustomException;
    boolean deleteSong(int songId) throws CustomException;
    List<Song> getAllSongs() throws CustomException;
    List<Song> getSongsByArtist(int artistId) throws CustomException;
    List<Song> getSongsByAlbum(int albumId) throws CustomException;
    List<Song> searchSongs(String keyword) throws CustomException;
    List<Song> getSongsByGenre(String genre) throws CustomException;
    boolean incrementPlayCount(int songId) throws CustomException;
    List<Song> getTopSongs(int limit) throws CustomException;
    List<Song> getRecentlyAddedSongs(int limit) throws CustomException;
}