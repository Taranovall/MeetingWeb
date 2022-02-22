package com.meeting.service.connection;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final Logger log = LogManager.getLogger(ConnectionPool.class);

    private ConnectionPool() {
    }

    private static ConnectionPool instance = null;

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    public Connection getConnection() {
        Context context;
        Connection c = null;
        try {
            context = new InitialContext();
            DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/postgres");
            c = ds.getConnection();
            c.setAutoCommit(false);
        } catch (SQLException | NamingException e) {
            log.error("Cannot get connection to data base", e);
        }
        return c;
    }

    public static void rollback(Connection c) {
        try {
            if (c != null) {
                c.rollback();
            }
        } catch (SQLException e) {
            log.error("Cannot rollback connection");
        }
    }

    public static void close(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            log.error("Cannot close connection");
        }
    }
}
