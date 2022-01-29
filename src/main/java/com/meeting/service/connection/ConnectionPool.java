package com.meeting.service.connection;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//TODO check singleton pattern for connection
public class ConnectionPool {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";

    private static final String USERNAME = "postgres";

    private static final String PASSWORD = "123123";


    public ConnectionPool() {
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
            e.printStackTrace();
        }
        return c;
    }

    public static void rollback(Connection c) {
        try {
            if (c != null) {
                c.rollback();
            }
        } catch (SQLException e) {
            // just write to log
            e.printStackTrace();
        }
    }

    public static void close(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            // just write to log
            e.printStackTrace();
        }
    }
}
