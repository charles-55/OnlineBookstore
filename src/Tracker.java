public class Tracker {

    private final String trackingNumber;
    private final String userID;
    private final String orderNum;
    private Status status;
    public enum Status {SHIPPED, IN_TRANSIT, DELIVERED}

    public Tracker(String trackingNumber, String userID, String orderNum){
        this.trackingNumber = trackingNumber;
        this.userID = userID;
        this.orderNum = orderNum;
    }

    public String getTrackingNumber(){
        return trackingNumber;
    }

    public String getUserID(){
        return userID;
    }

    public String getOrderNum(){
        return orderNum;
    }

    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }
}
