// Save this as panel/ResearchProjectPanel.java
package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import src.DBConnection;

public class ResearchProjectPanel extends JPanel {
    private JTextField tfProjectId, tfTitle, tfStartDate, tfEndDate, tfStatus;
    private JTextArea taDescription;
    private JTable table;
    private DefaultTableModel model;

    public ResearchProjectPanel() {
        setLayout(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Research Project Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input Fields
        tfProjectId = new JTextField(20);
        tfTitle = new JTextField(20);
        tfStartDate = new JTextField(20);
        tfEndDate = new JTextField(20);
        tfStatus = new JTextField(20);
        taDescription = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(taDescription);

        // Form Labels and Fields
        String[] labels = {"Project ID", "Title", "Start Date (yyyy-mm-dd)", "End Date (yyyy-mm-dd)", "Description", "Status"};
        JComponent[] inputs = {tfProjectId, tfTitle, tfStartDate, tfEndDate, descScroll, tfStatus};

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
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnUpdate);

        // Table Panel
        model = new DefaultTableModel(new String[]{"Project ID", "Title", "Start Date", "End Date", "Description", "Status"}, 0);
        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        // Table row click listener to populate fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    tfProjectId.setText(model.getValueAt(selectedRow, 0).toString());
                    tfTitle.setText(model.getValueAt(selectedRow, 1).toString());
                    tfStartDate.setText(model.getValueAt(selectedRow, 2).toString());
                    tfEndDate.setText(model.getValueAt(selectedRow, 3).toString());
                    taDescription.setText(model.getValueAt(selectedRow, 4).toString());
                    tfStatus.setText(model.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        // Add all panels
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(tableScroll, BorderLayout.SOUTH);

        // Load data initially
        loadData();
    }

    public void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM ResearchProject";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // clear previous rows

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Project_ID"),
                        rs.getString("Title"),
                        rs.getDate("Start_Date"),
                        rs.getDate("End_Date"),
                        rs.getString("Description"),
                        rs.getString("Status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void insertData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO ResearchProject (Project_ID, Title, Start_Date, End_Date, Description, Status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfProjectId.getText()));
            ps.setString(2, tfTitle.getText());
            ps.setDate(3, Date.valueOf(tfStartDate.getText()));
            ps.setDate(4, Date.valueOf(tfEndDate.getText()));
            ps.setString(5, taDescription.getText());
            ps.setString(6, tfStatus.getText());
            ps.executeUpdate();
            loadData(); // refresh table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inserting data: " + ex.getMessage());
        }
    }

    private void deleteData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM ResearchProject WHERE Project_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfProjectId.getText()));
            ps.executeUpdate();
            loadData(); // refresh table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());
        }
    }

    private void updateData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE ResearchProject SET Title=?, Start_Date=?, End_Date=?, Description=?, Status=? WHERE Project_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tfTitle.getText());
            ps.setDate(2, Date.valueOf(tfStartDate.getText()));
            ps.setDate(3, Date.valueOf(tfEndDate.getText()));
            ps.setString(4, taDescription.getText());
            ps.setString(5, tfStatus.getText());
            ps.setInt(6, Integer.parseInt(tfProjectId.getText()));
            ps.executeUpdate();
            loadData(); // refresh table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());
        }
    }
}
