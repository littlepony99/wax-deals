
package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.WaxDealsPostgresqlContainer;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.data.TestUserProvider;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@Slf4j
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class JdbcUserDaoITest {

    @Autowired
    private UserDao userDao;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Container
    public static PostgreSQLContainer container = WaxDealsPostgresqlContainer.getInstance();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        container.start();
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by existing email")
    void findByEmail() {
        //when
        Optional<User> optionalUserGottenByExistingEmail = userDao.findByEmail(dataGenerator.getUserWithNumber(1).getEmail());
        //then
        assertTrue(optionalUserGottenByExistingEmail.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by non-existent email")
    void findByEmailNonExistentEmail() {
        //when
        Optional<User> optionalUserGottenByNonexistentEmail = userDao.findByEmail(dataGenerator.getUserWithNumber(3).getEmail());
        //then
        assertFalse(optionalUserGottenByNonexistentEmail.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, executeStatementsBefore = "SELECT setval('users_id_seq', 3, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by existing id")
    void findById() {
        //when
        Optional<User> optionalUserGottenByExistingId = userDao.findById(3);
        //then
        assertTrue(optionalUserGottenByExistingId.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Finds user from db by non existing id")
    void FindByIdNonExistentId() {
        //when
        Optional<User> optionalUserGottenByNonexistentId = userDao.findById(0);
        //then
        assertFalse(optionalUserGottenByNonexistentId.isPresent());
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.AddedUserResultProvider.class)
    @DisplayName("Adds user to db")
    void add() {
        //prepare
        User expectedUser = dataGenerator.getUserWithNumber(2);
        //when
        assertEquals(1, userDao.add(expectedUser));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds user with the existing email")
    void addExistingEmail() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(2);
        userExistingEmail.setEmail(dataGenerator.getUserWithNumber(1).getEmail());
        //when
        assertThrows(DuplicateKeyException.class, () -> userDao.add(userExistingEmail));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds user with the existing email in upper case")
    void addExistingEmailToUpperCase() {
        //prepare
        User userExistingEmailInUppercase = dataGenerator.getUserWithNumber(2);
        userExistingEmailInUppercase.setEmail(dataGenerator.getUserWithNumber(1).getEmail().toUpperCase());
        //when
        assertThrows(DuplicateKeyException.class, () -> userDao.add(userExistingEmailInUppercase));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds user with null email")
    void addNullEmail() {
        //prepare
        User userNullEmail = dataGenerator.getUserWithNumber(2);
        userNullEmail.setEmail(null);
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.add(userNullEmail));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds user with null password")
    void addNullPassword() {
        //prepare
        User userNullPassword = dataGenerator.getUserWithNumber(2);
        userNullPassword.setPassword(null);
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.add(userNullPassword));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Adds user with null salt")
    void addNullSalt() {
        //prepare
        User userNullSalt = dataGenerator.getUserWithNumber(2);
        userNullSalt.setSalt(null);
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.add(userNullSalt));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class)
    @DisplayName("Update user in db by non-existent email")
    void updateNonExistentOldEmail() {
        //prepare
        User changedUser = dataGenerator.getUserWithNumber(3);
        String oldNonExistentEmail = changedUser.getEmail();
        //when
        userDao.update(oldNonExistentEmail, changedUser);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UpdatedUserResultProvider.class)
    @DisplayName("Update existing user in db with valid new values")
    void update() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(2).getEmail();
        User changedUser = dataGenerator.getUserWithNumber(3);
        //when
        userDao.update(oldExistingEmail, changedUser);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class)
    @DisplayName("Updates user with the existing email")
    void updateExistingEmail() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(2).getEmail();
        User userExistingEmail = dataGenerator.getUserWithNumber(3);
        userExistingEmail.setEmail(dataGenerator.getUserWithNumber(1).getEmail());
        //when
        assertThrows(DuplicateKeyException.class, () -> userDao.update(oldExistingEmail, userExistingEmail));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class)
    @DisplayName("Updates user with the existing email in upper case")
    void updateExistingEmailToUpperCase() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(2).getEmail();
        User userExistingEmailInUppercase = dataGenerator.getUserWithNumber(3);
        userExistingEmailInUppercase.setEmail(dataGenerator.getUserWithNumber(1).getEmail().toUpperCase());
        //when
        assertThrows(DuplicateKeyException.class, () -> userDao.update(oldExistingEmail, userExistingEmailInUppercase));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class)
    @DisplayName("Updates user with null email")
    void updateNullEmail() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(2).getEmail();
        User userNullEmail = dataGenerator.getUserWithNumber(3);
        userNullEmail.setEmail(null);
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.update(oldExistingEmail, userNullEmail));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class)
    @DisplayName("Updates user with null password")
    void updateNullPassword() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(2).getEmail();
        User userNullPassword = dataGenerator.getUserWithNumber(3);
        userNullPassword.setPassword(null);
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.update(oldExistingEmail, userNullPassword));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersBeforeUpdateResultProvider.class)
    @DisplayName("Updates user with null salt")
    void updateNullSalt() {
        //prepare
        String oldExistingEmail = dataGenerator.getUserWithNumber(2).getEmail();
        User userNullSalt = dataGenerator.getUserWithNumber(3);
        userNullSalt.setSalt(null);
        //when
        assertThrows(DataIntegrityViolationException.class, () -> userDao.update(oldExistingEmail, userNullSalt));
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.DeletedUserResultProvider.class)
    @DisplayName("Delete user by non-existent email")
    void delete() {
        //prepare
        User userExistingEmail = dataGenerator.getUserWithNumber(1);
        //when
        userDao.delete(userExistingEmail);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Delete by non-existent email")
    void deleteByNonExistentEmail() {
        //prepare
        User userNonExistentEmail = dataGenerator.getUserWithNumber(2);
        //when
        userDao.delete(userNonExistentEmail);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.SetUserStatusProvider.class)
    @DisplayName("Update user status")
    void setUserStatus() {
        //when
        userDao.setUserStatusTrue(1);
    }

    @Test
    @DataSet(provider = TestUserProvider.UsersProvider.class, cleanAfter = true, executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserProvider.UsersProvider.class)
    @DisplayName("Update user status by non existent id")
    void setUserStatusNonExistentId() {
        //when
        userDao.setUserStatusTrue(1000);
    }

}
