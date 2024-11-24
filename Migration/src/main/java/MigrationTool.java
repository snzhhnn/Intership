
import migration.MigrationExecutor;
import migration.MigrationFileReader;
import repository.MigrationRepository;

import java.sql.SQLException;
public class MigrationTool {
    public static void main(String[] args) {
        start();
    }

    public static void start() {
        createDefaultSchema();
        readMigrationFile();
    }

    public static void createDefaultSchema() {
        new MigrationRepository().createTable();
    }

    public static void readMigrationFile() {
        try {
            new MigrationFileReader(new MigrationRepository(), new MigrationExecutor());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}