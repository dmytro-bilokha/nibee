package com.dmytrobilokha.nibee.dao;

import com.dmytrobilokha.nibee.dao.flyway.DbMigrator;
import com.dmytrobilokha.nibee.dao.mybatis.SessionFactoryProducer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class AbstractRepositoryTest {

    private static final String DB_SCRIPT_BASE = "datasets/";
    private static final Pattern STATEMENT_DELIMITER = Pattern.compile(";\\h*\\r?\\n");

    private final DataSource dataSource;
    private final SqlSessionFactory sqlSessionFactory;

    private SqlSession sqlSession;

    protected AbstractRepositoryTest() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        dataSource = jdbcDataSource;
        jdbcDataSource.setUrl("jdbc:h2:mem:nibee;MODE=MYSQL;DB_CLOSE_DELAY=-1");
        jdbcDataSource.setUser("sa");
        jdbcDataSource.setPassword("sa");
        sqlSessionFactory = new SessionFactoryProducer(jdbcDataSource).produce();
        DbMigrator dbMigrator = new DbMigrator(jdbcDataSource);
        dbMigrator.migrateDb();
    }

    protected AbstractRepositoryTest(String... scriptNames) {
        this();
        executeSqlScripts(scriptNames);
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

    protected void executeSqlStatement(String statementString) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {
            statement.execute(statementString);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void executeSqlScripts(String[] scriptNames) {
        Connection connection = null;
        Statement statement = null;
        Scanner scanner = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            connection.setAutoCommit(false);
            for (String scriptName : scriptNames) {
                scanner = new Scanner(
                        this.getClass().getClassLoader().getResourceAsStream(DB_SCRIPT_BASE + scriptName)
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

    private void silentlyClose(AutoCloseable... openResources) {
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
