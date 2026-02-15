package ui;
import javax.swing.*;
import java.awt.*;

class Screen2 extends JPanel {

    public Screen2(MainFrame frame) {

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Screen 2", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel btnPanel = new JPanel();

        JButton backBtn = new JButton("Back to Screen 1");
        backBtn.addActionListener(e -> frame.showScreen("SCREEN1"));

        JButton nextBtn = new JButton("Go to Screen 3");
        nextBtn.addActionListener(e -> frame.showScreen("SCREEN3"));

        btnPanel.add(backBtn);
        btnPanel.add(nextBtn);

        add(label, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
}

