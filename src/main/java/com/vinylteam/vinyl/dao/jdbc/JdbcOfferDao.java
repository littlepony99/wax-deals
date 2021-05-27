package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.OfferRowMapper;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcOfferDao implements OfferDao {

    private static final String INSERT_VALID = "INSERT INTO offers (unique_vinyl_id, shop_id, price, currency, genre, cat_number, in_stock, link_to_offer) SELECT * FROM (VALUES(?, ?, ?, ?, ?, ?, ?, ?))" +
            " AS offer(unique_vinyl_id, shop_id, price, currency, genre, cat_number, in_stock , link_to_offer)" +
            " WHERE EXISTS (SELECT * FROM unique_vinyls WHERE unique_vinyls.id=offer.unique_vinyl_id AND unique_vinyls.has_offers)";
    private static final String UPDATE_UNIQUE_VINYLS_ALL_FALSE = "UPDATE unique_vinyls SET has_offers=FALSE WHERE has_offers=TRUE";
    private static final String UPSERT_UNIQUE_VINYLS = "INSERT INTO public.unique_vinyls(id, release, artist, full_name, link_to_image, has_offers) VALUES(?, ?, ?, ?, ?, ?)" +
            " ON CONFLICT(id) DO UPDATE SET has_offers = EXCLUDED.has_offers WHERE unique_vinyls.has_offers<>EXCLUDED.has_offers";
    private static final String SELECT_ALL = "SELECT id, unique_vinyl_id, shop_id, price, currency, genre, cat_number, in_stock, link_to_offer FROM public.offers";
    private static final String SELECT_MANY_BY_UNIQUE_VINYL_ID = SELECT_ALL + " WHERE unique_vinyl_id=?";
    private static final String TRUNCATE_RESTART_IDENTITY = "TRUNCATE offers RESTART IDENTITY";
    private static final RowMapper<Offer> rowMapper = new OfferRowMapper();
    private static final String PREPARED_STATEMENT = "Prepared statement {'preparedStatement':{}}";
    private static final String EXECUTED_STATEMENT = "Executed statement {'statement':{}}";
    private final HikariDataSource dataSource;

    public JdbcOfferDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Offer> findManyByUniqueVinylId(long uniqueVinylId) {
        List<Offer> offers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findVinylByIdStatement = connection.prepareStatement(SELECT_MANY_BY_UNIQUE_VINYL_ID)) {
            findVinylByIdStatement.setLong(1, uniqueVinylId);
            log.debug(PREPARED_STATEMENT, findVinylByIdStatement);
            try (ResultSet resultSet = findVinylByIdStatement.executeQuery()) {
                boolean isResultSetEmpty = true;
                while (resultSet.next()) {
                    isResultSetEmpty = false;
                    Offer offer = rowMapper.mapRow(resultSet);
                    offers.add(offer);
                }
                if (isResultSetEmpty) {
                    RuntimeException e = new RuntimeException();
                    log.error("No offer with that uniqueVinylId in table {'uniqueVinylId':{}}", uniqueVinylId, e);
                    throw e;
                }
            }
        } catch (SQLException e) {
            log.error("Error while finding offers by uniqueVinylId from db {'uniqueVinylId':{}}", uniqueVinylId, e);
            throw new RuntimeException(e);
        }
        log.debug("Resulting offers are {'offers':{}}", offers);
        return offers;
    }

    @Override
    public List<Offer> updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {
        if (uniqueVinyls == null) {
            RuntimeException e = new NullPointerException("List of unique vinyls is null");
            log.error("List of unique vinyls is null", e);
            throw e;
        }
        if (offers == null) {
            RuntimeException e = new NullPointerException("List of offers is null");
            log.error("List of offers is null", e);
            throw e;
        }
        if (uniqueVinyls.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("List of unique vinyls is empty");
            log.error("List of unique vinyls is empty", e);
            throw e;
        }
        if (offers.isEmpty()) {
            RuntimeException e = new IllegalArgumentException("List of offers is empty");
            log.error("List of offers is empty", e);
            throw e;
        }
        List<Offer> notAddedOffers = new ArrayList<>(offers);
        try (Connection connection = dataSource.getConnection();
             Statement prepareTables = connection.createStatement();
             PreparedStatement upsertUniqueVinyls = connection.prepareStatement(UPSERT_UNIQUE_VINYLS);
             PreparedStatement insertValidOffers = connection.prepareStatement(INSERT_VALID)) {
            connection.setAutoCommit(false);
            prepareTables.executeUpdate(UPDATE_UNIQUE_VINYLS_ALL_FALSE);
            log.debug(EXECUTED_STATEMENT, UPDATE_UNIQUE_VINYLS_ALL_FALSE);
            prepareTables.executeUpdate(TRUNCATE_RESTART_IDENTITY);
            log.debug(EXECUTED_STATEMENT, TRUNCATE_RESTART_IDENTITY);
            for (UniqueVinyl uniqueVinyl : uniqueVinyls) {
                upsertUniqueVinyls.setLong(1, uniqueVinyl.getId());
                upsertUniqueVinyls.setString(2, uniqueVinyl.getRelease());
                upsertUniqueVinyls.setString(3, uniqueVinyl.getArtist());
                upsertUniqueVinyls.setString(4, uniqueVinyl.getFullName());
                upsertUniqueVinyls.setString(5, uniqueVinyl.getImageLink());
                upsertUniqueVinyls.setBoolean(6, uniqueVinyl.getHasOffers());
                log.debug(PREPARED_STATEMENT, upsertUniqueVinyls);
                upsertUniqueVinyls.addBatch();
            }
            upsertUniqueVinyls.executeBatch();
            for (Offer offer : offers) {
                insertValidOffers.setLong(1, offer.getUniqueVinylId());
                insertValidOffers.setInt(2, offer.getShopId());
                insertValidOffers.setDouble(3, offer.getPrice());
                insertValidOffers.setString(4, offer.getCurrency().get().toString());
                insertValidOffers.setString(5, offer.getGenre());
                insertValidOffers.setString(6, offer.getCatNumber());
                insertValidOffers.setBoolean(7,offer.isInStock());
                insertValidOffers.setString(8, offer.getOfferLink());
                log.debug(PREPARED_STATEMENT, insertValidOffers);
                insertValidOffers.addBatch();
            }
            int[] updateCounts = insertValidOffers.executeBatch();
            for (int i = updateCounts.length - 1; i >= 0; i--) {
                if (updateCounts[i] == 1) {
                    notAddedOffers.remove(i);
                } else {
                    log.info("Offer wasn't added {'offer':{}}", notAddedOffers.get(i));
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("Error while updating database with uniqueVinyls and refilling database with offers {'uniqueVinyls':{}, 'offers':{}}",
                    uniqueVinyls, offers, e);
            throw new RuntimeException("Error while updating database with uniqueVinyls and refilling database with offers", e);
        }
        log.info("Database updated, couldn't add {} offers", notAddedOffers.size());
        return notAddedOffers;
    }

}
