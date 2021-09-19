package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchResultsControllerTest {

    @Autowired
    private SearchResultsController searchResultsController;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private JwtTokenProvider jwtService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private Filter jwtValidatorFilter;
    @MockBean
    private UniqueVinylService mockedUniqueVinylService;
    @SpyBean
    private UserDao userDao;

    private String testUserEmail;
    private String testUserPassword = "Password123";

    private MockMvc mockMvc;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeAll
    void beforeAll() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(jwtValidatorFilter)
                .build();
        String encPassword = encoder.encode(testUserPassword);
        testUserEmail = "user_email@gmail.com";
        createTestUser(testUserEmail, encPassword);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reset(mockedUniqueVinylService);
    }

    private void createTestUser(String user, String encPassword) {
        User userToBeCreated = User.builder()
                .status(true)
                .email(user)
                .salt("salt")
                .role(Role.USER)
                .password(encPassword)
                .build();
        userDao.add(userToBeCreated);
    }

    @Test
    @DisplayName("Gets list of filtered unique vinyl dtos")
    void getSearchResultPage() throws Exception {
        //prepare
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));
        String matcher = "release";
        List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
        when(mockedUniqueVinylService.findByFilter(matcher)).thenReturn(uniqueVinyls);
        List<UniqueVinylDto> expectedUniqueVinylDtoList = dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(uniqueVinyls);
        //when
        mockMvc.perform(get("/search?matcher=release")
                .header("Authorization", loginResponse.getJwtToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(expectedUniqueVinylDtoList.get(0).getId()))
                .andExpect(jsonPath("$[0].release").value(expectedUniqueVinylDtoList.get(0).getRelease()))
                .andExpect(jsonPath("$[0].artist").value(expectedUniqueVinylDtoList.get(0).getArtist()))
                .andExpect(jsonPath("$[0].imageLink").value(expectedUniqueVinylDtoList.get(0).getImageLink()))
                .andExpect(jsonPath("$[0].isWantListItem").value(expectedUniqueVinylDtoList.get(0).getIsWantListItem()))
                .andExpect(jsonPath("$[1].id").value(expectedUniqueVinylDtoList.get(1).getId()))
                .andExpect(jsonPath("$[1].release").value(expectedUniqueVinylDtoList.get(1).getRelease()))
                .andExpect(jsonPath("$[1].artist").value(expectedUniqueVinylDtoList.get(1).getArtist()))
                .andExpect(jsonPath("$[1].imageLink").value(expectedUniqueVinylDtoList.get(1).getImageLink()))
                .andExpect(jsonPath("$[1].isWantListItem").value(expectedUniqueVinylDtoList.get(1).getIsWantListItem()))
                .andExpect(jsonPath("$[2].id").value(expectedUniqueVinylDtoList.get(2).getId()))
                .andExpect(jsonPath("$[2].release").value(expectedUniqueVinylDtoList.get(2).getRelease()))
                .andExpect(jsonPath("$[2].artist").value(expectedUniqueVinylDtoList.get(2).getArtist()))
                .andExpect(jsonPath("$[2].imageLink").value(expectedUniqueVinylDtoList.get(2).getImageLink()))
                .andExpect(jsonPath("$[2].isWantListItem").value(expectedUniqueVinylDtoList.get(2).getIsWantListItem()))
                .andExpect(jsonPath("$[3].id").value(expectedUniqueVinylDtoList.get(3).getId()))
                .andExpect(jsonPath("$[3].release").value(expectedUniqueVinylDtoList.get(3).getRelease()))
                .andExpect(jsonPath("$[3].artist").value(expectedUniqueVinylDtoList.get(3).getArtist()))
                .andExpect(jsonPath("$[3].imageLink").value(expectedUniqueVinylDtoList.get(3).getImageLink()))
                .andExpect(jsonPath("$[3].isWantListItem").value(expectedUniqueVinylDtoList.get(3).getIsWantListItem()))
                .andExpect(status().isOk()).andReturn().getResponse();
        //then
        verify(mockedUniqueVinylService).findByFilter(matcher);
    }

    @Test
    @DisplayName("Checks the returning json when list is filled")
    void getCataloguePageJsonFilledList() throws Exception {
        //prepare
        String matcher = "release";
        List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
        when(mockedUniqueVinylService.findByFilter(matcher)).thenReturn(uniqueVinyls);
        //when
        MockHttpServletResponse response = mockMvc
                .perform(get("/search").param("matcher", matcher))
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
        verify(mockedUniqueVinylService).findByFilter(matcher);
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
    }

    @Test
    @DisplayName("Checks the returning json when list is empty")
    void getSearchResultPageJsonEmptyList() throws Exception {
        //prepare
        String noMatchMatcher = "newrelease";
        when(mockedUniqueVinylService.findByFilter(noMatchMatcher)).thenReturn(new ArrayList<>());
        //when
        MockHttpServletResponse response = mockMvc
                .perform(get("/search")
                        .param("matcher", noMatchMatcher))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();
        //then
        verify(mockedUniqueVinylService).findByFilter(noMatchMatcher);
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
        Assertions.assertEquals("[]", response.getContentAsString());
    }

}