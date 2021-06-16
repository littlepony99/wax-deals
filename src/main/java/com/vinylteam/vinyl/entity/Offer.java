package com.vinylteam.vinyl.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
public class Offer {

    private long id;
    private long uniqueVinylId;
    private int shopId;
    private double price;
    private Optional<Currency> currency;
    @EqualsAndHashCode.Exclude
    private String genre;
    private String catNumber;
    private boolean inStock;
    private String offerLink;

}
