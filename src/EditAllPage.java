import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EditAllPage extends JPanel {
    private Layout parent;

    private JTextArea titleTextArea;
    private JTextArea organizationTimeAddressTextArea;
    private JTextArea inAttendanceTextArea;
    private JTextArea notInAttendanceTextArea;
    private JTextArea financialsTextArea;
    private JTextArea meetingNotesTextArea;
    private JTextArea thanksTextArea;

    public EditAllPage(Layout parent) {
        this.parent = parent;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("Edit All Meeting Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        //Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        this.add(new JLabel("Title:"), gbc);
        gbc.gridy = 1;
        titleTextArea = new JTextArea(2, 20);
        titleTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(titleTextArea), gbc);

        //Organization, Time, Address
        gbc.gridy = 2;
        this.add(new JLabel("Organization, Time, Address:"), gbc);
        gbc.gridy = 3;
        organizationTimeAddressTextArea = new JTextArea(3, 20);
        organizationTimeAddressTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(organizationTimeAddressTextArea), gbc);

        //Attendance
        gbc.gridy = 4;
        gbc.weighty = 0.1;
        this.add(new JLabel("In Attendance:"), gbc);
        gbc.gridy = 5;
        inAttendanceTextArea = new JTextArea(5, 20);
        inAttendanceTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(inAttendanceTextArea), gbc);

        //Not in attendace
        gbc.gridy = 6;
        gbc.weighty = 0.1;
        this.add(new JLabel("Not in Attendance:"), gbc);
        gbc.gridy = 7;
        notInAttendanceTextArea = new JTextArea(5, 20);
        notInAttendanceTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(notInAttendanceTextArea), gbc);

        //Financials
        gbc.gridy = 8;
        gbc.weighty = 0.1;
        this.add(new JLabel("Financials:"), gbc);
        gbc.gridy = 9;
        financialsTextArea = new JTextArea(3, 20);
        financialsTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(financialsTextArea), gbc);

        //Meeting Notes
        gbc.gridy = 10;
        gbc.weighty = 0.1;
        this.add(new JLabel("Meeting Notes:"), gbc);
        gbc.gridy = 11;
        gbc.weighty = 1.0;
        meetingNotesTextArea = new JTextArea(10, 20);
        meetingNotesTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(meetingNotesTextArea), gbc);

        // Thanks everyone for being here
        gbc.gridy = 12;
        gbc.weighty = 0.1;
        this.add(new JLabel("Closing Remarks:"), gbc);
        gbc.gridy = 13;
        gbc.weighty = 0.1;
        thanksTextArea = new JTextArea(3, 20);
        thanksTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.add(new JScrollPane(thanksTextArea), gbc);
    }

    public void refreshData() {
        titleTextArea.setText(parent.getTitleFieldText());

        String orgTimeAddress = "Organization: " + parent.getOrganizationFieldText() + "\n" +
                "Time: " + parent.getTimeFieldText() + "\n" +
                "Address: " + parent.getAddressFieldText();
        organizationTimeAddressTextArea.setText(orgTimeAddress);

        String financials = "Expenses: " + parent.getExpensesFieldText() + "\n" +
                "Revenue: " + parent.getRevenueFieldText() + "\n" +
                "Balance: " + parent.getBalanceFieldText();
        financialsTextArea.setText(financials);

        List<JCheckBox> attendanceCheckboxes = parent.getAttendanceCheckboxes();
        if (attendanceCheckboxes != null) {
            StringBuilder presentBuilder = new StringBuilder();
            StringBuilder absentBuilder = new StringBuilder();

            List<String> presentNames = attendanceCheckboxes.stream()
                    .filter(JCheckBox::isSelected)
                    .map(JCheckBox::getText)
                    .collect(Collectors.toList());
            for (String name : presentNames) {
                presentBuilder.append("- ").append(name).append("\n");
            }

            List<String> absentNames = attendanceCheckboxes.stream()
                    .filter(c -> !c.isSelected())
                    .map(JCheckBox::getText)
                    .collect(Collectors.toList());
            for (String name : absentNames) {
                absentBuilder.append("- ").append(name).append("\n");
            }

            inAttendanceTextArea.setText(presentBuilder.toString());
            notInAttendanceTextArea.setText(absentBuilder.toString());

            if (!presentNames.isEmpty()) {
                String mostImportantPerson = presentNames.get(0);
                String closingRemarks = "lastly the " + mostImportantPerson + " thanks everyone for being here";
                thanksTextArea.setText(closingRemarks);
            } else {
                thanksTextArea.setText("lastly the [most important person] thanks everyone for being here");
            }
        }
    }
}
