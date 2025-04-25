package src;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import src.panel.*;

public class MainApp extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private HashMap<String, JPanel> panels = new HashMap<>();

    public MainApp() {
        setTitle("Research Project Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1));
        String[] sections = {
            "ResearchProject", "ManagingInstitution", "Monitor",
            "ResearchProjectMonitor", "ResearchProjectManagementInstitution",
            "ResearchProjectProgress", "ResearchProjectFunds", "Progress", "Funds", "Search"
        };

        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        for (String section : sections) {
            JButton btn = new JButton(section);
            btn.addActionListener(e -> cardLayout.show(contentPanel, section));
            sidebar.add(btn);

            JPanel panel = loadPanel(section);
            panels.put(section, panel);
            contentPanel.add(panel, section);
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel loadPanel(String section) {
        switch (section) {
            case "ResearchProject": return new ResearchProjectPanel();
            case "ManagingInstitution": return new ManagingInstitutionPanel();
            case "Monitor": return new MonitorPanel();
            case "ResearchProjectMonitor": return new ResearchProjectMonitorPanel();
            case "ResearchProjectManagementInstitution": return new ResearchProjectManagementInstitutionPanel();
            case "ResearchProjectProgress": return new ResearchProjectProgressPanel();
            case "ResearchProjectFunds": return new ResearchProjectFundsPanel();
            case "Progress": return new ProgressPanel();
            case "Funds": return new FundsPanel();
            case "Search": return new SearchPanel(); // new panel
            default: return new JPanel();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}
