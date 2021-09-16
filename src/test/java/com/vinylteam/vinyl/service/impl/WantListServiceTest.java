package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.WantListRepository;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WantListServiceTest {
    @Autowired
    private WantListServiceImpl wantListService;

    @MockBean
    private DiscogsService discogsService;
    @MockBean
    private UniqueVinylService uniqueVinylService;
    @MockBean
    private WantListRepository wantListRepository;
    @MockBean
    private UniqueVinylMapper uniqueVinylMapper;

    @Test
    @DisplayName("Return searchResults for user without wantList with valid fields")
    void mergeSearchResultForUserWithoutWantListTest() {
        // before
        Long userId = 1L;
        List<UniqueVinylDto> vinylsList = new ArrayList<>();
        UniqueVinylDto vinylDto = UniqueVinylDto.builder()
                .id("id")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .isWantListItem(null)
                .build();
        vinylsList.add(vinylDto);
        // when
        when(wantListRepository.findAllByUserId(userId)).thenReturn(null);
        List<UniqueVinylDto> resultList = wantListService.mergeSearchResult(userId, vinylsList);
        // then
        Assertions.assertNotNull(resultList);
        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(vinylsList.size(), resultList.size());
        Assertions.assertEquals(vinylsList.get(0).getId(), resultList.get(0).getId());
        Assertions.assertEquals(vinylsList.get(0).getArtist(), resultList.get(0).getArtist());
        Assertions.assertEquals(vinylsList.get(0).getImageLink(), resultList.get(0).getImageLink());
        Assertions.assertEquals(vinylsList.get(0).getRelease(), resultList.get(0).getRelease());
        Assertions.assertFalse(resultList.get(0).getIsWantListItem());
    }

    @Test
    @DisplayName("Return searchResults for user with wantList, with valid fields")
    void mergeSearchResultForUserWithWantListTest() {
        // before
        Long userId = 1L;
        List<UniqueVinylDto> vinylsList = new ArrayList<>();
        UniqueVinylDto vinylDto = UniqueVinylDto.builder()
                .id("id")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .isWantListItem(null)
                .build();
        vinylsList.add(vinylDto);
        UniqueVinylDto secondVinylDto = UniqueVinylDto.builder()
                .id("id2")
                .artist("artist2")
                .imageLink("imageLink2")
                .release("release2")
                .isWantListItem(null)
                .build();
        vinylsList.add(secondVinylDto);
        List<WantedVinyl> wantList = new ArrayList<>();
        WantedVinyl wantedItem = WantedVinyl.builder()
                .id("wantListId")
                .vinylId("id")
                .artist("artist")
                .release("release")
                .imageLink("imageLink")
                .addedAt(Date.valueOf(LocalDate.now()))
                .build();
        wantList.add(wantedItem);
        // when
        when(wantListRepository.findAllByUserId(userId)).thenReturn(wantList);
        List<UniqueVinylDto> resultList = wantListService.mergeSearchResult(userId, vinylsList);
        // then
        Assertions.assertNotNull(resultList);
        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(vinylsList.size(), resultList.size());
        Assertions.assertEquals(vinylsList.get(0).getId(), resultList.get(0).getId());
        Assertions.assertEquals(vinylsList.get(0).getArtist(), resultList.get(0).getArtist());
        Assertions.assertEquals(vinylsList.get(0).getImageLink(), resultList.get(0).getImageLink());
        Assertions.assertEquals(vinylsList.get(0).getRelease(), resultList.get(0).getRelease());
        Assertions.assertTrue(resultList.get(0).getIsWantListItem());
        Assertions.assertEquals(vinylsList.get(1).getId(), resultList.get(1).getId());
        Assertions.assertEquals(vinylsList.get(1).getArtist(), resultList.get(1).getArtist());
        Assertions.assertEquals(vinylsList.get(1).getImageLink(), resultList.get(1).getImageLink());
        Assertions.assertEquals(vinylsList.get(1).getRelease(), resultList.get(1).getRelease());
        Assertions.assertFalse(resultList.get(1).getIsWantListItem());
    }

    @Test
    @DisplayName("Add vinyl, not yet added to wantList")
    void addNewVinylToWantListTest() throws ForbiddenException {
        // before
        UniqueVinylDto vinylDto = UniqueVinylDto.builder()
                .id("id")
                .build();
        UniqueVinyl uniqueVinyl = UniqueVinyl.builder()
                .id("vinylId")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .fullName("fullName")
                .build();
        WantedVinyl wantedItem = WantedVinyl.builder()
                .id("wantedId")
                .userId(1L)
                .vinylId("vinylId")
                .artist("artist")
                .release("release")
                .imageLink("imageLink")
                .addedAt(Date.valueOf(LocalDate.now()))
                .build();
        User user = User.builder()
                .id(1L)
                .build();

        // when
        when(uniqueVinylService.findById(vinylDto.getId())).thenReturn(uniqueVinyl);
        when(wantListRepository.findByVinylIdAndUserId(vinylDto.getId(), user.getId())).thenReturn(Optional.empty());
        when(wantListRepository.save(any())).thenReturn(wantedItem);
        WantedVinyl result = wantListService.addWantedVinyl(user, vinylDto);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getAddedAt());
        Assertions.assertEquals(wantedItem.getId(), result.getId());
        Assertions.assertEquals(user.getId(), result.getUserId());
        Assertions.assertEquals(uniqueVinyl.getId(), result.getVinylId());
        Assertions.assertEquals(uniqueVinyl.getRelease(), result.getRelease());
        Assertions.assertEquals(uniqueVinyl.getArtist(), result.getArtist());
        Assertions.assertEquals(uniqueVinyl.getImageLink(), result.getImageLink());
    }

    @Test
    @DisplayName("Add existing vinyl in wantList(delete vinyl from wantList")
    void deleteVinylFromWantListTest() throws ForbiddenException {
        // before
        UniqueVinylDto vinylDto = UniqueVinylDto.builder()
                .id("id")
                .build();
        UniqueVinyl uniqueVinyl = UniqueVinyl.builder()
                .id("vinylId")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .fullName("fullName")
                .build();
        WantedVinyl wantedItem = WantedVinyl.builder()
                .vinylId("vinylId")
                .artist("artist")
                .release("release")
                .imageLink("imageLink")
                .addedAt(Date.valueOf(LocalDate.now()))
                .build();
        User user = User.builder()
                .id(1L)
                .build();

        // when
        when(uniqueVinylService.findById(vinylDto.getId())).thenReturn(uniqueVinyl);
        when(wantListRepository.findByVinylIdAndUserId(vinylDto.getId(), user.getId()))
                .thenReturn(Optional.of(wantedItem));
        doNothing().when(wantListRepository).deleteById(wantedItem.getId());
        WantedVinyl result = wantListService.addWantedVinyl(user, vinylDto);
        // then
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Add vinyl with wrong id to watchList")
    void addNonWrongVinylToWantListTest() {
        // before
        UniqueVinylDto vinylDto = UniqueVinylDto.builder()
                .id("wrong")
                .build();
        User user = User.builder()
                .email("test_user@gmail.com")
                .role(Role.USER)
                .id(1L)
                .build();
        //when
        when(uniqueVinylService.findById("wrong")).thenReturn(null);
        assertThrows(ForbiddenException.class, () -> wantListService.addWantedVinyl(user, vinylDto));
    }

    @Test
    @DisplayName("Get unique vinyls from wantList, with valid fields")
    void getUniqueVinylWantListTest() {
        // before
        Long userId = 1L;
        WantedVinyl wantedVinyl = WantedVinyl.builder()
                .id("id")
                .userId(userId)
                .vinylId("vinylId")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .build();
        List<WantedVinyl> wantList = new ArrayList<>();
        wantList.add(wantedVinyl);

        UniqueVinylDto uniqueVinylDto = UniqueVinylDto.builder()
                .id("vinylId")
                .release("release")
                .imageLink("imageLink")
                .artist("artist")
                .build();
        List<UniqueVinylDto> uniqueDtoVinylList = new ArrayList<>();
        uniqueDtoVinylList.add(uniqueVinylDto);

        // when
        when(wantListRepository.findAllByUserId(userId)).thenReturn(wantList);
        when(uniqueVinylMapper.wantedVinylsToUniqueVinylDtoList(wantList)).thenReturn(uniqueDtoVinylList);
        List<UniqueVinylDto> resultList = wantListService.getWantListUniqueVinyls(userId);

        // then
        Assertions.assertNotNull(resultList);
        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(uniqueVinylDto.getId(), resultList.get(0).getId());
        Assertions.assertEquals(uniqueVinylDto.getRelease(), resultList.get(0).getRelease());
        Assertions.assertEquals(uniqueVinylDto.getImageLink(), resultList.get(0).getImageLink());
        Assertions.assertEquals(uniqueVinylDto.getArtist(), resultList.get(0).getArtist());
        Assertions.assertTrue(resultList.get(0).getIsWantListItem());

    }

    @Test
    @DisplayName("Import wantList, check that all matches added to wantList")
    void importTaskAllMatchesAddedToWantListTest() {
        // before
        UniqueVinyl firstUniqueVinyl = UniqueVinyl.builder()
                .id("id")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .fullName("fullName")
                .build();
        UniqueVinyl secondUniqueVinyl = UniqueVinyl.builder()
                .id("secondId")
                .artist("secondArtist")
                .imageLink("secondImageLink")
                .release("secondRelease")
                .fullName("secondFullName")
                .build();
        UniqueVinyl thirdUniqueVinyl = UniqueVinyl.builder()
                .id("thirdVinylId")
                .artist("thirdArtist")
                .imageLink("thirdImageLink")
                .release("thirdRelease")
                .fullName("thirdFullName")
                .build();
        List<UniqueVinyl> findAllList = new ArrayList<>();
        findAllList.add(firstUniqueVinyl);
        findAllList.add(secondUniqueVinyl);
        findAllList.add(thirdUniqueVinyl);
        List<UniqueVinyl> matchedVinylsList = new ArrayList<>();
        matchedVinylsList.add(secondUniqueVinyl);
        matchedVinylsList.add(thirdUniqueVinyl);
        User user = User.builder()
                .email("test_user@gmail.com")
                .role(Role.USER)
                .id(1L)
                .discogsUserName("shelberg")
                .build();

        //when
        when(uniqueVinylService.findAll()).thenReturn(findAllList);
        when(discogsService.getDiscogsMatchList(user.getDiscogsUserName(), findAllList)).thenReturn(matchedVinylsList);
        wantListService.importWantList(user);

        // then
        verify(wantListRepository, times(2)).save(any(WantedVinyl.class));
    }

    @Test
    @DisplayName("Get wantList")
    void getWantListTest() {
        // before
        Long userId = 1L;
        WantedVinyl wantedVinyl = WantedVinyl.builder()
                .id("id")
                .userId(userId)
                .vinylId("vinylId")
                .artist("artist")
                .imageLink("imageLink")
                .release("release")
                .build();
        WantedVinyl secondWantedVinyl = WantedVinyl.builder()
                .id("secondId")
                .userId(userId)
                .vinylId("secondVinylId")
                .artist("secondArtist")
                .imageLink("secondImageLink")
                .release("secondRelease")
                .build();
        List<WantedVinyl> wantList = new ArrayList<>();
        wantList.add(wantedVinyl);
        wantList.add(secondWantedVinyl);

        // when
        when(wantListRepository.findAllByUserId(userId)).thenReturn(wantList);
        List<WantedVinyl> resultList = wantListService.getWantList(userId);
        // then
        Assertions.assertNotNull(resultList);
        Assertions.assertFalse(resultList.isEmpty());
        Assertions.assertEquals(wantList.size(), resultList.size());
        Assertions.assertEquals(userId, resultList.get(0).getUserId());
        Assertions.assertEquals(wantList.get(0).getId(), resultList.get(0).getId());
        Assertions.assertEquals(wantList.get(0).getArtist(), resultList.get(0).getArtist());
        Assertions.assertEquals(wantList.get(0).getRelease(), resultList.get(0).getRelease());
        Assertions.assertEquals(wantList.get(0).getImageLink(), resultList.get(0).getImageLink());
        Assertions.assertEquals(userId, resultList.get(1).getUserId());
        Assertions.assertEquals(wantList.get(1).getId(), resultList.get(1).getId());
        Assertions.assertEquals(wantList.get(1).getArtist(), resultList.get(1).getArtist());
        Assertions.assertEquals(wantList.get(1).getRelease(), resultList.get(1).getRelease());
        Assertions.assertEquals(wantList.get(1).getImageLink(), resultList.get(1).getImageLink());
    }

}
