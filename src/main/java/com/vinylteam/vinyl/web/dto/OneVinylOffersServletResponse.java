package com.vinylteam.vinyl.web.dto;

public class OneVinylOffersServletResponse {

    private Double price;
    private String currency;
    private String catNumber;
    private boolean inStock;
    private String offerLink;
    private String shopImageLink;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCatNumber() {
        return catNumber;
    }

    public void setCatNumber(String catNumber) {
        this.catNumber = catNumber;
    }

    public boolean getInStock() {
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

    public String getShopImageLink() {
        return shopImageLink;
    }

    public void setShopImageLink(String shopImageLink) {
        this.shopImageLink = shopImageLink;
    }

}
