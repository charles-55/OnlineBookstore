import java.util.ArrayList;
import java.util.HashMap;

public class Order {

    private final int orderNumber;
    private final int userID;
    private final HashMap<Book, Integer> basket;
    private final double totalPrice;
    private final BillingInfo billingInfo;
    private final String shippingInfo;

    public Order(int orderNumber, int userID, HashMap<Book, Integer> basket, double totalPrice, BillingInfo billingInfo, String shippingInfo) {
        this.orderNumber = orderNumber;
        this.userID = userID;
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.billingInfo = billingInfo;
        this.shippingInfo = shippingInfo;
    }

    public int getOrderNumber(){
        return orderNumber;
    }

    public HashMap<Book, Integer> getBasket(){
        return basket;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public BillingInfo getBillingInfo(){
        return billingInfo;
    }

    public String getShippingInfo(){
        return shippingInfo;
    }

    public ArrayList<String> getSQLStringRepresentation() {
        ArrayList<String> sqlStringRepresentations = new ArrayList<>();

        for(Book book : basket.keySet())
            sqlStringRepresentations.add(orderNumber + ", " + userID + ", " + book.getISBN() + ", " + basket.get(book)+ ", " + totalPrice + ", " + billingInfo.getCardNumber() + ", '" + shippingInfo.replace("\n", " ") + "'");

        return sqlStringRepresentations;
    }
}
