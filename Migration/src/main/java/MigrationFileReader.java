import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class MigrationFileReader {
    public MigrationFileReader() throws SQLException {
        this.init();
    }

    public void init() throws SQLException {
        String shortName = "";
        String prefix = null;
        int version;
        String description = "";
        String type = "";
        int checksum = 0;
        String installed_by = "";
        File[] listOfFiles = readNameFiles();
        Connection connection = new PostgresConnection().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO schema_history(version, description, script, type, checksum, installed_by) VALUES(?,?,?,?,?,?);");
        for (File file: listOfFiles) {
            shortName = file.getName();
            if (shortName.startsWith("V")) {
                prefix = shortName.substring(0 ,1);
            }

            if (prefix == null) {
                throw  new RuntimeException("Invalid Java-based migration class name");
            } else {
                version = Integer.parseInt(extractVersion(shortName));
                description = extractDescription(shortName);
                type = extractType(shortName);
                checksum = file.hashCode();
            }

            preparedStatement.setInt(1, version);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, shortName);
            preparedStatement.setString(4, type);
            preparedStatement.setInt(5, checksum);
            preparedStatement.setString(6, installed_by);
            preparedStatement.execute();

            try {
                String sql = Files.readString(Paths.get(file.getPath()));
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet == null) {
                    break;
                }

                int id = 0;
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
                Statement updateQuery = connection.createStatement();
                updateQuery.executeUpdate("UPDATE schema_history SET success=true where id=" + id);
                statement.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        preparedStatement.close();
    }

    public static File[] readNameFiles() {
        File folder = new File("C:\\Users\\snzhnn\\Desktop\\Intership\\Migration\\src\\main\\resources\\db\\migration");
        return folder.listFiles();
    }

    public String extractVersion(String shortName) {
        return shortName.substring(1, 2);
    }

    public String extractDescription(String shortName) {
        int lastIndex = shortName.indexOf(".");
        String description = shortName.substring(4, lastIndex);
        return description.replaceAll("_", " ");
    }

    public String extractType(String shortName) {
        int firstIndex = shortName.indexOf(".");
        return shortName.substring(firstIndex, shortName.length() - 1);
    }
}