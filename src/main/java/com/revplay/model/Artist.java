package com.revplay.model;

public class Artist {
    private int artistId;
    private String stageName;
    private String genre;
    private String recordLabel;
    private int monthlyListeners;
    private String socialMediaLinks;
    private User userDetails;

    public Artist() {}

    public Artist(int artistId, String stageName, String genre, String recordLabel,
                  int monthlyListeners, String socialMediaLinks) {
        this.artistId = artistId;
        this.stageName = stageName;
        this.genre = genre;
        this.recordLabel = recordLabel;
        this.monthlyListeners = monthlyListeners;
        this.socialMediaLinks = socialMediaLinks;
    }

    // Getters and Setters
    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getRecordLabel() { return recordLabel; }
    public void setRecordLabel(String recordLabel) { this.recordLabel = recordLabel; }

    public int getMonthlyListeners() { return monthlyListeners; }
    public void setMonthlyListeners(int monthlyListeners) { this.monthlyListeners = monthlyListeners; }

    public String getSocialMediaLinks() { return socialMediaLinks; }
    public void setSocialMediaLinks(String socialMediaLinks) { this.socialMediaLinks = socialMediaLinks; }

    public User getUserDetails() { return userDetails; }
    public void setUserDetails(User userDetails) { this.userDetails = userDetails; }

    @Override
    public String toString() {
        return "Artist{" +
                "artistId=" + artistId +
                ", stageName='" + stageName + '\'' +
                ", genre='" + genre + '\'' +
                ", recordLabel='" + recordLabel + '\'' +
                ", monthlyListeners=" + monthlyListeners +
                '}';
    }
}