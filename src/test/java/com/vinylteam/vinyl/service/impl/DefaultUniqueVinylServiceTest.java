package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultUniqueVinylServiceTest {

    @Autowired
    private UniqueVinylService uniqueVinylService;

    @Test
    @DisplayName("Checks that when amount<=0 UniqueVinylDao.findManyRandom(amount) is not called, empty list is returned")
    void findManyRandomInvalidAmountTest() {
        //prepare
        int amount = -300;
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findRandom(amount);
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Checks that when matcher is null UniqueVinylDao.findManyFiltered(matcher) is not called, empty list is returned")
    void findManyFilteredNullMatcherTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByFilter(null);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Checks that when artist is null UniqueVinylDao.findManyByArtist(artist) is not called, empty list is returned")
    void findManyByArtistNullArtistTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByArtist(null);
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Checks that when id is null UniqueVinylDao.findById(id) is not called, exception is thrown")
    void findByInvalidIdTest() {
        //when
        assertThrows(IllegalArgumentException.class, () -> uniqueVinylService.findById(null));
    }
}
