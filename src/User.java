import java.util.ArrayList;

public class User {

    private final int userID;
    private String username;
    private ArrayList<Book> basket;

    public User(int userID){
        this.userID = userID;
        this.username = " ";
        this.basket = new ArrayList<Book>();
    }

    public int getUserID(){
        return userID;
    }

    public String getUsername(){
        return username;
    }

    public ArrayList<Book> getBasket(){
        return basket;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setBasket(ArrayList<Book> basket) {
        this.basket = basket;
    }

}
