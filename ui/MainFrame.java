package ui;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    public MainFrame() {

        setTitle("Multi Screen App");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout setup
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Create screens
        Dashboard screen1 = new Dashboard(this);
        Screen2 screen2 = new Screen2(this);
        Screen3 screen3 = new Screen3(this);
        CustomerScreen customerscreen = new CustomerScreen(this, new ParkingLot("MyLot"));

        // Add screens
        container.add(screen1, "SCREEN1");
        container.add(screen2, "SCREEN2");
        container.add(screen3, "SCREEN3");
        container.add(customerscreen, "CUSTOMERSCREEN");
        

        add(container);

        setVisible(true);
    }

    // Navigation method
    public void showScreen(String name) {
        cardLayout.show(container, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
