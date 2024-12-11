package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class RemoveFlightPanel extends JPanel {
    private Connection connection;
    private JTextField flightIdField;
    private BufferedImage backgroundImage;

    public RemoveFlightPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());

        // Load background image
        loadBackgroundImage("F:/Project/IMG/backgroundlogin.jpg"); // Change to your background image path

        // Create a panel for input and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false); // Make input panel transparent for background visibility
        inputPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased insets for better spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label with black color and centered
        JLabel titleLabel = new JLabel("Remove Flight", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Bold font style for the title
        titleLabel.setForeground(Color.BLACK); // Set text color to black
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(titleLabel, gbc);

        // Flight ID Label
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; // Align label to the east
        inputPanel.add(new JLabel("Flight ID:"), gbc);

        // Flight ID Text Field
        flightIdField = new JTextField(10); // Adjusted text field size
        flightIdField.setToolTipText("Enter the Flight ID to search"); // Tooltip for text field
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align text field to the west
        inputPanel.add(flightIdField, gbc);

        // Search Flight Button
        JButton searchButton = new JButton("Search Flight");
        searchButton.setBackground(new Color(30, 144, 255)); // Stylish blue color
        searchButton.setForeground(Color.WHITE); // Button text color
        searchButton.setFocusPainted(false); // Remove focus paint
        searchButton.addActionListener(this::searchFlight);
        searchButton.setToolTipText("Click to search for the flight by ID"); // Tooltip
        gbc.gridwidth = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Center align the button
        inputPanel.add(searchButton, gbc);

        // Remove Flight Button
        JButton removeButton = new JButton("Remove Flight");
        removeButton.setBackground(new Color(220, 20, 60)); // Stylish red color
        removeButton.setForeground(Color.WHITE); // Button text color
        removeButton.setFocusPainted(false); // Remove focus paint
        removeButton.addActionListener(this::removeFlight);
        removeButton.setToolTipText("Click to remove the flight by ID"); // Tooltip
        gbc.gridy = 3; // Move to next row
        inputPanel.add(removeButton, gbc);

        // Add input panel to the main panel
        add(inputPanel, BorderLayout.CENTER);
    }

    private void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void searchFlight(ActionEvent e) {
        String flightId = flightIdField.getText().trim();
        if (flightId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Flight ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM Flights WHERE flight_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Format the flight details into a string to display in the dialog
                String flightDetails = String.format(
                        "Flight ID: %s\nAirline: %s\nDeparture: %s\nArrival: %s\nScheduled Departure: %s\nScheduled Arrival: %s\nActual Departure: %s\nActual Arrival: %s\nStatus: %s",
                        rs.getString("flight_id"),
                        rs.getString("airline"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getTimestamp("scheduled_departure"),
                        rs.getTimestamp("scheduled_arrival"),
                        rs.getTimestamp("actual_departure"),
                        rs.getTimestamp("actual_arrival"),
                        rs.getString("status")
                );
                // Show the flight details in a message dialog
                JOptionPane.showMessageDialog(this, flightDetails, "Flight Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Flight ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching flight: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeFlight(ActionEvent e) {
        String flightId = flightIdField.getText().trim();
        if (flightId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Flight ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM Flights WHERE flight_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, flightId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Flight removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                flightIdField.setText(""); // Clear the input field
            } else {
                JOptionPane.showMessageDialog(this, "Flight ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error removing flight: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

