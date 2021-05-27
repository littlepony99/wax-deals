package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.UniqueVinylDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UniqueVinylRowMapper;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcUniqueVinylDao implements UniqueVinylDao {

    private static final RowMapper<UniqueVinyl> rowMapper = new UniqueVinylRowMapper();
    private static final String SELECT_ALL = "SELECT id, release, artist, full_name, link_to_image FROM public.unique_vinyls";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id=?";
    private static final String SELECT_MANY_RANDOM = SELECT_ALL + " WHERE has_offers ORDER BY random() LIMIT ?";
    private static final String SELECT_MANY_BY_FULL_NAME_MATCH = SELECT_ALL + " WHERE full_name ILIKE ? AND has_offers";
    private static final String SELECT_BY_ARTIST = SELECT_ALL + " WHERE artist ILIKE ? AND has_offers";
    private final HikariDataSource dataSource;

    public JdbcUniqueVinylDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<UniqueVinyl> findAll() {
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findAllStatement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = findAllStatement.executeQuery()) {
            log.debug("Executed statement {'statement':{}}", findAllStatement);
            while (resultSet.next()) {
                UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                uniqueVinyls.add(uniqueVinyl);
            }
        } catch (SQLException e) {
            log.error("Error while finding uniqueVinyls in db {'uniqueVinyls':{}}", uniqueVinyls, e);
            throw new RuntimeException(e);
        }
        log.info("Found all uniqueVinyls from db.");
        log.debug("Resulting uniqueVinyls are {'uniqueVinyls':{}}", uniqueVinyls);
        return uniqueVinyls;
    }

    @Override
    public UniqueVinyl findById(long id) {
        UniqueVinyl uniqueVinyl;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(SELECT_BY_ID)) {
            findByIdStatement.setLong(1, id);
            log.debug("Prepared statement {'preparedStatement':{}}", findByIdStatement);
            try (ResultSet resultSet = findByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    uniqueVinyl = rowMapper.mapRow(resultSet);
                } else {
                    RuntimeException e = new RuntimeException();
                    log.error("No uniqueVinyl with that id in table {'id':{}}", id, e);
                    throw e;
                }
            }
        } catch (SQLException e) {
            log.error("Error while finding uniqueVinyl by id from db {'id':{}}", id);
            throw new RuntimeException(e);
        }
        log.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", uniqueVinyl);
        return uniqueVinyl;
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        List<UniqueVinyl> randomUniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findAmountOfRandom = connection.prepareStatement(SELECT_MANY_RANDOM)) {
            findAmountOfRandom.setInt(1, amount);
            log.debug("Prepared statement {'preparedStatement':{}}", findAmountOfRandom);
            try (ResultSet resultSet = findAmountOfRandom.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    randomUniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            log.error("Error while finding that amount of random uniqueVinyls from db {'amount':{}}", amount, e);
            throw new RuntimeException("Error while finding " + amount + " of random unique vinyls from the db", e);
        }
        log.debug("Resulting uniqueVinyls are {'uniqueVinyls':{}}", randomUniqueVinyls);
        return randomUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        if (matcher == null) {
            RuntimeException e = new NullPointerException();
            log.error("Passed matcher is null", e);
            throw e;
        }
        List<UniqueVinyl> filteredUniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findFilteredByFullNameSubstring = connection.prepareStatement(SELECT_MANY_BY_FULL_NAME_MATCH)) {
            findFilteredByFullNameSubstring.setString(1, '%' + matcher + '%');
            log.debug("Prepared statement {'preparedStatement':{}}", findFilteredByFullNameSubstring);
            try (ResultSet resultSet = findFilteredByFullNameSubstring.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    filteredUniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            log.error("Error while finding uniqueVinyls by fullName matcher from db {'matcher':{}}", matcher, e);
        }
        log.debug("Resulting uniqueVinyls are {'uniqueVinyls':{}}", filteredUniqueVinyls);
        return filteredUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        if (artist == null) {
            RuntimeException e = new NullPointerException();
            log.error("Passed artist is null", e);
            throw e;
        }
        List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByArtist = connection.prepareStatement(SELECT_BY_ARTIST)) {
            findByArtist.setString(1, artist);
            log.debug("Prepared statement {'preparedStatement':{}}", findByArtist);
            try (ResultSet resultSet = findByArtist.executeQuery()) {
                while (resultSet.next()) {
                    UniqueVinyl uniqueVinyl = rowMapper.mapRow(resultSet);
                    uniqueVinyls.add(uniqueVinyl);
                }
            }
        } catch (SQLException e) {
            log.error("Error while finding uniqueVinyls by artist from db {'artist':{}}", artist, e);
        }
        log.debug("Resulting uniqueVinyls are {'uniqueVinyls':{}}", uniqueVinyls);
        return uniqueVinyls;
    }

}
