package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.data.TestConfirmationTokenProvider;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@RequiredArgsConstructor
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class JdbcConfirmationTokenDaoITest {

    private final JdbcConfirmationTokenDao jdbcConfirmationTokenDao;
    private final DataGeneratorForTests dataGenerator;

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Returns optional with confirmation token when there is token with that user_id")
    void findByUserIdTest() {
        //prepare

        //when
        Optional<ConfirmationToken> optionalConfirmationToken = jdbcConfirmationTokenDao
                .findByUserId(dataGenerator.getConfirmationTokenWithUserId(1).getUserId());
        //then

    }
}

//import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
//import com.vinylteam.vinyl.entity.ConfirmationToken;
//import com.vinylteam.vinyl.entity.User;
//import com.vinylteam.vinyl.util.DataFinderFromDBForITests;
//import com.vinylteam.vinyl.util.DataGeneratorForTests;
//import com.vinylteam.vinyl.util.DatabasePreparerForITests;
//import org.junit.jupiter.api.*;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class JdbcConfirmationTokenDaoITest {
//
//    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
//    private final DataFinderFromDBForITests dataFinder = new DataFinderFromDBForITests(databasePreparer.getDataSource());
//    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
//    private final ConfirmationTokenDao confirmationTokenDao = new JdbcConfirmationTokenDao(databasePreparer.getDataSource());
//    private final List<User> users = dataGenerator.getUsersList();
//    private final List<ConfirmationToken> confirmationTokens = dataGenerator.getConfirmationTokensList();
//
//    @BeforeAll
//    void beforeAll() throws SQLException {
//        databasePreparer.truncateCascadeUsers();
//        databasePreparer.truncateConfirmationTokens();
//    }
//
//    @AfterAll
//    void afterAll() throws SQLException {
//        databasePreparer.truncateCascadeUsers();
//        databasePreparer.truncateConfirmationTokens();
//        databasePreparer.closeDataSource();
//    }
//
//    @BeforeEach
//    void beforeEach() throws SQLException {
//        databasePreparer.insertUsers(users);
//        databasePreparer.insertConfirmationTokens(confirmationTokens);
//    }
//
//    @AfterEach
//    void afterEach() throws SQLException {
//        databasePreparer.truncateCascadeUsers();
//        databasePreparer.truncateConfirmationTokens();
//    }
//
//    @Test
//    @DisplayName("Returns optional with confirmation token when there is token with that user_id")
//    void findByUserIdTest() {
//        //prepare
//        ConfirmationToken expectedConfirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
//        expectedConfirmationToken.setToken(confirmationTokens.get(0).getToken());
//        //when
//        Optional<ConfirmationToken> actualConfirmationTokenOptional = confirmationTokenDao.findByUserId(1);
//        //then
//        assertTrue(actualConfirmationTokenOptional.isPresent());
//        assertEquals(expectedConfirmationToken, actualConfirmationTokenOptional.get());
//    }
//
//    @Test
//    @DisplayName("Returns empty optional when there is no token with that user_id")
//    void findByNotExistingUserIdTest() {
//        //when
//        Optional<ConfirmationToken> actualConfirmationTokenOptional = confirmationTokenDao.findByUserId(3);
//        //then
//        assertTrue(actualConfirmationTokenOptional.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Returns optional with confirmation token when there is one with that token")
//    void findByTokenTest() {
//        //prepare
//        ConfirmationToken expectedConfirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
//        expectedConfirmationToken.setToken(confirmationTokens.get(0).getToken());
//        UUID token = expectedConfirmationToken.getToken();
//        //when
//        Optional<ConfirmationToken> actualConfirmationTokenOptional = confirmationTokenDao.findByToken(token);
//        //then
//        assertTrue(actualConfirmationTokenOptional.isPresent());
//        assertEquals(expectedConfirmationToken, actualConfirmationTokenOptional.get());
//    }
//
//    @Test
//    @DisplayName("Returns empty optional when there is no row with that token")
//    void findByNotExistingTokenTest() {
//        //when
//        Optional<ConfirmationToken> actualConfirmationTokenOptional = confirmationTokenDao.findByToken(UUID.randomUUID());
//        //then
//        assertTrue(actualConfirmationTokenOptional.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Returns true when there is user with status false with this user_id and all fields have valid values")
//    void add() throws SQLException {
//        //prepare
//        long userId = 3;
//        databasePreparer.truncateCascadeUsers();
//        databasePreparer.truncateConfirmationTokens();
//        List<User> listWithUserWithoutConfirmationToken = dataGenerator.getUsersList();
//        listWithUserWithoutConfirmationToken.add(dataGenerator.getUserWithNumber((int) userId));
//        databasePreparer.insertUsers(listWithUserWithoutConfirmationToken);
//        databasePreparer.insertConfirmationTokens(confirmationTokens);
//        ConfirmationToken expectedConfirmationToken = dataGenerator.getConfirmationTokenWithUserId(userId);
//        //when
//        assertDoesNotThrow(() -> confirmationTokenDao.add(expectedConfirmationToken));
//        //then
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(3, actualConfirmationTokens.size());
//        assertEquals(expectedConfirmationToken.getUserId(), actualConfirmationTokens.get(2).getUserId());
//        assertEquals(expectedConfirmationToken.getToken(), actualConfirmationTokens.get(2).getToken());
//        assertNotEquals(expectedConfirmationToken.getTimestamp(), actualConfirmationTokens.get(2).getTimestamp());
//    }
//
//    @Test
//    @DisplayName("Throws Runtime exception when there is user with status false with this user_id and token with same userId exists")
//    void addDuplicateUserId() throws SQLException {
//        //prepare
//        ConfirmationToken confirmationTokenDuplicateUserId = dataGenerator.getConfirmationTokenWithUserId(2);
//        confirmationTokenDuplicateUserId.setId(3);
//        //when
//        assertThrows(RuntimeException.class, () -> confirmationTokenDao.add(confirmationTokenDuplicateUserId));
//        //then
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(2, actualConfirmationTokens.size());
//        assertFalse(actualConfirmationTokens.contains(confirmationTokenDuplicateUserId));
//    }
//
//    @Test
//    @DisplayName("Throws Runtime exception when there is no user with given userId")
//    void addNotExistingUserId() throws SQLException {
//        //prepare
//        ConfirmationToken confirmationTokenDuplicateUserId = dataGenerator.getConfirmationTokenWithUserId(3);
//        //when
//        assertThrows(RuntimeException.class, () -> confirmationTokenDao.add(confirmationTokenDuplicateUserId));
//        //then
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(2, actualConfirmationTokens.size());
//        assertFalse(actualConfirmationTokens.contains(confirmationTokenDuplicateUserId));
//    }
//
//    @Test
//    @DisplayName("Throws runtime exception when confirmation token with same token exists")
//    void addExistingTokenValidUserId() throws SQLException {
//        //prepare
//        long userId = 3;
//        databasePreparer.truncateCascadeUsers();
//        databasePreparer.truncateConfirmationTokens();
//        List<User> listWithNewUser = dataGenerator.getUsersList();
//        listWithNewUser.add(dataGenerator.getUserWithNumber((int) userId));
//        databasePreparer.insertUsers(listWithNewUser);
//        databasePreparer.insertConfirmationTokens(confirmationTokens);
//        ConfirmationToken confirmationTokenExistingToken = dataGenerator.getConfirmationTokenWithUserId(userId);
//        confirmationTokenExistingToken.setToken(confirmationTokens.get(1).getToken());
//        //when
//        assertThrows(RuntimeException.class, () -> confirmationTokenDao.add(confirmationTokenExistingToken));
//        //then
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(2, actualConfirmationTokens.size());
//        assertFalse(actualConfirmationTokens.contains(confirmationTokenExistingToken));
//    }
//
//    @Test
//    @DisplayName("Returns true when there exists confirmation token with same id and it gets updated with valid unique uuid token and timestamp")
//    void update() throws SQLException {
//        //prepare
//        ConfirmationToken expectedConfirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
//        expectedConfirmationToken.setToken(UUID.randomUUID());
//        //when
//        boolean actualIsUpdated = confirmationTokenDao.update(expectedConfirmationToken);
//        //then
//        assertTrue(actualIsUpdated);
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(2, actualConfirmationTokens.size());
//        ConfirmationToken actualConfirmationToken = actualConfirmationTokens.get(0);
//        assertEquals(expectedConfirmationToken.getId(), actualConfirmationToken.getId());
//        assertEquals(expectedConfirmationToken.getUserId(), actualConfirmationToken.getUserId());
//        assertEquals(expectedConfirmationToken.getToken(), actualConfirmationToken.getToken());
//        assertTrue(actualConfirmationToken.getTimestamp().compareTo(confirmationTokens.get(0).getTimestamp()) > 0);
//    }
//
//    @Test
//    @DisplayName("Throws Runtime Exception when updating confirmation token with uuid token that already exists in the table in another confirmation token")
//    void updateWithTokenWithDuplicateUUIDToken() throws SQLException {
//        //prepare
//        ConfirmationToken confirmationTokenDuplicateToken = dataGenerator.getConfirmationTokenWithUserId(1);
//        confirmationTokenDuplicateToken.setToken(confirmationTokens.get(1).getToken());
//        //when
//        assertThrows(RuntimeException.class, () -> confirmationTokenDao.update(confirmationTokenDuplicateToken));
//        //then
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(confirmationTokens, actualConfirmationTokens);
//    }
//
//    @Test
//    @DisplayName("Throws Runtime Exception when updating confirmation token with null token")
//    void updateWithTokenWithNullUUIDToken() throws SQLException {
//        //prepare
//        ConfirmationToken confirmationTokenDuplicateToken = dataGenerator.getConfirmationTokenWithUserId(1);
//        confirmationTokenDuplicateToken.setToken(null);
//        //when
//        assertThrows(RuntimeException.class, () -> confirmationTokenDao.update(confirmationTokenDuplicateToken));
//        //then
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(confirmationTokens, actualConfirmationTokens);
//    }
//
//    @Test
//    @DisplayName("Returns true and change status when there is user with given user_id and status false and a token for it's user_id that gets deleted")
//    void deleteTokenByUserId() throws SQLException {
//        //when
//        boolean actualIsDeleted = confirmationTokenDao.deleteByUserId(2);
//        //then
//        assertTrue(actualIsDeleted);
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(1, actualConfirmationTokens.size());
//        assertFalse(actualConfirmationTokens.contains(confirmationTokens.get(1)));
//        List<User> allUsers = dataFinder.findAllUsers();
//        for (User user : allUsers) {
//            if (user.getId() == 2L) {
//                assertTrue(user.getStatus());
//            }
//        }
//    }
//
//    @Test
//    @DisplayName("Returns false when there is no confirmation token with that user_id")
//    void deleteNotExistingTokenByUserId() throws SQLException {
//        //when
//        boolean actualIsDeleted = confirmationTokenDao.deleteByUserId(3);
//        //then
//        assertFalse(actualIsDeleted);
//        List<ConfirmationToken> actualConfirmationTokens = dataFinder.findAllConfirmationTokens();
//        assertEquals(2, actualConfirmationTokens.size());
//    }
//
//}
