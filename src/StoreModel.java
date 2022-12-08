import java.util.ArrayList;
import java.util.HashMap;

public class StoreModel {

    private final ArrayList<User> users;
    private final ArrayList<Tracker> trackers;
    private final HashMap<Book, Integer> inventory;
    private final ArrayList<StoreView> views;
    private final ConnectionManager connectionManger;

    public StoreModel() {
        users = new ArrayList<>();
        trackers = new ArrayList<>();
        inventory = new HashMap<>();
        views = new ArrayList<>();
        connectionManger = new ConnectionManager();
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

    public boolean addUser(User user) {
        if(connectionManger.executeQuery("INSERT INTO Customer VALUES (" + user.getSQLStringRepresentation() + ");")) {
            users.add(user);
            return true;
        }
        return false;
    }

    public boolean removeUser(User user) {
        if(connectionManger.executeQuery("DELETE FROM Customer WHERE CustomerID = " + user.getUserID() + ";")) {
            users.add(user);
            return true;
        }
        return false;
    }

    public boolean addTracker(Tracker tracker) {
        if(connectionManger.executeQuery("INSERT INTO Tracker VALUES (" + tracker.getSQLStringRepresentation() + ");")) {
            trackers.add(tracker);
            return true;
        }
        return false;
    }

    public boolean removeTracker(Tracker tracker) {
        if(connectionManger.executeQuery("DELETE FROM Tracker WHERE TrackerNum = " + tracker.getTrackingNumber() + ";")) {
            trackers.add(tracker);
            return true;
        }
        return false;
    }

    public boolean addToInventory(Book book, int amount) {
        if(inventory.get(book) == null) {
            if(connectionManger.executeQuery("INSERT INTO Book VALUES (" + book.getSQLStringRepresentation() + ", " + amount  + ");")) {
                inventory.put(book, amount);
                return true;
            }
        }
        else {
            int newAmount = inventory.get(book) + amount;
            if(connectionManger.executeQuery("UPDATE Book SET Amount = " + newAmount  + " WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.put(book, newAmount);
                return true;
            }
        }
        return false;
    }

    public boolean removeToInventory(Book book, int amount) {
        if(!inventory.containsKey(book))
            return false;
        if(inventory.get(book) == amount) {
            if(connectionManger.executeQuery("DELETE FROM Book WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.remove(book);
                return true;
            }
        }
        else if(inventory.get(book) > amount) {
            int newAmount = inventory.get(book) - amount;
            if(connectionManger.executeQuery("UPDATE Book SET Amount = " + newAmount  + " WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.put(book, newAmount);
                return true;
            }
        }
        return false;
    }

    public void addView(StoreView view) {
        views.add(view);
    }

    public void quit() {
        connectionManger.disconnect();
    }
}
