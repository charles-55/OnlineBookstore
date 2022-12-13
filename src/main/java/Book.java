import java.util.ArrayList;

public class Book {

    private final long ISBN;
    private final String bookName;
    private final String authorName;
    private final ArrayList<String> contributingAuthors;
    private final Genre genre;
    private final Publisher publisher;
    private final int numOfPages;
    private final double price;
    private final double pubPercent;
    public enum Genre {ACTION, ADVENTURE, CHILDREN, COMEDY, COMICS, COOKBOOK, CRIME, EDUCATIONAL, FICTION, HORROR, MYSTERY, NON_FICTION, ROMANCE, SCI_FI, SELFHELP, UNKNOWN, YOUNG_ADULT}

    public Book(long ISBN, String bookName, String authorName, Publisher publisher, Genre genre, int numOfPages, double price, double pubPercent) {
        this.ISBN = ISBN;
        this.bookName = bookName;
        this.authorName = authorName;
        this.publisher = publisher;
        this.genre = genre;
        this.numOfPages = numOfPages;
        this.price = price;
        this.pubPercent = pubPercent;
        this.contributingAuthors = new ArrayList<>();
    }

    public long getISBN() {
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

    public Genre getGenre(){
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

    public void addContributingAuthor(String contributingAuthor) {
        contributingAuthors.add(contributingAuthor);
    }

    public void removeContributingAuthor(String contributingAuthor) {
        contributingAuthors.remove(contributingAuthor);
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

        str.append("', '").append(publisher.getName()).append("', '").append(genre.toString()).append("', ").append(numOfPages).append(", ").append(price).append(", ").append(pubPercent);

        return str.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Book))
            return false;
        Book book = (Book) obj;
        return (ISBN == book.getISBN()) && bookName.equals(book.getBookName()) && authorName.equals(book.getAuthorName())
                && (publisher == book.getPublisher()) && (genre == book.getGenre()) && (numOfPages == book.getNumOfPages())
                && (price == book.getPrice()) && (pubPercent == book.getPubPercent()) && contributingAuthors.equals(book.contributingAuthors);
    }
}
