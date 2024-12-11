package airline;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;

public class TrackShipmentModule extends JFrame {
    private JTextField trackingIdField;

    public TrackShipmentModule() {
        setTitle("Track Shipment");
        setSize(800, 600); // Increased frame size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the icon for the frame
        try {
            setIconImage(ImageIO.read(new File("F:/Project/IMG/track.png"))); // Update with your icon path
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a panel with a background image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false); // Make the input panel transparent
        inputPanel.add(new JLabel("Enter Tracking ID:"));
        trackingIdField = new JTextField(20);
        inputPanel.add(trackingIdField);

        JButton searchButton = new JButton("Track");
        searchButton.addActionListener(new TrackButtonListener());
        inputPanel.add(searchButton);

        backgroundPanel.add(inputPanel, BorderLayout.NORTH);

        add(backgroundPanel);
    }

    private class TrackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String trackingId = trackingIdField.getText();
            if (trackingId.isEmpty()) {
                JOptionPane.showMessageDialog(TrackShipmentModule.this, "Please enter a Tracking ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            trackShipmentDetails(trackingId);
        }
    }

    private void trackShipmentDetails(String trackingId) {
        String query = "SELECT *, CreatedAt FROM AirCargo WHERE TrackingID = ?"; // Include CreatedAt in the query

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, trackingId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Fetch and display shipment details
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date format
                String formattedDate = dateFormat.format(resultSet.getTimestamp("CreatedAt")); // Format the CreatedAt timestamp

                String shipmentDetails = "<html><b>Tracking ID:</b> " + resultSet.getString("TrackingID") + "<br>"
                        + "<b>Sender Name:</b> " + resultSet.getString("SenderName") + "<br>"
                        + "<b>Sender Address:</b> " + resultSet.getString("SenderAddress") + "<br>"
                        + "<b>Sender Phone:</b> " + resultSet.getString("SenderPhone") + "<br>"
                        + "<b>Receiver Name:</b> " + resultSet.getString("ReceiverName") + "<br>"
                        + "<b>Receiver Address:</b> " + resultSet.getString("ReceiverAddress") + "<br>"
                        + "<b>Receiver Phone:</b> " + resultSet.getString("ReceiverPhone") + "<br>"
                        + "<b>Cargo Type:</b> " + resultSet.getString("CargoType") + "<br>"
                        + "<b>Weight:</b> " + resultSet.getFloat("Weight") + " kg<br>"
                        + "<b>Charge:</b> Rs " + resultSet.getFloat("Charge") + "<br>"
                        + "<b>Status:</b> " + resultSet.getString("Status") + "<br>"
                        + "<b>Added At:</b> " + formattedDate + "</html>";

                // Display shipment details in a dialog
                JOptionPane.showMessageDialog(this, shipmentDetails, "Shipment Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No shipment found with the provided Tracking ID.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom JPanel to set background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load the background image (make sure to place your image file in the right directory)
                backgroundImage = new ImageIcon("F:/Project/IMG/backgroundlogin.jpg").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TrackShipmentModule().setVisible(true));
    }
}
