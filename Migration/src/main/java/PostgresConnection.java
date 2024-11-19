import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnection {
    private Connection connection;
    private static String databaseUser;
    private static String databasePassword;
    private static String databaseURL;

    public Connection getConnection() {
        return connection;
    }

    public PostgresConnection() {
        init();
    }

    public void init() {
//        try {
//            defineConnectionParameters();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/migration", "postgres", "postgres");
            System.out.println("Connection to PostgreSQL database established successfully.");
        } catch (SQLException e) {
            System.err.println(("Connection failed:"));
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void defineConnectionParameters() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/application.properties"));
        databaseUser = "root";
        databasePassword = "root";
        databaseURL = "jdbc:postgresql://localhost:5432/migration";
    }
}