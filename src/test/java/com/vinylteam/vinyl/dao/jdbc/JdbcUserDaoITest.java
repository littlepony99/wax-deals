
package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.data.TestUserProvider;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

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

//    @Container
//    public static PostgreSQLContainer container = new PostgreSQLContainer(PostgreSQLContainer.IMAGE)
//            .withDatabaseName("testDB")
//            .withUsername("user")
//            .withPassword("password");
//
//    @DynamicPropertySource
//    static void properties(DynamicPropertyRegistry registry) {
//        container.start();
//        registry.add("spring.datasource.url", container::getJdbcUrl);
//        registry.add("spring.datasource.username", container::getUsername);
//        registry.add("spring.datasource.password", container::getPassword);
//    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by existing email")
    void getByExistingEmailTest() {
        //when
        Optional<User> optionalUserGottenByExistingEmail = userDao.findByEmail(dataGenerator.getUserWithNumber(1).getEmail());
        //then
        assertTrue(optionalUserGottenByExistingEmail.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by non existing email")
    void getByNotExistingEmailTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentEmail = userDao.findByEmail(dataGenerator.getUserWithNumber(3).getEmail());
        //then
        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by existing id")
    void getByExistingIdTest() {
        //when
        Optional<User> optionalUserGottenByExistingId = userDao.findById(1);
        //then
        assertTrue(optionalUserGottenByExistingId.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by non existing id")
    void getByNotExistingIdTest() {
        //when
        Optional<User> optionalUserGottenByNonexistentId = userDao.findById(0);
        //then
        assertFalse(optionalUserGottenByNonexistentId.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.AddedUserResultProvider.class)
    @DisplayName("Adds user to db")
    void addNewTest() {
        //prepare
        User expectedUser = dataGenerator.getUserWithNumber(2);
        //when
        assertEquals(1, userDao.add(expectedUser));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Adds empty user to db")
    void addNewEmptyUser() {
        //prepare
        User expectedUser = User.builder().email("uniq").role(Role.USER).build();
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.add(expectedUser));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds existing user with the same email")
    void addExistingWithSameEmailTest() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(2);
        userExistingEmail.setEmail(dataGenerator.getUserWithNumber(1).getEmail());
        //when
        assertThrows(DuplicateKeyException.class, () -> userDao.add(userExistingEmail));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds existing user with the same email")
    void addExistingWithSameEmailToUpperCaseTest() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(2);
        userExistingEmail.setEmail(dataGenerator.getUserWithNumber(1).getEmail().toUpperCase());
        //when
        assertThrows(DuplicateKeyException.class, () -> userDao.add(userExistingEmail));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
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
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UpdatedUserResultProvider.class)
    @DisplayName("Edit existing user in db with valid new values")
    void editWithAnExistingUserInDbTest() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(1).getEmail();
        User changedUser = dataGenerator.getUserWithNumber(3);
        //when
        userDao.update(oldExistingEmail, changedUser);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.DeletedUserResultProvider.class)
    @DisplayName("Delete by user with non-existent email")
    void deleteExistingUserTest() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(1);
        //when
        userDao.delete(userExistingEmail);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Delete by user with non-existent email")
    void deleteNonExistentUserTest() {
        //prepare
        User userNonExistentEmail = dataGenerator.getUserWithNumber(2);
        //when
        userDao.delete(userNonExistentEmail);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.SetUserStatusProvider.class)
    @DisplayName("Update user status")
    void setUserStatusTrue(){
        //when
        userDao.setUserStatusTrue(1);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Update user status")
    void setUserStatusTrueIfInvalidUserId(){
        //when
        userDao.setUserStatusTrue(1000);
    }

}
