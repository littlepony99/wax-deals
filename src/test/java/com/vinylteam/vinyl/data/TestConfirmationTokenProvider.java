package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

public class TestConfirmationTokenProvider {

    public static class ConfirmationTokenProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("confirmation_tokens")
                    .columns("user_id", "token", "created_at")
                    .values("1", "userToken", "2021-06-24 18:00:10")
                    .build();
        }
    }

    public static class AddConfirmationTokenProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("confirmation_tokens")
                    .columns("user_id", "token", "created_at")
                    .values("1", "userToken", "2021-06-23 10:10:10")
                    .values("2", "anotherUserToken", "2021-06-24 18:20:00")
                    .build();
        }
    }

}