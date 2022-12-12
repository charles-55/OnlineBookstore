import java.util.ArrayList;

public class User {

    private final int ID;
    private String username;
    private String password;
    private final Basket basket;
    private final ArrayList<Order> orderHistory;
    private final boolean IS_ADMIN;

    public User(int ID, String username, String password) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        basket = new Basket(ID);
        orderHistory = new ArrayList<>();
        IS_ADMIN = false;
    }

    public User(int ID, String username, String password, boolean IS_ADMIN) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        basket = new Basket(ID);
        orderHistory = new ArrayList<>();
        this.IS_ADMIN = IS_ADMIN;
    }

    public int getID(){
        return ID;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Basket getBasket(){
        return basket;
    }

    public ArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    public boolean isAdmin() {
        return IS_ADMIN;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if(password.equals(oldPassword)) {
            password = newPassword;
            return true;
        }
        return false;
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public String getSQLStringRepresentation() {
        return ID + ", '" + username + "', '" + password + "', '" + (IS_ADMIN ? "YES" : "NO") + "'";
    }
}
