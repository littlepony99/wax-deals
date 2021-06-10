package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DataFinderFromDBForITests;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcRecoveryPasswordDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final RecoveryPasswordDao recoveryPasswordDao = new JdbcRecoveryPasswordDao(databasePreparer.getDataSource());
    private final DataFinderFromDBForITests dataFinder = new DataFinderFromDBForITests(databasePreparer.getDataSource());
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<User> users = dataGenerator.getUsersList();
    private RecoveryToken recoveryToken = new RecoveryToken();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeRecoveryPassword();
        databasePreparer.truncateCascadeUsers();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateCascadeRecoveryPassword();
        databasePreparer.truncateCascadeUsers();
        databasePreparer.closeDataSource();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertRecoveryToken(recoveryToken);
        databasePreparer.insertUsers(users);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateCascadeRecoveryPassword();
        databasePreparer.truncateCascadeUsers();
    }

    @Test
    @DisplayName("Add new recovery token into recovery_password table in db")
    void addRecoveryToken() {
        //prepare
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        //when
        boolean isAdded = recoveryPasswordDao.add(recoveryToken);
        //then
        assertTrue(isAdded);
    }

    @Test
    @DisplayName("Add new recovery token if user doesn't exist into recovery_password table in db")
    void addRecoveryTokenIfUserDoesNotExist() {
        //prepare
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(3L);
        //when
        boolean isAdded = recoveryPasswordDao.add(recoveryToken);
        //then
        assertFalse(isAdded);
    }

    @Test
    @DisplayName("Add new recovery token if token with this user already exist into recovery_password table in db")
    void addRecoveryTokenIfTokenWithThisUserAlreadyExist() {
        //prepare
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryPasswordDao.add(recoveryToken);
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        //when
        boolean isAdded = recoveryPasswordDao.add(recoveryToken);
        //then
        assertTrue(isAdded);
    }

    @Test
    @DisplayName("Add new recovery token if this token already exist into recovery_password table in db")
    void addRecoveryTokenIfTokenAlreadyExist() {
        //prepare
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryPasswordDao.add(recoveryToken);
        recoveryToken.setUserId(2L);
        //when
        boolean isAdded = recoveryPasswordDao.add(recoveryToken);
        //then
        assertFalse(isAdded);
    }

    @Test
    @DisplayName("Get optional recovery token by token frpm Recovery_token in db")
    void getRecoveryTokenByToken() {
        //prepare
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryPasswordDao.add(recoveryToken);
        //when
        Optional<RecoveryToken> optionalRecoveryToken = recoveryPasswordDao.findByToken("some-recovery-token");
        //then
        assertTrue(optionalRecoveryToken.isPresent());
    }

    @Test
    @DisplayName("Try get token if it doesn't exist into recovery_password in db")
    void getRecoveryTokenByTokenIfTokenDoesNotExist() {
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryPasswordDao.add(recoveryToken);
        //when
        Optional<RecoveryToken> optionalRecoveryToken = recoveryPasswordDao.findByToken("non-existed-token");
        //then
        assertFalse(optionalRecoveryToken.isPresent());
    }

    @Test
    @DisplayName("Remove token from recovery_password table in db")
    void removeRecoveryToken() {
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryPasswordDao.add(recoveryToken);
        //when
        boolean isDeleted = recoveryPasswordDao.deleteById(1);
        //then
        assertTrue(isDeleted);
    }

    @Test
    @DisplayName("Remove token if it doesn't exist from recovery_password table in db")
    void removeRecoveryTokenIfTokenDoesNotExist() {
        recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryPasswordDao.add(recoveryToken);
        //when
        boolean isDeleted = recoveryPasswordDao.deleteById(1000);
        //then
        assertFalse(isDeleted);
    }

}