package com.vinylteam.vinyl.discogs4j.client;

import com.vinylteam.vinyl.util.PropertiesReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscogsClientTest {

    @Test
    @DisplayName("Checking if wantlist from Discogs is correct")
    void wantlistTest() {
        //prepare
        PropertiesReader propertiesReader = new PropertiesReader();
        DiscogsClient discogsClient = new DiscogsClient(
                propertiesReader.getProperty("consumer.key"),
                propertiesReader.getProperty("consumer.secret"),
                propertiesReader.getProperty("user.agent"),
                propertiesReader.getProperty("callback.url")
        );
        String discogsWantList = "{\"pagination\": {\"page\": 1, \"pages\": 1, \"per_page\": 50, \"items\": 3, \"urls\": {}}, \"wants\": [{\"id\": 12748000, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/12748000\", \"rating\": 0, \"date_added\": \"2021-04-07T12:49:32-07:00\", \"basic_information\": {\"id\": 12748000, \"master_id\": 29341, \"master_url\": \"https://api.discogs.com/masters/29341\", \"resource_url\": \"https://api.discogs.com/releases/12748000\", \"title\": \"Best Of Scorpions\", \"year\": 1991, \"formats\": [{\"name\": \"Cassette\", \"qty\": \"1\", \"descriptions\": [\"Compilation\", \"Reissue\"]}], \"labels\": [{\"name\": \"RCA\", \"catno\": \"NK 74006\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 895, \"resource_url\": \"https://api.discogs.com/labels/895\"}], \"artists\": [{\"name\": \"Scorpions\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 63552, \"resource_url\": \"https://api.discogs.com/artists/63552\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": [\"Hard Rock\"]}}, {\"id\": 2288564, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/2288564\", \"rating\": 0, \"date_added\": \"2021-04-10T13:33:03-07:00\", \"basic_information\": {\"id\": 2288564, \"master_id\": null, \"master_url\": null, \"resource_url\": \"https://api.discogs.com/releases/2288564\", \"title\": \"No Freedom No Liberty\", \"year\": 2007, \"formats\": [{\"name\": \"Vinyl\", \"qty\": \"1\", \"text\": \"green\", \"descriptions\": [\"7\\\"\"]}], \"labels\": [{\"name\": \"True Rebel Records\", \"catno\": \"TRR012\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 139696, \"resource_url\": \"https://api.discogs.com/labels/139696\"}], \"artists\": [{\"name\": \"The Detectors\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 932626, \"resource_url\": \"https://api.discogs.com/artists/932626\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": []}}, {\"id\": 1400099, \"resource_url\": \"https://api.discogs.com/users/Anthony_Hopkins/wants/1400099\", \"rating\": 0, \"date_added\": \"2021-04-10T13:39:02-07:00\", \"basic_information\": {\"id\": 1400099, \"master_id\": 16990, \"master_url\": \"https://api.discogs.com/masters/16990\", \"resource_url\": \"https://api.discogs.com/releases/1400099\", \"title\": \"\\u0416\\u0430\\u043b\\u044c, \\u041d\\u0435\\u0442 \\u0420\\u0443\\u0436\\u044c\\u044f\", \"year\": 2002, \"formats\": [{\"name\": \"CD\", \"qty\": \"1\", \"descriptions\": [\"Album\"]}], \"labels\": [{\"name\": \"\\u041c\\u0438\\u0441\\u0442\\u0435\\u0440\\u0438\\u044f \\u0417\\u0432\\u0443\\u043a\\u0430\", \"catno\": \"MZ-076-2\", \"entity_type\": \"1\", \"entity_type_name\": \"Label\", \"id\": 29191, \"resource_url\": \"https://api.discogs.com/labels/29191\"}], \"artists\": [{\"name\": \"\\u041a\\u043e\\u0440\\u043e\\u043b\\u044c \\u0418 \\u0428\\u0443\\u0442\", \"anv\": \"\", \"join\": \"\", \"role\": \"\", \"tracks\": \"\", \"id\": 386468, \"resource_url\": \"https://api.discogs.com/artists/386468\"}], \"thumb\": \"\", \"cover_image\": \"\", \"genres\": [\"Rock\"], \"styles\": [\"Punk\"]}}]}";
        //when
        String resultWantList = discogsClient.wantlist("Anthony_Hopkins");
        //then
        assertEquals(discogsWantList, resultWantList);
    }

}