package repository;

import lombok.Getter;
import utils.PropertiesUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class PostgresConnection {
    private Connection connection;
    private static String databaseUser ;
    private static String databasePassword;
    private static String databaseURL;

    public PostgresConnection() {
        init();
    }

    public void init() {
        try {
            defineConnectionParameters();
            connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void defineConnectionParameters() throws IOException {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        databaseUser = propertiesUtils.getProperty("database.user");
        databasePassword = propertiesUtils.getProperty("database.password");
        databaseURL = propertiesUtils.getProperty("database.url");
    }
}