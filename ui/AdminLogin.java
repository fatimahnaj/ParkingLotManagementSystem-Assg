package ui;

import admin.AdminRepo;

import javax.swing.*;
import java.awt.*;

public class AdminLogin extends JPanel {

    private final MainFrame frame;
    private final AdminRepo repo;

    private JTextField idField;
    private JPasswordField passField;

    public AdminLogin(MainFrame frame, AdminRepo repo) {

        this.frame = frame;
        this.repo = repo;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel idLabel = new JLabel("Admin ID:");
        JLabel passLabel = new JLabel("Password:");

        idField = new JTextField(15);
        passField = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        loginBtn.addActionListener(e -> doLogin());
        backBtn.addActionListener(e -> frame.showScreen("SCREEN1"));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        add(idLabel, gbc);

        gbc.gridx = 1;
        add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(passLabel, gbc);

        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(backBtn, gbc);

        gbc.gridx = 1;
        add(loginBtn, gbc);
    }

    private void doLogin() {

        String id = idField.getText().trim();
        String pw = new String(passField.getPassword()).trim();

        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Check in database
        if (repo.checkAdminLogin(id, pw)) {

            JOptionPane.showMessageDialog(this, "Login successful.");
            frame.showScreen("ADMINDASHBOARD");

        } else {

            JOptionPane.showMessageDialog(this, "Invalid ID or password.");
        }
    }
}