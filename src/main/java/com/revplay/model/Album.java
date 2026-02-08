package com.revplay.model;

import java.time.LocalDate;
import java.util.List;

public class Album {
    private int albumId;
    private String title;
    private int artistId;
    private LocalDate releaseDate;
    private String genre;
    private String coverImage;
    private int totalTracks;
    private int durationMinutes;
    private Artist artist;
    private List<Song> songs;

    public Album() {}

    public Album(int albumId, String title, int artistId, LocalDate releaseDate,
                 String genre, String coverImage, int totalTracks, int durationMinutes) {
        this.albumId = albumId;
        this.title = title;
        this.artistId = artistId;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.coverImage = coverImage;
        this.totalTracks = totalTracks;
        this.durationMinutes = durationMinutes;
    }

    // Getters and Setters
    public int getAlbumId() { return albumId; }
    public void setAlbumId(int albumId) { this.albumId = albumId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public int getTotalTracks() { return totalTracks; }
    public void setTotalTracks(int totalTracks) { this.totalTracks = totalTracks; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }

    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }

    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", title='" + title + '\'' +
                ", artistId=" + artistId +
                ", releaseDate=" + releaseDate +
                ", genre='" + genre + '\'' +
                ", totalTracks=" + totalTracks +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}