
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
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    @Autowired
    private UserDao userDao;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @AfterAll
    void afterAll() {
        //FIXME: Leaves empty flyway_migration_history withing test class running.
       //flyway.clean();
    }

   @Test
   @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
   @DisplayName("Finds user from db by existing email")
   void getByExistingEmailTest() {
       //when
       Optional<User> optionalUserGottenByExistingEmail = userDao.findByEmail(dataGenerator.getUserWithNumber(1).getEmail());
       //then
       assertTrue(optionalUserGottenByExistingEmail.isPresent());
   }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by non existing email")
    void getByNotExistingEmailTest() {
        //when
//        Optional<User> optionalUserGottenByNonexistentEmail = userDao.findByEmail(dataGenerator.getUserWithNumber(3).getEmail());
        //then
        assertThrows(EmptyResultDataAccessException.class, () -> {userDao.findByEmail(dataGenerator.getUserWithNumber(3).getEmail());});
//        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true,  executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by existing id")
    void getByExistingIdTest() {
        //when
        Optional<User> optionalUserGottenByExistingId = userDao.findById(1);
        //then
        assertTrue(optionalUserGottenByExistingId.isPresent());
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by non existing id")
    void getByNotExistingIdTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentId = userDao.findById(0);
        //then
        assertFalse(optionalUserGottenByNonexistentId.isPresent());
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestData.AddedUserResultProvider.class)
    @DisplayName("Adds user to db")
    void addNewTest() {
        //prepare
        User expectedUser = dataGenerator.getUserWithNumber(2);
        //when
        userDao.add(expectedUser);
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
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
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
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
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestData.UsersProvider.class)
    @DisplayName("Update non-existent user in db")
    void updateNonExistentUserInDbTest() {
        //prepare
        User changedUser = dataGenerator.getUserWithNumber(3);
        changedUser.setDiscogsUserName("newDiscogsUserName");
        String oldNonExistentEmail = changedUser.getEmail();
        //when
        userDao.update(oldNonExistentEmail, changedUser);
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestData.UpdatedUserResultProvider.class)
    @DisplayName("Edit existing user in db with valid new values")
    void editWithAnExistingUserInDbTest() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(1).getEmail();
        User changedUser = dataGenerator.getUserWithNumber(3);
        //when
        userDao.update(oldExistingEmail, changedUser);
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestData.DeletedUserResultProvider.class)
    @DisplayName("Delete by user with non-existent email")
    void deleteExistingUserTest() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(1);
        //when
        userDao.delete(userExistingEmail);
    }

    @Test
    @DataSet(provider = TestData.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestData.UsersProvider.class)
    @DisplayName("Delete by user with non-existent email")
    void deleteNonExistentUserTest() {
        //prepare
        User userNonExistentEmail = dataGenerator.getUserWithNumber(2);
        //when
        userDao.delete(userNonExistentEmail);
    }

}
