import javax.swing.*;
import java.awt.*;

public class AttendancePage extends JPanel {
    public AttendancePage() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Attendance"));
        JLabel label = new JLabel("ATTENDANCE", JLabel.CENTER);
        this.add(label, BorderLayout.CENTER);
    }
}
