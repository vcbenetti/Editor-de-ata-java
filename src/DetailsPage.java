import javax.swing.*;
import java.awt.*;

public class DetailsPage extends JPanel {
    public DetailsPage() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Details"));
        JLabel label = new JLabel("DETAILS", JLabel.CENTER);
        this.add(label, BorderLayout.CENTER);
    }
}
