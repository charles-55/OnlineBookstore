import java.util.ArrayList;
import java.util.HashMap;

public class Basket {

    private final int userID;
    private final HashMap<Book, Integer> cart;

    public Basket(int userID) {
        this.userID = userID;
        cart = new HashMap<>();
    }

    public int getUserID() {
        return userID;
    }

    public HashMap<Book, Integer> getCart() {
        return cart;
    }

    public boolean addBook(Book book, int amount) {
        if(amount < 1)
            return false;
        cart.put(book, ((cart.get(book) == null) ? 0 : cart.get(book)) + amount);
        return true;
    }

    public boolean removeBook(Book book, int amount) {
        if(!cart.containsKey(book))
            return false;
        if(cart.get(book) == amount)
            cart.remove(book);
        else if(cart.get(book) > amount)
            cart.put(book, cart.get(book) - amount);
        else return amount <= cart.get(book);
        return true;
    }

    public Order checkOut(int orderNum, double totalPrice, BillingInfo billingInfo, String shippingInfo) {
        Order order = new Order(orderNum, userID, cart, totalPrice, billingInfo, shippingInfo);
        cart.clear();
        return order;
    }

    public void undoCheckOut(Order order) {
        cart.putAll(order.getBasket());
    }

    public ArrayList<String> getSQLStringRepresentation() {
        ArrayList<String> sqlStringRepresentations = new ArrayList<>();

        for(Book book : cart.keySet())
            sqlStringRepresentations.add(userID + ", " + book.getISBN() + ", " + cart.get(book));

        return sqlStringRepresentations;
    }
}
