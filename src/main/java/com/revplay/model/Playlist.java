package com.revplay.model;

import java.time.LocalDateTime;
import java.util.List;

public class Playlist {
    private int playlistId;
    private String name;
    private int userId;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdDate;
    private int totalSongs;
    private int totalDuration;
    private User user;
    private List<Song> songs;

    public Playlist() {}

    public Playlist(int playlistId, String name, int userId, String description,
                    boolean isPublic, LocalDateTime createdDate,
                    int totalSongs, int totalDuration) {
        this.playlistId = playlistId;
        this.name = name;
        this.userId = userId;
        this.description = description;
        this.isPublic = isPublic;
        this.createdDate = createdDate;
        this.totalSongs = totalSongs;
        this.totalDuration = totalDuration;
    }

    // Getters and Setters
    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public int getTotalSongs() { return totalSongs; }
    public void setTotalSongs(int totalSongs) { this.totalSongs = totalSongs; }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }

    public String getFormattedDuration() {
        int hours = totalDuration / 3600;
        int minutes = (totalDuration % 3600) / 60;
        int seconds = totalDuration % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistId=" + playlistId +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", isPublic=" + isPublic +
                ", createdDate=" + createdDate +
                ", totalSongs=" + totalSongs +
                ", totalDuration=" + getFormattedDuration() +
                '}';
    }
}