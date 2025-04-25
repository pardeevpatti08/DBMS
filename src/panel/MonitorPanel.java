// Save this as panel/MonitorPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import src.DBConnection;

public class MonitorPanel extends JPanel {
    private JTextField tfMonitorId, tfMonitorName, tfContactDetails;
    private JTable table;
    private DefaultTableModel model;

    public MonitorPanel() {
        setLayout(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Monitor Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        tfMonitorId = new JTextField(20);
        tfMonitorName = new JTextField(20);
        tfContactDetails = new JTextField(20);

        String[] labels = {"Monitor ID", "Monitor Name", "Contact Details"};
        JComponent[] inputs = {tfMonitorId, tfMonitorName, tfContactDetails};

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
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        btnAdd.addActionListener(e -> insertData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        // Table
        model = new DefaultTableModel(new String[]{"Monitor ID", "Monitor Name", "Contact Details"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Table row selection
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tfMonitorId.setText(model.getValueAt(selectedRow, 0).toString());
                    tfMonitorName.setText(model.getValueAt(selectedRow, 1).toString());
                    tfContactDetails.setText(model.getValueAt(selectedRow, 2).toString());
                }
            }
        });

        // Add to main panel
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        // Load data
        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Monitor";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // Clear table

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Monitor_ID"),
                        rs.getString("Monitor_Name"),
                        rs.getString("Contact_Details")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO Monitor (Monitor_ID, Monitor_Name, Contact_Details) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfMonitorId.getText()));
            ps.setString(2, tfMonitorName.getText());
            ps.setString(3, tfContactDetails.getText());
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inserting data: " + ex.getMessage());
        }
    }

    private void updateData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE Monitor SET Monitor_Name=?, Contact_Details=? WHERE Monitor_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tfMonitorName.getText());
            ps.setString(2, tfContactDetails.getText());
            ps.setInt(3, Integer.parseInt(tfMonitorId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM Monitor WHERE Monitor_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfMonitorId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());
        }
    }
}
