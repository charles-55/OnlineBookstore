import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class ConnectionManager {

    private final StoreModel model;
    private Connection connection;
    private Statement statement;

    /* SQL and Inventory files */
    private static final String DDL = "SQL Files/DDL.sql";
    private static final String DROP_TABLES = "SQL Files/drop_tables.sql";
    private static final String INVENTORY_FILE = "src/main/java/inventory.json";

    /* JDBC driver name and database URL */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/COMP3005Project";

    /* Database credentials */
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public ConnectionManager(StoreModel model) {
        this.model = model;

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
        if(execute(readFile(DDL))) {
            try {
                JsonReader reader = Json.createReader(new FileReader(INVENTORY_FILE));
                JsonObject starterObject = reader.readObject();
                JsonArray publishers = starterObject.get("publishers").asJsonArray();
                JsonArray books = starterObject.get("books").asJsonArray();

                for(Object object : publishers) {
                    JsonObject jsonObject = (JsonObject) object;
                    Publisher publisher = new Publisher(jsonObject.get("name").toString(), jsonObject.get("address").toString(), jsonObject.get("email").toString(), Long.parseLong(jsonObject.get("phoneNumber").toString()), Long.parseLong(jsonObject.get("account").toString()));
                    model.addPublisher(publisher);
                }

                for(Object object : books) {
                    JsonObject jsonObject = (JsonObject) object;
                    for(Publisher publisher : model.getPublishers()) {
                        if(publisher.getName().equals(jsonObject.get("publisher").toString())) {
                            Book.Genre genre = Book.Genre.UNKNOWN;
                            for(Book.Genre g : Book.Genre.values()) {
                                if(g.toString().equals(jsonObject.get("genre").toString())) {
                                    genre = g;
                                    break;
                                }
                            }
                            Book book = new Book(Long.parseLong(jsonObject.get("ISBN").toString()), jsonObject.get("name").toString(), jsonObject.get("author").toString(), publisher, genre, Integer.parseInt(jsonObject.get("pages").toString()), Double.parseDouble(jsonObject.get("price").toString()), Double.parseDouble(jsonObject.get("commission").toString()));
                            model.addToInventory(book, Integer.parseInt(jsonObject.get("amount").toString()));
                        }
                    }
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean cleanDatabase() {
        return execute(readFile(DROP_TABLES));
    }

    public boolean execute(String query) {
        try {
            statement.execute(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean disconnect() {
        try {
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
