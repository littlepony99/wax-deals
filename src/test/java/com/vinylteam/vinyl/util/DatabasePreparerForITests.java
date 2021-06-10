package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jca.cci.connection.ConnectionSpecConnectionFactoryAdapter;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class DatabasePreparerForITests {

    private static final String TRUNCATE_SHOPS_CASCADE = "TRUNCATE public.shops RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_UNIQUE_VINYLS_CASCADE = "TRUNCATE public.unique_vinyls RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_OFFERS = "TRUNCATE public.offers RESTART IDENTITY";
    private static final String TRUNCATE_USERS_CASCADE = "TRUNCATE public.users RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_USERS_POSTS_CASCADE = "TRUNCATE public.user_posts RESTART IDENTITY CASCADE";
    private static final String TRUNCATE_CONFIRMATION_TOKENS = "TRUNCATE confirmation_tokens RESTART IDENTITY";
    private static final String TRUNCATE_RECOVERY_PASSWORD = "TRUNCATE recovery_password_tokens RESTART IDENTITY CASCADE";
    private static final String INSERT_IN_SHOPS = "INSERT INTO public.shops(id, link_to_main_page, link_to_image, name, link_to_small_image) VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_IN_UNIQUE_VINYLS = "INSERT INTO public.unique_vinyls(id, release, artist, full_name, link_to_image, has_offers) VALUES(?, ?, ?, ?, ?, ?)";
    private static final String INSERT_IN_OFFERS = "INSERT INTO public.offers(unique_vinyl_id, shop_id, price, currency, genre, cat_number, in_stock, link_to_offer) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_IN_USERS = "INSERT INTO public.users (email, password, salt, iterations, status, role, discogs_user_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_IN_CONFIRMATION_TOKENS = "INSERT INTO confirmation_tokens (user_id, token, created_at) VALUES (?, ?, ?)";
    private static final String UPDATE_USER_STATUS = "UPDATE users SET status=? WHERE id=?";
    private static final String INSERT_RECOVERY_TOKEN = "INSERT INTO recovery_password_tokens" +
            " (user_id, token, created_at, token_lifetime)" +
            " VALUES (?, ?, ?, ?)" +
            " ON CONFLICT (user_id) DO UPDATE SET token = ?, created_at = ?, token_lifetime = ?";
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

    public void truncateConfirmationTokens() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateConfirmationTokens = connection.createStatement()) {
            truncateConfirmationTokens.executeUpdate(TRUNCATE_CONFIRMATION_TOKENS);
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

    public void truncateCascadeRecoveryPassword() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateRecovery = connection.createStatement()) {
            truncateRecovery.executeUpdate(TRUNCATE_RECOVERY_PASSWORD);
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
            connection.setAutoCommit(true);
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
            connection.setAutoCommit(true);
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
            connection.setAutoCommit(true);
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
            connection.setAutoCommit(true);
        }
    }

    public void insertConfirmationTokens(List<ConfirmationToken> confirmationTokens) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertTokens = connection.prepareStatement(INSERT_IN_CONFIRMATION_TOKENS)) {
            connection.setAutoCommit(false);
            for (ConfirmationToken confirmationToken : confirmationTokens) {
                insertTokens.setLong(1, confirmationToken.getUserId());
                insertTokens.setObject(2, confirmationToken.getToken());
                insertTokens.setTimestamp(3, confirmationToken.getTimestamp());
                insertTokens.addBatch();
            }
            insertTokens.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public void updateUserStatus(long id, boolean status) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateUserStatus = connection.prepareStatement(UPDATE_USER_STATUS)) {
            updateUserStatus.setBoolean(1, status);
            updateUserStatus.setLong(2, id);
            updateUserStatus.executeUpdate();
        }
    }

    public void insertRecoveryToken(RecoveryToken recoveryToken) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertRecovery = connection.prepareStatement(INSERT_RECOVERY_TOKEN)) {
            insertRecovery.setLong(1, recoveryToken.getUserId());
            insertRecovery.setObject(2, recoveryToken.getToken());
            insertRecovery.setTimestamp(3, Timestamp.from(Instant.now()));
            insertRecovery.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
            insertRecovery.setObject(5, recoveryToken.getToken());
            insertRecovery.setTimestamp(6, Timestamp.from(Instant.now()));
            insertRecovery.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        }
    }

    public void closeDataSource() {
        dataSource.close();
    }

}
