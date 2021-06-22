package com.vinylteam.vinyl.dao;

import java.sql.ResultSet;

public interface RowMapper<T> {

    T mapRow(ResultSet resultSet);

}
