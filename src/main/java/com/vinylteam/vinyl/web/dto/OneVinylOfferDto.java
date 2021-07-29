package com.vinylteam.vinyl.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneVinylOfferDto {

    private Double price;
    private String currency;
    private String catNumber;
    private boolean inStock;
    private String offerLink;
    private String shopImageLink;

}
