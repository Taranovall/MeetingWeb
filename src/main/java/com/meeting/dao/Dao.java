package com.meeting.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> getById(Long id, Connection c) throws SQLException;
}
