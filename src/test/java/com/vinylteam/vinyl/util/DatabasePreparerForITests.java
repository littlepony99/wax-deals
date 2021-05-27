package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
public class DatabasePreparerForITests {

    private static final String TRUNCATE_SHOPS_CASCADE = "TRUNCATE public.shops RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_UNIQUE_VINYLS_CASCADE = "TRUNCATE public.unique_vinyls RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_OFFERS = "TRUNCATE public.offers RESTART IDENTITY";
    private static final String TRUNCATE_USERS_CASCADE = "TRUNCATE public.users RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_USERS_POSTS_CASCADE = "TRUNCATE public.user_posts RESTART IDENTITY CASCADE";
    private static final String INSERT_IN_SHOPS = "INSERT INTO public.shops(id, link_to_main_page, link_to_image, name, link_to_small_image) VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_IN_UNIQUE_VINYLS = "INSERT INTO public.unique_vinyls(id, release, artist, full_name, link_to_image, has_offers) VALUES(?, ?, ?, ?, ?, ?)";
    private static final String INSERT_IN_OFFERS = "INSERT INTO public.offers(unique_vinyl_id, shop_id, price, currency, genre, cat_number, in_stock, link_to_offer) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_IN_USERS = "INSERT INTO public.users (email, password, salt, iterations, status, role, discogs_user_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final PropertiesReader propertiesReader = new PropertiesReader();
    private final HikariDataSource dataSource;
    private final HikariConfig config = new HikariConfig();

    public DatabasePreparerForITests() {
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        log.info("Configured and created HikariDataSource object {'dataSource':{}}", dataSource);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void truncateCascadeShops() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateShops = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS_CASCADE);
        }
    }

    public void truncateCascadeUniqueVinyls() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateUniqueVinyls = connection.createStatement()) {
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS_CASCADE);
        }
    }

    public void truncateOffers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateOffers = connection.createStatement()) {
            truncateOffers.executeUpdate(TRUNCATE_OFFERS);
        }
    }

    public void truncateCascadeUsers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateUsers = connection.createStatement()) {
            truncateUsers.executeUpdate(TRUNCATE_USERS_CASCADE);
        }
    }

    public void truncateCascadeUserPosts() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateUsers = connection.createStatement()) {
            truncateUsers.executeUpdate(TRUNCATE_USERS_POSTS_CASCADE);
        }
    }

    public void truncateAllVinylTables() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateShops = connection.createStatement();
             Statement truncateUniqueVinyls = connection.createStatement();
             Statement truncateOffers = connection.createStatement()) {
            truncateShops.executeUpdate(TRUNCATE_SHOPS_CASCADE);
            truncateUniqueVinyls.executeUpdate(TRUNCATE_UNIQUE_VINYLS_CASCADE);
            truncateOffers.executeUpdate(TRUNCATE_OFFERS);
        }
    }

    public void insertShops(List<Shop> shops) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertShops = connection.prepareStatement(INSERT_IN_SHOPS)) {
            connection.setAutoCommit(false);
            for (Shop shop : shops) {
                insertShops.setInt(1, shop.getId());
                insertShops.setString(2, shop.getMainPageLink());
                insertShops.setString(3, shop.getImageLink());
                insertShops.setString(4, shop.getName());
                insertShops.setString(5, shop.getSmallImageLink());
                insertShops.addBatch();
            }
            insertShops.executeBatch();
            connection.commit();
        }
    }

    public void insertUniqueVinyls(List<UniqueVinyl> uniqueVinyls) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertUniqueVinyls = connection.prepareStatement(INSERT_IN_UNIQUE_VINYLS)) {
            connection.setAutoCommit(false);
            for (UniqueVinyl uniqueVinyl : uniqueVinyls) {
                insertUniqueVinyls.setLong(1, uniqueVinyl.getId());
                insertUniqueVinyls.setString(2, uniqueVinyl.getRelease());
                insertUniqueVinyls.setString(3, uniqueVinyl.getArtist());
                insertUniqueVinyls.setString(4, uniqueVinyl.getFullName());
                insertUniqueVinyls.setString(5, uniqueVinyl.getImageLink());
                insertUniqueVinyls.setBoolean(6, uniqueVinyl.getHasOffers());
                insertUniqueVinyls.addBatch();
            }
            insertUniqueVinyls.executeBatch();
            connection.commit();
        }
    }

    public void insertOffers(List<Offer> offers) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertOffers = connection.prepareStatement(INSERT_IN_OFFERS)) {
            connection.setAutoCommit(false);
            for (Offer offer : offers) {
                insertOffers.setLong(1, offer.getUniqueVinylId());
                insertOffers.setInt(2, offer.getShopId());
                insertOffers.setDouble(3, offer.getPrice());
                insertOffers.setString(4, offer.getCurrency().get().toString());
                insertOffers.setString(5, offer.getGenre());
                insertOffers.setString(6, offer.getCatNumber());
                insertOffers.setBoolean(7, offer.isInStock());
                insertOffers.setString(8, offer.getOfferLink());
                insertOffers.addBatch();
            }
            insertOffers.executeBatch();
            connection.commit();
        }
    }

    public void insertUsers(List<User> users) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertUsers = connection.prepareStatement(INSERT_IN_USERS)) {
            connection.setAutoCommit(false);
            for (User user : users) {
                insertUsers.setString(1, user.getEmail());
                insertUsers.setString(2, user.getPassword());
                insertUsers.setString(3, user.getSalt());
                insertUsers.setInt(4, user.getIterations());
                insertUsers.setBoolean(5, user.getStatus());
                insertUsers.setString(6, user.getRole().toString());
                insertUsers.setString(7, user.getDiscogsUserName());
                insertUsers.addBatch();
            }
            insertUsers.executeBatch();
            connection.commit();
        }
    }

    public void closeDataSource() {
        dataSource.close();
    }

}
