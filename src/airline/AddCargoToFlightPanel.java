package airline;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddCargoToFlightPanel extends JPanel {
    private JTextField trackingIDField;
    private JTextField flightIDField;

    public AddCargoToFlightPanel(Connection con) {
        setLayout(new BorderLayout());

        BackgroundPanel backgroundPanel = new BackgroundPanel("F:/Project/IMG/backgroundlogin.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Add Cargo to Flight", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tracking ID:"), gbc);

        gbc.gridx = 1;
        trackingIDField = new JTextField(15);
        formPanel.add(trackingIDField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Flight ID:"), gbc);

        gbc.gridx = 1;
        flightIDField = new JTextField(15);
        formPanel.add(flightIDField, gbc);

      
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Add Cargo to Flight");
        addButton.addActionListener(e -> {
            String trackingID = trackingIDField.getText().trim();
            String flightID = flightIDField.getText().trim();
            if (!trackingID.isEmpty() && !flightID.isEmpty()) {
                if (validateCargoAndFlight(con, trackingID, flightID)) {
                    addCargoToFlight(con, trackingID, flightID);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both Tracking ID and Flight ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton fetchDetailsButton = new JButton("Fetch Details");
        fetchDetailsButton.addActionListener(e -> displayDetails(con));

        buttonPanel.add(fetchDetailsButton);
        buttonPanel.add(addButton);
        formPanel.add(buttonPanel, gbc);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void displayDetails(Connection con) {
        String trackingID = trackingIDField.getText().trim();
        String flightID = flightIDField.getText().trim();
        if (trackingID.isEmpty() || flightID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both Tracking ID and Flight ID to fetch details.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();

        try {
            
            String cargoQuery = "SELECT * FROM AirCargo WHERE TrackingID = ?";
            try (PreparedStatement cargoStmt = con.prepareStatement(cargoQuery)) {
                cargoStmt.setString(1, trackingID);
                ResultSet cargoRs = cargoStmt.executeQuery();
                if (cargoRs.next()) {
                    details.append("Tracking ID: ").append(cargoRs.getString("TrackingID")).append("\n");
                    details.append("Sender Name: ").append(cargoRs.getString("SenderName")).append("\n");
                    details.append("Receiver Name: ").append(cargoRs.getString("ReceiverName")).append("\n");
                    details.append("Weight: ").append(cargoRs.getString("Weight")).append("\n");
                    details.append("Status: ").append(cargoRs.getString("Status")).append("\n");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Tracking ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

           
            String flightQuery = "SELECT * FROM Flights WHERE flight_id = ?";
            try (PreparedStatement flightStmt = con.prepareStatement(flightQuery)) {
                flightStmt.setString(1, flightID);
                ResultSet flightRs = flightStmt.executeQuery();
                if (flightRs.next()) {
                    details.append("\nFlight ID: ").append(flightRs.getString("flight_id")).append("\n");
                    details.append("Airline: ").append(flightRs.getString("airline")).append("\n");
                    details.append("Departure: ").append(flightRs.getString("departure")).append("\n");
                    details.append("Arrival: ").append(flightRs.getString("arrival")).append("\n");
                    details.append("Status: ").append(flightRs.getString("status")).append("\n");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Flight ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        JOptionPane.showMessageDialog(this, details.toString(), "Cargo and Flight Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateCargoAndFlight(Connection con, String trackingID, String flightID) {
        try {
            String query = "SELECT COUNT(*) FROM AirCargo WHERE TrackingID = ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, trackingID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Cargo Tracking ID does not exist.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            query = "SELECT COUNT(*) FROM Flights WHERE flight_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, flightID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Flight ID does not exist.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addCargoToFlight(Connection con, String trackingID, String flightID) {
        String uniqueAssignmentCode = generateUniqueAssignmentCode(con);

        
        String checkQuery = "SELECT COUNT(*) FROM CargoToFlight WHERE TrackingID = ? AND flight_id = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setString(1, trackingID);
            checkStmt.setString(2, flightID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "This Tracking ID is already assigned to the specified flight.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                return; 
                }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking existing cargo assignment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        String query = "INSERT INTO CargoToFlight (TrackingID, flight_id, AssignmentCode) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, trackingID);
            stmt.setString(2, flightID);
            stmt.setString(3, uniqueAssignmentCode);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cargo added to flight successfully with Assignment Code: " + uniqueAssignmentCode, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding cargo to flight: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateUniqueAssignmentCode(Connection con) {
        String baseCode = "S-";
        int index = 1;

        while (true) {
            String assignmentCode = baseCode + index;
            String query = "SELECT COUNT(*) FROM CargoToFlight WHERE AssignmentCode = ?";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, assignmentCode);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    return assignmentCode; 
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            index++;
        }
    }

    private void clearFields() {
        trackingIDField.setText("");
        flightIDField.setText("");
    }
}


class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(imagePath).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
