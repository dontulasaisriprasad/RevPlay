package com.revplay.model;

import java.time.LocalDateTime;

public class ListeningHistory {
    private int historyId;
    private int userId;
    private int songId;
    private LocalDateTime listenedAt;
    private Integer playDurationSeconds;
    private Song song;
    private User user;

    public ListeningHistory() {}

    public ListeningHistory(int historyId, int userId, int songId,
                            LocalDateTime listenedAt, Integer playDurationSeconds) {
        this.historyId = historyId;
        this.userId = userId;
        this.songId = songId;
        this.listenedAt = listenedAt;
        this.playDurationSeconds = playDurationSeconds;
    }

    // Getters and Setters
    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public LocalDateTime getListenedAt() { return listenedAt; }
    public void setListenedAt(LocalDateTime listenedAt) { this.listenedAt = listenedAt; }

    public Integer getPlayDurationSeconds() { return playDurationSeconds; }
    public void setPlayDurationSeconds(Integer playDurationSeconds) { this.playDurationSeconds = playDurationSeconds; }

    public Song getSong() { return song; }
    public void setSong(Song song) { this.song = song; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "ListeningHistory{" +
                "historyId=" + historyId +
                ", userId=" + userId +
                ", songId=" + songId +
                ", listenedAt=" + listenedAt +
                ", playDurationSeconds=" + playDurationSeconds +
                '}';
    }
}