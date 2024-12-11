package airline;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class DisplayCargo extends JFrame {
    private JTextField trackingIdField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private BufferedImage backgroundImage;

    public DisplayCargo() {
        setTitle("Display Cargo Item");
        setSize(1000, 600); // Increased frame size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the icon for the frame
        try {
            setIconImage(ImageIO.read(new File("F:/Project/IMG/displaycargo.png"))); // Update with your icon path
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("F:/Project/IMG/backgroundlogin.jpg")); // Update with your image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Layout setup
        setLayout(new BorderLayout());
        JPanel mainPanel = new BackgroundPanel();

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Tracking ID:"));
        trackingIdField = new JTextField(20);
        inputPanel.add(trackingIdField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchButtonListener());
        inputPanel.add(searchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Result table setup
        String[] columnNames = {"Field", "Details"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFillsViewportHeight(true);
        resultTable.setEnabled(false); // Make table non-editable

        mainPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        add(mainPanel);
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String trackingId = trackingIdField.getText();
            if (trackingId.isEmpty()) {
                JOptionPane.showMessageDialog(DisplayCargo.this, "Please enter a Tracking ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            displayCargoDetails(trackingId);
        }
    }

    private void displayCargoDetails(String trackingId) {
        String cargoQuery = "SELECT *, CreatedAt FROM AirCargo WHERE TrackingID = ?"; // Include CreatedAt
        String paymentQuery = "SELECT * FROM Payments WHERE TrackingID = ? ORDER BY PaymentDate DESC LIMIT 1"; // Fetch latest payment record

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement cargoStatement = connection.prepareStatement(cargoQuery);
             PreparedStatement paymentStatement = connection.prepareStatement(paymentQuery)) {

            cargoStatement.setString(1, trackingId);
            ResultSet cargoResult = cargoStatement.executeQuery();

            // Clear previous results
            tableModel.setRowCount(0);

            if (cargoResult.next()) {
                // Fetch and display cargo details in a structured format
                tableModel.addRow(new Object[]{"Tracking ID", cargoResult.getString("TrackingID")});
                tableModel.addRow(new Object[]{"Sender Name", cargoResult.getString("SenderName")});
                tableModel.addRow(new Object[]{"Sender Address", cargoResult.getString("SenderAddress")});
                tableModel.addRow(new Object[]{"Sender Phone", cargoResult.getString("SenderPhone")});
                tableModel.addRow(new Object[]{"Receiver Name", cargoResult.getString("ReceiverName")});
                tableModel.addRow(new Object[]{"Receiver Address", cargoResult.getString("ReceiverAddress")});
                tableModel.addRow(new Object[]{"Receiver Phone", cargoResult.getString("ReceiverPhone")});
                tableModel.addRow(new Object[]{"Cargo Type", cargoResult.getString("CargoType")});
                tableModel.addRow(new Object[]{"Weight", String.format("%.2f kg", cargoResult.getFloat("Weight"))});
                tableModel.addRow(new Object[]{"Charge", String.format("%.2f", cargoResult.getFloat("Charge"))});

                // Format and add CreatedAt date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Desired date format
                String createdAt = dateFormat.format(cargoResult.getTimestamp("CreatedAt")); // Format CreatedAt
                tableModel.addRow(new Object[]{"Created At", createdAt}); // Add to table

                // Set up the payment details
                paymentStatement.setString(1, trackingId);
                ResultSet paymentResult = paymentStatement.executeQuery();

                if (paymentResult.next()) {
                    tableModel.addRow(new Object[]{"Payment Status", "Paid"});
                    tableModel.addRow(new Object[]{"Amount Paid", String.format("₹%.2f", paymentResult.getFloat("Amount"))});
                    tableModel.addRow(new Object[]{"Payment Date", dateFormat.format(paymentResult.getTimestamp("PaymentDate"))});
                    tableModel.addRow(new Object[]{"Card Holder", paymentResult.getString("CardHolderName")});
                    tableModel.addRow(new Object[]{"Card Number (Last 4)", "**** **** **** " + paymentResult.getString("CardNumber").substring(12)});
                } else {
                    tableModel.addRow(new Object[]{"Payment Status", "Pending"});
                }

            } else {
                JOptionPane.showMessageDialog(this, "No cargo found with the provided Tracking ID.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayCargo().setVisible(true));
    }
}
