// Save this file as panel/FundsPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import src.DBConnection;

public class FundsPanel extends JPanel {
    private JTextField tfFundId, tfProjectId, tfAmount, tfSource, tfAllocationDate;
    private JTextArea taUsageDetails;
    private JTable table;
    private DefaultTableModel model;

    public FundsPanel() {
        setLayout(new BorderLayout());

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Funds Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfFundId = new JTextField(20);
        tfProjectId = new JTextField(20);
        tfAmount = new JTextField(20);
        tfSource = new JTextField(20);
        tfAllocationDate = new JTextField(20);
        taUsageDetails = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(taUsageDetails);

        String[] labels = {
            "Fund ID", "Project ID", "Amount", "Source", "Usage Details", "Allocation Date (yyyy-mm-dd)"
        };
        JComponent[] inputs = {
            tfFundId, tfProjectId, tfAmount, tfSource, descScroll, tfAllocationDate
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(inputs[i], gbc);
        }

        // ===== Button Panel =====
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

        // ===== Table Panel =====
        model = new DefaultTableModel(new String[]{
            "Fund ID", "Project ID", "Amount", "Source", "Usage Details", "Allocation Date"
        }, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    tfFundId.setText(model.getValueAt(row, 0).toString());
                    tfProjectId.setText(model.getValueAt(row, 1).toString());
                    tfAmount.setText(model.getValueAt(row, 2).toString());
                    tfSource.setText(model.getValueAt(row, 3).toString());
                    taUsageDetails.setText(model.getValueAt(row, 4).toString());
                    tfAllocationDate.setText(model.getValueAt(row, 5).toString());
                }
            }
        });

        // ===== Main Layout =====
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Funds";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // Clear existing data

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Fund_ID"),
                    rs.getInt("Project_ID"),
                    rs.getBigDecimal("Amounts"),
                    rs.getString("Source"),
                    rs.getString("Usage_Details"),
                    rs.getDate("Allocation_Date")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO Funds (Fund_ID, Project_ID, Amounts, Source, Usage_Details, Allocation_Date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfFundId.getText()));
            ps.setInt(2, Integer.parseInt(tfProjectId.getText()));
            ps.setBigDecimal(3, new BigDecimal(tfAmount.getText()));
            ps.setString(4, tfSource.getText());
            ps.setString(5, taUsageDetails.getText());
            ps.setDate(6, parseDate(tfAllocationDate.getText()));
            ps.executeUpdate();
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + ex.getMessage());
        }
    }

    private void updateData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE Funds SET Project_ID=?, Amounts=?, Source=?, Usage_Details=?, Allocation_Date=? WHERE Fund_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfProjectId.getText()));
            ps.setBigDecimal(2, new BigDecimal(tfAmount.getText()));
            ps.setString(3, tfSource.getText());
            ps.setString(4, taUsageDetails.getText());
            ps.setDate(5, parseDate(tfAllocationDate.getText()));
            ps.setInt(6, Integer.parseInt(tfFundId.getText()));
            ps.executeUpdate();
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM Funds WHERE Fund_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfFundId.getText()));
            ps.executeUpdate();
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }

    private Date parseDate(String dateStr) throws DateTimeParseException {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return Date.valueOf(date);
        } catch (DateTimeParseException ex) {
            throw new DateTimeParseException("Invalid date format. Use YYYY-MM-DD", dateStr, ex.getErrorIndex());
        }
    }

    private void clearFields() {
        tfFundId.setText("");
        tfProjectId.setText("");
        tfAmount.setText("");
        tfSource.setText("");
        taUsageDetails.setText("");
        tfAllocationDate.setText("");
    }
}
