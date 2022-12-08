import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class StoreFrame extends JFrame {

    //private JTextField searchBar;
    private JPanel cards;
    private JPanel topPanel;
    private JPanel contentPane;
    private JPanel buttonPanel;

    private JButton profileButton;
    private JButton browseButton;
    private JButton cartButton;


    public StoreFrame(){
        super("Look Inna Book");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);

        StoreModel model = new StoreModel();
        cards = new JPanel(new CardLayout());

        //model.addStoreModelView(this);

        topPanel = new JPanel();
        contentPane = new JPanel();
        buttonPanel = new JPanel();

        profileButton = new JButton();
        browseButton = new JButton();
        cartButton = new JButton();

        buttonPanel.add(profileButton);
        buttonPanel.add(browseButton);
        buttonPanel.add(cartButton);
        buttonPanel.setLayout(new FlowLayout());


        JPanel profileCard = new JPanel();
        profileCard.add(profileButton);

        JPanel browseCard = new JPanel();
        browseCard.add(browseButton);

        JPanel cartCard = new JPanel();
        cartCard.add(cartButton);

        cards.add(profileCard,"Profile Card");
        cards.add(browseCard, "Browse Card");
        cards.add(cartCard,"Cart Card");
        getContentPane().add(cards);

        profileButton.addActionListener(new ActionListener() {
            @Override
            // Swap to profile card when button is pressed
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) (cards.getLayout());
                layout.show(cards, "Profile Card");
            }
        });

        browseButton.addActionListener(new ActionListener() {
            @Override
            // Swap to browse card when button is pressed
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) (cards.getLayout());
                layout.show(cards, "Browse Card");
            }
        });

        cartButton.addActionListener(new ActionListener() {
            @Override
            // Swap to cart card when button is pressed
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) (cards.getLayout());
                layout.show(cards, "gameCard");
            }
        });


        this.pack();
        this.setSize(300,300);
        this.setVisible(true);
    }

}
