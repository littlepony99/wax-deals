package com.vinylteam.vinyl.data;

import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

public class TestShopProvider {

    public static class ShopProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            return new DataSetBuilder()
                    .table("shops")
                    .columns("id", "link_to_main_page", "link_to_image", "name", "link_to_small_image", "shop_order")
                    .values(1, "pagelink1", "imagelink1", "name1", "smallimagelink1", "3")
                    .values(2, "pagelink2", "imagelink2", "name2", "smallimagelink2", "2")
                    .values(3, "pagelink3", "imagelink3", "name3", "smallimagelink3", "1")
                    .build();
        }
    }

}
