package com.meeting.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> getById(Long id, Connection c) throws SQLException;

    List<T> getAll(Connection c) throws SQLException;

    void save(T t, Connection c) throws SQLException;

}
