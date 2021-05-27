package com.vinylteam.vinyl.web.dto;

public class OneVinylOffersServletResponse {

    private Double price;
    private String offerLink;
    private String shopImageLink;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
