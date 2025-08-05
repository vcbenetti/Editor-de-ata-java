package org.example;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BrazilianPortuguese;
import org.languagetool.language.English;
import org.languagetool.language.Portuguese;
import org.languagetool.rules.RuleMatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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

    private JButton exportPdfButton;

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

        //Export to PDF button
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        exportPdfButton = new JButton("Export to PDF");
        this.add(exportPdfButton, gbc);

        //Check Grammar" button
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton checkGrammarButton = new JButton("Check Grammar");
        this.add(checkGrammarButton, gbc);

        exportPdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToPdf();
            }
        });

        checkGrammarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkGrammar();
            }
        });

    }

    public void refreshData() {
        titleTextArea.setText(parent.getTitleFieldText());

        String orgTimeAddress = "Organization: " + parent.getOrganizationFieldText() + "\n" +
                "Time: " + parent.getTimeFieldText() + "\n" +
                "Address: " + parent.getAddressFieldText();
        organizationTimeAddressTextArea.setText(orgTimeAddress);

        String expensesText = parent.getExpensesFieldText();
        String revenueText = parent.getRevenueFieldText();
        String balanceText = parent.getBalanceFieldText();

        String cleanedExpensesText = expensesText.replaceAll("R\\$|\\s|\\.", "").replace(",", ".");
        String cleanedRevenueText = revenueText.replaceAll("R\\$|\\s|\\.", "").replace(",", ".");
        String cleanedBalanceText = balanceText.replaceAll("R\\$|\\s|\\.", "").replace(",", ".");


        double expenses = cleanedExpensesText.isEmpty() ? 0.0 : Double.parseDouble(cleanedExpensesText);
        double revenue = cleanedRevenueText.isEmpty() ? 0.0 : Double.parseDouble(cleanedRevenueText);
        double balance = cleanedBalanceText.isEmpty() ? 0.0 : Double.parseDouble(cleanedBalanceText);


        String expensesInWords = NumberToWordsConverter.convertDecimal(expenses);
        String revenueInWords = NumberToWordsConverter.convertDecimal(revenue);
        String balanceInWords = NumberToWordsConverter.convertDecimal(balance);


        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        String financialsText = "Expenses: " + currencyFormat.format(expenses) + " (" + expensesInWords + ")\n" +
                "Revenue: " + currencyFormat.format(revenue) + " (" + revenueInWords + ")\n" +
                "Balance: " + currencyFormat.format(balance) + " (" + balanceInWords + ")";
        financialsTextArea.setText(financialsText);

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
                String firstPerson = presentNames.get(0);
                String namePart = "";
                String positionPart = "";

                if (firstPerson.contains(",")) {
                    String[] parts = firstPerson.split(",", 2);
                    namePart = parts[0].trim();
                    positionPart = parts[1].trim();
                } else {
                    namePart = firstPerson.trim();
                }

                String firstName = "";
                String lastName = "";
                String[] nameParts = namePart.split("\\s+");
                if (nameParts.length > 0) {
                    firstName = nameParts[0];
                    if (nameParts.length > 1) {
                        lastName = nameParts[nameParts.length - 1];
                    }
                }

                String closingRemarks = "lastly the " + positionPart + " " + firstName + " " + lastName + " thanks everyone for being here";
                thanksTextArea.setText(closingRemarks);
            } else {
                thanksTextArea.setText("lastly the [most important person] thanks everyone for being here");
            }
        }
    }

    private void exportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF File");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                PdfWriter writer = new PdfWriter(filePath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Title
                document.add(new Paragraph(titleTextArea.getText())
                        .setBold()
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setUnderline());

                //Oganization, Time, Address
                document.add(new Paragraph(new Text("Organization, Time, Address:").setBold().setFontSize(14)));
                document.add(new Paragraph(organizationTimeAddressTextArea.getText()).setFontSize(12));

                //In Attendance
                document.add(new Paragraph(new Text("In Attendance:").setBold().setFontSize(14)));
                document.add(new Paragraph(inAttendanceTextArea.getText()).setFontSize(12));

                //Not In Attendance
                document.add(new Paragraph(new Text("Not In Attendance:").setBold().setFontSize(14)));
                document.add(new Paragraph(notInAttendanceTextArea.getText()).setFontSize(12));

                //Financials
                document.add(new Paragraph(new Text("Financials:").setBold().setFontSize(14)));
                document.add(new Paragraph(financialsTextArea.getText()).setFontSize(12));

                //Meeting Notes
                document.add(new Paragraph(new Text("Meeting Notes:").setBold().setFontSize(14)));
                document.add(new Paragraph(meetingNotesTextArea.getText()).setFontSize(12));

                //Closing Remarks
                document.add(new Paragraph(new Text("Closing Remarks:").setBold().setFontSize(14)));
                document.add(new Paragraph(thanksTextArea.getText()).setFontSize(12));

                document.close();
                JOptionPane.showMessageDialog(this, "PDF exported successfully to " + filePath,
                        "Export Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void checkGrammar() {
        // Set XML processing limits to handle LanguageTool's large XML files
        System.setProperty("jdk.xml.entityExpansionLimit", "0");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");

        String textToCheck = meetingNotesTextArea.getText();

        if (textToCheck.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The meeting notes are empty.", "Grammar Check", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // Initialize LanguageTool for both languages
       //     JLanguageTool enTool = new JLanguageTool(new English());
            JLanguageTool ptBrTool = new JLanguageTool(new BrazilianPortuguese());

            // Check the text using both tools
        //    List<RuleMatch> enMatches = enTool.check(textToCheck);
            List<RuleMatch> ptBrMatches = ptBrTool.check(textToCheck);

            StringBuilder feedback = new StringBuilder();
/*
            // Process English grammar errors
            if (!enMatches.isEmpty()) {
                feedback.append("English Grammar Suggestions:\n");
                for (RuleMatch match : enMatches) {
                    feedback.append("  - Potential error at line ").append(match.getLine()).append(", column ").append(match.getColumn()).append(": ");
                    feedback.append(match.getMessage()).append("\n");
                    feedback.append("    -> Suggested correction(s): ").append(match.getSuggestedReplacements()).append("\n");
                    feedback.append("    -> Context: ").append(textToCheck.substring(match.getFromPos(), match.getToPos())).append("\n\n");
                }
            }
*/
            // Process Portuguese grammar errors
            if (!ptBrMatches.isEmpty()) {
                if (!feedback.isEmpty()) {
                    feedback.append("\n");
                }
                feedback.append("Brazilian Portuguese Grammar Suggestions:\n");
                for (RuleMatch match : ptBrMatches) {
                    feedback.append("  - Potential error at line ").append(match.getLine()).append(", column ").append(match.getColumn()).append(": ");
                    feedback.append(match.getMessage()).append("\n");
                    feedback.append("    -> Suggested correction(s): ").append(match.getSuggestedReplacements()).append("\n");
                    feedback.append("    -> Context: ").append(textToCheck.substring(match.getFromPos(), match.getToPos())).append("\n\n");
                }
            }

            if (feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No grammar errors found.", "Grammar Check", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(feedback.toString());
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Grammar Check Results", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error during grammar check: " + ex.getMessage(),
                    "Grammar Check Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

}
