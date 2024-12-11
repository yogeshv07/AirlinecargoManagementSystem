package airline;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    public UserDashboard() {
        setTitle("User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode

        // Set frame icon
        setIconImage(new ImageIcon("F:/Project/IMG/usericon.png").getImage());

        // Background image setup
        JPanel contentPane = new JPanel() {
            Image backgroundImage = new ImageIcon("F:/Project/IMG/backgroundlogin.jpg").getImage(); // Ensure it's a PNG

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        contentPane.setLayout(null); // Set to absolute layout
        setContentPane(contentPane);

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // User Menu (replaces File Menu)
        JMenu userMenu = new JMenu("User");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        userMenu.add(logoutItem); // Add logout item to user menu
        menuBar.add(userMenu);

        // Cargo Menu
        JMenu cargoMenu = new JMenu("Cargo");
        JMenuItem addCargoItem = new JMenuItem("Add Cargo Item");
        addCargoItem.addActionListener(e -> new CargoModule().addCargoItem());
        JMenuItem displayCargoItem = new JMenuItem("Display Cargo Item");
        displayCargoItem.addActionListener(e -> {
            DisplayCargo displayCargo = new DisplayCargo();
            displayCargo.setVisible(true); // Make the DisplayCargo window visible
        });
        cargoMenu.add(addCargoItem);
        cargoMenu.add(displayCargoItem);
        menuBar.add(cargoMenu);

        // Track Menu
        JMenu trackMenu = new JMenu("Track");
        JMenuItem trackShipmentItem = new JMenuItem("Track Shipment by ID");
        trackShipmentItem.addActionListener(e -> {
            TrackShipmentModule trackShipmentModule = new TrackShipmentModule();
            trackShipmentModule.setVisible(true); // Make the TrackShipmentModule window visible
        });
        trackMenu.add(trackShipmentItem);
        menuBar.add(trackMenu);

        // Payment Menu
        JMenu paymentMenu = new JMenu("Payment");
        JMenuItem payCargoItem = new JMenuItem("Pay for Cargo Item");
        payCargoItem.addActionListener(e -> {
            PaymentModule paymentModule = new PaymentModule(); // Instantiate PaymentModule
            paymentModule.setVisible(true); // Display the payment module window
        });
        paymentMenu.add(payCargoItem);
        menuBar.add(paymentMenu);

        // Add the menu bar to the frame
        setJMenuBar(menuBar);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to the Dashboard!");
        welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE); // Set text color to white for visibility
        welcomeLabel.setBounds(0, 100, getWidth(), 50); // Position label
        contentPane.add(welcomeLabel); // Add welcome label to content pane
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard().setVisible(true));
    }
}
