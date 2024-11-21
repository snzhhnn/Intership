
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationTool {
    public static void main(String[] args) throws SQLException {
            start();
    }

    public static void start() throws SQLException {
        createDefaultSchema();
        readMigrationFile();
    }

    public static void createDefaultSchema() throws SQLException{
        PostgresConnection postgresConnection = new PostgresConnection();
        Connection connection = postgresConnection.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS schema_history (" +
                "id serial PRIMARY KEY , " +
                "version int not null," +
                "description varchar not null," +
                "script varchar not null, " +
                "type varchar not null, " +
                "checksum varchar not null, " +
                "installed_by varchar," +
                "success boolean)");
        statement.close();
    }

    public static void readMigrationFile() {
        try {
            new MigrationFileReader();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}