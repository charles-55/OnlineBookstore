import java.util.ArrayList;
import java.util.HashMap;

public class Order {

    private final int orderNumber;
    private final int userID;
    private final HashMap<Book, Integer> basket;
    private String billingInfo;
    private String shippingInfo;
    private final Tracker tracker;

    public Order(int orderNumber, int userID, HashMap<Book, Integer> basket, String billingInfo, String shippingInfo, Tracker tracker) {
        this.orderNumber = orderNumber;
        this.userID = userID;
        this.basket = basket;
        this.billingInfo = billingInfo;
        this.shippingInfo = shippingInfo;
        this.tracker = tracker;
    }

    public int getOrderNumber(){
        return orderNumber;
    }

    public HashMap<Book, Integer> getBasket(){
        return basket;
    }

    public String getBillingInfo(){
        return billingInfo;
    }

    public String getShippingInfo(){
        return shippingInfo;
    }

    public Tracker getTracker(){
        return tracker;
    }

    public void setBillingInfo(String billingInfo){
        this.billingInfo = billingInfo;
    }

    public void setShippingInfo(String shippingInfo){
        this.shippingInfo = shippingInfo;
    }

    public ArrayList<String> getSQLStringRepresentation() {
        ArrayList<String> sqlStringRepresentations = new ArrayList<>();

        for(Book book : basket.keySet())
            sqlStringRepresentations.add(orderNumber + ", " + userID + ", " + book.getISBN() + ", " + basket.get(book) + ", '" + billingInfo + "', '" + shippingInfo + "'");

        return sqlStringRepresentations;
    }
}
