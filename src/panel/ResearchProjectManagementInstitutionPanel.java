// Save as panel/ResearchProjectManagementInstitutionPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import src.DBConnection;

public class ResearchProjectManagementInstitutionPanel extends JPanel {
    private JTextField tfRpmiId, tfInstitutionId, tfProjectId;
    private JTable table;
    private DefaultTableModel model;

    public ResearchProjectManagementInstitutionPanel() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Project-Institution Management Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfRpmiId = new JTextField(20);
        tfInstitutionId = new JTextField(20);
        tfProjectId = new JTextField(20);

        String[] labels = {"RPMI ID", "Institution ID", "Project ID"};
        JTextField[] fields = {tfRpmiId, tfInstitutionId, tfProjectId};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");

        btnAdd.addActionListener(e -> insertData());
        btnDelete.addActionListener(e -> deleteData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        // Table
        model = new DefaultTableModel(new String[]{"RPMI ID", "Institution ID", "Project ID"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Layout setup
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        // Load initial data
        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ResearchProjectManagementInstitution";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // Clear previous data

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("RPMI_ID"),
                        rs.getInt("Institution_ID"),
                        rs.getInt("Project_ID")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ResearchProjectManagementInstitution (RPMI_ID, Institution_ID, Project_ID) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRpmiId.getText()));
            ps.setInt(2, Integer.parseInt(tfInstitutionId.getText()));
            ps.setInt(3, Integer.parseInt(tfProjectId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ResearchProjectManagementInstitution WHERE RPMI_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRpmiId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }
}
