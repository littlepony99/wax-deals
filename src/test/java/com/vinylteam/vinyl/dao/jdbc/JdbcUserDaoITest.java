
package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.data.TestData;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {
/*
    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();*/
    @Autowired
    private UserDao userDao;
    @Autowired
    private Flyway flyway;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @AfterAll
    void afterAll() {
        //FIXME: Leaves empty flyway_migration_history withing test class running.
       flyway.clean();
    }

   /* @Test
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

*/
    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @DisplayName("Finds user from db by non existing id")
    void getByNotExistingIdTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentId = userDao.findById(3);
        //then
        assertFalse(optionalUserGottenByNonexistentId.isPresent());
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.AddedUserResultProvider.class)
    @DisplayName("Adds user to db")
    void addNewTest() {
        //prepare
        User expectedUser = dataGenerator.getUserWithNumber(2);
        //when
        assertTrue(userDao.add(expectedUser) > 0);
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.UsersProvider.class)
    @DisplayName("Adds existing user with the same email")
    void addExistingWithSameEmailTest() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(2);
        userExistingEmail.setEmail(dataGenerator.getUserWithNumber(1).getEmail());
        //when
        assertThrows(DataAccessException.class, () -> userDao.add(userExistingEmail));
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.UsersProvider.class)
    @DisplayName("Adds existing user with the same password")
    void addExistingWithSamePasswordTest() {
        //prepare
        User userExistingPassword = dataGenerator.getUserWithNumber(2);
        userExistingPassword.setPassword(dataGenerator.getUserWithNumber(1).getPassword());
        //when
        assertThrows(DataAccessException.class, () -> userDao.add(userExistingPassword));
    }
//TODO: add unique constraint on salt in migrations.

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.UsersProvider.class)
    @DisplayName("Update non-existent user in db")
    void updateNonExistentUserInDbTest() {
        //prepare
        User changedUser = dataGenerator.getUserWithNumber(3);
        changedUser.setDiscogsUserName("newDiscogsUserName");
        String oldNonExistentEmail = changedUser.getEmail();
        //when
        assertFalse(userDao.update(oldNonExistentEmail, changedUser));
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.UpdatedUserResultProvider.class)
    @DisplayName("Edit existing user in db with valid new values")
    void editWithAnExistingUserInDbTest() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(1).getEmail();
        User changedUser = dataGenerator.getUserWithNumber(3);
        //when
        assertTrue(userDao.update(oldExistingEmail, changedUser));
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.DeletedUserResultProvider.class)
    @DisplayName("Delete by user with non-existent email")
    void deleteExistingUserTest() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(1);
        //when
        assertTrue(userDao.delete(userExistingEmail));
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true)
    @ExpectedDataSet(provider = TestData.UsersProvider.class)
    @DisplayName("Delete by user with non-existent email")
    void deleteNonExistentUserTest() {
        //prepare
        User userNonExistentEmail = dataGenerator.getUserWithNumber(2);
        //when
        assertFalse(userDao.delete(userNonExistentEmail));
    }

}
