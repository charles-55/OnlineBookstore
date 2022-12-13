import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

        JScrollPane scrollPane = new JScrollPane(mainPanel);

        this.add(scrollPane);
        this.setSize(getMaximumSize());
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
        infoPanel.add(getAdminPanel(), "Admin");
        infoCardLayout.show(infoPanel, "Account");

        JPanel menuPanel = new JPanel(new GridLayout(7, 1));
        JButton account = new JButton("Account");
        JButton orders = new JButton("Order History");
        JButton contactUs = new JButton("Contact us");
        JButton changeUsername = new JButton("Change Username");
        JButton changePassword = new JButton("Change Password");
        JButton adminControls = new JButton("Admin Controls");
        JButton signOut = new JButton("Sign out");
        menuPanel.add(account);
        menuPanel.add(orders);
        menuPanel.add(contactUs);
        menuPanel.add(changeUsername);
        menuPanel.add(changePassword);
        if(model.getCurrentUser().isAdmin())
            menuPanel.add(adminControls);
        menuPanel.add(signOut);

        account.addActionListener(e -> infoCardLayout.show(infoPanel, "Account"));
        orders.addActionListener(e -> infoCardLayout.show(infoPanel, "Orders"));
        contactUs.addActionListener(e -> infoCardLayout.show(infoPanel, "Contact"));
        changeUsername.addActionListener(e -> {
            if(changeUsername())
                ((JLabel) ((JPanel) infoPanel.getComponent(0)).getComponent(3)).setText(model.getCurrentUser().getUsername());
        });
        changePassword.addActionListener(e -> changePassword());
        adminControls.addActionListener(e -> infoCardLayout.show(infoPanel, "Admin"));
        signOut.addActionListener(e -> {
            signOut();
            ((JButton) (topPanel.getComponent(3))).setText("Sign out");
            ((JButton) (topPanel.getComponent(3))).updateUI();
        });

        profilePanel.add(menuPanel);
        profilePanel.add(infoPanel);
        profilePanel.updateUI();
    }

    private JPanel getAccountPanel(User user) {
        JPanel accountPanel = new JPanel(new GridLayout(2, 2));
        accountPanel.add(new JLabel("UserID: "));
        accountPanel.add(new JLabel(String.valueOf(user.getID())));
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

    private JPanel getAdminPanel() {
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));

        JPanel inventoryControlPanel = new JPanel();
        inventoryControlPanel.setLayout(new BoxLayout(inventoryControlPanel, BoxLayout.X_AXIS));
        JButton addBook = new JButton("Add Book");
        JButton removeBook = new JButton("Remove Book");
        JButton updateTracker = new JButton("Update Tracker");
        JButton viewReport = new JButton("View Report");
        inventoryControlPanel.add(addBook);
        inventoryControlPanel.add(removeBook);
        inventoryControlPanel.add(updateTracker);
        inventoryControlPanel.add(viewReport);

        addBook.addActionListener(e -> {
            JPanel addBookPanel = new JPanel();
            JTextField isbn = new JTextField(13);
            JTextField name = new JTextField();
            JTextField author = new JTextField();
            JTextField publisher = new JTextField();
            JTextField genre = new JTextField();
            JTextField pages = new JTextField();
            JTextField price = new JTextField();
            JTextField pubPercent = new JTextField();
            JTextField amount = new JTextField();

            JPanel addBookInputPanel = new JPanel(new GridLayout(9,2));
            addBookInputPanel.add(new JLabel("ISBN: "));
            addBookInputPanel.add(isbn);
            addBookInputPanel.add(new JLabel("Name: "));
            addBookInputPanel.add(name);
            addBookInputPanel.add(new JLabel("Author: "));
            addBookInputPanel.add(author);
            addBookInputPanel.add(new JLabel("Publisher: "));
            addBookInputPanel.add(publisher);
            addBookInputPanel.add(new JLabel("Genre: "));
            addBookInputPanel.add(genre);
            addBookInputPanel.add(new JLabel("Number of Pages: "));
            addBookInputPanel.add(pages);
            addBookInputPanel.add(new JLabel("Price: "));
            addBookInputPanel.add(price);
            addBookInputPanel.add(new JLabel("Publisher Percentage: "));
            addBookInputPanel.add(pubPercent);
            addBookInputPanel.add(new JLabel("Amount: "));
            addBookInputPanel.add(amount);

            addBookPanel.add(addBookInputPanel);
            addBookPanel.add(new JLabel("ISBN should be 13 digits and Amount should be greater than 0"));

            JOptionPane.showMessageDialog(this, addBookPanel, "Add Book",JOptionPane.ERROR_MESSAGE);
            if(isbn.getText().length() != 13){
                JOptionPane.showMessageDialog(this, "ISBN length is invalid\nTry again!");
                return;
            }
            else if (amount.getText().equals("0")){
                JOptionPane.showMessageDialog(this, "Amount is invalid\nTry again!");
            }

            Publisher p = null;

            for(Publisher publisher1 : model.getPublishers() ){
                if(publisher1.getName().equals(publisher.getText().toUpperCase())){
                    p = publisher1;
                    break;
                }
            }

            if(p == null){
                JOptionPane.showMessageDialog(this, "Publisher not in Database\n Add New Publisher!");
                p = createNewPublisher();
            }

            Book.Genre g = Book.Genre.UNKNOWN;

            for(Book.Genre genre1 : Book.Genre.values()){
                if(genre1.toString().equals(genre.getText().toUpperCase())){
                    g = genre1;
                    break;
                }
            }

            if(g == Book.Genre.UNKNOWN){
                JOptionPane.showMessageDialog(this, "Genre Input is Unknown");
            }

            Book book = new Book(Long.parseLong(isbn.getText()), name.getText(), author.getText(),p,g,
                    Integer.parseInt(pages.getText()),Double.parseDouble(price.getText()), Double.parseDouble(pubPercent.getText()));

            boolean b = model.addToInventory(book, Integer.parseInt(amount.getText()));

            if(b) {
                JOptionPane.showMessageDialog(this, "Book(s) Has Been Added To The Inventory!");
            }
            else{
                JOptionPane.showMessageDialog(this, "Book(s) Was Not Added To The Inventory!");
            }

        });

        removeBook.addActionListener(e -> {
            JPanel removeBookPanel = new JPanel();
            JTextField isbn = new JTextField(13);
            JTextField amount = new JTextField();

            JPanel removeBookInputPanel = new JPanel(new GridLayout(2,2));
            removeBookInputPanel.add(new JLabel("ISBN: "));
            removeBookInputPanel.add(isbn);
            removeBookInputPanel.add(new JLabel("Amount: "));
            removeBookInputPanel.add(amount);

            removeBookPanel.add(removeBookInputPanel);
            removeBookPanel.add(new JLabel("ISBN should be 13 digits and Amount should be greater than 0"));

            JOptionPane.showMessageDialog(this, removeBookPanel, "Remove Book",JOptionPane.ERROR_MESSAGE);
            if(isbn.getText().length() != 13){
                JOptionPane.showMessageDialog(this, "ISBN length is invalid\nTry again!");
            }
            else if (amount.getText().equals("0")){
                JOptionPane.showMessageDialog(this, "Amount is invalid\nTry again!");
            }

            Book book = null;
            for(Book b : model.getInventory().keySet()){
                if(b.getISBN() == Long.parseLong(isbn.getText())){
                    book = b;
                    break;
                }
            }

            if(book == null){
                JOptionPane.showMessageDialog(this, "Book Not Found!");
            }
            else{
                boolean b = model.removeFromInventory(book, Integer.parseInt(amount.getText()));

                if(b) {
                    JOptionPane.showMessageDialog(this, "Book(s) Has Been Removed From The Inventory!");
                }
                else{
                    JOptionPane.showMessageDialog(this, "Book(s) Was Not Removed The Inventory!");
                }
            }
        });

        updateTracker.addActionListener(e -> {
            JPanel updateTrackerPanel = new JPanel(new GridLayout(2,2));
            JTextField trackerNum = new JTextField();
            JTextField status = new JTextField();

            JPanel updateTrackerInputPanel = new JPanel();
            updateTrackerInputPanel.add(new JLabel("Tracking Number"));
            updateTrackerInputPanel.add(trackerNum);
            updateTrackerInputPanel.add(new JLabel());

            Tracker.Status s = Tracker.Status.UNKNOWN;

            for(Tracker.Status status1 : Tracker.Status.values()){
                if(status1.toString().equals(status.getText().toUpperCase())){
                    s = status1;
                    break;
                }
            }

            if(s == Tracker.Status.UNKNOWN){
                JOptionPane.showMessageDialog(this, "Status is Unknown");
            }
            else{
                boolean b = model.updateTrackerStatus(Integer.parseInt(trackerNum.getText()), s);

                if(b){
                    JOptionPane.showMessageDialog(this, "Tracker Updated!");
                }
                else{
                    JOptionPane.showMessageDialog(this,"Tracker failed to update!");
                }
            }

            updateTrackerPanel.add(updateTrackerInputPanel);

            JOptionPane.showInputDialog("Input Tracking Information");
        });

        JPanel databaseControlPanel = new JPanel();
        databaseControlPanel.setLayout(new BoxLayout(databaseControlPanel, BoxLayout.X_AXIS));
        JButton addNewAdmin = new JButton("Add New Admin");
        JButton wipeDatabase = new JButton("Wipe Database");
        databaseControlPanel.add(addNewAdmin);
        databaseControlPanel.add(wipeDatabase);

        adminPanel.add(getCentreAlignedJLabel("Inventory Controls"));
        adminPanel.add(inventoryControlPanel);
        adminPanel.add(getCentreAlignedJLabel("Database Controls"));
        adminPanel.add(databaseControlPanel);
        return adminPanel;
    }

    private Publisher createNewPublisher(){
        JPanel newPublisherPanel = new JPanel(new GridLayout(5,2));
        JTextField name = new JTextField();
        JTextField address = new JTextField();
        JTextField email = new JTextField();
        JTextField phoneNumber = new JTextField();
        JTextField bankingAccount = new JTextField();

        newPublisherPanel.add(new JLabel("Name: "));
        newPublisherPanel.add(name);
        newPublisherPanel.add(new JLabel("Address: "));
        newPublisherPanel.add(address);
        newPublisherPanel.add(new JLabel("Email: "));
        newPublisherPanel.add(email);
        newPublisherPanel.add(new JLabel("Phone Number: "));
        newPublisherPanel.add(phoneNumber);
        newPublisherPanel.add(new JLabel("Banking Account: "));
        newPublisherPanel.add(bankingAccount);

        JOptionPane.showMessageDialog(this,newPublisherPanel);
        Publisher publisher = new Publisher(name.getText(), address.getText(), email.getText(),
                Long.parseLong(phoneNumber.getText()), Long.parseLong(bankingAccount.getText()));

        model.addPublisher(publisher);
        return publisher;
    }

    private void updateBrowsePanel() {
        if(browsePanel.getComponentCount() > 0)
            browsePanel.removeAll();

        JPanel filterOptions = new JPanel(new GridLayout(1, 2));

        JPanel info = new JPanel(new GridLayout(3, 1));
        JRadioButton name = new JRadioButton("Book name");
        name.setActionCommand("book");
        name.setSelected(true);
        JRadioButton author = new JRadioButton("Author name");
        author.setActionCommand("author");
        JRadioButton isbn = new JRadioButton("ISBN");
        isbn.setActionCommand("isbn");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(name);
        buttonGroup.add(author);
        buttonGroup.add(isbn);
        info.add(name);
        info.add(author);
        info.add(isbn);

        JPanel genreOptions = new JPanel();
        genreOptions.setLayout(new BoxLayout(genreOptions, BoxLayout.Y_AXIS));
        JCheckBox[] checkboxes = new JCheckBox[Book.Genre.values().length];
        for(int i = 0; i < Book.Genre.values().length; i++) {
            checkboxes[i] = new JCheckBox(Book.Genre.values()[i].toString());
            genreOptions.add(checkboxes[i]);
        }
        final ArrayList<Book.Genre>[] selectedGenres = new ArrayList[]{new ArrayList<>()};

        filterOptions.add(info);
        filterOptions.add(genreOptions);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        JTextField searchBar = new JTextField();
        searchBar.setMaximumSize(new Dimension(500, 50));
        JButton searchButton = new JButton("Search");
        JButton filters = new JButton("Filters");

        searchPanel.add(searchBar);
        searchPanel.add(searchButton);
        searchPanel.add(filters);

        searchButton.addActionListener(e -> {
            browsePanel.removeAll();
            browsePanel.add(searchPanel);
            browsePanel.add(getBookSearch(searchBar.getText(), buttonGroup.getSelection().getActionCommand(), selectedGenres[0]));
            browsePanel.updateUI();
        });
        filters.addActionListener(e -> {
            selectedGenres[0] = new ArrayList<>();
            JOptionPane.showMessageDialog(this, filterOptions);
            for(JCheckBox checkbox : checkboxes) {
                if(checkbox.isSelected()) {
                    for(Book.Genre genre : Book.Genre.values()) {
                        if(genre.toString().equals(checkbox.getText()))
                            selectedGenres[0].add(genre);
                    }
                }
            }
        });

        browsePanel.add(searchPanel);
        browsePanel.add(getBookSearch("", buttonGroup.getSelection().getActionCommand(), new ArrayList<>()));
        browsePanel.updateUI();
    }

    private JPanel getBookSearch(String search, String criteria, ArrayList<Book.Genre> genres) {
        HashMap<Book, Integer> results;
        if((search.equals("")) && (genres.isEmpty()))
            results = model.getInventory();
        else
            results = model.search(search, criteria, genres);

        JPanel allBooksPanel = new JPanel();
        allBooksPanel.setLayout(new BoxLayout(allBooksPanel, BoxLayout.Y_AXIS));
        for(Book book : results.keySet()) {
            JPanel bookPanel = new JPanel(new GridLayout(1, 5));
            bookPanel.add(new JLabel(book.getBookName().replace("\"", "")));
            bookPanel.add(getCentreAlignedJLabel(book.getAuthorName().replace("\"", "")));
            bookPanel.add(getCentreAlignedJLabel("$" + String.format("%.2f", book.getPrice())));

            Choice amount = new Choice();
            for(int i = 0; i <= results.get(book); i++)
                amount.add(String.valueOf(i));
            amount.addItemListener(e -> {
                if(model.getCurrentUser() == null) {
                    if(signInOrSignup())
                        model.addToCurrentUserBasket(book, amount.getSelectedIndex());
                }
                else
                    model.addToCurrentUserBasket(book, amount.getSelectedIndex());
            });

            amount.setSize(new Dimension(50, 50));

            bookPanel.add(amount);
            JButton view = new JButton("View more information");
            bookPanel.add(view);

            view.addActionListener(e -> {
                JPanel panel = new JPanel(new GridLayout(7, 2));
                panel.add(new JLabel("Name: "));
                panel.add(new JLabel(book.getBookName().replace("\"", "")));
                panel.add(new JLabel("Author: "));
                panel.add(new JLabel(book.getAuthorName().replace("\"", "")));
                panel.add(new JLabel("Publisher: "));
                panel.add(new JLabel(book.getPublisher().getName().replace("\"", "")));
                panel.add(new JLabel("Genre: "));
                panel.add(new JLabel(book.getGenre().toString()));
                panel.add(new JLabel("Number of pages: "));
                panel.add(new JLabel(String.valueOf(book.getNumOfPages())));
                panel.add(new JLabel("ISBN: "));
                panel.add(new JLabel(String.valueOf(book.getISBN())));
                panel.add(new JLabel("Price: "));
                panel.add(new JLabel("$" + String.format("%.2f", book.getPrice())));

                JOptionPane.showMessageDialog(this, panel);
            });
            allBooksPanel.add(bookPanel);
        }
        return allBooksPanel;
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
        billingPanel.add(new JLabel("Card number (should be 16 digits long): "));
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
                StringBuilder date = new StringBuilder(expiryDate.getText());
                if(date.charAt(0) == '0') {
                    date = new StringBuilder();
                    for(int i = 1; i < expiryDate.getText().length(); i++)
                        date.append(expiryDate.getText().charAt(i));
                }
                BillingInfo billingInfo = new BillingInfo(name.getText(), Long.parseLong(cardNum.getText()), Integer.parseInt(date.toString()), Integer.parseInt(cvv.getText()), billingAddress.getText(), billingPostalCode.getText(), billingCity.getText(), billingCountry.getText());
                Order order = model.getCurrentUser().getBasket().checkOut(model.getNewOrderNumber(), totalPrice, billingInfo, (shippingAddress.getText() + ", \n" + shippingCity.getText() + ", " + shippingCountry.getText() + ", " + shippingPostalCode.getText()));
                if(model.processOrder(order, billingInfo)) {
                    JOptionPane.showMessageDialog(this, "Order placed successfully!");
                    updateBrowsePanel();
                    cardLayout.show(contentPanel, "Browse");
                }
                else
                    JOptionPane.showMessageDialog(this, "Failed to place order!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
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
        JPanel adminOrUser = new JPanel(new BorderLayout());
        adminOrUser.add(getCentreAlignedJLabel("Sign in as admin or user:"), BorderLayout.NORTH);

        JButton admin = new JButton("Admin");
        JButton user = new JButton("User");
        adminOrUser.add(admin, BorderLayout.WEST);
        adminOrUser.add(user, BorderLayout.EAST);

        AtomicInteger i = new AtomicInteger(-1);
        admin.addActionListener(e -> {
            i.set(0);
            admin.setBackground(Color.GREEN);
            user.setBackground(null);
        });
        user.addActionListener(e -> {
            i.set(1);
            admin.setBackground(null);
            user.setBackground(Color.GREEN);
        });

        JOptionPane.showMessageDialog(this, adminOrUser);

        JPanel signInPanel = new JPanel(new GridLayout(2, 2));
        JTextField username = new JTextField(25);
        JTextField password = new JTextField(16);

        signInPanel.add(new JLabel("Username: "));
        signInPanel.add(username);
        signInPanel.add(new JLabel("Password: "));
        signInPanel.add(password);

        JOptionPane.showMessageDialog(this, signInPanel);

        boolean b = false;
        if(Integer.parseInt(String.valueOf(i)) == 0)
            b = model.signIn(username.getText(), password.getText(), true);
        else if(Integer.parseInt(String.valueOf(i)) == 0)
            b = model.signIn(username.getText(), password.getText(), false);

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

        boolean b = model.addUser(username.getText(), password.getText(), false) && model.signIn(username.getText(), password.getText(), false);
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
