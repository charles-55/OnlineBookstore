public class Publisher {

    private final String pName;
    private final String address;
    private final String email;
    private final int phoneNumber;
    private final String bankingAccount;
    private double profit;

    public Publisher(String pName, String address, String email, int phoneNumber, String bankingAccount){
        this.pName = pName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bankingAccount = bankingAccount;
        profit = 0.0;
    }

    public String getName(){
        return pName;
    }

    public String getAddress(){
        return address;
    }

    public String getEmail(){
        return email;
    }

    public int getPhoneNumber(){
        return phoneNumber;
    }

    public String getBankingAccount(){
        return bankingAccount;
    }

    public double getProfit() {
        return profit;
    }

    public void addProfit(double amount) {
        profit += amount;
    }

    public String getSQLStringRepresentation() {
        return  "'" + pName + "', '" + address + "', '" + email + "', " + phoneNumber + ", " + bankingAccount;
    }
}
