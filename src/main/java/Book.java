import java.util.ArrayList;

public class Book {

    private final long ISBN;
    private final String bookName;
    private final String authorName;
    private final ArrayList<String> contributingAuthors;
    private final ArrayList<Genre> genres;
    private final Publisher publisher;
    private final int numOfPages;
    private final double price;
    private final double pubPercent;
    public enum Genre {ACTION, ADVENTURE, COMEDY, CRIME, FICTION, NON_FICTION, HORROR, MYSTERY, ROMANCE, SCI_FI,YOUNG_ADULT,
        EDUCATIONAL, SELFHELP, CHILDREN, COOKBOOK, COMICS}

    public Book(long ISBN, String bookName, String authorName, Publisher publisher, int numOfPages, double price, double pubPercent){
        this.ISBN = ISBN;
        this.bookName = bookName;
        this.authorName = authorName;
        this.publisher = publisher;
        this.numOfPages = numOfPages;
        this.price = price;
        this.pubPercent = pubPercent;
        this.contributingAuthors = new ArrayList<>();
        this.genres = new ArrayList<Genre>();
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

    public ArrayList<Genre> getGenres(){
        return genres;
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

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
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
