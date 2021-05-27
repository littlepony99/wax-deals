package com.vinylteam.vinyl.entity;

import java.util.Optional;

public class RawOffer {

    private int shopId;
    private String release;
    private String artist;
    private double price;
    private Optional<Currency> currency;
    private String genre;
    private String offerLink;
    private String imageLink;

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Optional<Currency> getCurrency() {
        return currency;
    }

    public void setCurrency(Optional<Currency> currency) {
        this.currency = currency;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getOfferLink() {
        return offerLink;
    }

    public void setOfferLink(String offerLink) {
        this.offerLink = offerLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public String toString() {
        return "\nRawOffer{" +
                "shopId=" + shopId +
                ", release='" + release + '\'' +
                ", artist='" + artist + '\'' +
                ", price=" + price +
                ", currency=" + currency +
                ", offerLink='" + offerLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

}
