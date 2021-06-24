package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.IDataSet;

public class TestUserPostProvider {

    public static class UsersPostProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("user_posts")
                    .columns("name", "email", "theme", "message", "created_at")
                    .values("roma","user1@wax-deals.com", "registration", "hello", "2004-10-19 10:23:10")
                    .build();
        }
    }

    public static class AddUsersPostProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("user_posts")
                    .columns("name", "email", "theme", "message", "created_at")
                    .values("roma","user1@wax-deals.com", "registration", "hello", "2004-10-19 10:23:10")
                    .values("taras","user2@wax-deals.com", "help", "help", "2004-10-19 10:23:10")
                    .build();
        }
    }
}