package com.vinylteam.vinyl.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.stereotype.Component;

@Value
@Builder
@Component
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
