import java.util.ArrayList;
import java.util.HashMap;

public class StoreModel {

    private final ArrayList<User> users;
    private final ArrayList<Tracker> trackers;
    private final ArrayList<Publisher> publishers;
    private final HashMap<Book, Integer> inventory;
    private final ArrayList<StoreView> views;
    private User currentUser;
    private int newOrderNumber;
    private int newTrackingNumber;
    private final ConnectionManager CONNECTION_MANAGER;

    public StoreModel() {
        users = new ArrayList<>();
        trackers = new ArrayList<>();
        publishers = new ArrayList<>();
        inventory = new HashMap<>();
        views = new ArrayList<>();
        newOrderNumber = 1;
        newTrackingNumber = 1;
        CONNECTION_MANAGER = new ConnectionManager(this);
        initialize();
    }

    public void initialize() {
        if(!CONNECTION_MANAGER.initializeDatabase()) {
            for(StoreView view : views)
                view.handleMessage("Inventory failed to initialize!");
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Tracker> getTrackers() {
        return trackers;
    }

    public ArrayList<Publisher> getPublishers() {
        return publishers;
    }

    public HashMap<Book, Integer> getInventory() {
        return inventory;
    }

    public ConnectionManager getCONNECTION_MANAGER() {
        return CONNECTION_MANAGER;
    }

    public boolean addUser(String username, String password) {
        User user = new User(users.size() + 1, username, password);
        if(CONNECTION_MANAGER.executeQuery("INSERT INTO Customer VALUES (" + user.getSQLStringRepresentation() + ");")) {
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
            if(currentUser.getBasket().addBook(book, amount)) {
                if(CONNECTION_MANAGER.executeQuery("INSERT INTO Basket VALUES (" + currentUser.getUserID() + ", " + book.getISBN() + ", " + amount + ");")) {
                    for(StoreView view : views)
                        view.handleMessage("Added to basket.");
                    return true;
                }
                else
                    currentUser.getBasket().removeBook(book, amount);
            }
        }
        else {
            if(currentUser.getBasket().addBook(book, amount)) {
                if(CONNECTION_MANAGER.executeQuery("UPDATE Basket SET Amount = " + amount + " WHERE ISBN = " + book.getISBN() +";")) {
                    for(StoreView view : views)
                        view.handleMessage("Added to basket.");
                    return true;
                }
                else
                    currentUser.getBasket().removeBook(book, amount);
            }
        }
        for(StoreView view : views)
            view.handleMessage("Failed to add to basket.");
        return false;
    }

    public boolean removeFromCurrentUserBasket(Book book, int amount) {
        if(currentUser.getBasket().getCart().get(book) == amount) {
            if(currentUser.getBasket().removeBook(book, amount)) {
                if(CONNECTION_MANAGER.executeQuery("DELETE FROM Basket WHERE ISBN = " + book.getISBN() +";")) {
                    for(StoreView view : views)
                        view.handleMessage("Removed from basket.");
                    return true;
                }
                else
                    currentUser.getBasket().addBook(book, amount);
            }
        }
        if(currentUser.getBasket().removeBook(book, amount)) {
            if(CONNECTION_MANAGER.executeQuery("UPDATE Basket SET Amount = " + amount + " WHERE ISBN = " + book.getISBN() +";")) {
                for(StoreView view : views)
                    view.handleMessage("Removed from basket.");
                return true;
            }
            else
                currentUser.getBasket().addBook(book, amount);
        }
        for(StoreView view : views)
            view.handleMessage("Failed to remove from basket.");
        return false;
    }

    public int getNewOrderNumber() {
        newOrderNumber++;
        return newOrderNumber - 1;
    }

    public int getNewTrackingNumber() {
        newTrackingNumber++;
        return newTrackingNumber - 1;
    }

    public boolean processOrder(Order order) {
        for (String s : order.getSQLStringRepresentation()) {
            if (!CONNECTION_MANAGER.executeQuery("INSERT INTO BookOrder VALUES (" + s + ");")) {
                currentUser.getBasket().undoCheckOut(order);
                return false;
            }
        }
        for (Book book : order.getBasket().keySet()) {
            if (!removeFromInventory(book, order.getBasket().get(book))) {
                currentUser.getBasket().undoCheckOut(order);
                return false;
            }
        }
        currentUser.addOrder(order);
        trackers.add(new Tracker(getNewTrackingNumber(), currentUser, order.getOrderNumber()));
        return true;
    }

    public String getOrderStatus(Order order) {
        for(Tracker tracker : trackers) {
            if(tracker.getOrderNum() == order.getOrderNumber())
                return tracker.getStatus().toString();
        }
        return "Order not found.";
    }

    public void updateOrderStatus(Tracker tracker, Tracker.Status status) {
        tracker.setStatus(status);
    }

    public boolean signIn(String username, String password) {
        for(User user : users) {
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void signOut() {
        currentUser = null;
    }

    public boolean changeCurrentUserPassword(String oldPassword, String newPassword) {
        if(currentUser.changePassword(oldPassword, newPassword)) {
            if(CONNECTION_MANAGER.executeQuery("UPDATE Customer SET Pword = '" + newPassword  + "' WHERE CustomerID = " + currentUser.getUserID() + ";")) {
                return true;
            }
            currentUser.changePassword(newPassword, oldPassword);
        }
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
