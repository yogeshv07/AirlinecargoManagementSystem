package airline;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveCargoPanel extends JPanel {
    private Connection con;
    private JTextField trackingIDField;
    private Image backgroundImage;

    public RemoveCargoPanel(Connection connection) {
        this.con = connection;

        // Set layout and load background image
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Load the background image
        backgroundImage = new ImageIcon("F:/Project/IMG/backgroundlogin.jpg").getImage();

        // Title label
        JLabel titleLabel = new JLabel("Remove Cargo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // Input field for Tracking ID
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel trackingIDLabel = new JLabel("Tracking ID:");
        trackingIDLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(trackingIDLabel, gbc);

        trackingIDField = new JTextField(20);
        trackingIDField.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(trackingIDField, gbc);

        // Fetch button
        JButton fetchButton = new JButton("Fetch Details");
        fetchButton.setFont(new Font("Arial", Font.BOLD, 20));
        fetchButton.setPreferredSize(new Dimension(200, 40));
        fetchButton.setBackground(new Color(0, 123, 255)); // Blue color
        fetchButton.setForeground(Color.WHITE); // Text color
        fetchButton.addActionListener(e -> {
            String trackingID = trackingIDField.getText();
            if (!trackingID.isEmpty()) {
                fetchCargoDetails(trackingID);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a Tracking ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(fetchButton, gbc);

        // Remove button
        JButton removeButton = new JButton("Remove Cargo");
        removeButton.setFont(new Font("Arial", Font.BOLD, 20));
        removeButton.setPreferredSize(new Dimension(200, 40));
        removeButton.setBackground(new Color(220, 53, 69)); // Red color
        removeButton.setForeground(Color.WHITE); // Text color
        removeButton.addActionListener(e -> {
            String trackingID = trackingIDField.getText();
            if (!trackingID.isEmpty()) {
                removeCargo(trackingID);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a Tracking ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(removeButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void fetchCargoDetails(String trackingID) {
        String query = "SELECT * FROM AirCargo WHERE TrackingID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, trackingID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Build a formatted string for cargo details
                StringBuilder details = new StringBuilder();
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

                JOptionPane.showMessageDialog(this, details.toString(), "Cargo Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No cargo found for the provided Tracking ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching cargo details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeCargo(String trackingID) {
        String deletePaymentsQuery = "DELETE FROM Payments WHERE TrackingID = ?";
        String deleteCargoToFlightQuery = "DELETE FROM CargoToFlight WHERE TrackingID = ?";
        String deleteAirCargoQuery = "DELETE FROM AirCargo WHERE TrackingID = ?";

        try {
            // Start a transaction
            con.setAutoCommit(false);

            // Step 1: Delete from Payments table
            try (PreparedStatement psPayments = con.prepareStatement(deletePaymentsQuery)) {
                psPayments.setString(1, trackingID);
                psPayments.executeUpdate();
            }

            // Step 2: Delete from CargoToFlight table
            try (PreparedStatement psCargoToFlight = con.prepareStatement(deleteCargoToFlightQuery)) {
                psCargoToFlight.setString(1, trackingID);
                psCargoToFlight.executeUpdate();
            }

            // Step 3: Delete from AirCargo table
            try (PreparedStatement psAirCargo = con.prepareStatement(deleteAirCargoQuery)) {
                psAirCargo.setString(1, trackingID);
                int rowsAffected = psAirCargo.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Cargo record and related data removed successfully!");
                    trackingIDField.setText(""); // Clear input field
                } else {
                    JOptionPane.showMessageDialog(this, "No cargo found with the provided Tracking ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // Commit the transaction
            con.commit();
        } catch (SQLException e) {
            // Roll back the transaction in case of any errors
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
                JOptionPane.showMessageDialog(this, "Error during rollback: " + rollbackEx.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(this, "Error removing cargo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Reset auto-commit to default
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error resetting auto-commit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
