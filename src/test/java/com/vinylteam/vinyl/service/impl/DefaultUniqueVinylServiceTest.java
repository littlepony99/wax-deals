
/*package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUniqueVinylDao;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUniqueVinylServiceTest {

    private final UniqueVinylDao mockedUniqueVinylDao = mock(JdbcUniqueVinylDao.class);
    private final UniqueVinylService uniqueVinylService = new DefaultUniqueVinylService(mockedUniqueVinylDao);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();

    @BeforeEach
    void beforeEach() {
        reset(mockedUniqueVinylDao);
    }

    @Test
    @DisplayName("Checks that when amount>0 UniqueVinylDao.findManyRandom(amount) is called, it's result is returned")
    void findManyRandomValidAmountTest() {
        //prepare
        int amount = 300;
        when(mockedUniqueVinylDao.findManyRandom(amount)).thenReturn(uniqueVinyls);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findManyRandom(amount);
        //then
        assertSame(uniqueVinyls, actualUniqueVinyls);
        verify(mockedUniqueVinylDao).findManyRandom(amount);
    }

    @Test
    @DisplayName("Checks that when amount<=0 UniqueVinylDao.findManyRandom(amount) is not called, empty list is returned")
    void findManyRandomInvalidAmountTest() {
        //prepare
        int amount = -300;
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findManyRandom(amount);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedUniqueVinylDao, never()).findManyRandom(amount);
    }

    @Test
    @DisplayName("Checks that when matcher is not null UniqueVinylDao.findManyFiltered(matcher) is called, it's result is returned")
    void findManyFilteredNotNullMatcherTest() {
        //prepare
        String matcher = "release1";
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls.subList(0, 1));
        when(mockedUniqueVinylDao.findManyFiltered(matcher)).thenReturn(expectedUniqueVinyls);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findManyFiltered(matcher);
        //then
        assertSame(expectedUniqueVinyls, actualUniqueVinyls);
        verify(mockedUniqueVinylDao).findManyFiltered(matcher);
    }

    @Test
    @DisplayName("Checks that when matcher is null UniqueVinylDao.findManyFiltered(matcher) is not called, empty list is returned")
    void findManyFilteredNullMatcherTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findManyFiltered(null);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedUniqueVinylDao, never()).findManyFiltered(null);
    }

    @Test
    @DisplayName("Checks that when artist is not null UniqueVinylDao.findManyByArtist(artist) is called, it's result is returned")
    void findManyByArtistNotNullArtistTest() {
        //prepare
        String artist = "artist1";
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls.subList(0, 1));
        when(mockedUniqueVinylDao.findManyByArtist(artist)).thenReturn(expectedUniqueVinyls);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findManyByArtist(artist);
        //then
        assertSame(expectedUniqueVinyls, actualUniqueVinyls);
        verify(mockedUniqueVinylDao).findManyByArtist(artist);
    }

    @Test
    @DisplayName("Checks that when artist is null UniqueVinylDao.findManyByArtist(artist) is not called, empty list is returned")
    void findManyByArtistNullArtistTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findManyByArtist(null);

        assertTrue(actualUniqueVinyls.isEmpty());
        verify(mockedUniqueVinylDao, never()).findManyByArtist(null);
    }

    @Test
    @DisplayName("Checks that when id>0 UniqueVinylDao.findById(id) is called, it's result is returned")
    void findByValidIdTest() {
        //prepare
        long id = 1;
        UniqueVinyl expectedUniqueVinyl = uniqueVinyls.get(0);
        when(mockedUniqueVinylDao.findById(id)).thenReturn(expectedUniqueVinyl);
        //when
        UniqueVinyl actualUniqueVinyl = uniqueVinylService.findById(id);
        //then
        assertSame(expectedUniqueVinyl, actualUniqueVinyl);
        verify(mockedUniqueVinylDao).findById(id);
    }

    @Test
    @DisplayName("Checks that when id<=0 UniqueVinylDao.findById(id) is not called, Runtime exception is thrown")
    void findByInvalidIdTest() {
        //prepare
        long id = -1;
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylService.findById(id));
        //then
        verify(mockedUniqueVinylDao, never()).findById(id);
    }

}*/
