package code_files.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import code_files.User;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(MovieLibraryApp app) {
        // Set a BorderLayout to center the contents of the panel
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Create the login panel with BoxLayout for vertical arrangement
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setPreferredSize(new Dimension(350, 250));

        // Add a border with a title
        loginFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Welcome to Movie Library",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                Color.BLACK));

        // Add vertical space (padding) at the top
        loginFormPanel.add(Box.createVerticalStrut(20));
        Box usernameBox = Box.createHorizontalBox();
        JLabel usernameJLabel = new JLabel("Username: ");
        usernameJLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField usernameField = new JTextField(10);
        usernameBox.add(Box.createHorizontalStrut(10));
        usernameBox.add(usernameJLabel);
        usernameBox.add(Box.createHorizontalStrut(10));
        usernameBox.add(usernameField);
        usernameBox.add(Box.createHorizontalStrut(10));

        // Username label and text field
        loginFormPanel.add(usernameBox);

        // Add a small vertical space between components
        loginFormPanel.add(Box.createVerticalStrut(10));

        // Password label and password field
        Box passwordBox = Box.createHorizontalBox();
        JLabel passwordJLabel = new JLabel("Password: ");
        JPasswordField passwordField = new JPasswordField(10);
        passwordJLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordBox.add(Box.createHorizontalStrut(10));
        passwordBox.add(passwordJLabel);
        passwordBox.add(Box.createHorizontalStrut(10));
        passwordBox.add(passwordField);
        passwordBox.add(Box.createHorizontalStrut(10));

        // Username label and text field
        loginFormPanel.add(passwordBox);

        // Add a bit more space at the bottom of the form
        loginFormPanel.add(Box.createVerticalStrut(20));

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginFormPanel.add(loginButton);

        // Add the login form panel to the main panel
        add(loginFormPanel, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = null;
            try {
                // Attempt to log in the user with provided username and password
                user = User.doLogin(username, password);
            } catch (Exception ex) {
                // Handle any login failure due to exceptions (e.g., network errors)
                JOptionPane.showMessageDialog(this, "An error occurred while logging in: " + ex.getMessage(),
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Log the exception stack trace for debugging purposes
            }

            if (user != null) {
                // If login is successful, set the current user and switch to the search panel
                app.setCurrentUser(user); // Set the current logged-in user
                app.switchToPanel("search"); // Switch to the search panel
            } else {
                // If login fails, show an error message and clear the login fields
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                usernameField.setText(""); // Clear the username field
                passwordField.setText(""); // Clear the password field
            }
        });

    }

    public void setUsername(String username) {
        usernameField.setText(username);
    }

    public void setPassword(String password) {
        passwordField.setText(password);
    }


}
