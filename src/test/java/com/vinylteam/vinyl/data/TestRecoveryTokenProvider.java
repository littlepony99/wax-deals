package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

public class TestRecoveryTokenProvider {

    public static class RecoveryPasswordTokenProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("users")
                    .columns("id", "email", "salt", "iterations", "password", "status", "role", "discogs_user_name")
                    .values("1", "user1@wax-deals.com", "salt1", "1", "hash1", "false", "USER", "discogsUserName1")
                    .values("2", "user2@wax-deals.com", "salt2", "2", "hash2", "false", "USER", "discogsUserName2")
                    .table("recovery_password_tokens")
                    .columns("user_id", "token", "created_at")
                    .values("1", "123e4567-e89b-12d3-a456-426614174000", "2021-06-24 18:00:10")
                    .build();
        }
    }

    public static class AddRecoveryPasswordTokenProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("recovery_password_tokens")
                    .columns("user_id", "token")
                    .values("1", "123e4567-e89b-12d3-a456-426614174000")
                    .values("2", "123e4567-e89b-12d3-a456-426614174001")
                    .build();
        }
    }

    public static class UpdateRecoveryPasswordTokenProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("recovery_password_tokens")
                    .columns("user_id", "token")
                    .values("1", "123e4567-e89b-12d3-a456-426614174001")
                    .build();
        }
    }

    public static class DeleteRecoveryPasswordTokenProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("recovery_password_tokens")
                    .columns("user_id", "token")
                    .build();
        }
    }

}
