public class Publisher {

    private final String pName;
    private final String address;
    private final String email;
    private final long phoneNumber;
    private final long bankingAccount;
    private double profit;

    public Publisher(String pName, String address, String email, long phoneNumber, long bankingAccount){
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

    public long getPhoneNumber(){
        return phoneNumber;
    }

    public long getBankingAccount(){
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
