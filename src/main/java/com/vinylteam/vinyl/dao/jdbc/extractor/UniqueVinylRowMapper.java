package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UniqueVinylRowMapper implements RowMapper<UniqueVinyl> {

/*    @Override
    public UniqueVinyl extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        UniqueVinyl uniqueVinyl = null;
        if (resultSet.next()) {
            uniqueVinyl = UniqueVinyl.builder()
                    .id(resultSet.getLong("id"))
                    .artist(resultSet.getString("artist"))
                    .fullName(resultSet.getString("full_name"))
                    .hasOffers(resultSet.getBoolean("has_offers"))
                    .imageLink(resultSet.getString("link_to_image"))
                    .release(resultSet.getString("release"))
                    .build();
        }
        log.debug("Resulting User object {'user':{}}", uniqueVinyl);
        return uniqueVinyl;
    }*/

    @Override
    public UniqueVinyl mapRow(ResultSet resultSet, int i) throws SQLException {
        try {
            UniqueVinyl uniqueVinyl = new UniqueVinyl();
            uniqueVinyl.setId(resultSet.getLong("id"));
            uniqueVinyl.setArtist(resultSet.getString("artist"));
            uniqueVinyl.setFullName(resultSet.getString("full_name"));
            uniqueVinyl.setHasOffers(resultSet.getBoolean("has_offers"));
            uniqueVinyl.setImageLink(resultSet.getString("link_to_image"));
            uniqueVinyl.setRelease(resultSet.getString("release"));
            log.debug("Resulting UniqueVinyl object {'uniqueVinyl':{}}", uniqueVinyl);
            return uniqueVinyl;
        } catch (SQLException e) {
            log.error("Error while getting data from result set into UniqueVinyl object ", e);
            throw new RuntimeException(e);
        }
    }

}
