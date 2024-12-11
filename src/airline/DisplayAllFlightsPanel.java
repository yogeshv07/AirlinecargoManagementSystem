package airline;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.table.JTableHeader;

public class DisplayAllFlightsPanel extends JPanel {
    private Connection connection;
    private JTable flightDetailsTable;
    private DefaultTableModel tableModel;
    private BufferedImage backgroundImage;
    private JComboBox<String> statusComboBox; // ComboBox for status filter

    public DisplayAllFlightsPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());
        loadBackgroundImage("F:/Project/IMG/backgroundlogin.jpg"); // Change to your background image path

        // Title Label
        JLabel titleLabel = new JLabel("All Flights", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24)); // Use bold font for the title
        titleLabel.setForeground(Color.WHITE); // Set title color for visibility
        add(titleLabel, BorderLayout.NORTH);

        // Panel for search and filter
        JPanel filterPanel = new JPanel();
        filterPanel.setOpaque(false); // Make the panel transparent for background visibility
        filterPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center the components

        // Status selection combo box
        String[] statuses = {"All", "In Transit", "On Time", "Delayed", "Landed"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setPreferredSize(new Dimension(150, 30)); // Set preferred size for combo box
        filterPanel.add(statusComboBox);

        // Search Button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchByStatus);
        filterPanel.add(searchButton);

        add(filterPanel, BorderLayout.NORTH); // Add filter panel to the top

        // Create table for displaying flight details
        tableModel = new DefaultTableModel(new String[]{
                "Flight ID", "Airline", "Departure", "Arrival", "Scheduled Departure",
                "Scheduled Arrival", "Actual Departure", "Actual Arrival", "Status"}, 0);
        flightDetailsTable = new JTable(tableModel);
        flightDetailsTable.setFillsViewportHeight(true);
        flightDetailsTable.setFont(new Font("Arial", Font.PLAIN, 12)); // Standard font for the table
        flightDetailsTable.setRowHeight(22); // Slightly increased row height
        centerTableContent(flightDetailsTable); // Center the table content

        // Set table background color
        flightDetailsTable.setBackground(new Color(173, 216, 230)); // Light blue background for the table
        flightDetailsTable.setForeground(Color.BLACK); // Text color for better readability
        flightDetailsTable.setGridColor(new Color(0, 102, 204)); // Blue grid color
        flightDetailsTable.setSelectionBackground(new Color(30, 144, 255)); // Blue selection color
        flightDetailsTable.setSelectionForeground(Color.WHITE); // White text when selected

        // Set header background color
        JTableHeader header = flightDetailsTable.getTableHeader();
        header.setBackground(new Color(0, 102, 204)); // Dark blue background for the header
        header.setForeground(Color.WHITE); // White text for the header

        // Load all flight data
        loadFlightData();

        // Add table to the main panel
        add(new JScrollPane(flightDetailsTable), BorderLayout.CENTER);
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

    private void centerTableContent(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadFlightData() {
        String query = "SELECT * FROM Flights"; // Adjust the SQL query as needed
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                // Add flight details to the table
                tableModel.addRow(new Object[]{
                        rs.getString("flight_id"),
                        rs.getString("airline"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getTimestamp("scheduled_departure"),
                        rs.getTimestamp("scheduled_arrival"),
                        rs.getTimestamp("actual_departure"),
                        rs.getTimestamp("actual_arrival"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByStatus(ActionEvent e) {
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        String query = "SELECT * FROM Flights";

        // Modify query based on selected status
        if (!"All".equals(selectedStatus)) {
            query += " WHERE status = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (!"All".equals(selectedStatus)) {
                stmt.setString(1, selectedStatus);
            }
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                // Add flight details to the table
                tableModel.addRow(new Object[]{
                        rs.getString("flight_id"),
                        rs.getString("airline"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getTimestamp("scheduled_departure"),
                        rs.getTimestamp("scheduled_arrival"),
                        rs.getTimestamp("actual_departure"),
                        rs.getTimestamp("actual_arrival"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving flights: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
