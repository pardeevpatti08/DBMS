// Save as panel/ProgressPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import src.DBConnection;

public class ProgressPanel extends JPanel {
    private JTextField tfProgressId, tfProjectId, tfDate, tfStatus;
    private JTable table;
    private DefaultTableModel model;

    public ProgressPanel() {
        setLayout(new BorderLayout());

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Progress Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfProgressId = new JTextField(20);
        tfProjectId = new JTextField(20);
        tfDate = new JTextField(20);  // Format: YYYY-MM-DD
        tfStatus = new JTextField(20);

        String[] labels = {"Progress ID", "Project ID", "Date (YYYY-MM-DD)", "Status"};
        JComponent[] fields = {tfProgressId, tfProjectId, tfDate, tfStatus};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
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
        model = new DefaultTableModel(new String[]{"Progress ID", "Project ID", "Progress Date", "Status"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    tfProgressId.setText(model.getValueAt(row, 0).toString());
                    tfProjectId.setText(model.getValueAt(row, 1).toString());
                    tfDate.setText(model.getValueAt(row, 2).toString());
                    tfStatus.setText(model.getValueAt(row, 3).toString());
                }
            }
        });

        // ===== Add to Layout =====
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT PROGRESS_ID, PROJECT_ID, PROGRESS_DATE, STATUS FROM Progress";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("PROGRESS_ID"),
                        rs.getInt("PROJECT_ID"),
                        rs.getDate("PROGRESS_DATE").toString(),
                        rs.getString("STATUS")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO Progress (PROGRESS_ID, PROJECT_ID, PROGRESS_DATE, STATUS) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfProgressId.getText()));
            ps.setInt(2, Integer.parseInt(tfProjectId.getText()));
            ps.setDate(3, parseDate(tfDate.getText()));
            ps.setString(4, tfStatus.getText());
            ps.executeUpdate();
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Insert Error: " + ex.getMessage());
        }
    }

    private void updateData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE Progress SET PROJECT_ID=?, PROGRESS_DATE=?, STATUS=? WHERE PROGRESS_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfProjectId.getText()));
            ps.setDate(2, parseDate(tfDate.getText()));
            ps.setString(3, tfStatus.getText());
            ps.setInt(4, Integer.parseInt(tfProgressId.getText()));
            ps.executeUpdate();
            loadData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM Progress WHERE PROGRESS_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfProgressId.getText()));
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
        tfProgressId.setText("");
        tfProjectId.setText("");
        tfDate.setText("");
        tfStatus.setText("");
    }
}
