import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
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
    private String CONSOLE;

    public StoreModel() {
        users = new ArrayList<>();
        trackers = new ArrayList<>();
        publishers = new ArrayList<>();
        inventory = new HashMap<>();
        views = new ArrayList<>();
        newOrderNumber = 1;
        newTrackingNumber = 1;
        CONNECTION_MANAGER = new ConnectionManager(this);
        CONSOLE = "";
        initialize();
    }

    public void initialize() {
        if(!CONNECTION_MANAGER.initializeDatabase()) {
            for(StoreView view : views)
                view.handleMessage("Inventory failed to initialize!");
        }
    }

    public void updateConsole(String s) {
        CONSOLE += s + "\n";
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

    public boolean addUser(String username, String password, boolean isAdmin) {
        User user;
        if(isAdmin)
            user = new Admin(users.size() + 1, username, password);
        else
            user = new User(users.size() + 1, username, password);
        if(CONNECTION_MANAGER.execute("INSERT INTO Customer VALUES (" + user.getSQLStringRepresentation() + ");")) {
            users.add(user);
            return true;
        }
        return false;
    }

    public boolean addTracker(Tracker tracker) {
        if(CONNECTION_MANAGER.execute("INSERT INTO Tracker VALUES (" + tracker.getSQLStringRepresentation() + ");")) {
            trackers.add(tracker);
            return true;
        }
        return false;
    }

    public boolean removeTracker(Tracker tracker) {
        if(CONNECTION_MANAGER.execute("DELETE FROM Tracker WHERE TrackerNum = " + tracker.getTrackingNumber() + ";")) {
            trackers.remove(tracker);
            return true;
        }
        return false;
    }

    public boolean addPublisher(Publisher publisher) {
        if(CONNECTION_MANAGER.execute("INSERT INTO Publisher VALUES (" + publisher.getSQLStringRepresentation() + ");")) {
            publishers.add(publisher);
            return true;
        }
        return false;
    }

    public boolean removePublisher(Publisher publisher) {
        if(CONNECTION_MANAGER.execute("DELETE FROM Publisher WHERE Pname = " + publisher.getName() + ";")) {
            publishers.remove(publisher);
            return true;
        }
        return false;
    }

    public boolean addToInventory(Book book, int amount) {
        if(inventory.get(book) == null) {
            if(CONNECTION_MANAGER.execute("INSERT INTO Book VALUES (" + book.getSQLStringRepresentation() + ", " + amount  + ");")) {
                inventory.put(book, amount);
                return true;
            }
        }
        else {
            int newAmount = inventory.get(book) + amount;
            if(CONNECTION_MANAGER.execute("UPDATE Book SET Amount = " + newAmount  + " WHERE ISBN = " + book.getISBN() + ";")) {
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
            if(CONNECTION_MANAGER.execute("DELETE FROM Book WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.remove(book);
                autoPlaceBookOrder(book);
                return true;
            }
        }
        else if(inventory.get(book) > amount) {
            int newAmount = inventory.get(book) - amount;
            if(CONNECTION_MANAGER.execute("UPDATE Book SET Amount = " + newAmount  + " WHERE ISBN = " + book.getISBN() + ";")) {
                inventory.put(book, newAmount);
                if(inventory.get(book) < 10)
                    autoPlaceBookOrder(book);
                return true;
            }
        }
        return false;
    }

    private void autoPlaceBookOrder(Book book) {
        /* email publishers */
        int newAmount = 100;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            if(calendar.get(Calendar.MONTH) == Calendar.DECEMBER)
                calendar.add(Calendar.YEAR, -1);
            else
                calendar.add(Calendar.YEAR, 0);
            ResultSet resultSet = CONNECTION_MANAGER.executeQuery("SELECT Amount FROM MonthlyBookSales WHERE MonthOfSale = " + calendar.get(Calendar.MONTH) + " AND YearOfSale = " + calendar.get(Calendar.YEAR) + " ISBN = " + book.getISBN() + ";");
            if(resultSet.next())
                newAmount = resultSet.getInt("Amount");
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!addToInventory(book, newAmount))
            updateConsole("Running short on book " + book.getISBN() + ". Could not automatically place order!");
    }

    public void addView(StoreView view) {
        views.add(view);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addToCurrentUserBasket(Book book, int amount) {
        if(currentUser.getBasket().getCart().get(book) == null) {
            if(currentUser.getBasket().addBook(book, amount)) {
                if(CONNECTION_MANAGER.execute("INSERT INTO Basket VALUES (" + currentUser.getID() + ", " + book.getISBN() + ", " + amount + ");")) {
                    for(StoreView view : views)
                        view.handleMessage("Added to basket.");
                    return;
                }
                else
                    currentUser.getBasket().removeBook(book, amount);
            }
        }
        else {
            if(currentUser.getBasket().addBook(book, amount)) {
                if(CONNECTION_MANAGER.execute("UPDATE Basket SET Amount = " + amount + " WHERE ISBN = " + book.getISBN() +";")) {
                    for(StoreView view : views)
                        view.handleMessage("Added to basket.");
                    return;
                }
                else
                    currentUser.getBasket().removeBook(book, amount);
            }
        }
        for(StoreView view : views)
            view.handleMessage("Failed to add to basket.");
    }

    public boolean removeFromCurrentUserBasket(Book book, int amount) {
        if(currentUser.getBasket().getCart().get(book) == amount) {
            if(currentUser.getBasket().removeBook(book, amount)) {
                if(CONNECTION_MANAGER.execute("DELETE FROM Basket WHERE ISBN = " + book.getISBN() +";")) {
                    for(StoreView view : views)
                        view.handleMessage("Removed from basket.");
                    return true;
                }
                else
                    currentUser.getBasket().addBook(book, amount);
            }
        }
        if(currentUser.getBasket().removeBook(book, amount)) {
            if(CONNECTION_MANAGER.execute("UPDATE Basket SET Amount = " + amount + " WHERE ISBN = " + book.getISBN() +";")) {
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

    public HashMap<Book, Integer> search(String search, String criteria, ArrayList<Book.Genre> genres) {
        HashMap<Book, Integer> result = new HashMap<>();
        search = search.toUpperCase();

        switch (criteria) {
            case "book":
                try {
                    StringBuilder query;
                    if (genres.isEmpty())
                        query = new StringBuilder("SELECT ISBN FROM Book WHERE Bname = '" + search + "'");
                    else {
                        query = new StringBuilder("SELECT ISBN FROM Book WHERE Bname = '" + search + "' AND Genre IN ('UNKNOWN', '");
                        for (int i = 0; i < genres.size() - 1; i++)
                            query.append(genres.get(i).toString()).append("', '");
                        query.append(genres.get(genres.size() - 1).toString()).append("');");
                    }
                    ResultSet resultSet = CONNECTION_MANAGER.executeQuery(query.toString());
                    while (resultSet.next()) {
                        long isbn = resultSet.getLong("ISBN");
                        for(Book book : inventory.keySet()) {
                            if(book.getISBN() == isbn)
                                result.put(book, inventory.get(book));
                        }
                    }
                    resultSet.close();
                    return result;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "author":
                try {
                    StringBuilder query;
                    if (genres.isEmpty())
                        query = new StringBuilder("SELECT ISBN FROM Book WHERE AuthorName = '" + search + "'");
                    else {
                        query = new StringBuilder("SELECT ISBN FROM Book WHERE AuthorName = '" + search + "' AND Genre IN ('UNKNOWN', '");
                        for (int i = 0; i < genres.size() - 1; i++)
                            query.append(genres.get(i).toString()).append("', '");
                        query.append(genres.get(genres.size() - 1).toString()).append("');");
                    }
                    ResultSet resultSet = CONNECTION_MANAGER.executeQuery(query.toString());
                    while (resultSet.next()) {
                        long isbn = resultSet.getLong("ISBN");
                        for(Book book : inventory.keySet()) {
                            if(book.getISBN() == isbn)
                                result.put(book, inventory.get(book));
                        }
                    }
                    resultSet.close();
                    return result;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "isbn":
                try {
                    StringBuilder query;
                    if (genres.isEmpty())
                        query = new StringBuilder("SELECT ISBN FROM Book WHERE ISBN = " + search + "");
                    else {
                        query = new StringBuilder("SELECT ISBN FROM Book WHERE ISBN = " + search + " AND Genre IN ('UNKNOWN', '");
                        for (int i = 0; i < genres.size() - 1; i++)
                            query.append(genres.get(i).toString()).append("', '");
                        query.append(genres.get(genres.size() - 1).toString()).append("');");
                    }
                    ResultSet resultSet = CONNECTION_MANAGER.executeQuery(query.toString());
                    while (resultSet.next()) {
                        long isbn = resultSet.getLong("ISBN");
                        for(Book book : inventory.keySet()) {
                            if(book.getISBN() == isbn)
                                result.put(book, inventory.get(book));
                        }
                    }
                    resultSet.close();
                    return result;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

        if(result.isEmpty()) {
            for(StoreView view : views)
                view.handleMessage("Search not found. Showing all books.");
        }
        return inventory;
    }

    public int getNewOrderNumber() {
        newOrderNumber++;
        return newOrderNumber - 1;
    }

    public int getNewTrackingNumber() {
        newTrackingNumber++;
        return newTrackingNumber - 1;
    }

    public boolean addBillingInfo(BillingInfo billingInfo) {
        return (CONNECTION_MANAGER.execute("INSERT INTO BillingInfo VALUES (" + billingInfo.getSQLRepresentation() + ");"));
    }

    public boolean processOrder(Order order, BillingInfo billingInfo) {
        if(!addBillingInfo(billingInfo))
            return false;

        for(String s : order.getSQLStringRepresentation()) {
            if (!CONNECTION_MANAGER.execute("INSERT INTO BookOrder VALUES (" + s + ");")) {
                currentUser.getBasket().undoCheckOut(order);
                return false;
            }
        }
        for(Book book : order.getBasket().keySet()) {
            if(!CONNECTION_MANAGER.execute("DELETE FROM Basket WHERE ISBN = " + book.getISBN() + ";")) {
                CONNECTION_MANAGER.execute("DELETE FROM BookOrder WHERE ISBN = " + book.getISBN() + ";");
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

    public boolean updateOrderStatus(int trackingNumber, Tracker.Status status) {
        if(CONNECTION_MANAGER.execute("UPDATE Tracker SET Status = '" + status.toString() + "' WHERE TrackerNum = " + trackingNumber + ";")) {
            for(Tracker tracker : trackers) {
                if(tracker.getTrackingNumber() == trackingNumber) {
                    tracker.setStatus(status);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean signIn(String username, String password, boolean isAdmin) {
        try {
            String query = "SELECT CustomerID FROM Customer WHERE Username = '" + username + "' AND Pword = '" + password + "'";
            if(isAdmin)
                query += " AND Admin = 'YES'";
            else
                query += " AND Admin = 'NO'";
            ResultSet resultSet = CONNECTION_MANAGER.executeQuery(query);
            resultSet.next();
            int userID = resultSet.getInt("CustomerID");
            for (User user : users) {
                if(user.getID() == userID) {
                    currentUser = user;
                    resultSet.close();
                    return true;
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void signOut() {
        currentUser = null;
    }

    public boolean changeCurrentUserPassword(String oldPassword, String newPassword) {
        if(currentUser.changePassword(oldPassword, newPassword)) {
            if(CONNECTION_MANAGER.execute("UPDATE Customer SET Pword = '" + newPassword  + "' WHERE CustomerID = " + currentUser.getID() + ";")) {
                return true;
            }
            currentUser.changePassword(newPassword, oldPassword);
        }
        return false;
    }

    public boolean updateTrackerStatus(int trackingNumber, Tracker.Status status) {
        for(Tracker tracker : trackers) {
            if(tracker.getTrackingNumber() == trackingNumber) {
                if(CONNECTION_MANAGER.execute("UPDATE Tracker SET Status = " + status.toString()  + " WHERE TrackerNum = " + trackingNumber + ";")) {
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
