package giri;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class VoterRecordUI extends JFrame {

    private JTextField idField, nameField, ageField;
    private JTextArea displayArea;
    private static final String FILE_NAME = "voters.txt";

    public VoterRecordUI() {
        setTitle("💗 Voter Record Checker");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 🎀 Colors
        Color bgColor = new Color(255, 240, 245);
        Color panelColor = new Color(255, 228, 233);
        Color btnColor = new Color(255, 105, 180);
        Color textColor = new Color(90, 0, 50);
        Color borderColor = new Color(255, 182, 193);

        getContentPane().setBackground(bgColor);

        // 💖 Header
        JLabel header = new JLabel("Voter Record Management System", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(textColor);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // 🩷 Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor), "Enter Voter Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(12);
        nameField = new JTextField(12);
        ageField = new JTextField(12);

        JLabel idLabel = new JLabel("Voter ID:");
        JLabel nameLabel = new JLabel("Name:");
        JLabel ageLabel = new JLabel("Age:");
        idLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        ageLabel.setForeground(textColor);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(idLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(ageLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(ageField, gbc);

        JButton addBtn = new JButton("➕ Add");
        JButton searchBtn = new JButton("🔍 Search");
        styleButton(addBtn, btnColor);
        styleButton(searchBtn, btnColor);

        JPanel miniBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        miniBtnPanel.setBackground(panelColor);
        miniBtnPanel.add(addBtn);
        miniBtnPanel.add(searchBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(miniBtnPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // 📋 Display Area
        displayArea = new JTextArea(15, 25);
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        displayArea.setEditable(false);
        displayArea.setBackground(Color.white);
        displayArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor), "Voter Records"));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        // 🌷 Bottom Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        btnPanel.setBackground(bgColor);

        JButton viewBtn = new JButton("📋 View All");
        JButton deleteBtn = new JButton("🗑️ Delete");
        JButton clearBtn = new JButton("🧹 Clear");
        JButton exitBtn = new JButton("🚪 Exit");

        for (JButton btn : new JButton[]{viewBtn, deleteBtn, clearBtn, exitBtn}) {
            styleButton(btn, btnColor);
            btnPanel.add(btn);
        }

        add(btnPanel, BorderLayout.SOUTH);

        // 🎯 Actions
        addBtn.addActionListener(e -> addVoter());
        searchBtn.addActionListener(e -> searchVoter());
        viewBtn.addActionListener(e -> displayAllVoters());
        deleteBtn.addActionListener(e -> deleteVoter());
        clearBtn.addActionListener(e -> displayArea.setText(""));
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.white);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // 🌸 Core Methods
    private void addVoter() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age < 18) {
                JOptionPane.showMessageDialog(this, "Voter must be at least 18 years old.", "Invalid Age", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (searchVoterById(id) != null) {
                JOptionPane.showMessageDialog(this, "Voter ID already exists!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
                fw.write(id + "," + name + "," + age + "\n");
            }
            displayArea.append("✅ Added: " + name + " (" + id + ")\n");

            idField.setText("");
            nameField.setText("");
            ageField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid age entered!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void searchVoter() {
        String id = JOptionPane.showInputDialog(this, "Enter Voter ID to Search:");
        if (id == null || id.trim().isEmpty()) return;

        try {
            String record = searchVoterById(id);
            if (record != null) {
                displayArea.append("✅ Found: " + record + "\n");
            } else {
                displayArea.append("❌ No voter found with ID " + id + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String searchVoterById(String voterId) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(voterId)) {
                    return "ID: " + parts[0] + ", Name: " + parts[1] + ", Age: " + parts[2];
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        }
        return null;
    }

    private void displayAllVoters() {
        displayArea.setText("📋 Registered Voters:\n----------------------------------\n");
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                displayArea.append(line + "\n");
            }
        } catch (IOException e) {
            displayArea.append("⚠️ No records found.\n");
        }
    }

    private void deleteVoter() {
        String id = JOptionPane.showInputDialog(this, "Enter Voter ID to Delete:");
        if (id == null || id.trim().isEmpty()) return;

        File inputFile = new File(FILE_NAME);
        File tempFile = new File("temp.txt");
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(id)) {
                    deleted = true;
                    continue;
                }
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (deleted) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
            displayArea.append("🗑️ Deleted voter with ID: " + id + "\n");
        } else {
            tempFile.delete();
            displayArea.append("❌ No voter found with ID " + id + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VoterRecordUI().setVisible(true));
    }
}