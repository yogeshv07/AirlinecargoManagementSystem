package airline;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveCargoFromFlightPanel extends JPanel {
    private JTextField assignmentCodeField;

    public RemoveCargoFromFlightPanel(Connection con) {
        setLayout(new BorderLayout());

        // Load and set background image
        BackgroundPanel backgroundPanel = new BackgroundPanel("F:/Project/IMG/backgroundlogin.jpg"); // Replace with actual path
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Title label
        JLabel titleLabel = new JLabel("Remove Cargo from Flight", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Input Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Assignment Code Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Assignment Code:");
        codeLabel.setForeground(Color.BLACK);
        formPanel.add(codeLabel, gbc);

        // Assignment Code TextField
        gbc.gridx = 1;
        assignmentCodeField = new JTextField(15);
        formPanel.add(assignmentCodeField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton fetchDetailsButton = new JButton("Fetch Details");
        fetchDetailsButton.setBackground(new Color(70, 130, 180));
        fetchDetailsButton.setForeground(Color.WHITE);
        fetchDetailsButton.addActionListener(e -> displayDetails(con));
        buttonPanel.add(fetchDetailsButton);

        JButton removeButton = new JButton("Remove Cargo");
        removeButton.setBackground(new Color(205, 92, 92));
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> {
            String assignmentCode = assignmentCodeField.getText().trim();
            if (!assignmentCode.isEmpty()) {
                removeCargoFromFlight(con, assignmentCode);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the Assignment Code.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(removeButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void displayDetails(Connection con) {
        String assignmentCode = assignmentCodeField.getText().trim();
        if (assignmentCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Assignment Code to fetch details.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();

        String query = "SELECT a.TrackingID, a.SenderName, a.ReceiverName, a.Weight, a.Status, f.flight_id, f.airline, f.departure, f.arrival, f.status " +
                "FROM CargoToFlight ctf " +
                "JOIN AirCargo a ON ctf.TrackingID = a.TrackingID " +
                "JOIN Flights f ON ctf.flight_id = f.flight_id " +
                "WHERE ctf.AssignmentCode = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, assignmentCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                details.append("Tracking ID: ").append(rs.getString("TrackingID")).append("\n");
                details.append("Sender Name: ").append(rs.getString("SenderName")).append("\n");
                details.append("Receiver Name: ").append(rs.getString("ReceiverName")).append("\n");
                details.append("Weight: ").append(rs.getString("Weight")).append("\n");
                details.append("Status: ").append(rs.getString("Status")).append("\n");
                details.append("Flight ID: ").append(rs.getString("flight_id")).append("\n");
                details.append("Airline: ").append(rs.getString("airline")).append("\n");
                details.append("Departure: ").append(rs.getString("departure")).append("\n");
                details.append("Arrival: ").append(rs.getString("arrival")).append("\n");
                details.append("Flight Status: ").append(rs.getString("status")).append("\n");

                // Show details in a dialog box
                JOptionPane.showMessageDialog(this, details.toString(), "Cargo and Flight Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No details found for the given Assignment Code.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeCargoFromFlight(Connection con, String assignmentCode) {
        String deleteQuery = "DELETE FROM CargoToFlight WHERE AssignmentCode = ?";
        try (PreparedStatement stmt = con.prepareStatement(deleteQuery)) {
            stmt.setString(1, assignmentCode);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Cargo removed from flight successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No record found with the specified Assignment Code.", "No Match", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing cargo from flight: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        assignmentCodeField.setText("");
    }
}
