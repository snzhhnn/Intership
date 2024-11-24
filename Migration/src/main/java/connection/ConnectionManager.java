package connection;

import lombok.Getter;
import utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class ConnectionManager {
    private static ConnectionManager connectionManager;
    private Connection connection;
    private static String databaseUser;
    private static String databasePassword;
    private static String databaseURL;
    private static String databaseType;

    private ConnectionManager() {
        init();
    }

    public static ConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new ConnectionManager();
        }
        return connectionManager;
    }

    private void init() {
        defineConnectionParameters();
        try {
            connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
            databaseType = getDatabaseType();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void defineConnectionParameters() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        databaseUser = propertiesUtils.getProperty("database.user");
        databasePassword = propertiesUtils.getProperty("database.password");
        databaseURL = propertiesUtils.getProperty("database.url");
    }

    private String getDatabaseType() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName().toLowerCase();

        if (databaseProductName.contains("postgresql")) {
            return "postgresql";
        } else if (databaseProductName.contains("mysql")) {
            return "mysql";
        } else if (databaseProductName.contains("h2")) {
            return "h2";
        } else {
            return "unknown";
        }
    }


    public static String getCreateTableQuery() {
        return switch (databaseType) {
            case "postgresql" -> "CREATE TABLE IF NOT EXISTS schema_history (" +
                    "id SERIAL PRIMARY KEY, " +
                    "version INT NOT NULL, " +
                    "description VARCHAR(255), " +
                    "script VARCHAR(255) NOT NULL," +
                    "type VARCHAR(255) NOT NULL, " +
                    "checksum varchar(50) NOT NULL," +
                    "installed_by varchar(30) NOT NULL," +
                    "success BOOLEAN NOT NULL" +
                    ");";
            case "mysql" -> "CREATE TABLE IF NOT EXISTS schema_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "version INT NOT NULL, " +
                    "description VARCHAR(255), " +
                    "script VARCHAR(255), " +
                    "type VARCHAR(255) NOT NULL, " +
                    "checksum varchar(50) NOT NULL," +
                    "installed_by varchar(30) NOT NULL," +
                    "success BOOLEAN" +
                    ");";
            case "h2" -> "CREATE TABLE IF NOT EXISTS schema_history (" +
                    "id INT IDENTITY PRIMARY KEY, " +
                    "version INT NOT NULL, " +
                    "description VARCHAR(255), " +
                    "script VARCHAR(255), " +
                    "type VARCHAR(255) NOT NULL," +
                    "checksum varchar(50) NOT NULL," +
                    "installed_by varchar(30) NOT NULL," +
                    "success BOOLEAN NOT NULL" +
                    ");";
            default -> throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        };
    }
}