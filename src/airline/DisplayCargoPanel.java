package airline;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class DisplayCargoPanel extends JPanel {
    private Connection con; // Database connection
    private JTable cargoTable; // Table to display cargo details
    private BufferedImage backgroundImage;

    public DisplayCargoPanel(Connection connection) {
        this.con = connection;
        setLayout(new GridBagLayout()); // Use GridBagLayout

        try {
            // Load the background image
            backgroundImage = ImageIO.read(new File("F:/Project/IMG/backgroundlogin.jpg")); // Update with your image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill the cell horizontally
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // Title label
        JLabel titleLabel = new JLabel("Display Cargo by Status", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK); // Set title color for contrast
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Make the title span across 3 columns
        add(titleLabel, gbc);

        // Status label
        JLabel statusLabel = new JLabel("Status:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset to default
        add(statusLabel, gbc);

        // Combo box for selecting cargo status
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"All", "Pending", "In Transit", "Delivered"});
        gbc.gridx = 1;
        add(statusComboBox, gbc);

        // Button to fetch and display cargo
        JButton displayButton = new JButton("Display Cargo");
        displayButton.addActionListener(e -> {
            String status = (String) statusComboBox.getSelectedItem();
            displayCargoByStatus(status);
        });
        gbc.gridx = 2;
        add(displayButton, gbc);

        // Initialize the table for displaying cargo
        cargoTable = new JTable();
        cargoTable.setFillsViewportHeight(true);
        cargoTable.setRowHeight(30); // Set row height for better visibility
        cargoTable.setShowGrid(true);
        cargoTable.setGridColor(Color.LIGHT_GRAY);

        // Set custom renderer for blue color
        cargoTable.setDefaultRenderer(Object.class, new BlueTableCellRenderer());

        // Set up scroll pane with a larger preferred size
        JScrollPane scrollPane = new JScrollPane(cargoTable);
        scrollPane.setPreferredSize(new Dimension(800, 400)); // Increase preferred size for the scroll pane
        scrollPane.setBorder(BorderFactory.createTitledBorder("Cargo Details")); // Optional: Add a border title

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3; // Make the scroll pane span across 3 columns
        add(scrollPane, gbc);
    }

    // Custom cell renderer for blue color
    private static class BlueTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(new Color(173, 216, 230)); // Light blue background
            cell.setForeground(Color.BLACK); // Text color
            return cell;
        }
    }

    // Method to paint the background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Method to display cargo based on status
    private void displayCargoByStatus(String status) {
        String query = "SELECT ac.*, IFNULL(p.IsPaid, FALSE) AS IsPaid " +
                "FROM AirCargo ac " +
                "LEFT JOIN Payments p ON ac.TrackingID = p.TrackingID";
        if (!status.equals("All")) {
            query += " WHERE ac.Status = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(query)) {
            if (!status.equals("All")) {
                ps.setString(1, status);
            }
            ResultSet rs = ps.executeQuery();

            // Create a table model to hold the cargo data
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Tracking ID", "Sender Name", "Receiver Name", "Cargo Type", "Weight", "Charge", "Status", "Paid"}, 0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("TrackingID"),
                        rs.getString("SenderName"),
                        rs.getString("ReceiverName"),
                        rs.getString("CargoType"),
                        rs.getFloat("Weight"),
                        "₹" + rs.getFloat("Charge"),
                        rs.getString("Status"),
                        rs.getBoolean("IsPaid") ? "Yes" : "No" // Use IsPaid from the Payments table
                });
            }

            // Set the table model to the JTable
            cargoTable.setModel(tableModel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching cargo by status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
