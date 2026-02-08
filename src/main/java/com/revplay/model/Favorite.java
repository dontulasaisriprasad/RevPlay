package com.revplay.model;

import java.time.LocalDateTime;

public class Favorite {
    private int favoriteId;
    private int userId;
    private int songId;
    private LocalDateTime addedDate;
    private Song song;
    private User user;

    public Favorite() {}

    public Favorite(int favoriteId, int userId, int songId, LocalDateTime addedDate) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.songId = songId;
        this.addedDate = addedDate;
    }

    // Getters and Setters
    public int getFavoriteId() { return favoriteId; }
    public void setFavoriteId(int favoriteId) { this.favoriteId = favoriteId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public LocalDateTime getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }

    public Song getSong() { return song; }
    public void setSong(Song song) { this.song = song; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Favorite{" +
                "favoriteId=" + favoriteId +
                ", userId=" + userId +
                ", songId=" + songId +
                ", addedDate=" + addedDate +
                '}';
    }
}