package airline;

import javax.swing.*;
import java.awt.*;

public class AddEmployeePanel extends JPanel {

    public AddEmployeePanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Employee", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        formPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Position:"));
        JTextField positionField = new JTextField();
        formPanel.add(positionField);

        formPanel.add(new JLabel("Contact Info:"));
        JTextField contactInfoField = new JTextField();
        formPanel.add(contactInfoField);

        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String position = positionField.getText();
            String contactInfo = contactInfoField.getText();
            addEmployee(name, position, contactInfo); 
            });
        add(formPanel, BorderLayout.CENTER);
        add(addButton, BorderLayout.SOUTH);
    }

    private void addEmployee(String name, String position, String contactInfo) {
        
    }
}
