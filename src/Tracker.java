public class Tracker {

    private final String trackingNumber;
    private final String userID;
    private final String orderNum;
    private Status status;
    public enum Status {SHIPPED, IN_TRANSIT, DELIVERED}
}
