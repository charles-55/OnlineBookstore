public class User {

    private final int userID;
    private String username;
    private String password;
    private final Basket basket;

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.basket = new Basket(userID);
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

    public Basket getBasket(){
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

    public String getSQLStringRepresentation() {
        return userID + ", '" + username + "', '" + password + "'";
    }
}
