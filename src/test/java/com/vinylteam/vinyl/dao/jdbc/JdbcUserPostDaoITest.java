
package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.data.TestUserPostProvider;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class JdbcUserPostDaoITest {

    @Autowired
    private UserPostDao userPostDao;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DataSet(provider = TestUserPostProvider.UsersPostProvider.class, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @ExpectedDataSet(provider = TestUserPostProvider.AddUsersPostProvider.class)
    @DisplayName("Adds user post to db")
    void addNewUserPostTest() {
        //prepare
        UserPost expectedUserPost = UserPost.builder()
                .name("taras")
                .email("user2@wax-deals.com")
                .message("help")
                .createdAt(LocalDateTime.of(2004, 10, 19, 10, 23, 10)).build();
        //when
        userPostDao.add(expectedUserPost);
    }

}