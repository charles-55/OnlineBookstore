import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.*;

public class StoreModel {

    private final ArrayList<User> users;
    private final ArrayList<Tracker> trackers;
    private final ArrayList<Publisher> publishers;
    private final HashMap<Book, Integer> inventory;
    private final ArrayList<StoreView> views;
    private User currentUser;

    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
    private static final String INVENTORY_FILE = "src/main/java/inventory.json";

    public StoreModel() {
        users = new ArrayList<>();
        trackers = new ArrayList<>();
        publishers = new ArrayList<>();
        inventory = new HashMap<>();
        views = new ArrayList<>();
        users.add(new User(1,"Oyinda", "123"));
    }

    public boolean initialize() {
        try {
            JsonReader reader = Json.createReader(new FileReader(INVENTORY_FILE));
            JsonObject starterObject = reader.readObject();
            JsonArray publishers = starterObject.get("publishers").asJsonArray();
            JsonArray books = starterObject.get("books").asJsonArray();

            for(Object object : publishers) {
                JsonObject jsonObject = (JsonObject) object;
                Publisher publisher = new Publisher(jsonObject.get("name").toString(), jsonObject.get("address").toString(), jsonObject.get("email").toString(), Long.parseLong(jsonObject.get("phoneNumber").toString()), Long.parseLong(jsonObject.get("account").toString()));
                addPublisher(publisher);
            }

            for(Object object : books) {
                JsonObject jsonObject = (JsonObject) object;
                for(Publisher publisher : this.publishers) {
                    if(publisher.getName().equals(jsonObject.get("publisher").toString())) {
                        Book book = new Book(Long.parseLong(jsonObject.get("ISBN").toString()), jsonObject.get("name").toString(), jsonObject.get("author").toString(), publisher, Integer.parseInt(jsonObject.get("pages").toString()), Double.parseDouble(jsonObject.get("price").toString()), Double.parseDouble(jsonObject.get("commission").toString()));
                        addToInventory(book, Integer.parseInt(jsonObject.get("amount").toString()));
                    }
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
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

    public ArrayList<Publisher> getPublishers() {
        return publishers;
    }

    public boolean addUser(String username, String password) {
        User user = new User(users.size() + 1, username, password);
        if(CONNECTION_MANAGER.executeQuery("INSERT INTO Customer VALUES (" + user.getSQLStringRepresentation() + ");")) {
            users.add(user);
            return true;
        }
        return false;
    }

    /*
    public boolean removeUser(User user) {
        if(CONNECTION_MANAGER.executeQuery("DELETE FROM Customer WHERE CustomerID = " + user.getUserID() + ";")) {
            users.add(user);
            return true;
        }
        return false;
    }
     */

    public boolean addTracker(Tracker tracker) {
        if(CONNECTION_MANAGER.executeQuery("INSERT INTO Tracker VALUES (" + tracker.getSQLStringRepresentation() + ");")) {
            trackers.add(tracker);
            return true;
        }
        return false;
    }

    public boolean removeTracker(Tracker tracker) {
        if(CONNECTION_MANAGER.executeQuery("DELETE FROM Tracker WHERE TrackerNum = " + tracker.getTrackingNumber() + ";")) {
            trackers.remove(tracker);
            return true;
        }
        return false;
    }

    public boolean addPublisher(Publisher publisher) {
        if(CONNECTION_MANAGER.executeQuery("INSERT INTO Publisher VALUES (" + publisher.getSQLStringRepresentation() + ");")) {
            publishers.add(publisher);
            return true;
        }
        return false;
    }

    public boolean removePublisher(Publisher publisher) {
        if(CONNECTION_MANAGER.executeQuery("DELETE FROM Publisher WHERE Pname = " + publisher.getName() + ";")) {
            publishers.remove(publisher);
            return true;
        }
        return false;
    }

    public boolean addToInventory(Book book, int amount) {
        if(inventory.get(book) == null) {
            if(CONNECTION_MANAGER.executeQuery("INSERT INTO Book VALUES (" + book.getSQLStringRepresentation() + ", " + amount  + ");")) {
                inventory.put(book, amount);
                return true;
            }
        }
        else {
            int newAmount = inventory.get(book) + amount;
            if(CONNECTION_MANAGER.executeQuery("UPDATE Book SET Amount = " + newAmount  + " WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.put(book, newAmount);
                return true;
            }
        }
        return false;
    }

    public boolean removeFromInventory(Book book, int amount) {
        if(!inventory.containsKey(book))
            return false;
        if(inventory.get(book) == amount) {
            if(CONNECTION_MANAGER.executeQuery("DELETE FROM Book WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.remove(book);
                return true;
            }
        }
        else if(inventory.get(book) > amount) {
            int newAmount = inventory.get(book) - amount;
            if(CONNECTION_MANAGER.executeQuery("UPDATE Book SET Amount = " + newAmount  + " WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.put(book, newAmount);
                return true;
            }
        }
        return false;
    }

    public void addView(StoreView view) {
        views.add(view);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean addToCurrentUserBasket(Book book, int amount) {
        if(currentUser.getBasket().getCart().get(book) == null) {
            if(CONNECTION_MANAGER.executeQuery("INSERT INTO Basket VALUES (" + currentUser.getUserID() + ", " + book.getISBN() + ", " + amount  + ");")) {
                currentUser.getBasket().addBook(book, amount);
                for(StoreView view : views)
                    view.handleMessage("Added to basket.");
            }
        }
        return false;
    }

    public boolean login(String username, String password) {
        for(User user : users) {
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                for(StoreView view : views)
                    view.handleMessage("Login successful");
                return true;
            }
        }
        for(StoreView view : views)
            view.handleMessage("Login failed!");
        return false;
    }

    public void logout() {
        currentUser = null;
        for(StoreView view : views)
            view.handleMessage("You are now logged out.");
    }

    public boolean updateTrackerStatus(int trackingNumber, Tracker.Status status) {
        for(Tracker tracker : trackers) {
            if(tracker.getTrackingNumber() == trackingNumber) {
                if(CONNECTION_MANAGER.executeQuery("UPDATE Tracker SET Status = " + status.toString()  + " WHERE TrackerNum = " + trackingNumber + ";")) {
                    tracker.setStatus(status);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean quit() {
        if(CONNECTION_MANAGER.disconnect()) {
            for(StoreView view : views)
                view.handleMessage("Thanks for shopping with us!");
            return true;
        }
        for(StoreView view : views)
            view.handleMessage("Failed to close connection!");
        return false;
    }
}
