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
}
