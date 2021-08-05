package com.vinylteam.vinyl.security.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.data.TestUserProvider;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class SpringSecurityServiceITest {

    @Autowired
    private UserDetailsService service;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DisplayName("Checks whether Spring Security based security service loads the specified user")
    @DataSet(provider = TestUserProvider.UsersProvider.class/*, cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"}*/)
    void loadUserByUsername() {
        User expectedUser = dataGenerator.getUserWithNumber(1);

        var loadedUSer = service.loadUserByUsername(expectedUser.getEmail());
        assertEquals(expectedUser.getEmail(), loadedUSer.getUsername());
        assertEquals(expectedUser.getPassword(), loadedUSer.getPassword());
    }

/*    @Test
    @ExpectedDataSet(provider = TestUserProvider.AddedUserResultProvider.class)
    @DisplayName("Adds user to db")
    void add() {
        //prepare
        User expectedUser = dataGenerator.getUserWithNumber(2);
        //when
        assertEquals(1, userDao.add(expectedUser));
    }*/
}