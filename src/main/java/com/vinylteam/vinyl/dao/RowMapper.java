package com.vinylteam.vinyl.dao;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;

public interface RowMapper<T> extends ResultSetExtractor {

    T mapRow(ResultSet resultSet);

}
