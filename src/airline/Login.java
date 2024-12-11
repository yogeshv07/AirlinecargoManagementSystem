package airline;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {

    private JPanel contentPane;
    private JTextField userField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JCheckBox showPasswordCheckBox; 
 
    private static final String DB_URL = "jdbc:mysql://localhost:3306/login_system";
    private static final String DB_USER = "root";   // Update as needed
    private static final String DB_PASSWORD = "root"; // Update as needed

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Login frame = new Login();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Login() {
        // Set frame properties
        setTitle("Login ACM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen

        // Set the icon for the frame
        setIconImage(Toolkit.getDefaultToolkit().getImage("F:/Project/IMG/login.png"));

        // Set up the background image
        JLabel backgroundLabel = new JLabel(new ImageIcon("F:/Project/IMG/backgroundlogin.jpg"));
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        // Main content panel with transparent background to overlay on background image
        contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(false); // Make content pane transparent
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        backgroundLabel.add(contentPane, BorderLayout.CENTER);

        // Center panel for the form
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        // Form panel setup
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout()); // Using GridBagLayout for better control
        formPanel.setPreferredSize(new Dimension(400, 450)); // Adjust height for additional components

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill the width
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components
        gbc.gridx = 0; // Column index
        gbc.gridy = 0; // Row index

        // Adding a title to the form
        JLabel titleLabel = new JLabel("Login Form", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(titleLabel, gbc);

        // Username label and field
        gbc.gridwidth = 1; // Reset to one column
        gbc.gridy++; // Move to next row

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setForeground(Color.BLACK);
        formPanel.add(userLabel, gbc);

        userField = new JTextField("Enter Username");
        userField.setFont(new Font("Arial", Font.PLAIN, 18));
        userField.setForeground(Color.GRAY);
        setupPlaceholder(userField, "Enter Username");
        gbc.gridx = 1; // Move to second column
        formPanel.add(userField, gbc);

        // Password label and field
        gbc.gridx = 0; // Reset to first column
        gbc.gridy++; // Move to next row

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 18));
        passwordLabel.setForeground(Color.BLACK);
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setEchoChar('*'); // Set default echo character for password
        setupPlaceholder(passwordField, "Password"); // Placeholder for password
        gbc.gridx = 1; // Move to second column
        formPanel.add(passwordField, gbc);

        // Checkbox to show/hide password
        gbc.gridx = 0; // Reset to first column
        gbc.gridy++; // Move to next row

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setOpaque(false); // Make checkbox transparent
        showPasswordCheckBox.setForeground(Color.BLACK); // Change checkbox text color
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(showPasswordCheckBox, gbc);

        // Role selection
        gbc.gridwidth = 1; // Reset to one column
        gbc.gridy++; // Move to next row

        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roleLabel.setForeground(Color.BLACK);
        formPanel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(new String[]{"User", "Employee"});
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1; // Move to second column
        formPanel.add(roleComboBox, gbc);

        // Sign-in button with rounded corners
        gbc.gridx = 0; // Reset to first column
        gbc.gridy++; // Move to next row

        JButton signinButton = new JButton("Login");
        signinButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        signinButton.setForeground(Color.WHITE);
        signinButton.setBackground(new Color(70, 130, 180));
        signinButton.setFocusPainted(false);
        signinButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        signinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signinButton.addActionListener(e -> performSignIn());
        gbc.gridwidth = 2; // Span across two columns
        formPanel.add(signinButton, gbc);

        // Add the formPanel to the centerPanel
        centerPanel.add(formPanel, new GridBagConstraints());
        contentPane.add(centerPanel, BorderLayout.CENTER);

    }

    // Method to toggle password visibility
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            passwordField.setEchoChar('*'); // Hide password
        }
    }

    // Method to set up placeholders for text fields
    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Overloaded method for JPasswordField to manage placeholder functionality
    private void setupPlaceholder(JPasswordField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setEchoChar('*'); // Set echo char to default
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0); // Hide echo
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // Method to handle sign-in logic
    private void performSignIn() {
        String enteredUserName = userField.getText();
        String enteredPassword = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if (selectedRole == null || selectedRole.equals("Select Role")) {
            JOptionPane.showMessageDialog(this, "Please select a role.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, enteredUserName);
            stmt.setString(2, enteredPassword);
            stmt.setString(3, selectedRole.toLowerCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

                if (selectedRole.equalsIgnoreCase("User")) {
                    new UserDashboard().setVisible(true);
                } else if (selectedRole.equalsIgnoreCase("Employee")) {
                    new EmployeeDashboard().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or role selection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
