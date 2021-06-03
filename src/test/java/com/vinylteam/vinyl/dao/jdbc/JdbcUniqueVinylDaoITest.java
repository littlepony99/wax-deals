package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUniqueVinylDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final JdbcUniqueVinylDao uniqueVinylDao = new JdbcUniqueVinylDao(databasePreparer.getDataSource());
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<Shop> shops = dataGenerator.getShopsList();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
    private final List<Offer> offers = dataGenerator.getOffersList();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateAllVinylTables();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateAllVinylTables();
        databasePreparer.closeDataSource();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertShops(shops);
        databasePreparer.insertUniqueVinyls(uniqueVinyls);
        databasePreparer.insertOffers(offers);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateAllVinylTables();
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls from table that isn't empty")
    void findAllTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        for (UniqueVinyl expectedUniqueVinyl : expectedUniqueVinyls) {
            expectedUniqueVinyl.setHasOffers(false);
        }
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findAll();
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list from empty table")
    void findAllEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findAll();
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns uniqueVinyl by id match from table")
    void findByIdTest() {
        //prepare
        UniqueVinyl expectedUniqueVinyl = dataGenerator.getUniqueVinylsList().get(0);
        expectedUniqueVinyl.setHasOffers(false);
        //when
        UniqueVinyl actualUniqueVinyl = uniqueVinylDao.findById(1);
        //then
        assertEquals(expectedUniqueVinyl, actualUniqueVinyl);
    }

    @Test
    @DisplayName("Throws RuntimeException when id has no matches")
    void findByNoMatchIdTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findById(5));
    }

    @Test
    @DisplayName("Throws RuntimeException when finding by id in empty table")
    void findByIdEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findById(3));
    }

    @Test
    @DisplayName("Returns filled list with exact amount of different unique vinyls selected randomly from table when requested amount is smaller than amount of rows in table")
    void findManyRandomTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(2);
        //then
        assertEquals(2, actualUniqueVinyls.size());
        assertNotEquals(actualUniqueVinyls.get(0), actualUniqueVinyls.get(1));
    }

    @Test
    @DisplayName("Returns filled list with all unique vinyls selected randomly from table when requested amount is equal or bigger than amount of rows in table")
    void findManyRandomAmountBiggerThanTableSizeTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUniqueVinyls.remove(3);
        for (UniqueVinyl expectedUniqueVinyl : expectedUniqueVinyls) {
            expectedUniqueVinyl.setHasOffers(false);
        }
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(4);
        //then
        assertEquals(3, actualUniqueVinyls.size());
        assertTrue(expectedUniqueVinyls.containsAll(actualUniqueVinyls));
        assertNotEquals(actualUniqueVinyls.get(0), actualUniqueVinyls.get(1));
        assertNotEquals(actualUniqueVinyls.get(1), actualUniqueVinyls.get(2));
        assertNotEquals(actualUniqueVinyls.get(2), actualUniqueVinyls.get(0));
    }

    @Test
    @DisplayName("Returns empty list when requested valid amount from empty table")
    void findManyRandomEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(3);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns empty list when requested amount 0 from filled table")
    void findManyRandomAmountZeroTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyRandom(0);
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Throws RuntimeException when amount is less then 0")
    void findManyRandomNegativeAmountTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyRandom(-3));
    }

    @Test
    @DisplayName("Returns filled list by full name substring matcher that has matches with offers in table")
    void findManyFilteredTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUniqueVinyls.subList(1, 4).clear();
        expectedUniqueVinyls.get(0).setHasOffers(false);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by full name substring matcher that has no matches with offers in table")
    void findManyFilteredZeroMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list of all rows with has_offers=true when finding by full name substring matcher that is empty string")
    void findManyFilteredEmptyMatcherTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUniqueVinyls.remove(3);
        for (UniqueVinyl expectedUniqueVinyl : expectedUniqueVinyls) {
            expectedUniqueVinyl.setHasOffers(false);
        }
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Throws RuntimeException when matcher is null")
    void findManyFilteredNullMatcherTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyFiltered(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when matcher is null and table is empty")
    void findManyFilteredNullMatcherEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyFiltered(null));
    }

    @Test
    @DisplayName("Returns empty list when finding by matcher in empty table")
    void findManyFilteredEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyFiltered("1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Returns filled list of unique vinyls that have offers by artist")
    void findManyByArtistTest() {
        //prepare
        List<UniqueVinyl> expectedUniqueVinyls = dataGenerator.getUniqueVinylsList();
        expectedUniqueVinyls.subList(1, 4).clear();
        expectedUniqueVinyls.get(0).setHasOffers(false);
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist1");
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Returns empty list by artist that has no matches with offers in the table")
    void findManyByArtistNoMatchesTest() {
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist4");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Throws RuntimeException when passed artist is null")
    void findManyByNullArtistTest() {
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyByArtist(null));
    }

    @Test
    @DisplayName("Throws RuntimeException when passed artist is null and table is empty")
    void findManyByNullArtistEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        assertThrows(RuntimeException.class, () -> uniqueVinylDao.findManyByArtist(null));
    }

    @Test
    @DisplayName("Returns empty list when finding by artist and table is empty")
    void findManyByArtistEmptyTableTest() throws SQLException {
        //prepare
        databasePreparer.truncateCascadeUniqueVinyls();
        //when
        List<UniqueVinyl> actualUniqueVinyls = uniqueVinylDao.findManyByArtist("artist1");
        //then
        assertTrue(actualUniqueVinyls.isEmpty());
    }

    @Test
    @DisplayName("Checks whether upsertOneUniqueVinyl set hasOffers = false for existing Vinyl")
    void upsertOneUniqueVinylTest() {
        UniqueVinyl vinyl = uniqueVinyls.get(2);
        UniqueVinyl dbVinyl = uniqueVinylDao.findById(vinyl.getId());
        assertTrue(dbVinyl.getHasOffers());
        dbVinyl.setHasOffers(false);
        uniqueVinylDao.updateHasOffersFalse(dbVinyl);
        UniqueVinyl dbVinylAfterUpdate = uniqueVinylDao.findById(vinyl.getId());
        assertFalse(dbVinylAfterUpdate.getHasOffers());
    }

    @Test
    @DisplayName("Checks whether setVinylParameters call PreparedStatement`s methods and does it in the right order")
    void setVinylParametersTest() throws SQLException {
        var uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(34);
        var statement = mock(PreparedStatement.class);
        uniqueVinylDao.setVinylParameters(statement, uniqueVinyl);
        InOrder inOrderStatement = inOrder(statement);
        inOrderStatement.verify(statement).setLong(1, uniqueVinyl.getId());
    }

}