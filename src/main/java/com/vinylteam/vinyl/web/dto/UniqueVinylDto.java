package com.vinylteam.vinyl.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniqueVinylDto {

    private String id;
    private String release;
    private String artist;
    private String fullName;
    private String imageLink;
    private boolean hasOffers;

}
