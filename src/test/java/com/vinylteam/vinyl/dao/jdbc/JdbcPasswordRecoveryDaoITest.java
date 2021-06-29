package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.WaxDealsPostgresqlContainer;
import com.vinylteam.vinyl.dao.PasswordRecoveryDao;
import com.vinylteam.vinyl.data.TestRecoveryTokenProvider;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class JdbcPasswordRecoveryDaoITest {

    @Autowired
    private PasswordRecoveryDao passwordRecoveryDao;

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
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestRecoveryTokenProvider.AddRecoveryPasswordTokenProvider.class)
    @DisplayName("Add new recovery token into recovery_password table in db")
    void addRecoveryToken() {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(2L);
        recoveryToken.setToken(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        //when
        passwordRecoveryDao.add(recoveryToken);
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class)
    @DisplayName("Add new recovery token if user doesn't exist into recovery_password table in db")
    void addRecoveryTokenIfUserDoesNotExist() {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(3L);
        //when
        assertThrows(DataAccessException.class, () -> passwordRecoveryDao.add(recoveryToken));
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestRecoveryTokenProvider.UpdateRecoveryPasswordTokenProvider.class)
    @DisplayName("Add new recovery token if token with this user already exist into recovery_password table in db")
    void addRecoveryTokenIfTokenWithThisUserAlreadyExist() {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1L);
        recoveryToken.setToken(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        //when
        passwordRecoveryDao.add(recoveryToken);
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class)
    @DisplayName("Add new recovery token if this token already exist into recovery_password table in db")
    void addRecoveryTokenIfTokenAlreadyExist() {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(2);
        recoveryToken.setToken(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        //when
        assertThrows(DataAccessException.class, () -> passwordRecoveryDao.add(recoveryToken));
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Get optional recovery token by token from Recovery_token in db")
    void getRecoveryTokenByToken() {
        //when
        Optional<RecoveryToken> optionalRecoveryToken = passwordRecoveryDao.findByToken(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        //then
        assertTrue(optionalRecoveryToken.isPresent());
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Try get token if it doesn't exist into recovery_password in db")
    void getRecoveryTokenByTokenIfTokenDoesNotExist() {
        //when
        Optional<RecoveryToken> optionalRecoveryToken = passwordRecoveryDao.findByToken(UUID.randomUUID());
        //then
        assertFalse(optionalRecoveryToken.isPresent());
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, executeStatementsBefore = "SELECT setval('users_id_seq', 1, false);", cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestRecoveryTokenProvider.DeleteRecoveryPasswordTokenProvider.class)
    @DisplayName("Remove token from recovery_password table in db")
    void deleteByExistingId() {
        //when
        passwordRecoveryDao.deleteById(1);
    }

    @Test
    @DataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestRecoveryTokenProvider.RecoveryPasswordTokenProvider.class)
    @DisplayName("Remove token if it doesn't exist from recovery_password table in db")
    void DeleteByNonExistentId() {
        //when
        passwordRecoveryDao.deleteById(0);
    }

}
