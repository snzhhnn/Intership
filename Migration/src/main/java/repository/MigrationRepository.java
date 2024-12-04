package repository;

import connection.ConnectionManager;
import model.Migration;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MigrationRepository {
    private final Connection connection = ConnectionManager.getInstance().getConnection();

    public void createTable()  {
        try {
            Statement statement  = connection.createStatement();
            statement.execute(ConnectionManager.getCreateTableQuery());
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Migration migration) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO schema_history(version, description, script," +
                            " type, checksum, installed_by, success) " +
                            "VALUES(?,?,?,?,?,?,?);");
            preparedStatement.setInt(1, migration.getVersion());
            preparedStatement.setString(2, migration.getDescription());
            preparedStatement.setString(3, migration.getScript());
            preparedStatement.setString(4, migration.getType());
            preparedStatement.setString(5, migration.getChecksum());
            preparedStatement.setString(6, "postgresql");
            preparedStatement.setBoolean(7, false);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Migration migration) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE schema_history " +
                            "SET success = ? " +
                            "WHERE script = ?;");
            preparedStatement.setBoolean(1, migration.getSuccess());
            preparedStatement.setString(2, migration.getScript());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Migration getMigrationByScript(String script) {
        Migration migration = new Migration();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from schema_history where script=?");
            preparedStatement.setString(1, script);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                migration.setId(resultSet.getInt("id"));
                migration.setVersion(resultSet.getInt("version"));
                migration.setDescription(resultSet.getString("description"));
                migration.setScript(resultSet.getString("script"));
                migration.setType(resultSet.getString("type"));
                migration.setChecksum(resultSet.getString("checksum"));
                migration.setInstalledBy(resultSet.getString("installed_by"));
                migration.setSuccess(resultSet.getBoolean("success"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return migration;
    }
}