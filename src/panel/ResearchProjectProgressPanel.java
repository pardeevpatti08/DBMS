// Save as panel/ResearchProjectProgressPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import src.DBConnection;

public class ResearchProjectProgressPanel extends JPanel {
    private JTextField tfRppId, tfProgressId, tfProjectId;
    private JTable table;
    private DefaultTableModel model;

    public ResearchProjectProgressPanel() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Research Project Progress Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfRppId = new JTextField(20);
        tfProgressId = new JTextField(20);
        tfProjectId = new JTextField(20);

        String[] labels = {"RPP ID", "Progress ID", "Project ID"};
        JTextField[] fields = {tfRppId, tfProgressId, tfProjectId};

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

        // Table setup
        model = new DefaultTableModel(new String[]{"RPP ID", "Progress ID", "Project ID"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Add components to layout
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        // Load data
        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT RPP_ID, PROGRESS_ID, PROJECT_ID FROM ResearchProjectProgress";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // Clear previous table data

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("RPP_ID"),
                        rs.getInt("PROGRESS_ID"),
                        rs.getInt("PROJECT_ID")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ResearchProjectProgress (RPP_ID, PROGRESS_ID, PROJECT_ID) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRppId.getText()));
            ps.setInt(2, Integer.parseInt(tfProgressId.getText()));
            ps.setInt(3, Integer.parseInt(tfProjectId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ResearchProjectProgress WHERE RPP_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfRppId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }
}
