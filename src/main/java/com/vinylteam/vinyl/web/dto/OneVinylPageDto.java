package com.vinylteam.vinyl.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class OneVinylPageDto {

    List<OneVinylOfferDto> offersResponseList;

    List<UniqueVinylDto> preparedVinylsList;

    String discogsLink;

}
