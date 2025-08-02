import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Layout {

    private static final String FILE_NAME = "saved_names.txt";
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JTextArea savedTextArea;

    public Layout() {

        frame = new JFrame("Editor de Ata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 550);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        this.savedTextArea = new JTextArea(15, 30);
        this.savedTextArea.setEditable(false);
        this.savedTextArea.setLineWrap(true);

        mainPanel.add(new PeoplePage(this), "people");
        mainPanel.add(new DetailsPage(), "details");
        mainPanel.add(new AttendancePage(), "attendance");
        mainPanel.add(new EditAllPage(), "editAll");

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton peopleButton = new JButton("People");
        JButton detailsButton = new JButton("Details");
        JButton attendanceButton = new JButton("Attendance");
        JButton editAllButton = new JButton("Edit All");

        navPanel.add(peopleButton);
        navPanel.add(detailsButton);
        navPanel.add(attendanceButton);
        navPanel.add(editAllButton);

        peopleButton.addActionListener(e -> cardLayout.show(mainPanel, "people"));
        detailsButton.addActionListener(e -> cardLayout.show(mainPanel, "details"));
        attendanceButton.addActionListener(e -> cardLayout.show(mainPanel, "attendance"));
        editAllButton.addActionListener(e -> cardLayout.show(mainPanel, "editAll"));

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(navPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        loadSavedEntries();
    }

    public void saveToFile(String entry) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(entry);
            writer.newLine();
        }
    }

    public void deleteFromFile(String nameToDelete) throws IOException {
        List<String> remainingEntries = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(nameToDelete + ",")) {
                    found = true;
                } else {
                    remainingEntries.add(line);
                }
            }
        }

        if (found) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
                for (String entry : remainingEntries) {
                    writer.write(entry);
                    writer.newLine();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No entry found for '" + nameToDelete + "'.", "Entry Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void loadSavedEntries() {
        savedTextArea.setText("");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                savedTextArea.append(line + "\n");
            }
        } catch (IOException ex) {
            System.err.println("Could not read from file, assuming it's a new session.");
        }
    }

    public boolean deleteAllFromFile() {
        File file = new File(FILE_NAME);
        return file.delete();
    }

    public JTextArea getSavedTextArea() {
        return savedTextArea;
    }
}
