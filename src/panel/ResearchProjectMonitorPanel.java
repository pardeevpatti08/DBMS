// Save as panel/ResearchProjectMonitorPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import src.DBConnection;

public class ResearchProjectMonitorPanel extends JPanel {
    private JTextField tfRpmId, tfProjectId, tfMonitorId;
    private JTable table;
    private DefaultTableModel model;

    public ResearchProjectMonitorPanel() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Project-Monitor Management Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfRpmId = new JTextField(20);
        tfProjectId = new JTextField(20);
        tfMonitorId = new JTextField(20);

        String[] labels = {"RPM ID", "Project ID", "Monitor ID"};
        JTextField[] fields = {tfRpmId, tfProjectId, tfMonitorId};

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
        model = new DefaultTableModel(new String[]{"RPM ID", "Project ID", "Monitor ID"}, 0);
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
            String sql = "SELECT * FROM ResearchProjectMonitor";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // Clear previous data

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("RPM_ID"),
                        rs.getInt("Project_ID"),
                        rs.getInt("Monitor_ID")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ResearchProjectMonitor (RPM_ID, Project_ID, Monitor_ID) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRpmId.getText()));
            ps.setInt(2, Integer.parseInt(tfProjectId.getText()));
            ps.setInt(3, Integer.parseInt(tfMonitorId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ResearchProjectMonitor WHERE RPM_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRpmId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }
}
