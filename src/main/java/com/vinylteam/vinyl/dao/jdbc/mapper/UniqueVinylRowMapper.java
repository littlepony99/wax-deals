package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UniqueVinylRowMapper implements RowMapper<UniqueVinyl> {

    @Override
    public UniqueVinyl mapRow(ResultSet resultSet) {
        UniqueVinyl uniqueVinyl = new UniqueVinyl();
        try {
            uniqueVinyl.setId(resultSet.getInt("id"));
            uniqueVinyl.setRelease(resultSet.getString("release"));
            uniqueVinyl.setArtist(resultSet.getString("artist"));
            uniqueVinyl.setFullName(resultSet.getString("full_name"));
            uniqueVinyl.setImageLink(resultSet.getString("link_to_image"));
            log.debug("Resulting UniqueVinyl object {'uniqueVinyl':{}}", uniqueVinyl);
            return uniqueVinyl;
        } catch (SQLException e) {
            log.error("Error while getting data from result set into UniqueVinyl object {'uniqueVinyl':{}}", uniqueVinyl, e);
            throw new RuntimeException(e);
        }
    }

}
