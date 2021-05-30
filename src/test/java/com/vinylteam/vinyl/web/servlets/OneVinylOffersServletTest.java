package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.*;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.impl.ParserHolder;
import com.vinylteam.vinyl.util.impl.VinylParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class OneVinylOffersServletTest {

    private final UniqueVinylService mockedUniqueVinylService = mock(UniqueVinylService.class);
    private final OfferService mockedOfferService = mock(OfferService.class);
    private final ShopService mockedShopService = mock(ShopService.class);
    private final InOrder inOrderUniqueVinylService = inOrder(mockedUniqueVinylService);
    private final InOrder inOrderOfferService = inOrder(mockedOfferService);
    private final DiscogsService discogsService = mock(DiscogsService.class);
    private final VinylParser parser = mock(VinylParser.class);
    private final ParserHolder parserHolder = new ParserHolder(List.of(parser));
    private final OneVinylOffersServlet oneVinylOffersServlet = new OneVinylOffersServlet(mockedUniqueVinylService,
            mockedOfferService, mockedShopService, discogsService, parserHolder);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final UniqueVinyl mockedUniqueVinyl = mock(UniqueVinyl.class);
    private final Offer mockedOffer = mock(Offer.class);
    private final Shop mockedShop = mock(Shop.class);
    private final List<UniqueVinyl> uniqueVinyls = List.of(mockedUniqueVinyl);
    private final List<Offer> offers = List.of(mockedOffer);
    private final List<Integer> shopsIds = new ArrayList<>(List.of(1));
    private final List<Shop> shopsByIds = List.of(mockedShop);

    @BeforeEach
    void beforeEach() {
        reset(mockedUniqueVinylService);
        reset(mockedOfferService);
        reset(mockedShopService);
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
        reset(mockedUniqueVinyl);
        reset(mockedOffer);
        reset(mockedShop);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session isn't exist")
    void doGetWithNotExistedSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(null);

        when(mockedRequest.getParameter("id")).thenReturn("1");
        when(mockedUniqueVinylService.findById(1)).thenReturn(mockedUniqueVinyl);

        when(mockedUniqueVinyl.getId()).thenReturn(1L);
        when(mockedOfferService.findManyByUniqueVinylId(1)).thenReturn(offers);
        when(mockedOfferService.getListOfShopIds(offers)).thenReturn(shopsIds);
        when(mockedShopService.getManyByListOfIds(shopsIds)).thenReturn(shopsByIds);
        RawOffer returnedOffer = new RawOffer();
        returnedOffer.setPrice(12.11d);
        when(parser.getRawOfferFromOfferLink(any())).thenReturn(returnedOffer);

        when(mockedUniqueVinyl.getArtist()).thenReturn("artist1");
        when(mockedUniqueVinylService.findManyByArtist("artist1")).thenReturn(uniqueVinyls);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        oneVinylOffersServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        verify(mockedRequest).getParameter("id");
        inOrderUniqueVinylService.verify(mockedUniqueVinylService).findById(1);

        inOrderOfferService.verify(mockedOfferService).findManyByUniqueVinylId(1);
        inOrderOfferService.verify(mockedOfferService).getListOfShopIds(offers);
        verify(mockedShopService).getManyByListOfIds(shopsIds);
        verify(parser).getRawOfferFromOfferLink(any());

        verify(mockedUniqueVinyl, times(4)).getArtist();
        inOrderUniqueVinylService.verify(mockedUniqueVinylService).findManyByArtist("artist1");
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);

        when(mockedRequest.getParameter("id")).thenReturn("1");
        when(mockedUniqueVinylService.findById(1)).thenReturn(mockedUniqueVinyl);

        when(mockedUniqueVinyl.getId()).thenReturn(1L);
        when(mockedOfferService.findManyByUniqueVinylId(1)).thenReturn(List.of(mockedOffer));
        when(mockedOfferService.getListOfShopIds(offers)).thenReturn(shopsIds);
        when(mockedShopService.getManyByListOfIds(shopsIds)).thenReturn(shopsByIds);

        when(mockedUniqueVinyl.getArtist()).thenReturn("artist1");
        when(mockedUniqueVinylService.findManyByArtist("artist1")).thenReturn(uniqueVinyls);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        oneVinylOffersServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedRequest).getParameter("id");
        inOrderUniqueVinylService.verify(mockedUniqueVinylService).findById(1);

        inOrderOfferService.verify(mockedOfferService).findManyByUniqueVinylId(1L);
        inOrderOfferService.verify(mockedOfferService).getListOfShopIds(offers);
        verify(mockedShopService).getManyByListOfIds(shopsIds);
        verify(parser).getRawOfferFromOfferLink(any());

        verify(mockedUniqueVinyl, times(4)).getArtist();
        inOrderUniqueVinylService.verify(mockedUniqueVinylService).findManyByArtist("artist1");
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);

        when(mockedRequest.getParameter("id")).thenReturn("1");
        when(mockedUniqueVinylService.findById(1)).thenReturn(mockedUniqueVinyl);

        when(mockedUniqueVinyl.getId()).thenReturn(1L);
        when(mockedOfferService.findManyByUniqueVinylId(1)).thenReturn(List.of(mockedOffer));
        when(mockedOfferService.getListOfShopIds(offers)).thenReturn(shopsIds);
        when(mockedShopService.getManyByListOfIds(shopsIds)).thenReturn(shopsByIds);

        when(mockedUniqueVinyl.getArtist()).thenReturn("artist1");
        when(mockedUniqueVinylService.findManyByArtist("artist1")).thenReturn(uniqueVinyls);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        oneVinylOffersServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, never()).getRole();
        verify(mockedRequest).getParameter("id");
        inOrderUniqueVinylService.verify(mockedUniqueVinylService).findById(1);

        inOrderOfferService.verify(mockedOfferService).findManyByUniqueVinylId(1L);
        inOrderOfferService.verify(mockedOfferService).getListOfShopIds(offers);
        verify(mockedShopService).getManyByListOfIds(shopsIds);
        verify(parser).getRawOfferFromOfferLink(any());

        verify(mockedUniqueVinyl, times(4)).getArtist();
        inOrderUniqueVinylService.verify(mockedUniqueVinylService).findManyByArtist("artist1");
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all data from offer are copied into OneVinylOfferResponse")
    void getOfferResponseFromOfferTest() {
        var generator = new DataGeneratorForTests();
        var offers = generator.getOffersList();
        var shop = new Shop();
        shop.setId(4);
        shop.setSmallImageLink("imageLink");
        for (Offer offer : offers) {
            var offerResponse = oneVinylOffersServlet.getOfferResponseFromOffer(offer, shop);
            assertEquals(offer.getPrice(), offerResponse.getPrice());
            assertEquals(offer.getOfferLink(), offerResponse.getOfferLink());
            assertEquals(offer.getCurrency().get().getSymbol(), offerResponse.getCurrency());
            assertEquals(offer.getCatNumber(), offerResponse.getCatNumber());
            assertEquals(shop.getSmallImageLink(), offerResponse.getShopImageLink());
        }
    }

}