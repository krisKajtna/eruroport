import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database connection details
    private static final String PGHOST = "ep-cool-sea-a2dj5p9s.eu-central-1.aws.neon.tech";
    private static final String PGDATABASE = "europort2";
    private static final String PGUSER = "kris.kajtna";
    private static final String PGPASSWORD = "gk3F9qeiwtXD";
    private static final String URL = "jdbc:postgresql://" + PGHOST + "/" + PGDATABASE;

    // Method to establish a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
    }
}
