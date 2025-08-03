package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Layout {

    private static final String NAMES_FILE = "saved_names.txt";
    private static final String DETAILS_FILE = "saved_details.txt";

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JTextArea savedNamesTextArea;
    private JTextArea savedDetailsTextArea;
    private PeoplePage peoplePage;
    private DetailsPage detailsPage;
    private AttendancePage attendancePage;
    private EditAllPage editAllPage;

    public Layout() {

        frame = new JFrame("Editor de Ata");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        this.savedNamesTextArea = new JTextArea(15, 30);
        this.savedNamesTextArea.setEditable(false);
        this.savedNamesTextArea.setLineWrap(true);

        this.savedDetailsTextArea = new JTextArea(15, 30);
        this.savedDetailsTextArea.setEditable(false);
        this.savedDetailsTextArea.setLineWrap(true);

        peoplePage = new PeoplePage(this);
        detailsPage = new DetailsPage(this);
        attendancePage = new AttendancePage(this);
        editAllPage = new EditAllPage(this);

        mainPanel.add(peoplePage, "people");
        mainPanel.add(detailsPage, "details");
        mainPanel.add(attendancePage, "attendance");
        mainPanel.add(editAllPage, "editAll");

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton peopleButton = new JButton("People");
        JButton detailsButton = new JButton("Details");
        JButton attendanceButton = new JButton("Attendance");
        JButton editAllButton = new JButton("Edit All");

        navPanel.add(peopleButton);
        navPanel.add(detailsButton);
        navPanel.add(attendanceButton);
        navPanel.add(editAllButton);

        peopleButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "people");
        });
        detailsButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "details");
        });
        attendanceButton.addActionListener(e -> {
            loadSavedNames();
            attendancePage.refreshAttendanceList();
            cardLayout.show(mainPanel, "attendance");
        });
        editAllButton.addActionListener(e -> {
            editAllPage.refreshData();
            cardLayout.show(mainPanel, "editAll");
        });

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
        } catch (FileNotFoundException e) {
            return;
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

    public boolean deleteAllNamesFromFile() {
        File file = new File(NAMES_FILE);
        return file.delete();
    }

    public void loadSavedNames() {
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(NAMES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedName = line.trim();
                if (!trimmedName.isEmpty()) {
                    names.add(trimmedName);
                }
            }
        } catch (IOException ex) {
            System.err.println("Could not read from names file, assuming it's a new session.");
        }

        Collections.sort(names, new Comparator<String>() {
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

        savedNamesTextArea.setText("");
        for (String name : names) {
            savedNamesTextArea.append(name + "\n");
        }
    }

    public JTextArea getSavedNamesTextArea() {
        return savedNamesTextArea;
    }

    public void saveDetailsToFile(String entry) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DETAILS_FILE, false))) {
            writer.write(entry);
            writer.newLine();
        }
    }

    public boolean deleteAllDetailsFromFile() {
        File file = new File(DETAILS_FILE);
        return file.delete();
    }

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

    public JTextArea getSavedDetailsTextArea() {
        return savedDetailsTextArea;
    }

    public void updateAttendanceList() {
        this.attendancePage.refreshAttendanceList();
    }

    public String getTitleFieldText() { return detailsPage.getTitleField().getText(); }
    public String getOrganizationFieldText() { return detailsPage.getOrganizationField().getText(); }
    public String getTimeFieldText() { return detailsPage.getTimeField().getText(); }
    public String getAddressFieldText() { return detailsPage.getAddressField().getText(); }
    public String getExpensesFieldText() { return detailsPage.getExpensesField().getText(); }
    public String getRevenueFieldText() { return detailsPage.getRevenueField().getText(); }
    public String getBalanceFieldText() { return detailsPage.getBalanceField().getText(); }
    public List<JCheckBox> getAttendanceCheckboxes() { return attendancePage.getAttendanceCheckboxes(); }
}
