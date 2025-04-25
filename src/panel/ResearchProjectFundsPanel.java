// Save as panel/ResearchProjectFundsPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import src.DBConnection;

public class ResearchProjectFundsPanel extends JPanel {
    private JTextField tfRpfId, tfFundId, tfProjectId;
    private JTable table;
    private DefaultTableModel model;

    public ResearchProjectFundsPanel() {
        setLayout(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Research Project Funds Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfRpfId = new JTextField(20);
        tfFundId = new JTextField(20);
        tfProjectId = new JTextField(20);

        String[] labels = {"RPF ID", "Fund ID", "Project ID"};
        JTextField[] inputs = {tfRpfId, tfFundId, tfProjectId};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(inputs[i], gbc);
        }

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");

        btnAdd.addActionListener(e -> insertData());
        btnDelete.addActionListener(e -> deleteData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        // Table
        model = new DefaultTableModel(new String[]{"RPF ID", "Fund ID", "Project ID"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Layout Setup
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        // Load initial data
        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ResearchProjectFunds";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("RPF_ID"),
                        rs.getInt("Fund_ID"),
                        rs.getInt("Project_ID")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ResearchProjectFunds (RPF_ID, Fund_ID, Project_ID) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRpfId.getText()));
            ps.setInt(2, Integer.parseInt(tfFundId.getText()));
            ps.setInt(3, Integer.parseInt(tfProjectId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ResearchProjectFunds WHERE RPF_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRpfId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }
}
