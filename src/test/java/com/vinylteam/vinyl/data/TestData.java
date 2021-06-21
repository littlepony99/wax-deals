package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.IDataSet;

public class TestData {

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

    public static class UsersResultProvider implements DataSetProvider {
    @Override
    public IDataSet provide() {
        System.out.println("ResultProvider");
        return new DataSetBuilder()
                .table("users")
                .columns("id", "email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                .values("1", "user1@wax-deals.com", "salt1", "1", "hash1", "false", "USER", "discogsUserName1")
                .values("2", "user2@wax-deals.com", "salt2", "2", "hash2", "false", "USER", "discogsUserName2")
                .build();
    }
}

}
