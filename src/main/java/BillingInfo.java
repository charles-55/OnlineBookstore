public class BillingInfo {

    private final String name;
    private final long cardNumber;
    private final int expiryDate;
    private final int cvv;
    private final String address;
    private final String postalCode;
    private final String city;
    private final String country;

    public BillingInfo(String name, long cardNumber, int expiryDate, int cvv, String address, String postalCode, String city, String country) throws Exception {
        if(String.valueOf(cardNumber).length() != 16)
            throw new Exception("Invalid card number input!");
        if(!((String.valueOf(expiryDate).charAt(String.valueOf(expiryDate).length() - 2) == '2') && (expiryDate <= 1230) && (expiryDate >= 122)))
            throw new Exception("Invalid expiry date input!");
        if(cvv > 999)
            throw new Exception();
        this.name = name;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public int getExpiryDate() {
        return expiryDate;
    }

    public int getCvv() {
        return cvv;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getFullAddress() {
        return address + ", " + city + ", " + country + ", " + postalCode;
    }

    public String getSQLRepresentation() {
        return "'" + name + "', " + cardNumber + ", '" + getFullAddress() + "'";
    }
}
