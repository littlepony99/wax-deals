package com.vinylteam.vinyl.web.dto;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class OneVinylPageFullResponse {

    List<OneVinylOffersServletResponse> offersResponseList;

    List<UniqueVinyl> preparedVinylsList;

    String discogsLink;

}
