import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {

    private Connection connection;
    private Statement statement;

    /* JDBC driver name and database URL */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/COMP3005Project";

    /* Database credentials */
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public ConnectionManager() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            statement = connection.createStatement();
        }
        catch(SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean executeQuery(String query) {
        try {
            statement.execute(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
