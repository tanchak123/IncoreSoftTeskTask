package utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dto.EmployeeDto;
import service.EmployeeService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnection {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {

        Properties dbProperties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/postgres.props")) {
            dbProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.setJdbcUrl(dbProperties.getProperty("url"));
        config.setUsername(dbProperties.getProperty("user"));
        config.setPassword(dbProperties.getProperty("password"));
        config.setDriverClassName(dbProperties.getProperty("driver"));
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find Postgressql Driver", e);
        }
    }
    static int count = 0;
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection  = ds.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }
}