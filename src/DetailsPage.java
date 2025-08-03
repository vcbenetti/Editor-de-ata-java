import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        this.add(new JLabel("Organization:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        organizationField = new JTextField(20);
        this.add(organizationField, gbc);

        //Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        this.add(new JLabel("Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        try {
            MaskFormatter timeFormatter = new MaskFormatter("##:##");
            timeField = new JFormattedTextField(timeFormatter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.add(timeField, gbc);

        //Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        this.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        addressField = new JTextField(20);
        this.add(addressField, gbc);

        //Expenses
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        this.add(new JLabel("Expenses (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
        numberFormatter.setAllowsInvalid(false);
        expensesField = new JFormattedTextField(numberFormatter);
        this.add(expensesField, gbc);

        //Revenue
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        this.add(new JLabel("Revenue (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        revenueField = new JFormattedTextField(numberFormatter);
        this.add(revenueField, gbc);

        //Balance
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        this.add(new JLabel("Balance (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        balanceField = new JFormattedTextField(numberFormatter);
        this.add(balanceField, gbc);

        //Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        deleteAllButton = new JButton("Delete All");
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteAllButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        this.add(buttonPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(parent.getSavedDetailsTextArea());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(scrollPane, gbc);


        saveButton.addActionListener(new SaveButtonListener());
        clearButton.addActionListener(new ClearButtonListener());
        deleteAllButton.addActionListener(new DeleteAllButtonListener());

        loadSavedDetails();
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            String organization = organizationField.getText().trim();
            String time = timeField.getText().trim();
            String address = addressField.getText().trim();
            Number expenses = (Number) expensesField.getValue();
            Number revenue = (Number) revenueField.getValue();
            Number balance = (Number) balanceField.getValue();

            String expensesInWords = "";
            String revenueInWords = "";
            String balanceInWords = "";

            if (expenses != null) {
                expensesInWords = NumberToWordsConverter.convertDecimal(((Number) expenses).doubleValue());
            }
            if (revenue != null) {
                revenueInWords = NumberToWordsConverter.convertDecimal(((Number) revenue).doubleValue());
            }
            if (balance != null) {
                balanceInWords = NumberToWordsConverter.convertDecimal(((Number) balance).doubleValue());
            }

            String entry = String.format("Title: %s\nOrganization: %s\nTime: %s\nAddress: %s\nExpenses: %.2f (%s)\nRevenue: %.2f (%s)\nBalance: %.2f (%s)",
                    title, organization, time, address,
                    expenses != null ? expenses.doubleValue() : 0.0, expensesInWords,
                    revenue != null ? revenue.doubleValue() : 0.0, revenueInWords,
                    balance != null ? balance.doubleValue() : 0.0, balanceInWords);

            try {
                parent.saveDetailsToFile(entry);
                parent.loadSavedDetails();
                JOptionPane.showMessageDialog(DetailsPage.this, "Details saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DetailsPage.this, "Error saving to file: " + ex.getMessage(),
                        "File Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
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
        expensesField.setText("");
        revenueField.setText("");
        balanceField.setText("");
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

    public void loadSavedDetails() {
        String savedDetailsText = parent.getSavedDetailsTextArea().getText();
        if (savedDetailsText != null && !savedDetailsText.trim().isEmpty()) {
            Pattern titlePattern = Pattern.compile("Title: (.+)");
            Pattern organizationPattern = Pattern.compile("Organization: (.+)");
            Pattern timePattern = Pattern.compile("Time: (.+)");
            Pattern addressPattern = Pattern.compile("Address: (.+)");
            Pattern expensesPattern = Pattern.compile("Expenses: (.+)");
            Pattern revenuePattern = Pattern.compile("Revenue: (.+)");
            Pattern balancePattern = Pattern.compile("Balance: (.+)");

            Matcher titleMatcher = titlePattern.matcher(savedDetailsText);
            Matcher organizationMatcher = organizationPattern.matcher(savedDetailsText);
            Matcher timeMatcher = timePattern.matcher(savedDetailsText);
            Matcher addressMatcher = addressPattern.matcher(savedDetailsText);
            Matcher expensesMatcher = expensesPattern.matcher(savedDetailsText);
            Matcher revenueMatcher = revenuePattern.matcher(savedDetailsText);
            Matcher balanceMatcher = balancePattern.matcher(savedDetailsText);

            if (titleMatcher.find()) titleField.setText(titleMatcher.group(1).trim());
            if (organizationMatcher.find()) organizationField.setText(organizationMatcher.group(1).trim());
            if (timeMatcher.find()) timeField.setText(timeMatcher.group(1).trim());
            if (addressMatcher.find()) addressField.setText(addressMatcher.group(1).trim());
            if (expensesMatcher.find()) expensesField.setText(expensesMatcher.group(1).trim());
            if (revenueMatcher.find()) revenueField.setText(revenueMatcher.group(1).trim());
            if (balanceMatcher.find()) balanceField.setText(balanceMatcher.group(1).trim());
        } else {
            clearFields();
        }
    }

    public JTextField getTitleField() { return titleField; }
    public JTextField getOrganizationField() { return organizationField; }
    public JFormattedTextField getTimeField() { return timeField; }
    public JTextField getAddressField() { return addressField; }
    public JFormattedTextField getExpensesField() { return expensesField; }
    public JFormattedTextField getRevenueField() { return revenueField; }
    public JFormattedTextField getBalanceField() { return balanceField; }
}
