import javax.swing.*;
import java.awt.*;

public class StoreFrame extends JFrame implements StoreView {

    private CardLayout cardLayout;

    private JPanel profilePanel;
    private JPanel browsePanel;
    private JPanel basketPanel;

    private JPanel topPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;

    private final StoreModel model;

    public StoreFrame() {
        super("Look Inna Book");
        model = new StoreModel();
        model.addView(this);

        profilePanelSetup();
        browsePanelSetup();
        basketPanelSetup();

        topPanelSetup();
        contentPanelSetup();
        bottomPanelSetup();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(topPanel);
        mainPanel.add(contentPanel);
        mainPanel.add(bottomPanel);

        this.add(mainPanel);
        this.setLocationRelativeTo(null);
        this.setSize(500, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void profilePanelSetup() {
        profilePanel = new JPanel();
        profilePanel.setBackground(Color.BLUE);
    }

    private void browsePanelSetup() {
        browsePanel = new JPanel();
        browsePanel.setBackground(Color.GREEN);
    }

    private void basketPanelSetup() {
        basketPanel = new JPanel();
        basketPanel.setBackground(Color.BLACK);

//        if(model.getCurrentUser() == null){
//            String username = JOptionPane.showInputDialog(null, "Please Enter Your Username:", "USERNAME", JOptionPane.QUESTION_MESSAGE);
//            String password = JOptionPane.showInputDialog(null, "Please Enter Your Password:", "PASSWORD", JOptionPane.QUESTION_MESSAGE);
//        }
    }

    private void topPanelSetup() {
        JButton profile = new JButton("Profile");
        JButton browse = new JButton("Browse");
        JButton basket = new JButton("Basket");

        profile.addActionListener(e -> cardLayout.show(contentPanel, "Profile"));
        browse.addActionListener(e -> cardLayout.show(contentPanel, "Browse"));
        basket.addActionListener(e -> cardLayout.show(contentPanel, "Basket"));

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(profile);
        topPanel.add(browse);
        topPanel.add(basket);
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

    private boolean login() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2));
        JTextField username = new JTextField(25);
        JTextField password = new JTextField(16);

        loginPanel.add(new JLabel("Username: "));
        loginPanel.add(username);
        loginPanel.add(new JLabel("Password: "));
        loginPanel.add(password);

        JOptionPane.showMessageDialog(this, loginPanel, "Login", JOptionPane.ERROR_MESSAGE);

        return model.login(username.getText(), password.getText());
    }

    private boolean signup() {
        JPanel signupPanel = new JPanel(new GridLayout(2, 2));
        JTextField username = new JTextField(25);
        JTextField password = new JTextField(16);

        signupPanel.add(new JLabel("Username: "));
        signupPanel.add(username);
        signupPanel.add(new JLabel("Password: "));
        signupPanel.add(password);

        JOptionPane.showMessageDialog(this, signupPanel, "Sign up", JOptionPane.ERROR_MESSAGE);

        return model.addUser(username.getText(), password.getText());
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

    @Override
    public String getUserInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }
}
