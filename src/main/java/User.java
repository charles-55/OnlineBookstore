import java.util.ArrayList;

public class User {

    private final int userID;
    private String username;
    private String password;
    private final Basket basket;
    private final ArrayList<Order> orderHistory;

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.basket = new Basket(userID);
        orderHistory = new ArrayList<>();
    }

    public int getUserID(){
        return userID;
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
        return userID + ", '" + username + "', '" + password + "'";
    }
}
