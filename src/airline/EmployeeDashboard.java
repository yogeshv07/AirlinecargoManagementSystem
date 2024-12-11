package airline;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class EmployeeDashboard extends JFrame {

    private JPanel contentPane;
    private CardLayout cardLayout;
    private Connection connection;

    public EmployeeDashboard() {
        setTitle("Employee Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode

        // Set frame icon
        setIconImage(new ImageIcon("F:/Project/IMG/employeeicon.png").getImage());

        // Initialize database connection
        initializeConnection();

        // Initialize CardLayout and main content panel
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        contentPane.setBackground(Color.LIGHT_GRAY); // Set a background color
        getContentPane().add(contentPane, BorderLayout.CENTER); // Set contentPane as the main content area

        // Create panels for each functionality and add them to contentPane with unique identifiers
        contentPane.add(new UpdateCargoStatusPanel(connection), "UpdateCargoStatus");
        contentPane.add(new AddUserPanel(connection), "AddUser");
        contentPane.add(new RemoveCargoPanel(connection), "RemoveCargo");
        contentPane.add(new DisplayCargoPanel(connection), "DisplayCargo");
        contentPane.add(new AddFlightPanel(connection), "AddFlight");
        contentPane.add(new AddCargoToFlightPanel(connection), "AddCargoToFlight");
        contentPane.add(new RemoveCargoFromFlightPanel(connection), "RemoveCargoFromFlight");
        contentPane.add(new DisplayAllFlightsPanel(connection), "DisplayAllFlights");
        contentPane.add(new RemoveFlightPanel(connection), "RemoveFlight"); // Add RemoveFlightPanel

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Adding main menu items
        JMenu manageCargoMenu = new JMenu("Manage Cargo");
        menuBar.add(manageCargoMenu);

        // Adding sub-menu items for cargo management
        manageCargoMenu.add(createMenuItem("Update Cargo Status", "UpdateCargoStatus"));
        manageCargoMenu.add(createMenuItem("Remove Cargo", "RemoveCargo"));
        manageCargoMenu.add(createMenuItem("Display All Cargo", "DisplayCargo"));

        // Adding user management menu items
        JMenu userMenu = new JMenu("User Management");
        menuBar.add(userMenu);
        userMenu.add(createMenuItem("Add New User", "AddUser"));

        // Adding flight management menu items
        JMenu flightMenu = new JMenu("Manage Flights");
        menuBar.add(flightMenu);
        flightMenu.add(createMenuItem("Add New Flight", "AddFlight"));
        flightMenu.add(createMenuItem("Add Cargo to Flight", "AddCargoToFlight"));
        flightMenu.add(createMenuItem("Remove Cargo from Flight", "RemoveCargoFromFlight"));
        flightMenu.add(createMenuItem("Display All Flights", "DisplayAllFlights"));
        flightMenu.add(createMenuItem("Remove Flight", "RemoveFlight")); // Add this line

        // Adding logout button to the menu bar
        JMenu logoutMenu = new JMenu("Logout");
        menuBar.add(logoutMenu);
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        logoutMenu.add(logoutItem);
    }

    private JMenuItem createMenuItem(String text, String panelName) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(e -> cardLayout.show(contentPane, panelName)); // Show the panel on menu item selection
        return menuItem;
    }

    private void initializeConnection() {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Database connection failed. Please check your settings.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            dispose(); // Close the frame if connection fails
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeDashboard().setVisible(true));
    }
}
