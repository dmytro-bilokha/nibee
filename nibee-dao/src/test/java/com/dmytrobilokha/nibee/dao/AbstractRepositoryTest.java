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

public abstract class AbstractRepositoryTest {

    private final DataSource dataSource;
    private final SqlSessionFactory sqlSessionFactory;

    private SqlSession sqlSession;

    public AbstractRepositoryTest() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        dataSource = jdbcDataSource;
        jdbcDataSource.setUrl("jdbc:h2:mem:nibee;MODE=MYSQL;DB_CLOSE_DELAY=-1");
        jdbcDataSource.setUser("sa");
        jdbcDataSource.setPassword("sa");
        sqlSessionFactory = new SessionFactoryProducer(jdbcDataSource).produce();
        DbMigrator dbMigrator = new DbMigrator(jdbcDataSource);
        dbMigrator.migrateDb();
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

}
