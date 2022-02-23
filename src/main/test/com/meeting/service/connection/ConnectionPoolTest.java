package com.meeting.service.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConnectionPoolTest {

    @Test
    void shouldReturnConnection() {
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            assertNotNull(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}