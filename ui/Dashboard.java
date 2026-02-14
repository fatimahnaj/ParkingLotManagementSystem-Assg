package ui;
import java.awt.*;
import javax.swing.*;

class Dashboard extends JPanel {

    public Dashboard(MainFrame frame) {

        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("sini screen dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> frame.showScreen("SCREEN2"));
        centerPanel.add(label, BorderLayout.CENTER);
        centerPanel.add(registerBtn, BorderLayout.SOUTH);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton adminBtn = new JButton("admin");
        adminBtn.addActionListener(e -> frame.showScreen("SCREEN3"));
        bottomPanel.add(adminBtn, BorderLayout.EAST);

        JButton customerBtn = new JButton("Customer");
        customerBtn.addActionListener(e-> frame.showScreen("CUSTOMERSCREEN"));
        bottomPanel.add(customerBtn, BorderLayout.WEST);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

