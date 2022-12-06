import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;

public class StoreModel {

    private final ArrayList<User> users;
    private final ArrayList<Tracker> trackers;
    private final HashMap<Book, Integer> inventory;
    private final ArrayList<StoreView> views;

    /* JDBC driver name and database URL */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/COMP3005Project";

    /* Database credentials */
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public StoreModel() {
        users = new ArrayList<>();
        trackers = new ArrayList<>();
        inventory = new HashMap<>();
        views = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Tracker> getTrackers() {
        return trackers;
    }

    public HashMap<Book, Integer> getInventory() {
        return inventory;
    }

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connection to a database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            System.out.println("Executing SQL query...");
            statement = connection.createStatement();

            String sql = "";
            statement.execute(sql);
            System.out.println("Query executed successfully!");
            statement.close();
            connection.close();
        }
        catch(SQLException | ClassNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
