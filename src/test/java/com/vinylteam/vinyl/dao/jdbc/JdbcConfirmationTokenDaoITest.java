package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.WaxDealsPostgresqlContainer;
import com.vinylteam.vinyl.data.TestConfirmationTokenProvider;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.exception.entity.EmailConfirmationError;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class JdbcConfirmationTokenDaoITest {

    @Autowired
    private JdbcConfirmationTokenDao jdbcConfirmationTokenDao;

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
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Returns optional with confirmation token when there is token with that user_id")
    void findByUserIdTest() {
        //prepare
        ConfirmationToken expectedConfirmationToken = ConfirmationToken.builder()
                .id(1)
                .userId(1)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"))
                .timestamp(Timestamp.valueOf("2021-06-23 10:10:10"))
                .build();
        //when
        Optional<ConfirmationToken> optionalConfirmationToken = jdbcConfirmationTokenDao.findByUserId(1);
        //then
        assertTrue(optionalConfirmationToken.isPresent());
        assertEquals(expectedConfirmationToken, optionalConfirmationToken.get());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Returns empty optional when there is no token with that user_id")
    void findByNotExistingUserIdTest() {
        //when
        Optional<ConfirmationToken> optionalConfirmationToken = jdbcConfirmationTokenDao.findByUserId(3);
        //then
        assertTrue(optionalConfirmationToken.isEmpty());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Returns optional with confirmation token when there is one with that token")
    void findByTokenTest() {
        //prepare
        ConfirmationToken expectedConfirmationToken = ConfirmationToken.builder()
                .id(1)
                .userId(1)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"))
                .timestamp(Timestamp.valueOf("2021-06-23 10:10:10"))
                .build();
        UUID token = expectedConfirmationToken.getToken();
        //when
        Optional<ConfirmationToken> optionalConfirmationToken = jdbcConfirmationTokenDao.findByToken(token);
        //then
        assertTrue(optionalConfirmationToken.isPresent());
        assertEquals(expectedConfirmationToken, optionalConfirmationToken.get());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Returns empty optional when there is no row with that token")
    void findByNotExistingTokenTest() {
        //when
        Optional<ConfirmationToken> optionalConfirmationToken = jdbcConfirmationTokenDao.findByToken(UUID.randomUUID());
        //then
        assertTrue(optionalConfirmationToken.isEmpty());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestConfirmationTokenProvider.AddConfirmationTokenProvider.class)
    @DisplayName("Add when there is user with status false with this user_id")
    void add() {
        //prepare
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .userId(3)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440002"))
                .build();
        //when
        jdbcConfirmationTokenDao.add(confirmationToken);
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, executeStatementsBefore = "SELECT setval('confirmation_tokens_id_seq', 3, false);", cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Throws DataIntegrityViolationException when there is user with status false with this user_id and token with same userId exists")
    void addDuplicateUserId() {
        //prepare
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .userId(1)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440002"))
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> jdbcConfirmationTokenDao.add(confirmationToken));
        //then
        assertEquals(EmailConfirmationError.CAN_NOT_ADD_LINK_FOR_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, executeStatementsBefore = "SELECT setval('confirmation_tokens_id_seq', 3, false);", cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Throws DataIntegrityViolationException when there is no user with given userId")
    void addNotExistingUserId() {
        //prepare
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .userId(4)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440002"))
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> jdbcConfirmationTokenDao.add(confirmationToken));
        //then
        assertEquals(EmailConfirmationError.CAN_NOT_ADD_LINK_FOR_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, executeStatementsBefore = "SELECT setval('confirmation_tokens_id_seq', 3, false);", cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Throws DuplicateKeyException when confirmation token with same token exists")
    void addExistingTokenValidUserId() {
        //prepare
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .userId(2)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"))
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> jdbcConfirmationTokenDao.add(confirmationToken));
        //then
        assertEquals(EmailConfirmationError.CAN_NOT_ADD_LINK_FOR_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestConfirmationTokenProvider.UpdateConfirmationTokenProvider.class)
    @DisplayName("Update when there exists confirmation token with same user_id and it gets updated with valid unique uuid token and timestamp")
    void update() {
        //prepare
        ConfirmationToken expectedConfirmationToken = ConfirmationToken.builder()
                .userId(2)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440020"))
                .build();
        //when
        jdbcConfirmationTokenDao.update(expectedConfirmationToken);
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class)
    @DisplayName("Update when confirmation token with that user_id doesn't exist")
    void updateNonExistentUserId() {
        //prepare
        ConfirmationToken expectedConfirmationToken = ConfirmationToken.builder()
                .userId(3)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440020"))
                .build();
        //when
        jdbcConfirmationTokenDao.update(expectedConfirmationToken);
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Throws DuplicateKeyException when updating confirmation token with uuid token that already exists in the table in another confirmation token")
    void updateWithTokenWithDuplicateUUIDToken() {
        //prepare
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .userId(1)
                .token(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"))
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> jdbcConfirmationTokenDao.update(confirmationToken));
        //then
        assertEquals(EmailConfirmationError.CAN_NOT_CREATE_LINK_TRY_AGAIN.getMessage(), exception.getMessage());
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestConfirmationTokenProvider.DeleteConfirmationTokenProvider.class)
    @DisplayName("Delete when there is user with given user_id")
    void deleteTokenByUserId() {
        //when
        jdbcConfirmationTokenDao.deleteByUserId(2);
    }

    @Test
    @DataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestConfirmationTokenProvider.ConfirmationTokenProvider.class)
    @DisplayName("Delete when user id isn't exist")
    void deleteNotExistingUserId() {
        //when
        jdbcConfirmationTokenDao.deleteByUserId(100);
    }

}
