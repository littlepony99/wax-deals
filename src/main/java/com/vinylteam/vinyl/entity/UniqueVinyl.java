package com.vinylteam.vinyl.entity;

import java.util.Objects;

public class UniqueVinyl {

    private long id;
    private String release;
    private String artist;
    private String fullName;
    private String imageLink;
    private boolean hasOffers;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean getHasOffers() {
        return this.hasOffers;
    }

    public void setHasOffers(boolean hasOffers) {
        this.hasOffers = hasOffers;
    }

    @Override
    public String toString() {
        return "\nUniqueVinyl{" +
                "id=" + id +
                ", release='" + release + '\'' +
                ", artist='" + artist + '\'' +
                ", fullName='" + fullName + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", hasOffers='" + hasOffers + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniqueVinyl)) return false;
        UniqueVinyl uniqueVinyl = (UniqueVinyl) o;
        return id == uniqueVinyl.id &&
                Objects.equals(release, uniqueVinyl.release) &&
                Objects.equals(artist, uniqueVinyl.artist) &&
                Objects.equals(fullName, uniqueVinyl.fullName) &&
                Objects.equals(imageLink, uniqueVinyl.imageLink) &&
                hasOffers == uniqueVinyl.hasOffers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, release, artist, fullName, imageLink, hasOffers);
    }

}
