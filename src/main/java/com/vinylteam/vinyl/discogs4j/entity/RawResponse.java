package com.vinylteam.vinyl.discogs4j.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RawResponse {

    @JsonProperty("wants")
    private List<DiscogsVinylInfo> vinylsInfo;

    public Optional<List<DiscogsVinylInfo>> getVinylsInfo() {
        return Optional.ofNullable(vinylsInfo);
    }

}
