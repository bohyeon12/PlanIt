package view;

import controller.MainController;
import dao.TodoDAO;
import util.UIStyle;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import java.awt.*;

public class MainFrame extends JFrame {

    private JSplitPane splitPane;
    private final MainController controller;

    public MainFrame() {
        setTitle("PlanIt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        getContentPane().setBackground(UIStyle.getBackground());

        TodoDAO dao = new TodoDAO();
        controller = new MainController(dao);

        SearchPanel searchPanel = new SearchPanel(controller);
        add(searchPanel, BorderLayout.NORTH);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(UIStyle.getBackground());
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        g2.setColor(UIStyle.getCardBorder());
                        int w = getWidth();
                        g2.fillRect(w / 2 - 1, 0, 2, getHeight());

                        g2.dispose();
                    }
                };
            }
        });

        CalendarViewPanel calendarPanel = new CalendarViewPanel(controller);
        TodoListViewPanel listPanel = new TodoListViewPanel(controller);

        splitPane.setLeftComponent(calendarPanel);
        splitPane.setRightComponent(listPanel);
        splitPane.setDividerLocation(0.6);
        splitPane.setResizeWeight(0.6);

        add(splitPane, BorderLayout.CENTER);

        controller.setCalendarView(calendarPanel);
        controller.setListView(listPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
