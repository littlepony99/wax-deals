package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultUniqueVinylServiceITest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();

    @Autowired
    private UniqueVinylService uniqueVinylService;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UniqueVinylRepository uniqueVinylRepository;

    @BeforeEach
    void beforeEach() {
        uniqueVinylRepository.deleteAll();
        uniqueVinyls.get(1).setFullName("release2 Access All Worlds - artist2");
        uniqueVinyls.get(2).setFullName("release3 SONGS & Instrumentals - artist3");
        uniqueVinyls.get(3).setFullName("release4 SONGS");
        uniqueVinylRepository.saveAll(uniqueVinyls);
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls from table that isn't empty")
    void findAllTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findAll();
        //then
        assertEquals(uniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list from empty table")
    void findAllEmptyTableTest() {
        //prepare
        uniqueVinylRepository.deleteAll();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findAll();
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("findManyRandom - returns expected data")
    void findManyRandomValidAmountTest() {
        //prepare
        int amount = 2;
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findRandom(amount);

        //then
        assertEquals(2, actualUniqueVinyls.size());

        UniqueVinyl vinylWithHasOffersFalse = uniqueVinyls.get(3);
        assertFalse(actualUniqueVinyls.contains(vinylWithHasOffersFalse));
    }

    @Test
    @DisplayName("findManyRandom produces different results")
    void findManyRandomNotRepeatResultsAmountTest() {
        //prepare
        UniqueVinyl vinylWithHasOffersFalse = uniqueVinyls.get(3);
        int amount = 2;

        //uniqueVinylRepository.deleteAll();
        //uniqueVinylRepository.saveAll(dataGenerator.getUniqueVinylsList(9));

        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findRandom(amount);

        //then
        assertEquals(2, actualUniqueVinyls.size());
        assertFalse(actualUniqueVinyls.contains(vinylWithHasOffersFalse));

        String resultAttempt1 = actualUniqueVinyls.stream()
                .map(UniqueVinyl::getId)
                .reduce("", String::concat);

        List<UniqueVinyl> invocation2UniqueVinyls = uniqueVinylService.findRandom(amount);
        assertEquals(2, invocation2UniqueVinyls.size());
        assertFalse(invocation2UniqueVinyls.contains(vinylWithHasOffersFalse));

        String resultAttempt2 = invocation2UniqueVinyls.stream()
                .map(UniqueVinyl::getId)
                .reduce("", String::concat);

        assertNotEquals(resultAttempt1, resultAttempt2);
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls selected randomly from table when requested amount is equal or bigger than amount of rows in table")
    void findManyRandomAmountBiggerThanTableSizeTest() {
        //prepare
        int amount = 20;
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        expectedUniqueVinyls.remove(3);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findRandom(amount);

        //then
        assertEquals(3, actualUniqueVinyls.size());
        assertTrue(expectedUniqueVinyls.containsAll(actualUniqueVinyls));

        long distinctValues = actualUniqueVinyls.stream()
                .map(UniqueVinyl::getId)
                .distinct()
                .count();

        assertEquals(3, distinctValues);
    }

    @Test
    @DisplayName("Returns empty list when requested valid amount from empty table")
    void findManyRandomEmptyTableTest() {
        //prepare
        uniqueVinylRepository.deleteAll();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findRandom(3);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list by full name substring matcher that has matches with offers in table")
    void findManyFilteredTest() {
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>();
        expectedUniqueVinyls.add(uniqueVinyls.get(0));
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByFilter("1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by full name substring matcher that has no matches with offers in table")
    void findManyFilteredZeroMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByFilter("4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Return result when not whole word is in the matcher")
    void findManyFilteredNotWholeWord() {
        List<UniqueVinyl> vinylList = uniqueVinylService.findByFilter("ongs");
        assertEquals(1, vinylList.size());
        assertEquals(uniqueVinyls.get(2), vinylList.get(0));
    }

    @Test
    @DisplayName("Return result when one letter is different")
    void findManyFilteredOneLetterIsDifferent() {
        List<UniqueVinyl> vinylList = uniqueVinylService.findByFilter("bongs");
        assertEquals(1, vinylList.size());
        assertEquals(uniqueVinyls.get(2), vinylList.get(0));
    }

    @Test
    @DisplayName("Return some results when one letter is different")
    void findManyFilteredOneLetterIsDifferentSomeResults() {
        List<UniqueVinyl> vinylList = uniqueVinylService.findByFilter("gelease");
        assertEquals(3, vinylList.size());
        assertFalse(vinylList.contains(uniqueVinyls.get(3)));
    }

    @Test
    @DisplayName("Return result when matcher is contains some words with mistake")
    void findManyFilteredSomeWordsWithMistakes() {
        List<UniqueVinyl> vinylList = uniqueVinylService.findByFilter("acess word");
        assertEquals(1, vinylList.size());
        assertEquals(uniqueVinyls.get(1), vinylList.get(0));
    }

    @Test
    @DisplayName("Return result when matcher is contains some words with mistake and different order")
    void findManyFilteredSomeWordsWithMistakesDifferentOrder() {
        List<UniqueVinyl> vinylList = uniqueVinylService.findByFilter("word acess");
        assertEquals(1, vinylList.size());
        assertEquals(uniqueVinyls.get(1), vinylList.get(0));
    }

    @Test
    @DisplayName("Returns filled list of unique vinyls that have offers by artist")
    void findManyByArtistTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUniqueVinyls.subList(1, 4).clear();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByArtist("artist1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by artist that has no matches with offers in the table")
    void findManyByArtistNoMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByArtist("artist4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns empty list when finding by artist and table is empty")
    void findManyByArtistEmptyTableTest() {
        //prepare
        uniqueVinylRepository.deleteAll();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylService.findByArtist("artist1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Checks that when id is not null UniqueVinylDao.findById(id) is called, it's result is returned")
    void findByValidIdTest() throws NotFoundException {
        //prepare
        UniqueVinyl expectedUniqueVinyl = uniqueVinyls.get(0);
        //when
        UniqueVinyl actualUniqueVinyl = uniqueVinylService.findById("1");
        //then
        assertEquals(expectedUniqueVinyl, actualUniqueVinyl);
    }

    @Test
    @DisplayName("Throws RuntimeException when id has no matches")
    void findByNoMatchIdTest() {
        //when
        assertThrows(NotFoundException.class, () -> uniqueVinylService.findById("100"));
    }

}