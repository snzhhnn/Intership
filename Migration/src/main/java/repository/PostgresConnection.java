package repository;

import lombok.Getter;
import utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class PostgresConnection {
    private static PostgresConnection postgresConnection;
    private Connection connection;
    private static String databaseUser;
    private static String databasePassword;
    private static String databaseURL;

    private PostgresConnection() {
        init();
    }

    public static PostgresConnection getInstance() {
        if (postgresConnection == null) {
            postgresConnection = new PostgresConnection();
        }
        return postgresConnection;
    }

    private void init() {
        try {
            defineConnectionParameters();
            connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void defineConnectionParameters() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        databaseUser = propertiesUtils.getProperty("database.user");
        databasePassword = propertiesUtils.getProperty("database.password");
        databaseURL = propertiesUtils.getProperty("database.url");
    }
}