package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.IDataSet;

public class TestUserProvider {

    public static class UsersProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("users")
                    .columns("email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .values("user1@wax-deals.com", "salt1", "1", "hash1", "false", "USER", "discogsUserName1")
                    .build();
        }
    }

    public static class AddedUserResultProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            System.out.println("ResultProvider");
            return new DataSetBuilder()
                    .table("users")
                    .columns("email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .values("user1@wax-deals.com", "salt1", "1", "hash1", "false", "USER", "discogsUserName1")
                    .values("user2@wax-deals.com", "salt2", "2", "hash2", "false", "USER", "discogsUserName2")
                    .build();
        }

    }

    public static class UpdatedUserResultProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("users")
                    .columns("email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .values("user3@wax-deals.com", "salt3", "3", "hash3", "false", "USER", "discogsUserName3")
                    .build();
        }
    }

    public static class DeletedUserResultProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("users")
                    .columns("email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .build();
        }
    }

    public static class SetUserStatusProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("users")
                    .columns("email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .values("user1@wax-deals.com", "salt1", "1", "hash1", "true", "USER", "discogsUserName1")
                    .build();
        }
    }

}
