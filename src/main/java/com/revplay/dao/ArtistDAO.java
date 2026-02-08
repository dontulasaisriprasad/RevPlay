package com.revplay.dao;

import com.revplay.model.Artist;
import com.revplay.exception.CustomException;
import java.util.List;

public interface ArtistDAO {
    Artist registerArtist(Artist artist) throws CustomException;
    Artist getArtistById(int artistId) throws CustomException;
    Artist getArtistByStageName(String stageName) throws CustomException;
    boolean updateArtist(Artist artist) throws CustomException;
    List<Artist> getAllArtists() throws CustomException;
    List<Artist> searchArtists(String keyword) throws CustomException;
    boolean updateMonthlyListeners(int artistId, int listenerCount) throws CustomException;
    List<Artist> getTopArtists(int limit) throws CustomException;
}