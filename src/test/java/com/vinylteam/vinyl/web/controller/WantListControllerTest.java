package com.vinylteam.vinyl.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WantListControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private Filter jwtValidatorFilter;
    @Autowired
    private JwtTokenProvider jwtService;

    @MockBean
    private WantListService wantListService;
    @SpyBean
    private UserDao userDao;

    private String testUserEmail;
    private String testUserPassword = "Password123";

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
    @DisplayName("Get wanted vinyls list for authenticated user")
    void getUserWantListTest() throws Exception {
        //before
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        List<UniqueVinylDto> resultList = new ArrayList<>();
        UniqueVinylDto wantedVinyl = UniqueVinylDto.builder()
                .id("12")
                .release("release")
                .artist("artist")
                .imageLink("link")
                .isWantListItem(Boolean.TRUE)
                .build();
        resultList.add(wantedVinyl);

        // when
        when(wantListService.getWantListUniqueVinyls(anyLong())).thenReturn(resultList);
        mockMvc.perform(get("/wantlist")
                .header("Authorization", loginResponse.getJwtToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(wantedVinyl.getId()))
                .andExpect(jsonPath("$[0].release").value(wantedVinyl.getRelease()))
                .andExpect(jsonPath("$[0].artist").value(wantedVinyl.getArtist()))
                .andExpect(jsonPath("$[0].imageLink").value(wantedVinyl.getImageLink()))
                .andExpect(jsonPath("$[0].isWantListItem").value(wantedVinyl.getIsWantListItem()))
                .andExpect(status().isOk()).andReturn().getResponse();
    }

    @Test
    @DisplayName("Add wanted vinyl for authenticated user")
    void addWantedVinylTest() throws Exception {
        //before
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        UniqueVinylDto wantedVinyl = UniqueVinylDto.builder()
                .id("12")
                .build();

        String jsonRequest = (new ObjectMapper()).writeValueAsString(wantedVinyl);
        // when
        WantedVinyl wantItem = WantedVinyl.builder()
                .id("1234")
                .vinylId("12")
                .addedAt(Date.valueOf(LocalDate.now()))
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .build();
        when(wantListService.addWantedVinyl(ArgumentMatchers.any(User.class),
                ArgumentMatchers.any(UniqueVinylDto.class))).thenReturn(wantItem);
        mockMvc.perform(post("/wantlist")
                .header("Authorization", loginResponse.getJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Controller calls wantList import method for authenticated user")
    void importWantListTest() throws Exception {
        //before
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        // when
        mockMvc.perform(post("/wantlist/import")
                .header("Authorization", loginResponse.getJwtToken()))
                .andExpect(status().isOk());
        // then
        verify(wantListService).importWantList(any());
    }


    @Test
    @WithAnonymousUser
    @DisplayName("Controller returns error as a response for non-existing user for get watchList request")
    void getWantListRequestNonExistingUserTest() throws Exception {
        mockMvc.perform(get("/wantlist"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Access is denied")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Controller returns error as a response for non-existing user for get watchList request")
    void getWantListRequestTest() throws Exception {
        mockMvc.perform(get("/wantlist"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Access is denied")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Controller returns error as a response for non-existing user for add wantedItem request")
    void AddWantedItemRequestNonExistingUserTest() throws Exception {
        UniqueVinylDto vinylDto = UniqueVinylDto.builder().id("1234").build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(vinylDto);
        mockMvc.perform(post("/wantlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Access is denied")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Controller returns error as a response for non-existing user for add wantedItem request")
    void WantListImportRequestNonExistingUserTest() throws Exception {
        mockMvc.perform(post("/wantlist/import"))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Access is denied")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}
