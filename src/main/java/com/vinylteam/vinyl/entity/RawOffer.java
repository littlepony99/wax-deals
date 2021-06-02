package com.vinylteam.vinyl.entity;

import java.util.Objects;
import java.util.Optional;

public class RawOffer {

    private int shopId;
    private String release;
    private String artist;
    private double price;
    private Optional<Currency> currency;
    private String genre;
    private String catNumber;
    private boolean inStock;
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

    public String getCatNumber() {
        return catNumber;
    }

    public void setCatNumber(String catNumber) {
        this.catNumber = catNumber;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
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
                ", genre='" + genre + '\'' +
                ", catNumber='" + catNumber + '\'' +
                ", inStock=" + inStock +
                ", offerLink='" + offerLink + '\'' +
                ", imageLink='" + imageLink + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawOffer)) return false;
        RawOffer rawOffer = (RawOffer) o;
        return shopId == rawOffer.shopId &&
                Double.compare(rawOffer.price, price) == 0 &&
                inStock == rawOffer.inStock &&
                Objects.equals(release, rawOffer.release) &&
                Objects.equals(artist, rawOffer.artist) &&
                Objects.equals(currency, rawOffer.currency) &&
                Objects.equals(genre, rawOffer.genre) &&
                Objects.equals(catNumber, rawOffer.catNumber) &&
                Objects.equals(offerLink, rawOffer.offerLink) &&
                Objects.equals(imageLink, rawOffer.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopId, release, artist, price, currency, genre, catNumber, inStock, offerLink, imageLink);
    }

}
