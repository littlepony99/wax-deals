package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CatalogControllerTest {

    @Autowired
    private CatalogController catalogController;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private UniqueVinylService mockedUniqueVinylService;
    @MockBean
    private WantListService mockedWantListService;
    @MockBean
    private UniqueVinylMapper mockedUniqueVinylMapper;

    private MockMvc mockMvc;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reset(mockedUniqueVinylService);
    }

    @Test
    @DisplayName("Gets list of unique vinyl dtos")
    void getCatalogPage() {
        //prepare
        List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
        User user = dataGenerator.getUserWithNumber(1);
        List<UniqueVinylDto> expectedUniqueVinylDtoList = dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(uniqueVinyls);
        when(mockedUniqueVinylService.findRandom(50)).thenReturn(uniqueVinyls);
        when(mockedWantListService.mergeVinylsWithWantList(any(), any())).thenReturn(expectedUniqueVinylDtoList);
        //when
        List<UniqueVinylDto> actualUniqueVinylDtoList = catalogController.getCatalogPage(user);
//        then
        assertEquals(expectedUniqueVinylDtoList, actualUniqueVinylDtoList);
        verify(mockedUniqueVinylService).findRandom(50);
    }

    @Test
    @DisplayName("Checks the returning json when list is filled")
    void getCataloguePageJsonFilledList() throws Exception {
        //prepare
        List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
        List<UniqueVinylDto> expectedUniqueVinylDtoList = dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(uniqueVinyls);
        when(mockedUniqueVinylService.findRandom(50)).thenReturn(uniqueVinyls);
        when(mockedUniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(anyList())).thenReturn(expectedUniqueVinylDtoList);
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
        verify(mockedUniqueVinylService).findRandom(50);
        Assertions.assertNotNull(response.getContentAsString());
    }

    @Test
    @DisplayName("Checks the returning json when list is empty")
    void getCatalogPageJsonEmptyList() throws Exception {
        //prepare
        when(mockedUniqueVinylService.findRandom(50)).thenReturn(new ArrayList<>());
        when(mockedUniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(any())).thenReturn(new ArrayList<>());
        //when
        MockHttpServletResponse response = mockMvc.perform(get("/catalog"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();
        //then
        verify(mockedUniqueVinylService).findRandom(50);
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        assertEquals("application/json", response.getHeader("Content-Type"));
        assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
        assertEquals("[]", response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Controller do not query watListService for anonymous user catalog request")
    void WantListImportRequestNonExistingUserTest() throws Exception {
        //when
        mockMvc.perform(get("/catalog"));
        //then
        verify(mockedWantListService, never()).mergeVinylsWithWantList(any(Long.class), anyList());
    }

}