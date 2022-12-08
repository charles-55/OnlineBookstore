public class Publisher {

    private final String pName;
    private final String address;
    private final String email;
    private final int phoneNumber;
    private final int bankingAccount;

    public Publisher(String pName, String address, String email, int phoneNumber, int bankingAccount){
        this.pName = pName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bankingAccount = bankingAccount;
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

    public int getBankingAccount(){
        return bankingAccount;
    }

    public String getSQLStringRepresentation() {
        return  "'" + pName + "', '" + address + "', '" + email + "', " + phoneNumber + ", " + bankingAccount;
    }
}
