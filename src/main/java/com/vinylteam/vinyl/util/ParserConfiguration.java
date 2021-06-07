package com.vinylteam.vinyl.util;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
public class ParserConfiguration {

    String vinylGenresSelector;
    String offerLinkSelector;
    String releaseSelector;
    String artistSelector;
    String catalogNumberSelector;
    String priceDetailsSelector;
    String highResolutionImageSelector;
    String inStockMarkerSelector;
    String inStockMarker;

}
