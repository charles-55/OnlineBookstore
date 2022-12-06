import java.util.ArrayList;

public class Book {

    private final String ISBN;
    private final String bName;
    private final String authorName;
    private ArrayList<String> contributingAuthors;
    private ArrayList<Genre> genre;
    private final Publisher publisher;
    private final int numOfPages;
    private final double price;
    private final double pubPercent;
    public enum Genre {ACTION, ADVENTURE, COMEDY, CRIME, FICTION, HORROR, MYSTERY, ROMANCE, SCI_FI}
}
