package com.vinylteam.vinyl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.PropertiesReader;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultDiscogsServiceITest {

    private final List<UniqueVinyl> vinylListWithOneMatch;
    private final List<UniqueVinyl> vinylListWithNoMatch;
    private final PropertiesReader propertiesReader;
    private final DefaultDiscogsService defaultDiscogsService;

    public DefaultDiscogsServiceITest() {
        this.propertiesReader = new PropertiesReader();
        this.defaultDiscogsService = new DefaultDiscogsService(propertiesReader.getProperty("consumer.key"),
                propertiesReader.getProperty("consumer.secret"), propertiesReader.getProperty("user.agent"),
                propertiesReader.getProperty("callback.url"), new ObjectMapper());
        this.vinylListWithOneMatch = new ArrayList<>();
        this.vinylListWithNoMatch = new ArrayList<>();
    }

    @BeforeAll
    void beforeAll() {
        vinylListWithOneMatch.add(createVinyl("The Detectors", "No Freedom No Liberty"));
        vinylListWithOneMatch.add(createVinyl("Paul Jacobs", "Soul Grabber Part 2 (Remixes)"));
        vinylListWithOneMatch.add(createVinyl("Donnell & Douglas", "The Club Is Open"));
        vinylListWithNoMatch.add(createVinyl("Charis", "The Music, The Feelin'"));
        vinylListWithNoMatch.add(createVinyl("Paul Jacobs", "Soul Grabber Part 2 (Remixes)"));
        vinylListWithNoMatch.add(createVinyl("Donnell & Douglas", "The Club Is Open"));
    }

    @Test
    @DisplayName("Return empty list if discogs username is null")
    void getDiscogsMatchListWhenDiscogsUsernameIsNullTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList(null, vinylListWithOneMatch);
        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if discogs username is empty String")
    void getDiscogsMatchListWhenDiscogsUsernameIsEmptyStringTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("", vinylListWithOneMatch);
        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if list of unique vinyls is null")
    void getDiscogsMatchListWhenListOfUniqueVinylsIsNullTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("discogsUserName",
                null);
        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if list of unique vinyls is empty")
    void getDiscogsMatchListWhenListOfUniqueVinylsIsEmptyTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("discogsUserName",
                new ArrayList<>());
        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if discogs username isn't exist")
    void getDiscogsMatchListWhenDiscogsUsernameIsNotExistTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("not_exit_user_name",
                vinylListWithOneMatch);
        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return empty list if there is no match after matching")
    void getDiscogsMatchListWhenNoMatchingTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("Anthony_Hopkins",
                vinylListWithNoMatch);
        //then
        assertTrue(listAfterMatching.isEmpty());
    }

    @Test
    @DisplayName("Return list whit matched vinyls")
    void getDiscogsMatchListWhenThereIsMatchingTest() {
        //when
        List<UniqueVinyl> listAfterMatching = defaultDiscogsService.getDiscogsMatchList("Anthony_Hopkins",
                vinylListWithOneMatch);
        //then
        assertEquals(1, listAfterMatching.size());
    }

    @Test
    @DisplayName("Return empty String when artist is null")
    void getDiscogsLinkWhenArtistIsNullTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink(null, "No Freedom No Liberty",
                "null - No Freedom No Liberty");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when release is null")
    void getDiscogsLinkWhenReleaseIsNullTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", null,
                "The Detectors - null");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when full name is null")
    void getDiscogsLinkWhenFullNameIsNullTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("null", "null",
                null);
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when artist is empty")
    void getDiscogsLinkWhenArtistIsEmptyTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("", "No Freedom No Liberty",
                " - No Freedom No Liberty");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when release is empty")
    void getDiscogsLinkWhenReleaseIsEmptyTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", "",
                "The Detectors - ");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when full name is empty")
    void getDiscogsLinkWhenFullNameIsEmptyTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("empty", "empty",
                "");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when no link on Discogs")
    void getDiscogsLinkWhenNoSearchReleaseOnDiscogsTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("qwedsazxcvfr", "qwedsazxcvfr",
                "qwedsazxcvfr - qwedsazxcvfr");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when link on Discogs exist but full name doesn't contains artist & release")
    void getDiscogsLinkWhenFullNameDoesNotContainsArtistAndReleaseTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("artist", "release",
                "No Freedom No Liberty - The Detectors");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when link on Discogs exist but full name doesn't contains artist")
    void getDiscogsLinkWhenFullNameDoesNotContainsArtistTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("artist", "No Freedom No Liberty",
                "No Freedom No Liberty - The Detectors");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return empty String when link on Discogs exist but full name doesn't contains release")
    void getDiscogsLinkWhenFullNameDoesNotContainsReleaseTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", "release",
                "No Freedom No Liberty - The Detectors");
        //then
        assertEquals("", discogsLink);
    }

    @Test
    @DisplayName("Return Discogs link on release")
    void getDiscogsLinkWhenReleaseExistOnDiscogsTest() throws ParseException {
        //when
        String discogsLink = defaultDiscogsService.getDiscogsLink("The Detectors", "No Freedom No Liberty",
                "No Freedom No Liberty - The Detectors");
        //then
        assertEquals("https://www.discogs.com/ru/The-Detectors-No-Freedom-No-Liberty/release/2288564", discogsLink);
    }

    @Test
    @DisplayName("Return empty list of discogs vinyl info when discogs user name is null")
    void getDiscogsVinylInfoWhenDiscogsUserNameIsNullTest() {
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService.getDiscogsVinylInfo(null);
        //then
        assertEquals(Optional.empty(), discogsVinylInfoList);
    }

    @Test
    @DisplayName("Return empty list of discogs vinyl info when discogs user name is empty String")
    void getDiscogsVinylInfoWhenDiscogsUserNameIsEmptyStringTest() {
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService.getDiscogsVinylInfo("");
        //then
        assertEquals(Optional.empty(), discogsVinylInfoList);
    }

    @Test
    @DisplayName("Return empty list of discogs vinyl info when discogs want list is null")
    void getDiscogsVinylInfoWhenDiscogsWantListIsNullTest() {
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService
                .getDiscogsVinylInfo("not_exist_discogs_user_name");
        //then
        assertEquals(Optional.empty(), discogsVinylInfoList);
    }

    @Test
    @DisplayName("Return list of discogs vinyl info")
    void getDiscogsVinylInfoTest() {
        //when
        Optional<List<DiscogsVinylInfo>> discogsVinylInfoList = defaultDiscogsService
                .getDiscogsVinylInfo("Anthony_Hopkins");
        //then
        assertEquals(3, discogsVinylInfoList.get().size());
    }

    @Test
    @DisplayName("Return empty String when parameter is null")
    void getParametersForComparisonWhenParameterIsNullTest() {
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison(null);
        //then
        assertEquals("", parameterForComparison);
    }

    @Test
    @DisplayName("Return first word 'the' when String is 'ThE'")
    void getParametersForComparisonWhenStringContainsOnlyOneWordAndItIsTheTest() {
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("ThE");
        //then
        assertEquals("the", parameterForComparison);
    }

    @Test
    @DisplayName("Return first word 'a' when String is 'A'")
    void getParametersForComparisonWhenStringContainsOnlyOneWordAndItIsATest() {
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("A");
        //then
        assertEquals("a", parameterForComparison);
    }

    @Test
    @DisplayName("Return second word 'artist' when String is 'The Artist'")
    void getParametersForComparisonWhenStringContainsTwoWordsWithArticleTheTest() {
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("The Artist");
        //then
        assertEquals("artist", parameterForComparison);
    }

    @Test
    @DisplayName("Return second word 'release' when String is 'A Release'")
    void getParametersForComparisonWhenStringContainsTwoWordsWithArticleATest() {
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("A Release");
        //then
        assertEquals("release", parameterForComparison);
    }

    @Test
    @DisplayName("Return first word 'best' when String is 'BEST RELEASE is here'")
    void getParametersForComparisonWhenStringContainsManyWordsWithoutArticleTest() {
        //when
        String parameterForComparison = defaultDiscogsService.getParametersForComparison("BEST RELEASE is here");
        //then
        assertEquals("best", parameterForComparison);
    }

    private UniqueVinyl createVinyl(String artist, String release) {
        UniqueVinyl vinyl = new UniqueVinyl();
        vinyl.setArtist(artist);
        vinyl.setRelease(release);
        return vinyl;
    }

}