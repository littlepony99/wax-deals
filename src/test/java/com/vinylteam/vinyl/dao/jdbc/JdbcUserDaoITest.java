
/*
package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DataFinderFromDBForITests;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final UserDao userDao = new JdbcUserDao(databasePreparer.getDataSource());
    private final DataFinderFromDBForITests dataFinder = new DataFinderFromDBForITests(databasePreparer.getDataSource());
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<User> users = dataGenerator.getUsersList();

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
        databasePreparer.closeDataSource();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        databasePreparer.insertUsers(users);
    }

    @AfterEach
    void afterEach() throws SQLException {
        databasePreparer.truncateCascadeUsers();
    }

    @Test
    @DisplayName("Gets an existing user from db by email")
    void getByExistingEmailTest() {
        //prepare
        Optional<User> optionalUserGottenByExistingEmail;
        User expectedUser = dataGenerator.getUserWithNumber(1);
        //when
        optionalUserGottenByExistingEmail = userDao.findByEmail("user1@wax-deals.com");
        //then
        assertFalse(optionalUserGottenByExistingEmail.isEmpty());
        assertEquals(expectedUser, optionalUserGottenByExistingEmail.get());
    }

    @Test
    @DisplayName("Gets not existing user from db by email")
    void getByNotExistingEmailTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentEmail = userDao.findByEmail("user3@wax-deals.com");
        //then
        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
        assertEquals(2, dataFinder.findAllUsers().size());
    }

    @Test
    @DisplayName("Gets an existing user from db by id")
    void getByExistingIdTest() {
        //prepare
        Optional<User> optionalUserGottenByExistingId;
        User expectedUser = dataGenerator.getUserWithNumber(1);
        //when
        optionalUserGottenByExistingId = userDao.findById(1);
        //then
        assertFalse(optionalUserGottenByExistingId.isEmpty());
        assertEquals(expectedUser, optionalUserGottenByExistingId.get());
    }

    @Test
    @DisplayName("Gets not existing user from db by id")
    void getByNotExistingIdTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentId = userDao.findById(3);
        //then
        assertFalse(optionalUserGottenByNonexistentId.isPresent());
        assertEquals(2, dataFinder.findAllUsers().size());
    }

    @Test
    @DisplayName("Adds user to db")
    void addNewTest() {
        //prepare
        User expectedUser = dataGenerator.getUserWithNumber(3);
        //when
        assertTrue(userDao.add(expectedUser) > 0);
        //then
        assertEquals(3, dataFinder.findAllUsers().size());
        Optional<User> optionalAddedUser = userDao.findByEmail(expectedUser.getEmail());
        assertEquals(expectedUser, optionalAddedUser.get());
    }

    @Test
    @DisplayName("Adds existing user with the same password")
    void addExistingWithSamePasswordTest() {
        //when
        assertTrue(userDao.add(users.get(0)) == -1);
        //then
        assertEquals(2, dataFinder.findAllUsers().size());
    }

    @Test
    @DisplayName("Adds new user with existing discogsUserName")
    void addNewWithExistingDiscogsUserNameTest() {
        //prepare
        User existentDiscogsUserNameUser = dataGenerator.getUserWithNumber(3);
        existentDiscogsUserNameUser.setDiscogsUserName("discogsUserName1");
        //when
        assertTrue(userDao.add(existentDiscogsUserNameUser) == -1);
        //then
        assertEquals(2, dataFinder.findAllUsers().size());
    }

    @Test
    @DisplayName("Adds existing user with new password and salt")
    void addExistingWithNewPasswordTest() {
        //prepare
        User existingUserNewPassword = dataGenerator.getUserWithNumber(1);
        existingUserNewPassword.setPassword("hash3");
        existingUserNewPassword.setSalt("salt3");
        //when
        assertTrue(userDao.add(existingUserNewPassword) == -1);
        //then
        assertEquals(2, dataFinder.findAllUsers().size());
    }

    @Test
    @DisplayName("Edit non-existent user in db")
    void editNonExistentUserInDbTest() {
        //prepare
        User changedUser = dataGenerator.getUserWithNumber(3);
        changedUser.setDiscogsUserName("newDiscogsUserName");
        String oldNonExistentEmail = changedUser.getEmail();
        //then
        assertFalse(userDao.update(oldNonExistentEmail, changedUser));
        assertEquals(2, dataFinder.findAllUsers().size());
    }

    @Test
    @DisplayName("Edit existing user in db with valid new values")
    void editWithAnExistingUserInDbTest() {
        //prepare
        String oldExistingEmail = users.get(1).getEmail();
        User changedUser = dataGenerator.getUserWithNumber(3);
        //when
        assertTrue(userDao.update(oldExistingEmail, changedUser));
        //then
        assertEquals(2, dataFinder.findAllUsers().size());
        assertEquals(changedUser, userDao.findByEmail(changedUser.getEmail()).get());
    }

    @Test
    @DisplayName("Edit user and try to change discogsUserName that already exist in db")
    void editWithAnExistingUserInDbAndTryToChangeDiscogsUserNameThatAlreadyExistTest() {
        //prepare
        User changedUser = dataGenerator.getUserWithNumber(2);
        changedUser.setDiscogsUserName("discogsUserName1");
        String oldExistingEmail = changedUser.getEmail();
        //when
        assertFalse(userDao.update(oldExistingEmail, changedUser));
        //then
        assertEquals(2, dataFinder.findAllUsers().size());
        User actualUser = userDao.findByEmail(changedUser.getEmail()).get();
        assertNotEquals(changedUser, actualUser);
        assertNotEquals(changedUser.getDiscogsUserName(), actualUser.getDiscogsUserName());
    }

}*/
