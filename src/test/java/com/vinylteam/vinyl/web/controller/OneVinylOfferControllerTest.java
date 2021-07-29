package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.impl.OneVinylOffersServiceImpl;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class OneVinylOfferControllerTest {
    @Autowired
    private OneVinylOfferController vinylOfferController;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UniqueVinylMapper uniqueVinylMapper;
    @MockBean
    private OneVinylOffersServiceImpl oneVinylOffersService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void getOneVinyl() throws Exception {
        // prepare
        UniqueVinyl uniqueVinyl = UniqueVinyl.builder()
                .artist("AC/DC")
                .fullName("AC/DC - Razor's Edge")
                .hasOffers(true)
                .id("AQYASABEgLaHPD_BwE")
                .imageLink("https://vinyla.com/files/products/3d9d222277344794bfdc706d2f735c82.800x800.png?92bb0783d44f109e08cc28b2963d572b")
                .release("2010")
                .build();
        List<OneVinylOfferDto> offersList = new ArrayList<>();
        offersList.add(OneVinylOfferDto.builder()
                .catNumber("catNumber")
                .currency("UAH")
                .inStock(true)
                .offerLink("www.offers.com/ao901")
                .price(50.6)
                .shopImageLink("www.shelagio.com/acdc-201012387.png")
                .build());

        List<UniqueVinyl> authorVinyls = new ArrayList<>();
        UniqueVinyl anotherAuthorVinyl = UniqueVinyl.builder()
                .artist("AC/DC")
                .fullName("AC/DC - Highway To Hell (Limited Edition)")
                .hasOffers(true)
                .id("EAIaIQobChMIpIqQ4_WH8gIVDrh")
                .imageLink("https://vinyla.com/files/products/ac-dc-highway-to-hell-limited-edition.1280x1280.jpg?d5e5db11fe0b2263ab644ab906259e51")
                .release("2010")
                .build();
        authorVinyls.add(uniqueVinyl);
        authorVinyls.add(anotherAuthorVinyl);

        when(oneVinylOffersService.getUniqueVinyl(anyString())).thenReturn(uniqueVinyl);
        when(oneVinylOffersService.getOffers(anyString())).thenReturn(offersList);
        when(oneVinylOffersService.addAuthorVinyls(any(UniqueVinyl.class))).thenReturn(authorVinyls);
        when(oneVinylOffersService.getDiscogsLink(any(UniqueVinyl.class))).thenReturn("www.disckogs.com");
        // when
        MockHttpServletResponse response = mockMvc.perform(get("/oneVinyl?id=1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(status().isOk()).andReturn().getResponse();
        // then
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
        System.out.println(response.getContentAsString());
    }

    @Test
    public void testMapper() {
        UniqueVinyl vinyl = UniqueVinyl.builder()
                .release("RELEASE")
                .imageLink("imageLine")
                .id("123")
                .hasOffers(true)
                .fullName("funn lame")
                .artist("artist")
                .build();
        UniqueVinylDto dto = uniqueVinylMapper.map(vinyl);
        Assertions.assertEquals(dto.getId(), vinyl.getId());
        Assertions.assertEquals(dto.getArtist(), vinyl.getArtist());
        Assertions.assertEquals(dto.getFullName(), vinyl.getFullName());
        Assertions.assertEquals(dto.getImageLink(), vinyl.getImageLink());
        Assertions.assertEquals(dto.getRelease(), vinyl.getRelease());
    }

    @Test
    public void testMapperList() {
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
        List<UniqueVinylDto> dto = uniqueVinylMapper.listVinylsToListVinylsDto(vinylList);
        Assertions.assertEquals(dto.get(0).getId(), vinylList.get(0).getId());
        Assertions.assertEquals(dto.get(0).getRelease(), vinylList.get(0).getRelease());
        Assertions.assertEquals(dto.get(0).getImageLink(), vinylList.get(0).getImageLink());
        Assertions.assertEquals(dto.get(0).getFullName(), vinylList.get(0).getFullName());
        Assertions.assertEquals(dto.get(0).getArtist(), vinylList.get(0).getArtist());
    }
}
