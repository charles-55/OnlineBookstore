public class Tracker {

    private final int trackingNumber;
    private final int userID;
    private final int orderNum;
    private Status status;
    public enum Status {SHIPPED, IN_TRANSIT, DELIVERED}

    public Tracker(int trackingNumber, User user, Order order){
        this.trackingNumber = trackingNumber;
        this.userID = user.getUserID();
        this.orderNum = order.getOrderNumber();
    }

    public int getTrackingNumber(){
        return trackingNumber;
    }

    public int getUserID(){
        return userID;
    }

    public int getOrderNum(){
        return orderNum;
    }

    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public String getSQLStringRepresentation() {
        return trackingNumber + ", " + userID + ", " + orderNum + ", " + status.toString();
    }
}
