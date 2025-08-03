import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PeoplePage extends JPanel {

    private Layout parent;
    private JTextField nameField;
    private JTextField lastNameField;
    private JComboBox<String> positionComboBox;
    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JButton deleteAllButton;

    public PeoplePage(Layout parent) {
        this.parent = parent;
        this.setBorder(BorderFactory.createTitledBorder("People: Save/Delete People"));
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        this.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        this.add(nameField, gbc);

        //Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        this.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        lastNameField = new JTextField(20);
        this.add(lastNameField, gbc);

        //Position
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        this.add(new JLabel("Position:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        positionComboBox = new JComboBox<>(new String[]{"President", "Vice President", "Second Vice President",
                "Treasurer", "Second Treasurer", "Third Treasurer", "First Secretary", "Second Secretary",
                "Third Secretary", "Member"});
        this.add(positionComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        deleteButton = new JButton("Delete");
        deleteAllButton = new JButton("Delete All");

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(deleteAllButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        this.add(buttonPanel, gbc);

        // Saved names display
        JScrollPane scrollPane = new JScrollPane(parent.getSavedNamesTextArea());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(scrollPane, gbc);

        saveButton.addActionListener(new SaveButtonListener());
        clearButton.addActionListener(new ClearButtonListener());
        deleteButton.addActionListener(new DeleteButtonListener());
        deleteAllButton.addActionListener(new DeleteAllButtonListener());
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String position = (String) positionComboBox.getSelectedItem();

            if (name.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(PeoplePage.this, "Name and Last Name cannot be empty.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String entry = name + " " + lastName + ", " + position;

            try {
                parent.saveNamesToFile(entry);
                parent.loadSavedNames();
                nameField.setText("");
                lastNameField.setText("");
                positionComboBox.setSelectedIndex(0);
                parent.updateAttendanceList();
                JOptionPane.showMessageDialog(PeoplePage.this, "Entry saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(PeoplePage.this, "Error saving to file: " + ex.getMessage(),
                        "File Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nameField.setText("");
            lastNameField.setText("");
            positionComboBox.setSelectedIndex(0);
        }
    }

    private class DeleteAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(PeoplePage.this);
            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete ALL people data? This action cannot be undone.",
                    "Confirm Delete All",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                if (parent.deleteAllNamesFromFile()) {
                    JOptionPane.showMessageDialog(frame, "All people data has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parent.getSavedNamesTextArea().setText("");
                    parent.updateAttendanceList();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete all data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nameToDelete = nameField.getText().trim() + " " + lastNameField.getText().trim();
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(PeoplePage.this);

            if (nameToDelete.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name to delete.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete the entry for '" + nameToDelete + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    parent.deleteNamesFromFile(nameToDelete);
                    JOptionPane.showMessageDialog(frame, "Entry for '" + nameToDelete + "' deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    parent.loadSavedNames();
                    parent.updateAttendanceList();
                    nameField.setText("");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error deleting from file: " + ex.getMessage(),
                            "File Deletion Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }
    }
}
