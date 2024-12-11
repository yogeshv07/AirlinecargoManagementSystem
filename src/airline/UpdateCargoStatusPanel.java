package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateCargoStatusPanel extends JPanel {

    private Connection con;
    private Image backgroundImage;

    public UpdateCargoStatusPanel(Connection connection) {
        this.con = connection;

        // Load the background image
        backgroundImage = new ImageIcon("F:/Project/IMG/backgroundlogin.jpg").getImage();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Label and TextField for Cargo ID (TrackingID)
        JLabel cargoIdLabel = new JLabel("Tracking ID:");
        cargoIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField cargoIdField = new JTextField(20);
        cargoIdField.setFont(new Font("Arial", Font.PLAIN, 16));

        // Status selection
        JLabel statusLabel = new JLabel("New Status:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        String[] statusOptions = {"Pending", "In Transit", "Delivered"}; // Added "In Transit"
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        // Buttons
        JButton fetchButton = new JButton("Fetch Details");
        fetchButton.setFont(new Font("Arial", Font.BOLD, 16));
        JButton updateButton = new JButton("Update Status");
        updateButton.setFont(new Font("Arial", Font.BOLD, 16));
        updateButton.setBackground(Color.BLUE);

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        add(cargoIdLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(cargoIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        add(fetchButton, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(statusLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        add(statusComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(updateButton, gbc);

        // Fetch button action listener
        fetchButton.addActionListener(e -> {
            String cargoId = cargoIdField.getText();
            if (cargoId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Tracking ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String details = fetchProductDetails(cargoId);
                if (!details.isEmpty()) {
                    JOptionPane.showMessageDialog(this, details, "Cargo Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No details found for the provided Tracking ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update button action listener
        updateButton.addActionListener(e -> {
            String cargoId = cargoIdField.getText();
            String newStatus = (String) statusComboBox.getSelectedItem();

            try {
                // Update the status in AirCargo table only
                String updateCargoQuery = "UPDATE AirCargo SET Status=? WHERE TrackingID=?";
                PreparedStatement psCargo = con.prepareStatement(updateCargoQuery);
                psCargo.setString(1, newStatus);
                psCargo.setString(2, cargoId);
                int rowsAffected = psCargo.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Cargo status updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cargo not found!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Fetches cargo details and returns them as a formatted string
    private String fetchProductDetails(String trackingId) throws SQLException {
        StringBuilder details = new StringBuilder();
        String query = "SELECT * FROM AirCargo WHERE TrackingID = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, trackingId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            details.append("Tracking ID: ").append(rs.getString("TrackingID")).append("\n")
                    .append("Sender Name: ").append(rs.getString("SenderName")).append("\n")
                    .append("Sender Address: ").append(rs.getString("SenderAddress")).append("\n")
                    .append("Sender Phone: ").append(rs.getString("SenderPhone")).append("\n")
                    .append("Receiver Name: ").append(rs.getString("ReceiverName")).append("\n")
                    .append("Receiver Address: ").append(rs.getString("ReceiverAddress")).append("\n")
                    .append("Receiver Phone: ").append(rs.getString("ReceiverPhone")).append("\n")
                    .append("Cargo Type: ").append(rs.getString("CargoType")).append("\n")
                    .append("Weight: ").append(rs.getDouble("Weight")).append(" kg\n")
                    .append("Charge: ₹").append(rs.getDouble("Charge")).append("\n")
                    .append("Status: ").append(rs.getString("Status")).append("\n");

            String paymentQuery = "SELECT IsPaid FROM Payments WHERE TrackingID = ?";
            PreparedStatement psPayment = con.prepareStatement(paymentQuery);
            psPayment.setString(1, trackingId);
            ResultSet paymentRs = psPayment.executeQuery();

            if (paymentRs.next()) {
                boolean isPaid = paymentRs.getBoolean("IsPaid");
                details.append("Payment Status: ").append(isPaid ? "Paid" : "Not Paid");
            }
        }

        return details.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
