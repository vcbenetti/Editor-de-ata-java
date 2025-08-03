import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AttendancePage extends JPanel {

    private Layout parent;
    private JPanel attendanceListPanel;
    private List<JCheckBox> attendanceCheckboxes;

    public AttendancePage(Layout parent) {
        this.parent = parent;
        this.attendanceCheckboxes = new ArrayList<>();
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Attendance"));

        attendanceListPanel = new JPanel();
        attendanceListPanel.setLayout(new BoxLayout(attendanceListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(attendanceListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton saveButton = new JButton("Save Attendance");

        saveButton.addActionListener(e -> saveAttendance());

        buttonPanel.add(saveButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        refreshAttendanceList();
    }

    public void refreshAttendanceList() {
        attendanceListPanel.removeAll();
        attendanceCheckboxes.clear();

        String savedNamesText = parent.getSavedNamesTextArea().getText();
        String[] names = savedNamesText.split("\\n");

        List<String> sortedNames = new ArrayList<>();
        for (String name : names) {
            if (!name.trim().isEmpty()) {
                sortedNames.add(name);
            }
        }

        Collections.sort(sortedNames, new Comparator<String>() {
            @Override
            public int compare(String name1, String name2) {

                int importance1 = getImportance(name1);
                int importance2 = getImportance(name2);

                if (importance1 != importance2) {
                    return Integer.compare(importance1, importance2);
                }

                return name1.compareTo(name2);
            }

            private int getImportance(String nameWithPosition) {
                String[] parts = nameWithPosition.split(",", 2);
                if (parts.length > 1) {
                    String position = parts[1].trim().toLowerCase();
                    switch (position) {
                        case "president":
                            return 1;
                        case "vice president":
                            return 2;
                        case "second vice president":
                            return 3;
                        case "treasurer":
                            return 4;
                        case "second treasurer":
                            return 5;
                        case "third treasurer":
                            return 6;
                        case "first secretary":
                            return 7;
                        case "second secretary":
                            return 8;
                        case "third secretary":
                            return 9;
                        default:
                            return 10; // Member or any other position
                    }
                }
                return 10; // Default for no position found
            }
        });

        for (String name : sortedNames) {
            JCheckBox checkBox = new JCheckBox(name);
            attendanceCheckboxes.add(checkBox);
            attendanceListPanel.add(checkBox);
        }

        attendanceListPanel.revalidate();
        attendanceListPanel.repaint();
    }

    private void saveAttendance() {
        StringBuilder attendanceSummary = new StringBuilder();
        attendanceSummary.append("Attendance for the meeting:\n");
        for (JCheckBox checkBox : attendanceCheckboxes) {
            String status = checkBox.isSelected() ? "Present" : "Absent";
            attendanceSummary.append(checkBox.getText()).append(": ").append(status).append("\n");
        }

        JOptionPane.showMessageDialog(this, attendanceSummary.toString(), "Attendance Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    public List<JCheckBox> getAttendanceCheckboxes() {
        return attendanceCheckboxes;
    }
}
