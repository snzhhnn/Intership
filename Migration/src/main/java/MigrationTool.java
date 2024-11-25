
import migration.MigrationExecutor;
import migration.MigrationFileReader;
import repository.MigrationRepository;

import java.sql.SQLException;
public class MigrationTool {

    public static void start() {
        createDefaultSchema();
        readMigrationFile();
    }

    private static void createDefaultSchema() {
        new MigrationRepository().createTable();
    }

    private static void readMigrationFile() {
        try {
            new MigrationFileReader(new MigrationRepository(), new MigrationExecutor());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}