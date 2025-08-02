import javax.swing.*;
import java.awt.*;

public class EditAllPage extends JPanel {
    public EditAllPage() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Edit All"));
        JLabel label = new JLabel("EDIT ALL", JLabel.CENTER);
        this.add(label, BorderLayout.CENTER);
    }
}
