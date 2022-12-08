import java.util.HashMap;

public class Order {

    private final int orderNumber;
    private final Basket basket;
    private String billingInfo;
    private String shippingInfo;
    private final Tracker tracker;

    public Order(int orderNumber, Basket basket, Tracker tracker, String billingInfo, String shippingInfo){
        this.orderNumber = orderNumber;
        this.basket = basket;
        this.tracker = tracker;
        this.billingInfo = billingInfo;
        this.shippingInfo = shippingInfo;
    }

    public int getOrderNumber(){
        return orderNumber;
    }

    public Basket getBasket(){
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

    public String getSQLStringRepresentation() {
        return orderNumber + ", " + basket.getBasketID() + ", '" + billingInfo + "', '" + shippingInfo + "'";
    }
}
