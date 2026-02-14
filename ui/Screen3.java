package ui;
import java.awt.*;
import javax.swing.*;

class Screen3 extends JPanel {

    public Screen3(MainFrame frame) {

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Screen 3", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        JButton backBtn = new JButton("Back to Screen 1");
        backBtn.addActionListener(e -> frame.showScreen("SCREEN1"));

        add(label, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }
}

