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
}
