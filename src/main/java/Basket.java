import java.util.ArrayList;
import java.util.HashMap;

public class Basket {

    private final int basketID;
    private final int userID;
    private final HashMap<Book, Integer> cart;

    public Basket(int basketID, int userID) {
        this.basketID = basketID;
        this.userID = userID;
        cart = new HashMap<>();
    }

    public int getBasketID() {
        return basketID;
    }

    public int getUserID() {
        return userID;
    }

    public HashMap<Book, Integer> getCart() {
        return cart;
    }

    public void addBook(Book book, int amount) {
        cart.put(book, ((cart.get(book) == null) ? 0 : cart.get(book)) + amount);
    }

    public void removeBook(Book book, int amount) {
        if(!cart.containsKey(book))
            return;
        if(cart.get(book) == amount)
            cart.remove(book);
        else if(cart.get(book) > amount)
            cart.put(book, cart.get(book) - amount);
    }

    public Order checkOut(int orderNum, Tracker tracker, String billingInfo, String shippingInfo) {
        return new Order(orderNum, this, tracker, billingInfo, shippingInfo);
    }

    public ArrayList<String> getSQLStringRepresentation() {
        ArrayList<Book> books = new ArrayList<>(cart.keySet());
        ArrayList<String> sqlStringRepresentations = new ArrayList<>();

        for(int i = 0; i < cart.size(); i++) {
            sqlStringRepresentations.add(basketID + ", " + userID + ", " + books.get(i) + ", " + cart.get(books.get(i)));
        }

        return sqlStringRepresentations;
    }
}
