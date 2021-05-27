package com.vinylteam.vinyl.entity;

import java.util.Objects;
import java.util.Optional;

public class Offer {

    private long id;
    private long uniqueVinylId;
    private int shopId;
    private double price;
    private Optional<Currency> currency;
    private String genre;
    private String catNumber;
    private boolean inStock;
    private String offerLink;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUniqueVinylId() {
        return uniqueVinylId;
    }

    public void setUniqueVinylId(long uniqueVinylId) {
        this.uniqueVinylId = uniqueVinylId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
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

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", uniqueVinylId=" + uniqueVinylId +
                ", shopId=" + shopId +
                ", price=" + price +
                ", currency=" + currency +
                ", genre='" + genre + '\'' +
                ", catNumber='" + catNumber + '\'' +
                ", inStock=" + inStock +
                ", offerLink='" + offerLink + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer)) return false;
        Offer offer = (Offer) o;
        return id == offer.id && uniqueVinylId == offer.uniqueVinylId && shopId == offer.shopId && Double.compare(offer.price, price) == 0 && inStock == offer.inStock && Objects.equals(currency, offer.currency) && Objects.equals(genre, offer.genre) && Objects.equals(catNumber, offer.catNumber) && Objects.equals(offerLink, offer.offerLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uniqueVinylId, shopId, price, currency, genre, catNumber, inStock, offerLink);
    }

}
