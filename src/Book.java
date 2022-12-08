import java.util.ArrayList;

public class Book {

    private final int ISBN;
    private final String bookName;
    private final String authorName;
    private ArrayList<String> contributingAuthors;
    private ArrayList<Genre> genre;
    private final Publisher publisher;
    private final int numOfPages;
    private final double price;
    private final double pubPercent;
    public enum Genre {ACTION, ADVENTURE, COMEDY, CRIME, FICTION, HORROR, MYSTERY, ROMANCE, SCI_FI}

    public Book(int ISBN, String bookName, String authorName, Publisher publisher, int numOfPages, double price){
        this.ISBN = ISBN;
        this.bookName = bookName;
        this.authorName = authorName;
        this.publisher = publisher;
        this.numOfPages = numOfPages;
        this.price = 0.0;
        this.pubPercent = 0.0;
        this.contributingAuthors = new ArrayList<>();
        this.genre = new ArrayList<Genre>();
    }

    public int getISBN() {
        return ISBN;
    }

    public String getBookName(){
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public ArrayList<String> getContributingAuthors(){
        return contributingAuthors;
    }

    public ArrayList<Genre> getGenre(){
        return genre;
    }

    public Publisher getPublisher(){
    return publisher;
    }

    public int getNumOfPages(){
        return numOfPages;
    }

    public double getPrice(){
        return price;
    }

    public double getPubPercent(){
        return pubPercent;
    }

    public void setContributingAuthors(ArrayList<String> contributingAuthors) {
        this.contributingAuthors = contributingAuthors;
    }

    public void setGenre(ArrayList<Genre> genre) {
        this.genre = genre;
    }

    public String getSQLStringRepresentation() {
        StringBuilder str = new StringBuilder(ISBN + ", '" + bookName + "', '" + authorName + "', '");

        for(int i = 0; i < contributingAuthors.size(); i++) {
            str.append(contributingAuthors.get(i));
            if(i == (contributingAuthors.size() - 1)) {
                break;
            }
            str.append(", ");
        }

        str.append("', '").append(publisher.getName()).append("', ").append(numOfPages).append(", ").append(price).append(", ").append(pubPercent);

        return str.toString();
    }
}
