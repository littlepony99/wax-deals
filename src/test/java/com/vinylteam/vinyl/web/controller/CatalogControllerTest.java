package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.exception.entity.CatalogErrors;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.impl.DefaultCatalogService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class CatalogControllerTest {

    @Autowired
    private CatalogController catalogController;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UniqueVinylMapper uniqueVinylMapper;
    @MockBean
    private DefaultCatalogService catalogService;

    private MockMvc mockMvc;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reset(catalogService);
    }

    @Test
    @DisplayName("Gets list of unique vinyl dtos")
    void getCatalogPage() {
        //prepare
        List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
        when(catalogService.findRandomUniqueVinyls(50)).thenReturn(dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(uniqueVinyls));
        List<UniqueVinylDto> expectedUniqueVinylDtoList = dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(uniqueVinyls);
        //when
        List<UniqueVinylDto> actualUniqueVinylDtoList = catalogController.getCatalogPage();
        //then
        assertEquals(expectedUniqueVinylDtoList, actualUniqueVinylDtoList);
        verify(catalogService).findRandomUniqueVinyls(50);
    }

    @Test
    @DisplayName("Checks the returning json when list is filled")
    void getCataloguePageJsonFilledList() throws Exception {
        //prepare
        List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
        when(catalogService.findRandomUniqueVinyls(50)).thenReturn(dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(uniqueVinyls));
        //when
        MockHttpServletResponse response = mockMvc.perform(get("/catalog"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].release").isNotEmpty())
                .andExpect(jsonPath("$[0].artist").isNotEmpty())
                .andExpect(jsonPath("$[0].imageLink").isNotEmpty())
                .andExpect(jsonPath("$[1].id").isNotEmpty())
                .andExpect(jsonPath("$[1].release").isNotEmpty())
                .andExpect(jsonPath("$[1].artist").isNotEmpty())
                .andExpect(jsonPath("$[1].imageLink").isNotEmpty())
                .andExpect(jsonPath("$[2].id").isNotEmpty())
                .andExpect(jsonPath("$[2].release").isNotEmpty())
                .andExpect(jsonPath("$[2].artist").isNotEmpty())
                .andExpect(jsonPath("$[2].imageLink").isNotEmpty())
                .andExpect(jsonPath("$[3].id").isNotEmpty())
                .andExpect(jsonPath("$[3].release").isNotEmpty())
                .andExpect(jsonPath("$[3].artist").isNotEmpty())
                .andExpect(jsonPath("$[3].imageLink").isNotEmpty())
                .andExpect(status().isOk()).andReturn().getResponse();
        //then
        verify(catalogService).findRandomUniqueVinyls(50);
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
    }

    @Test
    @DisplayName("Checks the returning json when list is empty")
    void getCatalogPageJsonEmptyList() throws Exception {
        //prepare
        when(catalogService.findRandomUniqueVinyls(50)).thenReturn(new ArrayList<>());
        //when
        MockHttpServletResponse response = mockMvc.perform(get("/catalog"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();
        //then
        verify(catalogService).findRandomUniqueVinyls(50);
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
        Assertions.assertEquals("[]", response.getContentAsString());
    }

    @Test
    public void getOneVinyl() throws Exception {
        // prepare
        String id = "1";
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        Map<String, List<?>> offersAndShopsMap = dataGenerator.getOneVinylOffersAndShopsMap();
        List<UniqueVinyl> artistVinyls = dataGenerator.getUniqueVinylsByArtistList(uniqueVinyl.getArtist());
        artistVinyls.remove(uniqueVinyl);
        String discogsLink = "link";
        OneVinylPageDto oneVinylPageDto = dataGenerator.getOneVinylPageDto(discogsLink, uniqueVinyl, offersAndShopsMap, artistVinyls);
        when(catalogService.getOneVinylPageDto(id)).thenReturn(oneVinylPageDto);
        // when
        MockHttpServletResponse response = mockMvc.perform(get("/catalog/1").param("id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.offersList").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.id").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.release").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.artist").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.imageLink").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].id").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].release").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].artist").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].imageLink").isNotEmpty())
                .andExpect(jsonPath("$.discogsLink").isNotEmpty())
                .andExpect(status().isOk()).andReturn().getResponse();
        // then
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getHeader("Content-Type"));
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
    }

    @Test
    void getOneVinylNoVinylsByArtist() throws Exception {
        // prepare
        String id = "1";
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        Map<String, List<?>> offersAndShopsMap = dataGenerator.getOneVinylOffersAndShopsMap();
        String discogsLink = "link";
        OneVinylPageDto oneVinylPageDto = dataGenerator.getOneVinylPageDto(discogsLink, uniqueVinyl, offersAndShopsMap, new ArrayList<>());
        when(catalogService.getOneVinylPageDto(id)).thenReturn(oneVinylPageDto);
        // when
        MockHttpServletResponse response = mockMvc.perform(get("/catalog/1").param("id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.offersList").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.id").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.release").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.artist").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.imageLink").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList").isEmpty())
                .andExpect(jsonPath("$.discogsLink").isNotEmpty())
                .andExpect(status().isOk()).andReturn().getResponse();
        // then
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getHeader("Content-Type"));
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
    }


    @Test
    void getOneVinylNoVinylById() throws Exception {
        String wrongId = "id";
        when(catalogService.getOneVinylPageDto(eq(wrongId))).thenThrow(new NotFoundException(CatalogErrors.VINYL_BY_ID_NOT_FOUND.getMessage()));
        var response = mockMvc.perform(get("/catalog/" + wrongId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo(CatalogErrors.VINYL_BY_ID_NOT_FOUND.getMessage())));
    }

    @Test
    public void testUniqueVinylMapper() {
        UniqueVinyl vinyl = UniqueVinyl.builder()
                .release("RELEASE")
                .imageLink("imageLine")
                .id("123")
                .offers(true)
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
                .offers(true)
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