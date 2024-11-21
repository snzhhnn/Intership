package repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationRepository {
    private final Connection connection = new PostgresConnection().getConnection();

    public void executeFileMigration(File file) throws IOException, SQLException {
        String sql = Files.readString(Paths.get(file.getPath()));
        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}