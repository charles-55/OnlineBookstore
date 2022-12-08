import java.util.ArrayList;

public class Order {

    private final int orderNumber;
    private final ArrayList<Book> books;
    private String billingInfo;
    private String shippingInfo;
    private final Tracker tracker;

    public Order(int orderNumber, ArrayList<Book> books, Tracker tracker){
        this.orderNumber = orderNumber;
        this.books = books;
        this.tracker = tracker;
        billingInfo = " ";
        shippingInfo = " ";
    }

    public int getOrderNumber(){
        return orderNumber;
    }

    public  ArrayList<Book> getBooks(){
        return books;
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

    public String getSQLStringRepresentation() {
        return orderNumber + ", " + "";
    }
}
