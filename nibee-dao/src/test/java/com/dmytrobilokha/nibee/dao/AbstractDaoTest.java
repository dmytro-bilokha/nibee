package com.dmytrobilokha.nibee.dao;

import com.dmytrobilokha.nibee.dao.flyway.DbMigrator;
import com.dmytrobilokha.nibee.dao.mybatis.SessionFactoryProducer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class AbstractDaoTest {

    private static final String DB_SCRIPT_BASE = "datasets/";
    private static final Pattern STATEMENT_DELIMITER = Pattern.compile(";\\h*\\r?\\n");

    private static DataSource dataSource;
    private static SqlSessionFactory sqlSessionFactory;

    private SqlSession sqlSession;

    @BeforeClass
    public static void initDb() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        dataSource = jdbcDataSource;
        jdbcDataSource.setUrl("jdbc:h2:mem:nibee;MODE=MYSQL;DB_CLOSE_DELAY=-1");
        jdbcDataSource.setUser("sa");
        jdbcDataSource.setPassword("sa");
        sqlSessionFactory = new SessionFactoryProducer(jdbcDataSource).produce();
        DbMigrator dbMigrator = new DbMigrator(jdbcDataSource);
        dbMigrator.migrateDb();
    }

    @AfterClass
    public static void shutdownDb() {
        executeSqlStatement("SHUTDOWN");
    }

    @Before
    public void openSqlSession() {
        sqlSession = sqlSessionFactory.openSession();
    }

    @After
    public void closeSqlSession() {
        sqlSession.rollback();
        sqlSession.close();
        sqlSession = null;
    }

    protected <T> T getMapper(Class<T> mapperClass) {
        return sqlSession.getMapper(mapperClass);
    }

    protected static int calculateTableRows(String tableName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
            resultSet.first();
            return resultSet.getInt(1);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        } finally {
            silentlyClose(resultSet, statement, connection);
        }
    }

    protected static void executeSqlStatement(String statementString) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {
            statement.execute(statementString);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected static void executeSqlScripts(String... scriptNames) {
        Connection connection = null;
        Statement statement = null;
        Scanner scanner = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            connection.setAutoCommit(false);
            for (String scriptName : scriptNames) {
                scanner = new Scanner(
                        AbstractDaoTest.class.getClassLoader().getResourceAsStream(DB_SCRIPT_BASE + scriptName)
                ).useDelimiter(STATEMENT_DELIMITER);
                while (scanner.hasNext()) {
                    String sqlStatementString = scanner.next();
                    statement.addBatch(sqlStatementString);
                }
                scanner.close();
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            silentlyClose(scanner, statement, connection);
        }
    }

    private static void silentlyClose(AutoCloseable... openResources) {
        for (AutoCloseable resource : openResources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
