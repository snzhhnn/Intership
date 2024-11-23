package repository;

import model.Schema;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchemaRepository {
    private final Connection connection = PostgresConnection.getInstance().getConnection();

    public void createTable()  {
        try {
            Statement statement  = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS schema_history (" +
                    "id serial PRIMARY KEY , " +
                    "version int not null," +
                    "description varchar not null," +
                    "script varchar(255) not null, " +
                    "type varchar not null, " +
                    "checksum varchar not null, " +
                    "installed_by varchar," +
                    "success boolean) ");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Schema schema) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO schema_history(version, description, script," +
                            " type, checksum, installed_by, success) " +
                            "VALUES(?,?,?,?,?,?,?);");
            preparedStatement.setInt(1, schema.getVersion());
            preparedStatement.setString(2, schema.getDescription());
            preparedStatement.setString(3, schema.getScript());
            preparedStatement.setString(4, schema.getType());
            preparedStatement.setString(5, schema.getChecksum());
            preparedStatement.setString(6, "postgresql");
            preparedStatement.setBoolean(7, false);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Schema schema) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE schema_history " +
                            "SET success = ? " +
                            "WHERE script = ?;");
            preparedStatement.setBoolean(1, schema.getSuccess());
            preparedStatement.setString(2, schema.getScript());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Schema getMigrationByScript(String script) {
        Schema schema = new Schema();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from schema_history where script=?");
            preparedStatement.setString(1, script);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                schema.setId(resultSet.getInt("id"));
                schema.setVersion(resultSet.getInt("version"));
                schema.setDescription(resultSet.getString("description"));
                schema.setScript(resultSet.getString("script"));
                schema.setType(resultSet.getString("type"));
                schema.setChecksum(resultSet.getString("checksum"));
                schema.setInstalledBy(resultSet.getString("installed_by"));
                schema.setSuccess(resultSet.getBoolean("success"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return schema;
    }
}