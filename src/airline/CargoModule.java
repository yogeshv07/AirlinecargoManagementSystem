package airline;

import javax.swing.*;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.sql.*;

public class CargoModule {
    private static int trackingIDCounter = 1; // Counter for generating unique tracking IDs

    public void addCargoItem() {
        JDialog addCargoDialog = new JDialog();
        addCargoDialog.setTitle("Add Cargo");

        // Set dialog size and properties
        addCargoDialog.setSize(800, 600);
        addCargoDialog.setLocationRelativeTo(null);
        addCargoDialog.setModal(true);

        // Set dialog icon
        ImageIcon icon = new ImageIcon("F:/Project/IMG/addcargoicon.png");
        addCargoDialog.setIconImage(icon.getImage());

        // Background panel with image
        BackgroundPanel backgroundPanel = new BackgroundPanel("F:/Project/IMG/backgroundlogin.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        addCargoDialog.add(backgroundPanel);

        // Layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Sender Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(createLabel("Sender Name:"), gbc);

        gbc.gridx = 1;
        JTextField senderNameField = initializeTextField();
        backgroundPanel.add(senderNameField, gbc);

        // Sender Address
        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundPanel.add(createLabel("Sender Address:"), gbc);

        gbc.gridx = 1;
        JTextField senderAddressField = initializeTextField();
        backgroundPanel.add(senderAddressField, gbc);

        // Sender Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundPanel.add(createLabel("Sender Phone No:"), gbc);

        gbc.gridx = 1;
        JTextField senderPhoneField = initializeTextField();
        backgroundPanel.add(senderPhoneField, gbc);

        // Receiver Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        backgroundPanel.add(createLabel("Receiver Name:"), gbc);

        gbc.gridx = 1;
        JTextField receiverNameField = initializeTextField();
        backgroundPanel.add(receiverNameField, gbc);

        // Receiver Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        backgroundPanel.add(createLabel("Receiver Address:"), gbc);

        gbc.gridx = 1;
        JTextField receiverAddressField = initializeTextField();
        backgroundPanel.add(receiverAddressField, gbc);

        // Receiver Phone
        gbc.gridx = 0;
        gbc.gridy = 5;
        backgroundPanel.add(createLabel("Receiver Phone No:"), gbc);

        gbc.gridx = 1;
        JTextField receiverPhoneField = initializeTextField();
        backgroundPanel.add(receiverPhoneField, gbc);

        // Cargo Type
        gbc.gridx = 0;
        gbc.gridy = 6;
        backgroundPanel.add(createLabel("Cargo Type:"), gbc);

        gbc.gridx = 1;
        String[] cargoTypes = {
                "Hazardous Materials", "Live Animals (Pets)", "Perishable Goods",
                "Electronics", "Large Items", "General Cargo", "E-commerce Shipments"
        };
        JComboBox<String> cargoTypeCombo = new JComboBox<>(cargoTypes);
        backgroundPanel.add(cargoTypeCombo, gbc);

        // Weight
        gbc.gridx = 0;
        gbc.gridy = 7;
        backgroundPanel.add(createLabel("Weight (kg):"), gbc);

        gbc.gridx = 1;
        JTextField weightField = initializeTextField();
        backgroundPanel.add(weightField, gbc);

        // Shipping Charge
        gbc.gridx = 0;
        gbc.gridy = 8;
        backgroundPanel.add(createLabel("Shipping Charge (in Rs):"), gbc);

        gbc.gridx = 1;
        JTextField chargeField = initializeTextField();
        chargeField.setEditable(false);
        backgroundPanel.add(chargeField, gbc);

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Cargo");
        addButton.setBackground(new Color(30, 144, 255));
        addButton.setForeground(Color.WHITE);
        backgroundPanel.add(addButton, gbc);

        // Calculate charge dynamically based on weight and cargo type
        weightField.addCaretListener(e -> {
            try {
                float weight = Float.parseFloat(weightField.getText());
                float charge = calculateShippingCharge(weight, (String) cargoTypeCombo.getSelectedItem());
                chargeField.setText(String.valueOf(charge));
            } catch (NumberFormatException ex) {
                chargeField.setText("Invalid weight");
            }
        });

        // Add button logic
        addButton.addActionListener(e -> {
            // Collect inputs
            String senderName = senderNameField.getText().trim();
            String senderAddress = senderAddressField.getText().trim();
            String senderPhone = senderPhoneField.getText().trim();
            String receiverName = receiverNameField.getText().trim();
            String receiverAddress = receiverAddressField.getText().trim();
            String receiverPhone = receiverPhoneField.getText().trim();
            String cargoType = (String) cargoTypeCombo.getSelectedItem();
            String weight = weightField.getText().trim();

            // Validate inputs
            String validationError = validateInputs(senderName, senderAddress, senderPhone,
                                                    receiverName, receiverAddress, receiverPhone,
                                                    weight, cargoType);

            if (validationError != null) {
                JOptionPane.showMessageDialog(addCargoDialog, validationError, "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Generate Tracking ID
                String trackingID = generateTrackingID();

                // Insert into database
                String query = "INSERT INTO AirCargo (TrackingID, SenderName, SenderAddress, SenderPhone, ReceiverName, ReceiverAddress, ReceiverPhone, CargoType, Weight, Charge, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";
                try (Connection con = DatabaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, trackingID);
                    ps.setString(2, senderName);
                    ps.setString(3, senderAddress);
                    ps.setString(4, senderPhone);
                    ps.setString(5, receiverName);
                    ps.setString(6, receiverAddress);
                    ps.setString(7, receiverPhone);
                    ps.setString(8, cargoType);
                    ps.setFloat(9, Float.parseFloat(weight));
                    ps.setFloat(10, calculateShippingCharge(Float.parseFloat(weight), cargoType));
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(addCargoDialog, "Cargo added successfully with Tracking ID: " + trackingID);
                addCargoDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addCargoDialog, "Error adding cargo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Finalize dialog settings
        addCargoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addCargoDialog.setVisible(true);
    }

    // Input validation
    private String validateInputs(String senderName, String senderAddress, String senderPhone,
                                  String receiverName, String receiverAddress, String receiverPhone,
                                  String weight, String cargoType) {
        if (senderName.isEmpty() || !senderName.matches("^[a-zA-Z ]+$")) {
            return "Sender Name cannot be empty and must contain only letters.";
        }
        if (senderAddress.isEmpty() || senderAddress.length() < 5) {
            return "Sender Address must be at least 5 characters long.";
        }
        if (!senderPhone.matches("\\d{10}")) {
            return "Invalid Sender Phone Number. It must be a 10-digit numeric value.";
        }
        if (receiverName.isEmpty() || !receiverName.matches("^[a-zA-Z ]+$")) {
            return "Receiver Name cannot be empty and must contain only letters.";
        }
        if (receiverAddress.isEmpty() || receiverAddress.length() < 5) {
            return "Receiver Address must be at least 5 characters long.";
        }
        if (!receiverPhone.matches("\\d{10}")) {
            return "Invalid Receiver Phone Number. It must be a 10-digit numeric value.";
        }
        if (cargoType == null || cargoType.isEmpty()) {
            return "Please select a valid Cargo Type.";
        }
        try {
            float weightValue = Float.parseFloat(weight);
            if (weightValue <= 0) {
                return "Weight must be a positive numeric value.";
            }
        } catch (NumberFormatException ex) {
            return "Invalid Weight. Please enter a valid numeric value.";
        }
        return null; // No validation errors
    }

    // Shipping charge calculation
    private float calculateShippingCharge(float weight, String cargoType) {
        float rate;
        switch (cargoType) {
            case "Hazardous Materials":
                rate = 300;
                break;
            case "Live Animals (Pets)":
                rate = 750;
                break;
            case "Perishable Goods":
                rate = 200;
                break;
            case "Electronics":
                rate = 150;
                break;
            case "Large Items":
                rate = 250;
                break;
            case "General Cargo":
                rate = 100;
                break;
            case "E-commerce Shipments":
                rate = 30;
                break;
            default:
                rate = 100; // Default rate
        }
        return weight * rate;
    }

    private String generateTrackingID() {
        String trackingID;
        boolean idExists = true;

        while (idExists) {
            trackingID = "T-" + trackingIDCounter; // Use "T-" as the prefix
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM AirCargo WHERE TrackingID = ?")) {
                ps.setString(1, trackingID);
                ResultSet rs = ps.executeQuery();
                rs.next();
                idExists = rs.getInt(1) > 0; // Check if the ID already exists in the database
                if (idExists) {
                    trackingIDCounter++; // Increment counter if the ID exists
                } else {
                    return trackingID; // Return unique tracking ID
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Error checking for existing Tracking ID: " + ex.getMessage());
            }
        }
        return null; // This line should never be reached
    }

    // Helper method to create labels
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    // Helper method to initialize text fields
    private JTextField initializeTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        return textField;
    }
}
