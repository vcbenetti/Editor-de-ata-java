import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Layout {

    private static final String NAMES_FILE = "saved_names.txt";
    private static final String DETAILS_FILE = "saved_details.txt";

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JTextArea savedNamesTextArea;
    private JTextArea savedDetailsTextArea;

    public Layout() {

        frame = new JFrame("Editor de Ata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 500);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        this.savedNamesTextArea = new JTextArea(15, 30);
        this.savedNamesTextArea.setEditable(false);
        this.savedNamesTextArea.setLineWrap(true);

        this.savedDetailsTextArea = new JTextArea(15, 30);
        this.savedDetailsTextArea.setEditable(false);
        this.savedDetailsTextArea.setLineWrap(true);

        mainPanel.add(new PeoplePage(this), "people");
        mainPanel.add(new DetailsPage(this), "details");
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

        loadSavedNames();
        loadSavedDetails();
    }

    public void saveNamesToFile(String entry) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NAMES_FILE, true))) {
            writer.write(entry);
            writer.newLine();
        }
    }

    /**
     * Deletes all entries with a matching name from the names save file.
     * @param nameToDelete The name to search for and delete.
     * @throws IOException if an I/O error occurs.
     */
    public void deleteNamesFromFile(String nameToDelete) throws IOException {
        List<String> remainingEntries = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(NAMES_FILE))) {
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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(NAMES_FILE, false))) {
                for (String entry : remainingEntries) {
                    writer.write(entry);
                    writer.newLine();
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No entry found for '" + nameToDelete + "'.", "Entry Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Deletes the entire names data file.
     * @return true if the file was deleted successfully, false otherwise.
     */
    public boolean deleteAllNamesFromFile() {
        File file = new File(NAMES_FILE);
        return file.delete();
    }

    /**
     * Loads all entries from the names save file and displays them in the JTextArea.
     */
    public void loadSavedNames() {
        savedNamesTextArea.setText(""); // Clear the text area before reloading
        try (BufferedReader reader = new BufferedReader(new FileReader(NAMES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                savedNamesTextArea.append(line + "\n");
            }
        } catch (IOException ex) {
            System.err.println("Could not read from names file, assuming it's a new session.");
        }
    }

    // Getter for the JTextArea on the PeoplePage
    public JTextArea getSavedNamesTextArea() {
        return savedNamesTextArea;
    }

    // --- Methods for DetailsPage functionality ---

    /**
     * Appends a new entry to the details save file.
     * @param entry The string to be saved.
     * @throws IOException if an I/O error occurs.
     */
    public void saveDetailsToFile(String entry) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DETAILS_FILE, false))) {
            writer.write(entry);
            writer.newLine();
        }
    }

    /**
     * Deletes the entire details data file.
     * @return true if the file was deleted successfully, false otherwise.
     */
    public boolean deleteAllDetailsFromFile() {
        File file = new File(DETAILS_FILE);
        return file.delete();
    }

    /**
     * Loads all entries from the details save file and displays them in the JTextArea.
     */
    public void loadSavedDetails() {
        savedDetailsTextArea.setText(""); // Clear the text area before reloading
        try (BufferedReader reader = new BufferedReader(new FileReader(DETAILS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                savedDetailsTextArea.append(line + "\n");
            }
        } catch (IOException ex) {
            System.err.println("Could not read from details file, assuming it's a new session.");
        }
    }

    // Getter for the JTextArea on the DetailsPage
    public JTextArea getSavedDetailsTextArea() {
        return savedDetailsTextArea;
    }

}
