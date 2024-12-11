package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PaymentModule extends JFrame {

    private JTextField trackingIdField;
    private JTextField amountField;
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JTextField cardHolderNameField;
    private JButton payButton;
    private JPanel contentPane;

    // Path to your background image
    private final String backgroundImagePath = "F:/Project/IMG/backgroundlogin.jpg"; // Change this to your image path

    public PaymentModule() {
        // Frame setup
        setTitle("Payment Module");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600); // Increase frame size
        setLocationRelativeTo(null); // Center the frame

        // Set frame icon
        try {
            setIconImage(new ImageIcon("F:/Project/IMG/payment.png").getImage());
        } catch (Exception e) {
            System.err.println("Icon image not found!");
        }

        // Content panel setup with GridBagLayout
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load and draw the background image
                ImageIcon background = new ImageIcon(backgroundImagePath);
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // Tracking ID label and input field
        JLabel trackingIdLabel = new JLabel("Tracking ID:");
        trackingIdLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment
        contentPane.add(trackingIdLabel, gbc);

        trackingIdField = new JTextField(20);
        trackingIdField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        gbc.gridx = 1;
        contentPane.add(trackingIdField, gbc);

        // Amount label and input field
        JLabel amountLabel = new JLabel("Amount (₹):");
        amountLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(amountLabel, gbc);

        amountField = new JTextField(20);
        amountField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        amountField.setEditable(false); // Make amount field read-only
        gbc.gridx = 1;
        contentPane.add(amountField, gbc);

        // Card Holder Name label and input field
        JLabel cardHolderNameLabel = new JLabel("Card Holder Name:");
        cardHolderNameLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPane.add(cardHolderNameLabel, gbc);

        cardHolderNameField = new JTextField(20);
        cardHolderNameField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        gbc.gridx = 1;
        contentPane.add(cardHolderNameField, gbc);

        // Card Number label and input field
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPane.add(cardNumberLabel, gbc);

        cardNumberField = new JTextField(20);
        cardNumberField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        gbc.gridx = 1;
        contentPane.add(cardNumberField, gbc);

        // Expiry Date label and input field
        JLabel expiryDateLabel = new JLabel("Expiry Date (MM/YY):");
        expiryDateLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPane.add(expiryDateLabel, gbc);

        expiryDateField = new JTextField(20);
        expiryDateField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        gbc.gridx = 1;
        contentPane.add(expiryDateField, gbc);

        // CVV label and input field
        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPane.add(cvvLabel, gbc);

        cvvField = new JTextField(4);
        cvvField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        gbc.gridx = 1;
        contentPane.add(cvvField, gbc);

        // Pay button
        payButton = new JButton("Pay Now");
        payButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        payButton.addActionListener(e -> proceedWithPayment());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(payButton, gbc);

        // Add content pane to frame
        setContentPane(contentPane);

        // Action listener for tracking ID input
        trackingIdField.addActionListener(e -> fetchCargoAmount());
    }

    // Fetch cargo amount and check if it's already paid
    private void fetchCargoAmount() {
        String trackingId = trackingIdField.getText();
        if (trackingId == null || trackingId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tracking ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isAlreadyPaid(trackingId)) {
            JOptionPane.showMessageDialog(this, "Payment already made for this Tracking ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            amountField.setText(""); // Clear amount field
            payButton.setEnabled(false); // Disable pay button
        } else {
            Float amount = getCargoAmount(trackingId);
            if (amount != null) {
                amountField.setText(String.format("%.2f", amount));
                payButton.setEnabled(true); // Enable pay button
            } else {
                JOptionPane.showMessageDialog(this, "No cargo found with the provided Tracking ID.");
                amountField.setText(""); // Clear amount field if no cargo found
                payButton.setEnabled(false); // Disable pay button if no cargo is found
            }
        }
    }

    // Check if payment is already made
    private boolean isAlreadyPaid(String trackingId) {
        String query = "SELECT IsPaid FROM Payments WHERE TrackingID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trackingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("IsPaid");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking payment status: " + e.getMessage());
        }
        return false;
    }

    // Fetch cargo amount based on tracking ID
    private Float getCargoAmount(String trackingId) {
        String query = "SELECT Charge FROM AirCargo WHERE TrackingID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trackingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getFloat("Charge"); // Return charge
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking cargo amount: " + e.getMessage());
        }
        return null; // Return null if no amount found
    }

    // Proceed with payment after fetching details
    private void proceedWithPayment() {
        String trackingId = trackingIdField.getText();
        String cardNumber = cardNumberField.getText();
        String expiryDate = expiryDateField.getText();
        String cvv = cvvField.getText();
        Float amount;

        try {
            amount = Float.parseFloat(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            return;
        }

        if (!validateExpiryDate(expiryDate)) {
            return; // Exit if expiry date is invalid
        }

        if (!validateCVV(cvv)) {
            return; // Exit if CVV is invalid
        }

        // Process payment logic here (e.g., integration with payment gateway)

        // Mark payment as completed
        markPaymentAsCompleted(trackingId, amount);

        // Clear all fields after successful payment
        clearFields();
        JOptionPane.showMessageDialog(this, "Payment successfully processed.");
    }

    // Validate expiry date (MM/YY format and check if it's expired)
    private boolean validateExpiryDate(String expiryDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        sdf.setLenient(false); // Set lenient to false to avoid incorrect parsing

        try {
            // Parse the expiry date entered by the user
            Date expiry = sdf.parse(expiryDate);

            // Get the current date (set to the first of the current month)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month
            Date currentDate = calendar.getTime();

            // Check if the expiry date is before the current date
            if (expiry.before(currentDate)) {
                // If the expiry date is in the past, notify the user
                JOptionPane.showMessageDialog(this, "The card has expired. Please check the expiry date and try again.", "Card Expired", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true; // If expiry date is valid and not expired
        } catch (Exception e) {
            // If there is an error parsing the expiry date
            JOptionPane.showMessageDialog(this, "Invalid expiry date format. Please enter in MM/YY format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Validate CVV
    private boolean validateCVV(String cvv) {
        if (cvv.length() != 3 && cvv.length() != 4) {
            JOptionPane.showMessageDialog(this, "Invalid CVV. Please enter a valid CVV.", "Invalid CVV", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // Mark payment as completed in the database
    private void markPaymentAsCompleted(String trackingId, float amount) {
        String query = "INSERT INTO Payments (TrackingID, Amount, PaymentDate, CardNumber, ExpiryDate, CVV, CardHolderName, IsPaid) VALUES (?, ?, NOW(), ?, ?, ?, ?, TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trackingId);
            ps.setFloat(2, amount);
            ps.setString(3, cardNumberField.getText());
            ps.setString(4, expiryDateField.getText());
            ps.setString(5, cvvField.getText());
            ps.setString(6, cardHolderNameField.getText());
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error marking payment as completed: " + e.getMessage());
        }
    }

    // Clear all input fields
    private void clearFields() {
        trackingIdField.setText("");
        amountField.setText("");
        cardNumberField.setText("");
        expiryDateField.setText("");
        cvvField.setText("");
        cardHolderNameField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaymentModule frame = new PaymentModule();
            frame.setVisible(true);
        });
    }
}
