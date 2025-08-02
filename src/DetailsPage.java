import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;

public class DetailsPage extends JPanel {

    private Layout parent;
    private JTextField titleField;
    private JTextField organizationField;
    private JFormattedTextField timeField;
    private JFormattedTextField expensesField;
    private JFormattedTextField revenueField;
    private JFormattedTextField balanceField;
    private JTextField addressField;

    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteAllButton;


    public DetailsPage(Layout parent) {
        this.parent = parent;
        this.setBorder(BorderFactory.createTitledBorder("Details: All Details"));
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        this.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        this.add(titleField, gbc);

        //Organization
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        this.add(new JLabel("Organization Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        organizationField = new JTextField(20);
        this.add(organizationField, gbc);

        //Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        this.add(new JLabel("Meeting Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        MaskFormatter timeFormatter = null;
        try {
            timeFormatter = new MaskFormatter("##:##");
            timeFormatter.setPlaceholderCharacter('0');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeField = new JFormattedTextField(timeFormatter);
        this.add(timeField, gbc);

        //address
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        this.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        addressField = new JTextField(20);
        this.add(addressField, gbc);

        //Number for currency
        NumberFormatter currencyFormatter = new NumberFormatter(new DecimalFormat("#0.00"));
        currencyFormatter.setValueClass(Double.class);
        currencyFormatter.setAllowsInvalid(false);
        currencyFormatter.setCommitsOnValidEdit(true);

        //Expenses
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        this.add(new JLabel("Expenses ($):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        expensesField = new JFormattedTextField(currencyFormatter);
        this.add(expensesField, gbc);

        //Revenue
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        this.add(new JLabel("Revenue ($):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        revenueField = new JFormattedTextField(currencyFormatter);
        this.add(revenueField, gbc);

        //Balance
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        this.add(new JLabel("Balance ($):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        balanceField = new JFormattedTextField(currencyFormatter);
        this.add(balanceField, gbc);

        //Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear Fields");
        deleteAllButton = new JButton("Delete All");
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteAllButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        this.add(buttonPanel, gbc);

        //Saved Details Text Area
        JScrollPane scrollPane = new JScrollPane(parent.getSavedDetailsTextArea());

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(scrollPane, gbc);

        saveButton.addActionListener(new SaveButtonListener());
        clearButton.addActionListener(new ClearButtonListener());
        deleteAllButton.addActionListener(new DeleteAllButtonListener());
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            String organization = organizationField.getText().trim();
            String time = timeField.getText().trim();
            String address = addressField.getText().trim();

            Object expensesValue = expensesField.getValue();
            Object revenueValue = revenueField.getValue();
            Object balanceValue = balanceField.getValue();

            String expensesText = expensesField.getText().trim();
            String revenueText = revenueField.getText().trim();
            String balanceText = balanceField.getText().trim();

            //Validation for the formatted time
            try {
                String[] parts = time.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                    JOptionPane.showMessageDialog(parent.getSavedDetailsTextArea(), "Please enter a valid time (HH:mm).", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent.getSavedDetailsTextArea(), "Please enter a valid time (HH:mm).", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            if (title.isEmpty() || organization.isEmpty()) {
                JOptionPane.showMessageDialog(parent.getSavedDetailsTextArea(), "Title and Organization cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Convert currency to words
            String expensesInWords = "";
            String revenueInWords = "";
            String balanceInWords = "";

            try {
                if (expensesValue instanceof Number) {
                    expensesInWords = NumberToWordsConverter.convertDecimal(((Number) expensesValue).doubleValue());
                }
                if (revenueValue instanceof Number) {
                    revenueInWords = NumberToWordsConverter.convertDecimal(((Number) revenueValue).doubleValue());
                }
                if (balanceValue instanceof Number) {
                    balanceInWords = NumberToWordsConverter.convertDecimal(((Number) balanceValue).doubleValue());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent.getSavedDetailsTextArea(), "Please enter valid numbers for Expenses, Revenue, and Balance.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String entry = String.format("Title: %s\nOrg: %s\nTime: %s\nAddress: %s\nExp: %s (%s)\nRev: %s (%s)\nBal: %s (%s)",
                    title, organization, time, address, expensesText, expensesInWords, revenueText, revenueInWords, balanceText, balanceInWords);

            try {
                parent.saveDetailsToFile(entry);

                // Saved information prompt, debug
                //JOptionPane.showMessageDialog(parent.getSavedDetailsTextArea(), "Entry saved successfully!", "Success",
                //        JOptionPane.INFORMATION_MESSAGE);
                parent.loadSavedDetails();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent.getSavedDetailsTextArea(), "Error saving to file: " + ex.getMessage(),
                        "File Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            clearFields();
        }
    }

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearFields();
        }
    }

    private void clearFields() {
        titleField.setText("");
        organizationField.setText("");
        timeField.setText("");
        addressField.setText("");
        expensesField.setValue(null);
        revenueField.setValue(null);
        balanceField.setValue(null);
    }

    private class DeleteAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(DetailsPage.this);
            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete ALL details data?",
                    "Confirm Delete All",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                if (parent.deleteAllDetailsFromFile()) {
                    JOptionPane.showMessageDialog(frame, "All details data has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parent.getSavedDetailsTextArea().setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete all data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
