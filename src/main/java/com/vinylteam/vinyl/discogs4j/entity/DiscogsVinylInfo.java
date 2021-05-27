package com.vinylteam.vinyl.discogs4j.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscogsVinylInfo {

    @JsonIgnore
    private String release;
    @JsonIgnore
    private String artist;

    @JsonSetter("basic_information")
    public void deserializeBasicInformationNode(Map<String, Object> basicInformationNode) {
        if (basicInformationNode.isEmpty()) {
            log.error("Failed to deserialize basic information node: {}", Arrays.toString(basicInformationNode.entrySet().toArray()));
            return;
        }

        ArrayList<Map<String, String>> artistInfo = (ArrayList<Map<String, String>>) basicInformationNode.get("artists");
        this.release = (String) basicInformationNode.get("title");
        this.artist = artistInfo.get(0).get("name");
    }

    public String getRelease() {
        return release;
    }

    public String getArtist() {
        return artist;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MyCustomObject{" +
                "release='" + release + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

}


