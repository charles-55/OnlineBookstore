import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreFrame extends JFrame implements StoreView {

    private CardLayout cardLayout;

    private final JPanel profilePanel;
    private final JPanel browsePanel;
    private final JPanel basketPanel;
    private final JPanel checkoutPanel;

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
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
        browsePanel = new JPanel();
        browsePanel.setLayout(new BoxLayout(browsePanel, BoxLayout.Y_AXIS));
        basketPanel = new JPanel();
        basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));
        checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BoxLayout(checkoutPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setAlignmentY(CENTER_ALIGNMENT);

        updateBrowsePanel();

        topPanelSetup();
        contentPanelSetup();
        bottomPanelSetup();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel);
        mainPanel.add(contentPanel);
        mainPanel.add(bottomPanel);
        JScrollBar scrollBar = new JScrollBar(Adjustable.VERTICAL);

        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(scrollBar, BorderLayout.EAST);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void updateProfilePanel() {
        if(profilePanel.getComponentCount() > 0)
            profilePanel.removeAll();

        if(model.getCurrentUser() == null){
            profilePanel.add(getCentreAlignedJLabel("You Are Not Signed In!"));
            profilePanel.updateUI();
            return;
        }

        CardLayout infoCardLayout = new CardLayout();
        JPanel infoPanel = new JPanel(infoCardLayout);
        infoPanel.add(getAccountPanel(model.getCurrentUser()), "Account");
        infoPanel.add(getOrderHistoryPanel(model.getCurrentUser()), "Orders");
        infoPanel.add(getContactPanel(), "Contact");
        infoCardLayout.show(infoPanel, "Account");

        JPanel menuPanel = new JPanel(new GridLayout(6, 1));
        JButton account = new JButton("Account");
        JButton orders = new JButton("Order History");
        JButton contactUs = new JButton("Contact us");
        JButton changeUsername = new JButton("Change Username");
        JButton changePassword = new JButton("Change Password");
        JButton signOut = new JButton("Sign out");
        menuPanel.add(account);
        menuPanel.add(orders);
        menuPanel.add(contactUs);
        menuPanel.add(changeUsername);
        menuPanel.add(changePassword);
        menuPanel.add(signOut);

        account.addActionListener(e -> infoCardLayout.show(infoPanel, "Account"));
        orders.addActionListener(e -> infoCardLayout.show(infoPanel, "Orders"));
        contactUs.addActionListener(e -> infoCardLayout.show(infoPanel, "Contact"));
        changeUsername.addActionListener(e -> {
            if(changeUsername())
                ((JLabel) ((JPanel) infoPanel.getComponent(0)).getComponent(3)).setText(model.getCurrentUser().getUsername());
        });
        changePassword.addActionListener(e -> changePassword());
        signOut.addActionListener(e -> {
            signOut();
            ((JButton) (topPanel.getComponent(3))).setText("Sign out");
        });

        profilePanel.add(menuPanel);
        profilePanel.add(infoPanel);
        profilePanel.updateUI();
    }

    private JPanel getAccountPanel(User user) {
        JPanel accountPanel = new JPanel(new GridLayout(2, 2));
        accountPanel.add(new JLabel("UserID: "));
        accountPanel.add(new JLabel(String.valueOf(user.getUserID())));
        accountPanel.add(new JLabel("Username: "));
        accountPanel.add(new JLabel(user.getUsername()));

        return accountPanel;
    }

    private JPanel getOrderHistoryPanel(User user) {
        JPanel orderHistoryPanel = new JPanel();
        orderHistoryPanel.setLayout(new BoxLayout(orderHistoryPanel, BoxLayout.Y_AXIS));

        for(Order order : user.getOrderHistory()) {
            JButton orderButton = new JButton("Order " + order.getOrderNumber());
            orderButton.addActionListener(e -> {
                JPanel panel = new JPanel(new GridLayout(order.getBasket().size() + 3, 2));
                for(Book book : order.getBasket().keySet()) {
                    panel.add(new JLabel(book.getBookName().replace("\"", "")));
                    panel.add(getRightAlignedJLabel(String.valueOf(order.getBasket().get(book))));
                }
                panel.add(new JLabel("Total price: "));
                panel.add(getRightAlignedJLabel("$" + String.format("%.2f", order.getTotalPrice())));
                panel.add(new JLabel("Shipping information: "));
                panel.add(new JLabel(order.getShippingInfo()));
                panel.add(new JLabel("Status: "));
                panel.add(new JLabel(model.getOrderStatus(order)));

                JOptionPane.showMessageDialog(this, panel);
            });

            orderHistoryPanel.add(orderButton);
        }

        return orderHistoryPanel;
    }

    private JPanel getContactPanel() {
        JPanel contactPanel = new JPanel(new GridLayout(2, 2));

        contactPanel.add(new JLabel("Osamudiamen Nwoko: "));
        contactPanel.add(new JLabel("osamudiamennwoko@cmail.carleton.ca"));
        contactPanel.add(new JLabel("Oyindamola Taiwo-Olupeka: "));
        contactPanel.add(new JLabel("oyindamolataiwoolupe@cmail.carleton.ca"));

        return contactPanel;
    }

    private void updateBrowsePanel() {
        if(browsePanel.getComponentCount() > 0)
            browsePanel.removeAll();

        for(Book book : model.getInventory().keySet()) {
            JPanel bookPanel = new JPanel(new GridLayout(1, 5));
            bookPanel.add(new JLabel(book.getBookName().replace("\"", "")));
            bookPanel.add(getCentreAlignedJLabel(book.getAuthorName().replace("\"", "")));
            bookPanel.add(getCentreAlignedJLabel("$" + String.format("%.2f", book.getPrice())));

            Choice amount = new Choice();
            for(int i = 0; i <= model.getInventory().get(book); i++)
                amount.add(String.valueOf(i));
            amount.addItemListener(e -> {
                if(model.getCurrentUser() == null) {
                    if(signInOrSignup())
                        model.addToCurrentUserBasket(book, amount.getSelectedIndex());
                }
                else
                    model.addToCurrentUserBasket(book, amount.getSelectedIndex());
            });

            bookPanel.add(amount);
            JButton view = new JButton("View more information");
            bookPanel.add(view);

            view.addActionListener(e -> {
                JPanel panel = new JPanel(new GridLayout(6, 2));
                panel.add(new JLabel("Name: "));
                panel.add(new JLabel(book.getBookName().replace("\"", "")));
                panel.add(new JLabel("Author: "));
                panel.add(new JLabel(book.getAuthorName().replace("\"", "")));
                panel.add(new JLabel("Publisher: "));
                panel.add(new JLabel(book.getPublisher().getName().replace("\"", "")));
                panel.add(new JLabel("Number of pages: "));
                panel.add(new JLabel(String.valueOf(book.getNumOfPages())));
                panel.add(new JLabel("ISBN: "));
                panel.add(new JLabel(String.valueOf(book.getISBN())));
                panel.add(new JLabel("Price: "));
                panel.add(new JLabel("$" + String.format("%.2f", book.getPrice())));

                JOptionPane.showMessageDialog(this, panel);
            });
            browsePanel.add(bookPanel);
        }
        browsePanel.updateUI();
    }

    private void updateBasketPanel() {
        if(basketPanel.getComponentCount() > 0)
            basketPanel.removeAll();

        if(model.getCurrentUser() == null){
            basketPanel.add(getCentreAlignedJLabel("You Are Not Signed In!"));
            basketPanel.updateUI();
            return;
        }

        HashMap<Book, Integer> cart = model.getCurrentUser().getBasket().getCart();
        double subTotal = 0;

        for(Book book : cart.keySet()) {
            JPanel cartItemsPanel = new JPanel(new GridLayout(1, 5));
            double individualTotals = book.getPrice() * cart.get(book);
            subTotal += individualTotals;

            cartItemsPanel.add(new JLabel(book.getBookName().replace("\"", "")));
            cartItemsPanel.add(getCentreAlignedJLabel("$" + String.format("%.2f", book.getPrice())));
            cartItemsPanel.add(getCentreAlignedJLabel(String.valueOf(cart.get(book))));

            JPanel buttonPan = new JPanel(new GridLayout(2, 1));
            JButton addButton = new JButton("+");
            JButton removeButton = new JButton("-");
            buttonPan.add(addButton);
            buttonPan.add(removeButton);

            Dimension buttonSize = new Dimension(10, 10);
            buttonPan.setMaximumSize(buttonSize);

            addButton.addActionListener(e -> {
                model.addToCurrentUserBasket(book, 1);
                updateBasketPanel();
            });
            removeButton.addActionListener(e -> {
                if(!model.removeFromCurrentUserBasket(book, 1))
                    JOptionPane.showMessageDialog(this, "Remove failed!");
                updateBasketPanel();
            });

            JLabel total = new JLabel("$" + String.format("%.2f", individualTotals));
            total.setHorizontalAlignment(SwingConstants.RIGHT);
            cartItemsPanel.add(buttonPan);
            cartItemsPanel.add(total);
            cartItemsPanel.setMaximumSize(new Dimension(500, 50));
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
        JButton checkOut = new JButton("Checkout");
        checkOut.addActionListener(e -> {
            updateCheckoutPanel(total);
            cardLayout.show(contentPanel, "Checkout");
        });
        basketPanel.add(checkOut);
        basketPanel.updateUI();
    }

    private void updateCheckoutPanel(double totalPrice) {
        if(checkoutPanel.getComponentCount() > 0)
            checkoutPanel.removeAll();

        JLabel shippingLabel = new JLabel("Shipping Information");
        JPanel shippingPanel = new JPanel(new GridLayout(4, 2));
        JTextField shippingAddress = new JTextField();
        JTextField shippingPostalCode = new JTextField();
        JTextField shippingCity = new JTextField();
        JTextField shippingCountry = new JTextField();

        shippingPanel.add(new JLabel("Address: "));
        shippingPanel.add(shippingAddress);
        shippingPanel.add(new JLabel("Postal code: "));
        shippingPanel.add(shippingPostalCode);
        shippingPanel.add(new JLabel("City: "));
        shippingPanel.add(shippingCity);
        shippingPanel.add(new JLabel("Country: "));
        shippingPanel.add(shippingCountry);
        checkoutPanel.add(shippingLabel);
        checkoutPanel.add(shippingPanel);

        JLabel billingLabel = new JLabel("Billing Information");
        JPanel billingPanel = new JPanel(new GridLayout(8, 2));
        JTextField name = new JTextField(100);
        JTextField cardNum = new JTextField(16);
        JTextField expiryDate = new JTextField(4);
        JTextField cvv = new JTextField(3);
        JTextField billingAddress = new JTextField();
        JTextField billingPostalCode = new JTextField();
        JTextField billingCity = new JTextField();
        JTextField billingCountry = new JTextField();

        billingPanel.add(new JLabel("Name: "));
        billingPanel.add(name);
        billingPanel.add(new JLabel("Card number: "));
        billingPanel.add(cardNum);
        billingPanel.add(new JLabel("Expiration date: "));
        billingPanel.add(expiryDate);
        billingPanel.add(new JLabel("CVV: "));
        billingPanel.add(cvv);
        billingPanel.add(new JLabel("Address: "));
        billingPanel.add(billingAddress);
        billingPanel.add(new JLabel("Postal code: "));
        billingPanel.add(billingPostalCode);
        billingPanel.add(new JLabel("City: "));
        billingPanel.add(billingCity);
        billingPanel.add(new JLabel("Country: "));
        billingPanel.add(billingCountry);
        checkoutPanel.add(billingLabel);
        checkoutPanel.add(billingPanel);

        JButton placeOrder = new JButton("Place Order");
        placeOrder.addActionListener(e -> {
            try {
                BillingInfo billingInfo = new BillingInfo(name.getText(), Long.parseLong(cardNum.getText()), Integer.parseInt(expiryDate.getText()), Integer.parseInt(cvv.getText()), billingAddress.getText(), billingPostalCode.getText(), billingCity.getText(), billingCountry.getText());
                Order order = model.getCurrentUser().getBasket().checkOut(model.getNewOrderNumber(), totalPrice, billingInfo, (shippingAddress.getText() + ",\n" + shippingCity.getText() + ", " + shippingCountry.getText() + ", " + shippingPostalCode));
                if(model.processOrder(order))
                    JOptionPane.showMessageDialog(this, "Order placed successfully!");
                else
                    JOptionPane.showMessageDialog(this, "Failed to place order!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input!");
            }
        });
        checkoutPanel.add(placeOrder);

        checkoutPanel.updateUI();
    }

    private JLabel getCentreAlignedJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return jLabel;
    }

    private JLabel getRightAlignedJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
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
                if(signInOrSignup())
                    signIn.setText("Sign out");
            }
            else {
                signOut();
                signIn.setText("Sign in");
            }
            updateProfilePanel();
            updateBasketPanel();
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
        contentPanel.add(checkoutPanel, "Checkout");

        cardLayout.show(contentPanel, "Browse");
    }

    private void bottomPanelSetup() {
        bottomPanel = new JPanel();
    }

    private boolean signInOrSignup() {
        JPanel signInOrSignupPanel = new JPanel(new BorderLayout());
        signInOrSignupPanel.add(getCentreAlignedJLabel("Sign in or Signup:"), BorderLayout.NORTH);
        JButton signIn = new JButton("Sign in");
        JButton signUp = new JButton("Sign up");
        signInOrSignupPanel.add(signIn, BorderLayout.WEST);
        signInOrSignupPanel.add(signUp, BorderLayout.EAST);

        AtomicInteger i = new AtomicInteger(-1);
        signIn.addActionListener(e -> {
            i.set(0);
            signIn.setBackground(Color.GREEN);
            signUp.setBackground(null);
        });
        signUp.addActionListener(e -> {
            i.set(1);
            signIn.setBackground(null);
            signUp.setBackground(Color.GREEN);
        });

        JOptionPane.showMessageDialog(this, signInOrSignupPanel);
        boolean b = false;
        if((Integer.parseInt(String.valueOf(i))) == 0)
            b = signIn();
        else if(Integer.parseInt(String.valueOf(i)) == 1)
            b = signup();

        if(b)
            ((JButton) (topPanel.getComponent(3))).setText("Sign out");
        return b;
    }

    private boolean signIn() {
        JPanel signInPanel = new JPanel(new GridLayout(2, 2));
        JTextField username = new JTextField(25);
        JTextField password = new JTextField(16);

        signInPanel.add(new JLabel("Username: "));
        signInPanel.add(username);
        signInPanel.add(new JLabel("Password: "));
        signInPanel.add(password);

        JOptionPane.showMessageDialog(this, signInPanel);

        boolean b = model.signIn(username.getText(), password.getText());
        if(b)
            JOptionPane.showMessageDialog(this, "Sign in successful!");
        else
            JOptionPane.showMessageDialog(this, "Sign in failed!");
        return b;
    }

    private void signOut() {
        model.signOut();
        JOptionPane.showMessageDialog(this, "You are now signed out.");
        cardLayout.show(contentPanel, "Browse");
        updateProfilePanel();
        updateBasketPanel();
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

        boolean b = model.addUser(username.getText(), password.getText()) && model.signIn(username.getText(), password.getText());
        if(b)
            JOptionPane.showMessageDialog(this, "Sign up successful!");
        else
            JOptionPane.showMessageDialog(this, "Sign up failed!");
        return b;
    }

    private void changePassword() {
        JPanel changePasswordPanel = new JPanel(new GridLayout(2, 2));
        JTextField oldPassword = new JTextField(25);
        JTextField newPassword = new JTextField(16);

        changePasswordPanel.add(new JLabel("Old password: "));
        changePasswordPanel.add(oldPassword);
        changePasswordPanel.add(new JLabel("New password: "));
        changePasswordPanel.add(newPassword);

        JOptionPane.showMessageDialog(this, changePasswordPanel, "Change password", JOptionPane.ERROR_MESSAGE);

        if(model.changeCurrentUserPassword(oldPassword.getText(), newPassword.getText()))
            JOptionPane.showMessageDialog(this, "Password change successful!");
        else
            JOptionPane.showMessageDialog(this, "Password change failed!");
    }

    private boolean changeUsername() {
        String newName = JOptionPane.showInputDialog("Enter new username:");
        if(newName.length() > 0) {
            model.getCurrentUser().setUsername(newName);
            JOptionPane.showMessageDialog(this, "Username updated!");
            return true;
        }
        JOptionPane.showMessageDialog(this, "Username update failed!");
        return false;
    }

    @Override
    public void handleMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public String getUserInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }
}
