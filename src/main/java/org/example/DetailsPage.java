package org.example; // Added package declaration

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.NumberFormat;
import java.util.Locale;


public class DetailsPage extends JPanel {

    private Layout parent;
    private JTextField titleField;
    private JTextField organizationField;
    private JFormattedTextField timeField;
    private JFormattedTextField expensesField;
    private JFormattedTextField revenueField;
    private JFormattedTextField balanceField;
    private JTextField addressField;

    private JButton saveBasicDetailsButton;
    private JButton saveFinancialsButton;
    private JButton saveAllButton;
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

        saveBasicDetailsButton = new JButton("Save Basic Details");
        saveFinancialsButton = new JButton("Save Financials");
        saveAllButton = new JButton("Save All");
        clearButton = new JButton("Clear");
        deleteAllButton = new JButton("Delete All");

        buttonPanel.add(saveBasicDetailsButton);
        buttonPanel.add(saveFinancialsButton);
        buttonPanel.add(saveAllButton);
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

        saveBasicDetailsButton.addActionListener(e -> saveBasicDetails());
        saveFinancialsButton.addActionListener(e -> saveFinancials());
        saveAllButton.addActionListener(e -> saveAll());
        clearButton.addActionListener(e -> clearFields());
        deleteAllButton.addActionListener(e -> deleteAllDetails());

        loadSavedDetails();
    }

    //Helper method to get current details from the saved text area
    private Map<String, String> getCurrentDetailsMap() {
        Map<String, String> detailsMap = new HashMap<>();
        String savedDetailsText = parent.getSavedDetailsTextArea().getText();

        detailsMap.put("Title", "");
        detailsMap.put("Organization", "");
        detailsMap.put("Time", "");
        detailsMap.put("Address", "");
        detailsMap.put("Expenses", "0.00 (zero reais)");
        detailsMap.put("Revenue", "0.00 (zero reais)");
        detailsMap.put("Balance", "0.00 (zero reais)");

        if (savedDetailsText != null && !savedDetailsText.trim().isEmpty()) {
            Pattern pattern = Pattern.compile("^(Title|Organization|Time|Address|Expenses|Revenue|Balance):\\s*(.*)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(savedDetailsText);
            while (matcher.find()) {
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();

                if (key.equals("Expenses") || key.equals("Revenue") || key.equals("Balance")) {
                    Pattern financialWordsPattern = Pattern.compile("^(.*?)\\s*\\(.*\\)$");
                    Matcher financialWordsMatcher = financialWordsPattern.matcher(value);
                    if (financialWordsMatcher.find()) {
                        value = financialWordsMatcher.group(1).trim();
                    }
                }
                detailsMap.put(key, value);
            }
        }
        return detailsMap;
    }

    //Helper method to build the entry string
    private String buildEntryString(Map<String, String> detailsMap) {
        return String.format("Title: %s\nOrganization: %s\nTime: %s\nAddress: %s\nExpenses: %s\nRevenue: %s\nBalance: %s",
                detailsMap.get("Title"),
                detailsMap.get("Organization"),
                detailsMap.get("Time"),
                detailsMap.get("Address"),
                detailsMap.get("Expenses"),
                detailsMap.get("Revenue"),
                detailsMap.get("Balance"));
    }

    //Helper method to format financial values
    private String formatFinancialValue(JFormattedTextField field) {
        Number value = (Number) field.getValue();
        if (value != null) {
            String valueInWords = NumberToWordsConverter.convertDecimal(value.doubleValue());
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            return currencyFormat.format(value.doubleValue()) + " (" + valueInWords + ")";
        }
        return "0.00 (zero reais)"; // Default if field is empty or null
    }

    private void saveBasicDetails() {
        Map<String, String> detailsToSave = getCurrentDetailsMap();

        detailsToSave.put("Title", titleField.getText().trim());
        detailsToSave.put("Organization", organizationField.getText().trim());
        detailsToSave.put("Time", timeField.getText().trim());
        detailsToSave.put("Address", addressField.getText().trim());


        String entry = buildEntryString(detailsToSave);
        try {
            parent.saveDetailsToFile(entry);
            parent.loadSavedDetails();
            JOptionPane.showMessageDialog(this, "Basic details saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving basic details: " + ex.getMessage(),
                    "File Save Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveFinancials() {
        Map<String, String> detailsToSave = getCurrentDetailsMap();

        Number expenses = (Number) expensesField.getValue();
        Number revenue = (Number) revenueField.getValue();
        Number balance = (Number) balanceField.getValue();

        String expensesInWords = (expenses != null) ? NumberToWordsConverter.convertDecimal(expenses.doubleValue()) : "zero reais";
        String revenueInWords = (revenue != null) ? NumberToWordsConverter.convertDecimal(revenue.doubleValue()) : "zero reais";
        String balanceInWords = (balance != null) ? NumberToWordsConverter.convertDecimal(balance.doubleValue()) : "zero reais";

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        detailsToSave.put("Expenses", currencyFormat.format(expenses != null ? expenses.doubleValue() : 0.0) + " (" + expensesInWords + ")");
        detailsToSave.put("Revenue", currencyFormat.format(revenue != null ? revenue.doubleValue() : 0.0) + " (" + revenueInWords + ")");
        detailsToSave.put("Balance", currencyFormat.format(balance != null ? balance.doubleValue() : 0.0) + " (" + balanceInWords + ")");

        String entry = buildEntryString(detailsToSave);
        try {
            parent.saveDetailsToFile(entry);
            parent.loadSavedDetails();
            JOptionPane.showMessageDialog(this, "Financials saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving financials: " + ex.getMessage(),
                    "File Save Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveAll() {
        Map<String, String> currentDetails = getCurrentDetailsMap();

        currentDetails.put("Title", titleField.getText().trim());
        currentDetails.put("Organization", organizationField.getText().trim());
        currentDetails.put("Time", timeField.getText().trim());
        currentDetails.put("Address", addressField.getText().trim());

        Number expenses = (Number) expensesField.getValue();
        Number revenue = (Number) revenueField.getValue();
        Number balance = (Number) balanceField.getValue();

        String expensesInWords = (expenses != null) ? NumberToWordsConverter.convertDecimal(expenses.doubleValue()) : "zero reais";
        String revenueInWords = (revenue != null) ? NumberToWordsConverter.convertDecimal(revenue.doubleValue()) : "zero reais";
        String balanceInWords = (balance != null) ? NumberToWordsConverter.convertDecimal(balance.doubleValue()) : "zero reais";

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        currentDetails.put("Expenses", currencyFormat.format(expenses != null ? expenses.doubleValue() : 0.0) + " (" + expensesInWords + ")");
        currentDetails.put("Revenue", currencyFormat.format(revenue != null ? revenue.doubleValue() : 0.0) + " (" + revenueInWords + ")");
        currentDetails.put("Balance", currencyFormat.format(balance != null ? balance.doubleValue() : 0.0) + " (" + balanceInWords + ")");

        String entry = buildEntryString(currentDetails);
        try {
            parent.saveDetailsToFile(entry);
            parent.loadSavedDetails();
            JOptionPane.showMessageDialog(this, "All details saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving all details: " + ex.getMessage(),
                    "File Save Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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

    private void deleteAllDetails() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(DetailsPage.this);
        int response = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete ALL details data? This action cannot be undone.",
                "Confirm Delete All",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            if (parent.deleteAllDetailsFromFile()) {
                JOptionPane.showMessageDialog(frame, "All details data has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.getSavedDetailsTextArea().setText("");
                clearFields(); // Clear the text fields after deleting the file
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete all data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadSavedDetails() {
        Map<String, String> detailsMap = getCurrentDetailsMap();

        titleField.setText(detailsMap.get("Title"));
        organizationField.setText(detailsMap.get("Organization"));
        timeField.setText(detailsMap.get("Time"));
        addressField.setText(detailsMap.get("Address"));

        try {
            String expensesValue = detailsMap.get("Expenses");
            if (expensesValue != null && !expensesValue.isEmpty()) {
                String cleanedExpensesText = expensesValue.replaceAll("[R$ .]", "").replace(",", "."); // Remove R$, spaces, dots, then replace comma with dot
                if (!cleanedExpensesText.isEmpty()) {
                    expensesField.setValue(Double.parseDouble(cleanedExpensesText));
                } else {
                    expensesField.setValue(0.0);
                }
            } else {
                expensesField.setValue(0.0);
            }

            String revenueValue = detailsMap.get("Revenue");
            if (revenueValue != null && !revenueValue.isEmpty()) {
                String cleanedRevenueText = revenueValue.replaceAll("[R$ .]", "").replace(",", ".");
                if (!cleanedRevenueText.isEmpty()) {
                    revenueField.setValue(Double.parseDouble(cleanedRevenueText));
                } else {
                    revenueField.setValue(0.0);
                }
            } else {
                revenueField.setValue(0.0);
            }

            String balanceValue = detailsMap.get("Balance");
            if (balanceValue != null && !balanceValue.isEmpty()) {
                String cleanedBalanceText = balanceValue.replaceAll("[R$ .]", "").replace(",", ".");
                if (!cleanedBalanceText.isEmpty()) {
                    balanceField.setValue(Double.parseDouble(cleanedBalanceText));
                } else {
                    balanceField.setValue(0.0);
                }
            } else {
                balanceField.setValue(0.0);
            }

        } catch (NumberFormatException ex) {
            System.err.println("Error parsing financial data during load: " + ex.getMessage());
            expensesField.setValue(0.0);
            revenueField.setValue(0.0);
            balanceField.setValue(0.0);
        }
    }

    public JTextField getTitleField() { return titleField; }
    public JTextField getOrganizationField() { return organizationField; }
    public JFormattedTextField getTimeField() { return timeField; }
    public JFormattedTextField getExpensesField() { return expensesField; }
    public JFormattedTextField getRevenueField() { return revenueField; }
    public JFormattedTextField getBalanceField() { return balanceField; }
    public JTextField getAddressField() { return addressField; }
}