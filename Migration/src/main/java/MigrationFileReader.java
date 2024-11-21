import exception.InvalidClassNameException;
import exception.MigrationExecutedException;
import lombok.extern.slf4j.Slf4j;
import utils.ExtractUtils;
import utils.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class MigrationFileReader {

    Connection connection = new PostgresConnection().getConnection();
    private static final Logger log = Logger.getLogger(MigrationFileReader.class.getName());

    public MigrationFileReader() throws SQLException {
        this.init();
    }

    public void init() throws SQLException {
        String shortName = "";
        String prefix = null;
        int version;
        String description = "";
        String type = "";
        String installed_by = "";
        File[] listOfFiles = readNameFiles();
        for (int i=0; i <= listOfFiles.length-1;i++) {
            shortName = listOfFiles[i].getName();
            if (shortName.startsWith("V")) {
                prefix = shortName.substring(0, 1);
            }

            if (prefix == null) throw new InvalidClassNameException();

            version = Integer.parseInt(ExtractUtils.extractVersion(shortName));
            description = ExtractUtils.extractDescription(shortName);
            type = ExtractUtils.extractType(shortName);

            String checksum;
            try {
                checksum = checksum(listOfFiles[i]);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            if (compareSum(checksum, shortName)) {
                log.warning("migration was executed");
                break;
            }

            PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO schema_history(version, description, script, type, checksum) VALUES(?,?,?,?,?);");
                preparedStatement.setInt(1, version);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, shortName);
                preparedStatement.setString(4, type);
                preparedStatement.setString(5, checksum);
                preparedStatement.execute();

            try {
                Statement statement = connection.createStatement();
                log.info("start execute migration");
                boolean isResultSet = executeFileMigration(listOfFiles[i], statement);
                updateSuccessAboutMigration(shortName, !isResultSet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean compareSum(String checksum1, String shortName) throws SQLException {
        String savedChecksum;
        boolean isEqualsSum = false;
        PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * from schema_history where script=?");
        preparedStatement1.setString(1, shortName);
        ResultSet resultSet = preparedStatement1.executeQuery();
        while (resultSet.next()) {
            savedChecksum = resultSet.getString("checksum");
            isEqualsSum = checksum1.equals(savedChecksum);
        }
        return isEqualsSum;
    }

    private String checksum(File file) throws IOException {
        Path path = file.toPath();
        try (InputStream is = Files.newInputStream(path)) {
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        }
    }

    private static File[] readNameFiles() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        File folder = new File(propertiesUtils.getProperty("catalog.migration.name"));
        return folder.listFiles();
    }

    private boolean executeFileMigration(File file, Statement statement) throws IOException {
        String sql = Files.readString(Paths.get(file.getPath()));
        try {
            return statement.execute(sql);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private void updateSuccessAboutMigration(String shortName, boolean isResultSet) throws SQLException {
        PreparedStatement updateQuery = connection.prepareStatement("UPDATE schema_history SET success=? where script=?");
        updateQuery.setBoolean(1, isResultSet);
        updateQuery.setString(2, shortName);
        updateQuery.executeUpdate();
        updateQuery.close();
    }
}