import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ConnectionManager {

    private Connection connection;
    private Statement statement;
    private static final String DDL = "C:\\Users\\carlo\\Desktop\\Charles\\Fall 2022\\COMP 3005\\Project\\SQL Files\\DDL.sql";
    private static final String DROP_TABLES = "C:\\Users\\carlo\\Desktop\\Charles\\Fall 2022\\COMP 3005\\Project\\SQL Files\\drop_tables.sql";

    /* JDBC driver name and database URL */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/COMP3005Project";

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

    private static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines)
                sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
    public boolean initializeDatabase() {
        return executeQuery(readFile(DDL));
    }

    public boolean cleanDatabase() {
        return executeQuery(readFile(DROP_TABLES));
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

    public boolean disconnect() {
        try {
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
