package com.revplay.model;

import java.time.LocalDate;

public class Song {
    private int songId;
    private String title;
    private int artistId;
    private Integer albumId;
    private int durationSeconds;
    private String genre;
    private String filePath;
    private LocalDate releaseDate;
    private int playCount;
    private boolean isActive;
    private Artist artist;
    private Album album;

    public Song() {}

    public Song(int songId, String title, int artistId, Integer albumId,
                int durationSeconds, String genre, String filePath,
                LocalDate releaseDate, int playCount, boolean isActive) {
        this.songId = songId;
        this.title = title;
        this.artistId = artistId;
        this.albumId = albumId;
        this.durationSeconds = durationSeconds;
        this.genre = genre;
        this.filePath = filePath;
        this.releaseDate = releaseDate;
        this.playCount = playCount;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public int getPlayCount() { return playCount; }
    public void setPlayCount(int playCount) { this.playCount = playCount; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", title='" + title + '\'' +
                ", artistId=" + artistId +
                ", albumId=" + albumId +
                ", duration=" + getFormattedDuration() +
                ", genre='" + genre + '\'' +
                ", playCount=" + playCount +
                '}';
    }
}