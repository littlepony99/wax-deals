package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.ConfirmationTokenRowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.OfferRowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DataFinderFromDBForITests {

    private final HikariDataSource dataSource;
    private final RowMapper<User> userRowMapper = new UserRowMapper();
    private final RowMapper<UniqueVinyl> uniqueVinylRowMapper = new UniqueVinylRowMapper();
    private final RowMapper<Offer> offerRowMapper = new OfferRowMapper();
    private final RowMapper<ConfirmationToken> userTokenRowMapper = new ConfirmationTokenRowMapper();
    private static final String SELECT_ALL_UNIQUE_VINYLS = "SELECT id, release, artist, full_name, link_to_image, has_offers FROM unique_vinyls ORDER BY id";
    private static final String SELECT_ALL_OFFERS = "SELECT id, unique_vinyl_id, shop_id, price, currency, genre, cat_number, in_stock, link_to_offer FROM offers ORDER BY id";
    private static final String SELECT_ALL_USERS = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name FROM users ORDER BY id";
    private static final String SELECT_ALL_CONFIRMATION_TOKENS = "SELECT id, user_id, token, created_at FROM confirmation_tokens ORDER BY id";

    public DataFinderFromDBForITests(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<UniqueVinyl> findAllUniqueVinyls() {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(SELECT_ALL_UNIQUE_VINYLS)) {
            while (resultSet.next()) {
                UniqueVinyl uniqueVinyl = uniqueVinylRowMapper.mapRow(resultSet);
                uniqueVinyl.setHasOffers(resultSet.getBoolean("has_offers"));
                uniqueVinyls.add(uniqueVinyl);
            }
        } catch (SQLException e) {
            log.error("Error while finding all unique vinyls from test db {'uniqueVinyls':{}}", uniqueVinyls, e);
            throw new RuntimeException(e);
        }
        return uniqueVinyls;
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(SELECT_ALL_USERS)) {
            while (resultSet.next()) {
                User user = userRowMapper.mapRow(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            log.error("Error while finding all users from test db {'users':{}}", users, e);
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<Offer> findAllOffers() {
        List<Offer> offers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(SELECT_ALL_OFFERS)) {
            while (resultSet.next()) {
                Offer offer = offerRowMapper.mapRow(resultSet);
                offers.add(offer);
            }
        } catch (SQLException e) {
            log.error("Error while finding all offers from test db {'offers':{}}", offers, e);
            throw new RuntimeException(e);
        }
        return offers;
    }

    public List<ConfirmationToken> findAllConfirmationTokens() throws SQLException {
        List<ConfirmationToken> confirmationTokens = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(SELECT_ALL_CONFIRMATION_TOKENS)) {
            while (resultSet.next()) {
                ConfirmationToken confirmationToken = userTokenRowMapper.mapRow(resultSet);
                confirmationTokens.add(confirmationToken);
            }
        }
        return confirmationTokens;
    }

}
