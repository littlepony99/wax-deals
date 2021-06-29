package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.IDataSet;

public class TestConfirmationTokenProvider {

    public static class ConfirmationTokenProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("users")
                    .columns("id", "email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .values("1", "user1@wax-deals.com", "salt1", "1", "hash1", "false", "USER", "discogsUserName1")
                    .values("2", "user2@wax-deals.com", "salt2", "2", "hash2", "false", "USER", "discogsUserName2")
                    .values("3", "user3@wax-deals.com", "salt3", "3", "hash3", "false", "USER", "discogsUserName3")
                    .table("confirmation_tokens")
                    .columns("id", "user_id", "token", "created_at")
                    .values("1", "1", "123e4567-e89b-12d3-a456-556642440000", "2021-06-23 10:10:10")
                    .values("2", "2", "123e4567-e89b-12d3-a456-556642440001", "2021-04-04 10:10:10")
                    .build();
        }
    }

    public static class AddConfirmationTokenProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("confirmation_tokens")
                    .columns("user_id", "token")
                    .values("1", "123e4567-e89b-12d3-a456-556642440000")
                    .values("2", "123e4567-e89b-12d3-a456-556642440001")
                    .values("3", "123e4567-e89b-12d3-a456-556642440002")
                    .build();
        }
    }

    public static class UpdateConfirmationTokenProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("confirmation_tokens")
                    .columns("user_id", "token")
                    .values("1", "123e4567-e89b-12d3-a456-556642440000")
                    .values("2", "123e4567-e89b-12d3-a456-556642440020")
                    .build();

        }
    }

    public static class DeleteConfirmationTokenProvider implements DataSetProvider {
        @Override
        public IDataSet provide() {
            return new DataSetBuilder()
                    .table("confirmation_tokens")
                    .columns("user_id", "token")
                    .values("1", "123e4567-e89b-12d3-a456-556642440000")
                    .build();

        }
    }

}