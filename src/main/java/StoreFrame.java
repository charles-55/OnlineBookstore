import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreFrame extends JFrame implements StoreView {

    private CardLayout cardLayout;

    private JPanel profilePanel;
    private JPanel homePanel;
    private JPanel browsePanel;
    private JPanel basketPanel;
    private JPanel checkoutPanel;
    private JPanel trackPanel;

    private JPanel topPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;

    private final StoreModel model;

    public StoreFrame() {
        super("Look Inna Book");
        model = new StoreModel();
        model.addView(this);

        StoreWindowController controller = new StoreWindowController(model);
        this.addWindowListener(controller);

        profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        browsePanel = new JPanel();
        browsePanel.setLayout(new BoxLayout(browsePanel, BoxLayout.Y_AXIS));
        basketPanel = new JPanel();
        basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));

        updateBrowsePanel();

        topPanelSetup();
        contentPanelSetup();
        bottomPanelSetup();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel);
        mainPanel.add(contentPanel);
        mainPanel.add(bottomPanel);

        this.add(mainPanel);
        this.setSize(500, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void updateProfilePanel() {
        if(profilePanel.getComponentCount() > 0)
            profilePanel.removeAll();

        profilePanel.updateUI();
    }

    private void updateBrowsePanel() {
        if(browsePanel.getComponentCount() > 0)
            browsePanel.removeAll();

        for(Book book : model.getInventory().keySet()) {
            JPanel bookPanel = new JPanel(new GridLayout(1, 3));
            bookPanel.add(new JLabel(book.getBookName()));
            bookPanel.add(new JLabel(book.getAuthorName()));

            Choice amount = new Choice();
            for(int i = 0; i <= model.getInventory().get(book); i++)
                amount.add(String.valueOf(i));
            amount.addItemListener(e -> {
                if(model.getCurrentUser() == null) {
                    if(loginOrSignup())
                        model.addToCurrentUserBasket(book, amount.getSelectedIndex());
                }
                else
                    model.addToCurrentUserBasket(book, amount.getSelectedIndex());
            });

            bookPanel.add(amount);
            browsePanel.add(bookPanel);
        }
        browsePanel.updateUI();
    }

    private void updateBasketPanel() {
        if(basketPanel.getComponentCount() > 0)
            basketPanel.removeAll();

        if(model.getCurrentUser() == null){
            basketPanel.add(getCentreAlignedJLabel("You Are Not Logged In!"));
            return;
        }

        HashMap<Book, Integer> cart = model.getCurrentUser().getBasket().getCart();
        JPanel cartItemsPanel = new JPanel(new GridLayout(cart.size(), 5));
        double subTotal = 0;

        for(Book book : cart.keySet()){
            double individualTotals = book.getPrice() * cart.get(book);
            subTotal += individualTotals;

            cartItemsPanel.add(new JLabel(book.getBookName()));
            cartItemsPanel.add(getCentreAlignedJLabel("$" + String.format("%.2f", book.getPrice())));
            JLabel amount = new JLabel(String.valueOf(cart.get(book)));
            cartItemsPanel.add(amount);

            JPanel buttonPan = new JPanel(new GridLayout(2, 1));
            JButton addButton = new JButton("+");
            JButton removeButton = new JButton("-");
            buttonPan.add(addButton);
            buttonPan.add(removeButton);

            Dimension buttonSize = new Dimension(10, 10);
            addButton.setMaximumSize(buttonSize);
            removeButton.setMaximumSize(buttonSize);

            addButton.addActionListener(e -> {
                model.addToCurrentUserBasket(book, 1);
                updateBasketPanel();
            });
            removeButton.addActionListener(e -> {
                model.removeFromCurrentUserBasket(book, 1);
                updateBasketPanel();
            });

            JLabel total = new JLabel("$" + String.format("%.2f", individualTotals));
            total.setHorizontalAlignment(SwingConstants.RIGHT);
            cartItemsPanel.add(buttonPan);
            cartItemsPanel.add(total);
            basketPanel.add(cartItemsPanel);
        }

        double taxAmount = 0.13 * subTotal;
        double shippingAmount = 0;
        double total = subTotal + taxAmount + shippingAmount;

        JPanel summary = new JPanel(new GridLayout(4,2));
        summary.add(new JLabel("Subtotal"));
        summary.add(getCentreAlignedJLabel("$" + String.format("%.2f", subTotal)));
        summary.add(new JLabel("Tax"));
        summary.add(getCentreAlignedJLabel("$" + String.format("%.2f", taxAmount)));
        summary.add(new JLabel("Shipping"));
        summary.add(getCentreAlignedJLabel("$" + String.format("%.2f", shippingAmount)));
        summary.add(new JLabel("Total"));
        summary.add(getCentreAlignedJLabel("$" + String.format("%.2f", total)));

        basketPanel.add(summary);
        basketPanel.updateUI();
    }

    private JLabel getCentreAlignedJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return jLabel;
    }

    private void topPanelSetup() {
        JButton profile = new JButton("Profile");
        JButton browse = new JButton("Browse");
        JButton basket = new JButton("Basket");
        JButton signIn = new JButton("Sign in");

        profile.addActionListener(e -> {
            updateProfilePanel();
            cardLayout.show(contentPanel, "Profile");
        });
        browse.addActionListener(e -> {
            updateBrowsePanel();
            cardLayout.show(contentPanel, "Browse");
        });
        basket.addActionListener(e -> {
            updateBasketPanel();
            cardLayout.show(contentPanel, "Basket");
        });
        signIn.addActionListener(e -> {
            if(model.getCurrentUser() == null) {
                if(loginOrSignup())
                    signIn.setText("Sign out");
            }
            else {
                logout();
                signIn.setText("Sign in");
                updateProfilePanel();
                updateBasketPanel();
            }
        });

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(profile);
        topPanel.add(browse);
        topPanel.add(basket);
        topPanel.add(signIn);
    }

    private void contentPanelSetup() {
        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.add(profilePanel, "Profile");
        contentPanel.add(browsePanel, "Browse");
        contentPanel.add(basketPanel, "Basket");

        cardLayout.show(contentPanel, "Browse");
    }

    private void bottomPanelSetup() {
        bottomPanel = new JPanel();
    }

    private boolean loginOrSignup() {
        JPanel loginOrSignupPanel = new JPanel(new BorderLayout());
        loginOrSignupPanel.add(new JLabel("Login or Signup:"), BorderLayout.NORTH);
        JButton login = new JButton("Login");
        JButton signup = new JButton("Sign up");
        loginOrSignupPanel.add(login, BorderLayout.WEST);
        loginOrSignupPanel.add(signup, BorderLayout.EAST);

        AtomicInteger i = new AtomicInteger(-1);
        login.addActionListener(e -> {
            i.set(0);
            login.setBackground(Color.GREEN);
            signup.setBackground(null);
        });
        signup.addActionListener(e -> {
            i.set(1);
            login.setBackground(null);
            signup.setBackground(Color.GREEN);
        });

        JOptionPane.showMessageDialog(this, loginOrSignupPanel);
        boolean b = false;
        if((Integer.parseInt(String.valueOf(i))) == 0)
            b = login();
        else if(Integer.parseInt(String.valueOf(i)) == 1)
            b = signup();

        return b;
    }

    private boolean login() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2));
        JTextField username = new JTextField(25);
        JTextField password = new JTextField(16);

        loginPanel.add(new JLabel("Username: "));
        loginPanel.add(username);
        loginPanel.add(new JLabel("Password: "));
        loginPanel.add(password);

        JOptionPane.showMessageDialog(this, loginPanel, "Login", JOptionPane.ERROR_MESSAGE);

        boolean b = model.login(username.getText(), password.getText());
        if(b)
            JOptionPane.showMessageDialog(this, "Login successful!");
        else
            JOptionPane.showMessageDialog(this, "Login failed!");
        return b;
    }

    private void logout() {
        model.logout();
        JOptionPane.showMessageDialog(this, "You are now logged out.");
    }

    private boolean signup() {
        JPanel signupPanel = new JPanel();
        signupPanel.setLayout(new BoxLayout(signupPanel, BoxLayout.Y_AXIS));
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JTextField username = new JTextField(25);
        JTextField password = new JTextField(16);

        inputPanel.add(new JLabel("Username: "));
        inputPanel.add(username);
        inputPanel.add(new JLabel("Password: "));
        inputPanel.add(password);
        signupPanel.add(inputPanel);
        signupPanel.add(new JLabel("Password should be between 6-16 characters"));

        JOptionPane.showMessageDialog(this, signupPanel, "Sign up", JOptionPane.ERROR_MESSAGE);
        if((password.getText().length() < 6) || (password.getText().length() > 16)) {
            JOptionPane.showMessageDialog(this, "Password length is not in range. Sign up failed!");
            return false;
        }

        boolean b = model.addUser(username.getText(), password.getText()) && model.login(username.getText(), password.getText());
        if(b)
            JOptionPane.showMessageDialog(this, "Sign up successful!");
        else
            JOptionPane.showMessageDialog(this, "Sign up failed!");
        return b;
    }

    private boolean changePassword() {
        JPanel changePasswordPanel = new JPanel(new GridLayout(2, 2));
        JTextField oldPassword = new JTextField(25);
        JTextField newPassword = new JTextField(16);

        changePasswordPanel.add(new JLabel("Old password: "));
        changePasswordPanel.add(oldPassword);
        changePasswordPanel.add(new JLabel("New password: "));
        changePasswordPanel.add(newPassword);

        JOptionPane.showMessageDialog(this, changePasswordPanel, "Change password", JOptionPane.ERROR_MESSAGE);

        if(model.getCurrentUser().changePassword(oldPassword.getText(), newPassword.getText())) {
            JOptionPane.showMessageDialog(this, "Password change successful!");
            return true;
        }

        JOptionPane.showMessageDialog(this, "Password change failed!");
        return false;
    }

    @Override
    public void handleMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void handleBasketUpdate() {

    }

    @Override
    public String getUserInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }
}
