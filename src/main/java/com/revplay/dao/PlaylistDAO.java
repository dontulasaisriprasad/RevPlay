package com.revplay.dao;

import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.exception.CustomException;
import java.util.List;

public interface PlaylistDAO {
    Playlist createPlaylist(Playlist playlist) throws CustomException;
    Playlist getPlaylistById(int playlistId) throws CustomException;
    boolean updatePlaylist(Playlist playlist) throws CustomException;
    boolean deletePlaylist(int playlistId) throws CustomException;
    List<Playlist> getPlaylistsByUser(int userId) throws CustomException;
    List<Playlist> getPublicPlaylists() throws CustomException;
    boolean addSongToPlaylist(int playlistId, int songId) throws CustomException;
    boolean removeSongFromPlaylist(int playlistId, int songId) throws CustomException;
    List<Song> getSongsInPlaylist(int playlistId) throws CustomException;
    boolean isSongInPlaylist(int playlistId, int songId) throws CustomException;
    List<Playlist> searchPlaylists(String keyword) throws CustomException;
}