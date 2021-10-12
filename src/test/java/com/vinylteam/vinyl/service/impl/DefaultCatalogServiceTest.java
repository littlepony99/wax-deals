package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultCatalogServiceTest {

    @Autowired
    @InjectMocks
    private DefaultCatalogService catalogService;

    @Autowired
    @MockBean
    private OfferService offerService;

    @Autowired
    @MockBean
    private ShopService shopService;

    @Autowired
    @MockBean
    private UniqueVinylService uniqueVinylService;

    @Autowired
    @MockBean
    private DiscogsService discogsService;

    @Autowired
    private UniqueVinylMapper uniqueVinylMapper;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void beforeEach() {
        reset(offerService);
        reset(shopService);
        reset(uniqueVinylService);
    }

    @Test
    @DisplayName("Checks whether vinyl.hasOffers flag is true when there are offers and is false when there ias no any offer for the vinyl")
    void givenVinylListWithNoOffer_whenHasOffersSetToFalse_thenCorrect() {
        //prepare
        var vinylsList = dataGenerator
                .getUniqueVinylsList()
                .stream()
                .filter(UniqueVinyl::hasOffers)
                .collect(Collectors.toList());
        List<Offer> offers = new ArrayList<>();
        UniqueVinyl testedUniqueVinyl = vinylsList.get(0);
        //when
        catalogService.checkIsVinylInStock(testedUniqueVinyl, offers);
        //then
        verify(uniqueVinylService).updateOneUniqueVinyl(testedUniqueVinyl);
        assertFalse(testedUniqueVinyl.hasOffers());
    }

    @Test
    @DisplayName("Checks whether OneVinylOffersServletResponse`s list is prepared based on offers list")
    void prepareOffersSection() {
        List<Offer> offers = dataGenerator.getOffersList();
        when(offerService.actualizeOffer(any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        when(offerService.mergeOfferChanges(any(), any(), any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        List<Offer> sortedInStockOffers = catalogService.getActualizedInStockOffersByPrice(offers);
        assertFalse(sortedInStockOffers.isEmpty());
        assertEquals(offers.size(), sortedInStockOffers.size());
    }

    @Test
    @DisplayName("checks whether shop can be found cby offer`s shop id")
    void findOfferShop() {
        var offer = dataGenerator.getOffersList().get(0);
        assertTrue(offer.getShopId() > 0);
        List<Shop> shopsList = dataGenerator.getShopsList();
        var shop = catalogService.findOfferShop(shopsList, offer);
        assertEquals(shop.getId(), offer.getShopId());
    }


    @Test
    @DisplayName("Checks whether another vinyls of the same artist are prepared to be shown on the 'one vinyl page'")
    void prepareVinylsByArtistSection() {
        //prepare
        List<UniqueVinyl> vinylListToBeReturned = dataGenerator.getUniqueVinylsList();
        UniqueVinyl vinyl = dataGenerator.getUniqueVinylsList().get(1);
        vinyl.setId("111");
        when(uniqueVinylService.findByArtist(vinyl.getArtist())).thenReturn(vinylListToBeReturned);
        //when
        List<UniqueVinyl> vinylsList = catalogService.getOtherUniqueVinylsByVinylArtist(vinyl);
        //then
        assertEquals(-1, vinylsList.indexOf(vinyl));
        assertEquals(vinylListToBeReturned.size(), vinylsList.size());
        for (UniqueVinyl uniqueVinyl : vinylListToBeReturned) {
            assertTrue(vinylsList.contains(uniqueVinyl));
        }
    }

    @Test
    @DisplayName("gets sorted in stock offers and shops for them when unique vinyl has currently valid offers")
    void getSortedInStockOffersAndShops_whenUniqueVinylHasValidOffers() throws NotFoundException {
        //prepare
        String id = "1";
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        uniqueVinyl.setHasOffers(true);
        List<Offer> offers = dataGenerator.getOffersList().subList(0, 2);
        List<Integer> shopIds = new ArrayList<>(List.of(1, 2));
        List<Shop> shops = dataGenerator.getShopsList().subList(0, 2);
        when(uniqueVinylService.findById(id)).thenReturn(uniqueVinyl);
        when(offerService.findByUniqueVinylId(id)).thenReturn(offers);
        when(offerService.actualizeOffer(offers.get(0))).thenReturn(offers.get(0));
        when(offerService.actualizeOffer(offers.get(1))).thenReturn(offers.get(1));
        when(offerService.findShopIds(offers)).thenReturn(shopIds);
        when(shopService.findShopsByListOfIds(shopIds)).thenReturn(shops);
        Map<String, List<?>> expectedSortedOffersAndShopsMap = new HashMap<>();
        expectedSortedOffersAndShopsMap.put("offers", offers);
        expectedSortedOffersAndShopsMap.put("shops", shops);
        //when
        Map<String, List<?>> actualSortedOffersAndShopsMap = catalogService.getSortedInStockOffersAndShops(id);
        //then
        assertEquals(expectedSortedOffersAndShopsMap, actualSortedOffersAndShopsMap);
        verify(uniqueVinylService).findById(id);
        verify(offerService).findByUniqueVinylId(id);
        verify(offerService).actualizeOffer(offers.get(0));
        verify(offerService).actualizeOffer(offers.get(1));
        verify(offerService).findShopIds(offers);
        verify(shopService).findShopsByListOfIds(shopIds);
    }

    @Test
    @DisplayName("Prepares one vinyl page DTO when offers for vinyl are in stock")
    void getOneVinylPageDtoActualisedInStockOffersExist() throws NotFoundException, ParseException {
        //prepare
        String id = "1";
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        uniqueVinyl.setHasOffers(true);
        List<Offer> offers = dataGenerator.getOffersList().subList(0, 2);
        List<Integer> shopIds = new ArrayList<>(List.of(1, 2));
        List<Shop> shops = dataGenerator.getShopsList().subList(0, 2);
        when(uniqueVinylService.findById(id)).thenReturn(uniqueVinyl);
        when(offerService.findByUniqueVinylId(id)).thenReturn(offers);
        when(offerService.actualizeOffer(offers.get(0))).thenReturn(offers.get(0));
        when(offerService.actualizeOffer(offers.get(1))).thenReturn(offers.get(1));
        when(offerService.findShopIds(offers)).thenReturn(shopIds);
        when(shopService.findShopsByListOfIds(shopIds)).thenReturn(shops);
        Map<String, List<?>> sortedOffersAndShopsMap = new HashMap<>();
        sortedOffersAndShopsMap.put("offers", offers);
        sortedOffersAndShopsMap.put("shops", shops);
        List<UniqueVinyl> fullVinylsByAuthor = dataGenerator.getUniqueVinylsByArtistList(uniqueVinyl.getArtist());
        when(uniqueVinylService.findByArtist(uniqueVinyl.getArtist())).thenReturn(fullVinylsByAuthor);
        List<UniqueVinyl> expectedOtherVinylsByArtist = fullVinylsByAuthor.subList(1, 2);
        String expectedDiscogsLink = "discogs/artist/release1";
        when(discogsService.getDiscogsLink(uniqueVinyl.getArtist(), uniqueVinyl.getRelease(), uniqueVinyl.getFullName())).thenReturn(expectedDiscogsLink);
        OneVinylPageDto expectedOneVinylPageDto = dataGenerator.getOneVinylPageDto(expectedDiscogsLink, uniqueVinyl, sortedOffersAndShopsMap, expectedOtherVinylsByArtist);
        //when
        OneVinylPageDto actualOneVinylPageDto = catalogService.getOneVinylPageDto(id, null);
        //then
        assertEquals(expectedOneVinylPageDto, actualOneVinylPageDto);
    }

    @Test
    @DisplayName("Prepares one vinyl page DTO when no offers are in stock")
    void getOneVinylPageDtoNoActualisedInStockOffersExist() throws NotFoundException, ParseException {
        //prepare
        String id = "1";
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        List<Offer> outOfStockOffers = dataGenerator.getOffersList().subList(0, 2);
        outOfStockOffers.get(0).setInStock(false);
        outOfStockOffers.get(1).setInStock(false);
        List<Integer> shopIds = new ArrayList<>();
        when(uniqueVinylService.findById(id)).thenReturn(uniqueVinyl);
        when(offerService.findByUniqueVinylId(id)).thenReturn(outOfStockOffers);
        when(offerService.actualizeOffer(outOfStockOffers.get(0))).thenReturn(outOfStockOffers.get(0));
        when(offerService.actualizeOffer(outOfStockOffers.get(1))).thenReturn(outOfStockOffers.get(1));
        when(offerService.findShopIds(outOfStockOffers)).thenReturn(shopIds);
        when(shopService.findShopsByListOfIds(shopIds)).thenReturn(new ArrayList<>());
        Map<String, List<?>> sortedOffersAndShopsMap = new HashMap<>();
        sortedOffersAndShopsMap.put("offers", new ArrayList<>());
        sortedOffersAndShopsMap.put("shops", new ArrayList<>());
        List<UniqueVinyl> fullVinylsByAuthor = dataGenerator.getUniqueVinylsByArtistList(uniqueVinyl.getArtist());
        when(uniqueVinylService.findByArtist(uniqueVinyl.getArtist())).thenReturn(fullVinylsByAuthor);
        List<UniqueVinyl> expectedOtherVinylsByArtist = fullVinylsByAuthor.subList(1, 2);
        String expectedDiscogsLink = "discogs/artist/release1";
        when(discogsService.getDiscogsLink(uniqueVinyl.getArtist(), uniqueVinyl.getRelease(), uniqueVinyl.getFullName())).thenReturn(expectedDiscogsLink);
        OneVinylPageDto expectedOneVinylPageDto = dataGenerator.getOneVinylPageDto(expectedDiscogsLink, uniqueVinyl, sortedOffersAndShopsMap, expectedOtherVinylsByArtist);
        //when
        OneVinylPageDto actualOneVinylPageDto = catalogService.getOneVinylPageDto(id, null);
        //then
        assertEquals(expectedOneVinylPageDto, actualOneVinylPageDto);
    }


    @Test
    public void testUniqueVinylMapper() {
        UniqueVinyl vinyl = UniqueVinyl.builder()
                .release("RELEASE")
                .imageLink("imageLine")
                .id("123")
                .hasOffers(true)
                .artist("artist")
                .build();
        UniqueVinylDto dto = uniqueVinylMapper.uniqueVinylToDto(vinyl);
        assertEquals(dto.getId(), vinyl.getId());
        assertEquals(dto.getArtist(), vinyl.getArtist());
        assertEquals(dto.getImageLink(), vinyl.getImageLink());
        assertEquals(dto.getRelease(), vinyl.getRelease());
    }

    @Test
    public void testUniqueVinylMapperList() {
        List<UniqueVinyl> vinylList = new ArrayList<>();
        UniqueVinyl vinyl = UniqueVinyl.builder()
                .release("RELEASE")
                .imageLink("imageLine")
                .id("123")
                .hasOffers(true)
                .fullName("funn lame")
                .artist("artist")
                .build();
        vinylList.add(vinyl);
        List<UniqueVinylDto> dto = uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(vinylList);
        Assertions.assertEquals(dto.get(0).getId(), vinylList.get(0).getId());
        Assertions.assertEquals(dto.get(0).getRelease(), vinylList.get(0).getRelease());
        Assertions.assertEquals(dto.get(0).getImageLink(), vinylList.get(0).getImageLink());
        Assertions.assertEquals(dto.get(0).getArtist(), vinylList.get(0).getArtist());
    }

}