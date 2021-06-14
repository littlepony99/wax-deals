
/*
package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.DatabasePreparerForITests;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserPostDaoITest {

    private final DatabasePreparerForITests databasePreparer = new DatabasePreparerForITests();
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final UserPostDao userPostDao = new JdbcUserPostDao(databasePreparer.getDataSource());

    @BeforeAll
    void beforeAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
        databasePreparer.truncateCascadeUserPosts();
        databasePreparer.insertUsers(dataGenerator.getUsersList());
    }

    @AfterAll
    void afterAll() throws SQLException {
        databasePreparer.truncateCascadeUsers();
        databasePreparer.truncateCascadeUserPosts();
    }

    @Test
    @DisplayName("Adds user post to db")
    void addNewUserPostTest() {
        LocalDateTime createdAt = LocalDateTime.of(2021, 5, 19, 21, 0);
        //prepare
        UserPost expectedUserPost = new UserPost("name", "email", "theme", "message", createdAt);
        //when
        assertTrue(userPostDao.add(expectedUserPost));
    }

}*/
