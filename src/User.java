import java.util.HashMap;

public class User {

    private final int userID;
    private String username;
    private String password;
    private final HashMap<Book, Integer> basket;

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.basket = new HashMap<>();
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

    public HashMap<Book, Integer> getBasket(){
        return basket;
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

    public void addBook(Book book, int amount) {
        basket.put(book, ((basket.get(book) == null) ? basket.get(book) : 0) + amount);
    }

    public void removeBook(Book book, int amount) {
        if(!basket.containsKey(book))
            return;
        if(basket.get(book) == amount)
            basket.remove(book);
        else if(basket.get(book) > amount)
            basket.put(book, basket.get(book) - amount);
    }
}
