package airline;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AddFlightPanel extends JPanel {
    private Connection con;
    private BufferedImage backgroundImage;

    public AddFlightPanel(Connection con) {
        this.con = con;
        loadBackgroundImage("F:/Project/IMG/backgroundlogin.jpg");

        setLayout(new BorderLayout());
        CustomBackgroundPanel backgroundPanel = new CustomBackgroundPanel(backgroundImage);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Add New Flight", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("Flight ID:"), gbc);
        JTextField flightIdField = new JTextField(15);
        flightIdField.setEditable(false);
        flightIdField.setText(generateFlightId());
        gbc.gridx = 1;
        mainPanel.add(flightIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Airline:"), gbc);
        gbc.gridx = 1;
        JTextField airlineField = new JTextField(15);
        mainPanel.add(airlineField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Departure Airport:"), gbc);
        gbc.gridx = 1;
        JTextField departureField = new JTextField(15);
        mainPanel.add(departureField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Arrival Airport:"), gbc);
        gbc.gridx = 1;
        JTextField arrivalField = new JTextField(15);
        mainPanel.add(arrivalField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Scheduled Departure Time:"), gbc);
        gbc.gridx = 1;
        JButton scheduledDepartureButton = createDateTimeButton("Select Date and Time");
        mainPanel.add(scheduledDepartureButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Scheduled Arrival Time:"), gbc);
        gbc.gridx = 1;
        JButton scheduledArrivalButton = createDateTimeButton("Select Date and Time");
        mainPanel.add(scheduledArrivalButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Actual Departure Time:"), gbc);
        gbc.gridx = 1;
        JButton actualDepartureButton = createDateTimeButton("Select Date and Time");
        mainPanel.add(actualDepartureButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Actual Arrival Time:"), gbc);
        gbc.gridx = 1;
        JButton actualArrivalButton = createDateTimeButton("Select Date and Time");
        mainPanel.add(actualArrivalButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"On Time", "Delayed", "In Transit", "Landed"});
        mainPanel.add(statusComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Flight");
        addButton.setBackground(Color.BLUE);
        addButton.setForeground(Color.WHITE);
        mainPanel.add(addButton, gbc);

        backgroundPanel.add(mainPanel);
        add(backgroundPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            String flightIdText = flightIdField.getText();
            String airline = airlineField.getText();
            String departure = departureField.getText();
            String arrival = arrivalField.getText();
            String scheduledDeparture = scheduledDepartureButton.getText();
            String scheduledArrival = scheduledArrivalButton.getText();
            String actualDeparture = actualDepartureButton.getText();
            String actualArrival = actualArrivalButton.getText();
            String status = (String) statusComboBox.getSelectedItem();

            
            if (scheduledDeparture.equals("Select Date and Time") ||
                    scheduledArrival.equals("Select Date and Time") ||
                    actualDeparture.equals("Select Date and Time") ||
                    actualArrival.equals("Select Date and Time")) {
                JOptionPane.showMessageDialog(this, "Please select valid date and time for all datetime fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            if (checkFlightIdExists(flightIdText)) {
                JOptionPane.showMessageDialog(this, "Flight ID already exists. Please choose another ID.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                
                addFlight(flightIdText, airline, departure, arrival, scheduledDeparture, scheduledArrival, actualDeparture, actualArrival, status);
                clearFields(flightIdField, airlineField, departureField, arrivalField, scheduledDepartureButton, scheduledArrivalButton, actualDepartureButton, actualArrivalButton);
                flightIdField.setText(generateFlightId());
            }
        });
    }

    private String generateFlightId() {
        String baseQuery = "SELECT MAX(CAST(SUBSTRING(flight_id, 3) AS UNSIGNED)) FROM flights"; // Adjusted query for ID formatting
        try (PreparedStatement stmt = con.prepareStatement(baseQuery);
             ResultSet rs = stmt.executeQuery()) {
            int newId = 1; 
            if (rs.next()) {
                newId = rs.getInt(1) + 1; 
            }
            return "F-" + newId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "F-1"; 
    }

    private JButton createDateTimeButton(String text) {
        JButton dateTimeButton = new JButton(text);
        dateTimeButton.addActionListener(e -> {
            JSpinner dateTimeSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dateTimeSpinner, "yyyy-MM-dd HH:mm:ss");
            dateTimeSpinner.setEditor(editor);

            int result = JOptionPane.showConfirmDialog(this, dateTimeSpinner, "Select Date and Time", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateTimeButton.setText(sdf.format(dateTimeSpinner.getValue())); 
            }
        });
        return dateTimeButton;
    }

    private void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }
    }

    class CustomBackgroundPanel extends JPanel {
        private BufferedImage image;

        public CustomBackgroundPanel(BufferedImage image) {
            this.image = image;
            setLayout(new GridBagLayout());
        }

        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private boolean checkFlightIdExists(String flightId) {
        String query = "SELECT COUNT(*) FROM flights WHERE flight_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addFlight(String flightId, String airline, String departure, String arrival,
                           String scheduledDeparture, String scheduledArrival, String actualDeparture,
                           String actualArrival, String status) {
        String query = "INSERT INTO flights (flight_id, airline, departure, arrival, " +
                "scheduled_departure, scheduled_arrival, actual_departure, actual_arrival, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, flightId);
            stmt.setString(2, airline);
            stmt.setString(3, departure);
            stmt.setString(4, arrival);
            stmt.setString(5, scheduledDeparture);
            stmt.setString(6, scheduledArrival);
            stmt.setString(7, actualDeparture);
            stmt.setString(8, actualArrival);
            stmt.setString(9, status);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Flight added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(JTextField flightIdField, JTextField airlineField, JTextField departureField, JTextField arrivalField,
                             JButton scheduledDepartureButton, JButton scheduledArrivalButton, JButton actualDepartureButton, JButton actualArrivalButton) {
        airlineField.setText("");
        departureField.setText("");
        arrivalField.setText("");
        scheduledDepartureButton.setText("Select Date and Time");
        scheduledArrivalButton.setText("Select Date and Time");
        actualDepartureButton.setText("Select Date and Time");
        actualArrivalButton.setText("Select Date and Time");
    }
}
