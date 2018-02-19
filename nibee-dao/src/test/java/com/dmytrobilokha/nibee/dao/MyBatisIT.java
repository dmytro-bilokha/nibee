package com.dmytrobilokha.nibee.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class MyBatisIT {

    private static DataSource DATA_SOURCE;

    @BeforeClass
    public static void createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:nibee");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        DATA_SOURCE = dataSource;
    }

    @Test
    public void checkH2connection() throws SQLException, ClassNotFoundException {
        Connection connection = DATA_SOURCE.getConnection();
        assertTrue(connection != null);
        connection.close();
    }

}
