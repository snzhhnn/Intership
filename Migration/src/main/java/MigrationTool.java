
import repository.MigrationRepository;
import repository.SchemaRepository;

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
        new SchemaRepository().createTable();
    }

    public static void readMigrationFile() {
        try {
            new MigrationFileReader(new SchemaRepository(), new MigrationRepository());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}