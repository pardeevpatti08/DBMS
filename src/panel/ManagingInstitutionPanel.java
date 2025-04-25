// Save this as panel/ManagingInstitutionPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import src.DBConnection;

public class ManagingInstitutionPanel extends JPanel {
    private JTextField tfInstitutionId, tfInstitutionName;
    private JTextArea taAddress;
    private JTable table;
    private DefaultTableModel model;

    public ManagingInstitutionPanel() {
        setLayout(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Managing Institution Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fields
        tfInstitutionId = new JTextField(20);
        tfInstitutionName = new JTextField(20);
        taAddress = new JTextArea(3, 20);
        JScrollPane addressScroll = new JScrollPane(taAddress);

        String[] labels = {"Institution ID", "Institution Name", "Address"};
        JComponent[] inputs = {tfInstitutionId, tfInstitutionName, addressScroll};

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
        JButton btnUpdate = new JButton("Update");

        btnAdd.addActionListener(e -> insertData());
        btnDelete.addActionListener(e -> deleteData());
        btnUpdate.addActionListener(e -> updateData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        // Table
        model = new DefaultTableModel(new String[]{"Institution ID", "Institution Name", "Address"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Add row click event
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tfInstitutionId.setText(model.getValueAt(selectedRow, 0).toString());
                    tfInstitutionName.setText(model.getValueAt(selectedRow, 1).toString());
                    taAddress.setText(model.getValueAt(selectedRow, 2).toString());
                }
            }
        });

        // Add to Main Panel
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        // Load existing data
        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ManagingInstitution";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Institution_ID"),
                        rs.getString("Institution_Name"),
                        rs.getString("Address")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ManagingInstitution (Institution_ID, Institution_Name, Address) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfInstitutionId.getText()));
            ps.setString(2, tfInstitutionName.getText());
            ps.setString(3, taAddress.getText());
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inserting data: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ManagingInstitution WHERE Institution_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfInstitutionId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());
        }
    }

    private void updateData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE ManagingInstitution SET Institution_Name=?, Address=? WHERE Institution_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tfInstitutionName.getText());
            ps.setString(2, taAddress.getText());
            ps.setInt(3, Integer.parseInt(tfInstitutionId.getText()));
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());
        }
    }
}
