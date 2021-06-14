package com.vinylteam.vinyl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.discogs4j.client.DiscogsClient;
import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.discogs4j.entity.RawResponse;
import com.vinylteam.vinyl.discogs4j.util.HttpRequest;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DefaultDiscogsService implements DiscogsService {

    public static final String REGEX_FOR_SPLIT = "[- ()!@#$%^&*_+={}:;\"']";

    private final ObjectMapper objectMapper;
    private final DiscogsClient discogsClient;

    private final String CONSUMER_KEY;
    private final String CONSUMER_SECRET;
    private final String USER_AGENT;
    private final String CALLBACK_URL;

    public DefaultDiscogsService(@Value("${consumer.key}") String consumerKey,
                                 @Value("${consumer.secret}") String consumerSecret,
                                 @Value("${user.agent}") String userAgent,
                                 @Value("${callback.url}") String callbackUrl) {
        this.CONSUMER_KEY = consumerKey;
        this.CONSUMER_SECRET = consumerSecret;
        this.USER_AGENT = userAgent;
        this.CALLBACK_URL = callbackUrl;
        this.discogsClient = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL);
        this.objectMapper = new ObjectMapper();
        try {
            discogsClient.getRequestToken();
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Failed to connect to discogs user with {'consumeKey': {}, {'consumerSecret': {}, " +
                    "{'userAgent': {}, {'callbackUrl': {}} ", CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UniqueVinyl> getDiscogsMatchList(String discogsUserName, List<UniqueVinyl> allUniqueVinyl) {
        List<UniqueVinyl> forShowing = new ArrayList<>();
        if (discogsUserName == null || allUniqueVinyl == null || discogsUserName.isEmpty() || allUniqueVinyl.isEmpty()) {
            return forShowing;
        }
        Optional<List<DiscogsVinylInfo>> discogsVinylInfo = getDiscogsVinylInfo(discogsUserName);
        log.debug("Get list with DiscogsVinylInfo {'discogsVinylInfo':{}}", discogsVinylInfo);
        if (discogsVinylInfo.isPresent()) {
            for (DiscogsVinylInfo vinylInfo : discogsVinylInfo.get()) {
                String release = getParametersForComparison(vinylInfo.getRelease());
                log.debug("Prepared release from DiscogsVinylInfo for comparison with release from UniqueVinyl {'release':{}}",
                        release);
                String artist = getParametersForComparison(vinylInfo.getArtist());
                log.debug("Prepared artist from DiscogsVinylInfo for comparison with artist from UniqueVinyl {'artist':{}}",
                        artist);
                for (UniqueVinyl uniqueVinyl : allUniqueVinyl) {
                    String uniqueRelease = getParametersForComparison(uniqueVinyl.getRelease());
                    log.debug("Prepared uniqueRelease from UniqueVinyl for comparison with release from DiscogsVinylInfo {'uniqueRelease':{}}",
                            uniqueRelease);
                    String uniqueArtist = getParametersForComparison(uniqueVinyl.getArtist());
                    log.debug("Prepared uniqueArtist from UniqueVinyl for comparison with artist from DiscogsVinylInfo {'uniqueArtist':{}}",
                            uniqueArtist);
                    if (release.equals(uniqueRelease) && artist.equals(uniqueArtist)) {
                        forShowing.add(uniqueVinyl);
                        log.debug("Comparison with artist & release from DiscogsVinylInfo & UniqueVinyl is successful. UniqueVinyl " +
                                        "was added into list of UniqueVinyl that call 'forShowing' {'forShowing':{}}",
                                forShowing);
                    }
                }
            }
        }
        return forShowing;
    }

    @Override
    public String getDiscogsLink(String artist, String release, String fullName) throws ParseException {
        String query;
        String requestBody;
        String discogsLink = "";
        if (artist == null || release == null || fullName == null
                || artist.isEmpty() || release.isEmpty() || fullName.isEmpty()
                || !fullName.toLowerCase().contains(artist.toLowerCase())
                || !fullName.toLowerCase().contains(release.toLowerCase())) {
            return discogsLink;
        }
        artist = artist.toLowerCase();
        release = release.toLowerCase();
        fullName = fullName.toLowerCase();
        String[] preparedFullNameForMatching;
        if (artist.toLowerCase().contains("various")) {
            preparedFullNameForMatching = Arrays.stream(release.split(REGEX_FOR_SPLIT)).filter(e -> e.trim().length() > 0).toArray(String[]::new);
        } else {
            preparedFullNameForMatching = Arrays.stream(fullName.split(REGEX_FOR_SPLIT)).filter(e -> e.trim().length() > 0).toArray(String[]::new);
        }
        log.debug("Prepared preparedFullNameForMatching for comparison with artist from Discogs {'preparedFullNameForMatching':{}}", preparedFullNameForMatching);
        query = String.join("+", preparedFullNameForMatching);
        log.debug("Prepared query for search vinyl on Discogs {'query':{}}", query);
        HttpRequest request = HttpRequest.get("https://api.discogs.com/database/search?q=" + query +
                "&token=DzUqaiWPuQDWExZlqrAUuIZBYAHuBjNnapETonep");
        requestBody = request.body();
        log.debug("Get requestBody after search vinyl on Discogs {'requestBody':{}}", requestBody);
        JSONObject jsonRequest = (JSONObject) new JSONParser().parse(requestBody);
        log.debug("Get JSONObject from requestBody after search vinyl on Discogs {'jsonRequest':{}}", jsonRequest);
        JSONArray resultSearch = (JSONArray) jsonRequest.get("results");
        log.debug("Get JSONArray of necessary data from JSONObject after search vinyl on Discogs {'resultSearch':{}}", resultSearch);
        if (resultSearch != null) {
            discogsLink = getMatchedDiscogsLinks(resultSearch, preparedFullNameForMatching);
        }
        return discogsLink;
    }

    String getMatchedDiscogsLinks(JSONArray resultSearch, String[] preparedFullNameForMatching) {
        String discogsLink = "";
        int maxMatching = 0;
        float currentMatchPercent;
        int requiredPercentageOfMatch = 75;
        for (Object searchItem : resultSearch) {
            int currentMatching = 0;
            JSONObject jsonItem = (JSONObject) searchItem;
            String discogsFullName = jsonItem.get("title").toString().toLowerCase();
            String[] preparedDiscogsFullName = Arrays.stream(discogsFullName.split(REGEX_FOR_SPLIT)).filter(e -> e.trim().length() > 0).toArray(String[]::new);
            for (String prepareItem : preparedFullNameForMatching) {
                if (discogsFullName.contains(prepareItem.toLowerCase())) {
                    currentMatching++;
                }
            }
            if (currentMatching > maxMatching) {
                maxMatching = currentMatching;
                currentMatchPercent = ((float) maxMatching) / preparedFullNameForMatching.length * 100;
                if (currentMatchPercent >= requiredPercentageOfMatch
                        || preparedDiscogsFullName.length == currentMatching) {
                    discogsLink = jsonItem.get("uri").toString();
                    discogsLink = "https://www.discogs.com/ru" + discogsLink;
                }
            }
        }
        log.debug("Created link of vinyl to Discogs after successful comparison with data from db {'discogsLink':{}}", discogsLink);
        return discogsLink;
    }

    Optional<List<DiscogsVinylInfo>> getDiscogsVinylInfo(String discogsUserName) {
        if (discogsUserName == null || discogsUserName.isEmpty()) {
            return Optional.empty();
        }
        String discogsWantList = discogsClient.wantlist(discogsUserName);
        try {
            if (discogsWantList != null) {
                RawResponse rawResponse = objectMapper.readValue(discogsWantList, RawResponse.class);
                return rawResponse.getVinylsInfo();
            }
            return Optional.empty();
        } catch (JsonProcessingException e) {
            log.error("{'wantList':{}}", discogsWantList, e);
            throw new RuntimeException("Exception while want list json processing", e);
        }
    }

    String getParametersForComparison(String param) {
        if (param == null) {
            return "";
        }
        String[] paramArray = param.split(" ");
        log.debug("Split param into param array {'param':{}, 'paramArray':{}}", param, paramArray);
        if (paramArray.length > 1 && (paramArray[0].equalsIgnoreCase("the") || paramArray[0].equalsIgnoreCase("a"))) {
            paramArray[0] = paramArray[1];
        }
        log.debug("Resulting string is {'resultParam':{}}", paramArray[0]);
        return paramArray[0].toLowerCase();
    }

}
