import java.util.ArrayList;
import java.util.HashMap;

public class StoreModel {

    private final ArrayList<User> users;
    private final ArrayList<Tracker> trackers;
    private final HashMap<Book, Integer> inventory;
    private final ArrayList<StoreView> views;
    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
    private User currentUser;

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

    public boolean addUser(User user) {
        if(CONNECTION_MANAGER.executeQuery("INSERT INTO Customer VALUES (" + user.getSQLStringRepresentation() + ");")) {
            users.add(user);
            return true;
        }
        return false;
    }

    public boolean removeUser(User user) {
        if(CONNECTION_MANAGER.executeQuery("DELETE FROM Customer WHERE CustomerID = " + user.getUserID() + ";")) {
            users.add(user);
            return true;
        }
        return false;
    }

    public boolean addTracker(Tracker tracker) {
        if(CONNECTION_MANAGER.executeQuery("INSERT INTO Tracker VALUES (" + tracker.getSQLStringRepresentation() + ");")) {
            trackers.add(tracker);
            return true;
        }
        return false;
    }

    public boolean removeTracker(Tracker tracker) {
        if(CONNECTION_MANAGER.executeQuery("DELETE FROM Tracker WHERE TrackerNum = " + tracker.getTrackingNumber() + ";")) {
            trackers.add(tracker);
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
