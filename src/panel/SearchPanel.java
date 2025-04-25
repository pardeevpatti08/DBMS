package src.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import src.DBConnection;

public class SearchPanel extends JPanel {
    private JTextField tfSearch;
    private JButton btnSearch;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public SearchPanel() {
        setLayout(new BorderLayout());

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Project"));

        tfSearch = new JTextField(30);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(this::performSearch);

        searchPanel.add(new JLabel("Enter Project ID or Title:"));
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);

        // Table for results
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performSearch(ActionEvent e) {
        String searchText = tfSearch.getText().trim();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Project ID or Title.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = """
                SELECT rp.PROJECT_ID, rp.TITLE,
                       TO_CHAR(rp.START_DATE, 'YYYY-MM-DD') AS START_DATE,
                       TO_CHAR(rp.END_DATE, 'YYYY-MM-DD') AS END_DATE,
                       DBMS_LOB.SUBSTR(rp.DESCRIPTION, 4000, 1) AS DESCRIPTION,
                       rp.STATUS,
                       f.AMOUNTS AS FUNDS_AMOUNT,
                       f.SOURCE AS FUNDS_SOURCE,
                       TO_CHAR(f.ALLOCATION_DATE, 'YYYY-MM-DD') AS ALLOC_DATE,
                       p.PROGRESS_DATE,
                       p.STATUS AS PROGRESS_STATUS
                FROM RESEARCHPROJECT rp
                LEFT JOIN FUNDS f ON rp.PROJECT_ID = f.PROJECT_ID
                LEFT JOIN PROGRESS p ON rp.PROJECT_ID = p.PROJECT_ID
                WHERE LOWER(rp.TITLE) LIKE ? OR TO_CHAR(rp.PROJECT_ID) = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchText.toLowerCase() + "%");
            ps.setString(2, searchText);

            ResultSet rs = ps.executeQuery();

            // Update table headers
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            tableModel.setColumnIdentifiers(new String[]{
                "Project ID", "Title", "Start Date", "End Date", "Description", "Status",
                "Funds Amount", "Fund Source", "Allocation Date", "Progress Date", "Progress Status"
            });

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("PROJECT_ID"),
                    rs.getString("TITLE"),
                    rs.getString("START_DATE"),
                    rs.getString("END_DATE"),
                    rs.getString("DESCRIPTION"),
                    rs.getString("STATUS"),
                    rs.getBigDecimal("FUNDS_AMOUNT"),
                    rs.getString("FUNDS_SOURCE"),
                    rs.getString("ALLOC_DATE"),
                    rs.getString("PROGRESS_DATE"),
                    rs.getString("PROGRESS_STATUS")
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No matching projects found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search Error: " + ex.getMessage());
        }
    }
}

