package view;

import controller.MainController;
import model.FilterOptions;
import util.UIStyle;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    private final MainController controller;

    private JTextField searchField;
    private JButton filterButton;
    private JButton themeButton;
    private JButton advancedButton;   // ⭐ 고급검색 버튼

    private JLabel appTitleLabel;
    private JLabel searchLabel;

    private enum FilterMode { ALL, COMPLETED, NOT_COMPLETED }
    private FilterMode filterMode = FilterMode.ALL;

    public SearchPanel(MainController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 0));
        setOpaque(false);

        // ----- 왼쪽: PlanIt + 검색창 -----
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        left.setOpaque(false);

        appTitleLabel = new JLabel("PlanIt");
        appTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        searchLabel = new JLabel("검색:");
        searchLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        searchField = new JTextField(18);
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        searchField.addActionListener(e -> applyFilter());

        left.add(appTitleLabel);
        left.add(Box.createHorizontalStrut(20));
        left.add(searchLabel);
        left.add(searchField);

        add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        right.setOpaque(false);

        filterButton = new JButton("전체");
        advancedButton = new JButton("고급검색");
        themeButton = new JButton("다크모드");

        styleChipButton(filterButton);
        styleChipButton(advancedButton);
        styleChipButton(themeButton);

        filterButton.addActionListener(e -> toggleFilterMode());
        themeButton.addActionListener(e -> toggleTheme());
        advancedButton.addActionListener(e -> openAdvancedSearch());

        right.add(filterButton);
        right.add(advancedButton);
        right.add(themeButton);

        add(right, BorderLayout.EAST);

        applyTheme();
    }

    private void styleChipButton(JButton button) {
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    private void applyTheme() {
        Color primary = UIStyle.getTextPrimary();
        Color secondary = UIStyle.getTextSecondary();

        appTitleLabel.setForeground(primary);
        searchLabel.setForeground(primary);

        searchField.setForeground(primary);
        searchField.setBackground(
                UIStyle.isDarkMode()
                        ? new Color(40, 44, 52)
                        : Color.WHITE
        );
        searchField.setCaretColor(primary);
        searchField.setBorder(BorderFactory.createLineBorder(
                UIStyle.isDarkMode() ? new Color(90, 90, 110) : new Color(200, 200, 200)
        ));

        filterButton.setForeground(secondary);
        advancedButton.setForeground(secondary);
        themeButton.setForeground(secondary);
    }

    private void toggleFilterMode() {
        switch (filterMode) {
            case ALL -> {
                filterMode = FilterMode.COMPLETED;
                filterButton.setText("완료만");
            }
            case COMPLETED -> {
                filterMode = FilterMode.NOT_COMPLETED;
                filterButton.setText("미완료만");
            }
            case NOT_COMPLETED -> {
                filterMode = FilterMode.ALL;
                filterButton.setText("전체");
            }
        }
        applyFilter();
    }

    private void applyFilter() {
        FilterOptions f = new FilterOptions();
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) f.setKeyword(keyword);

        switch (filterMode) {
            case COMPLETED -> f.setCompleted(true);
            case NOT_COMPLETED -> f.setCompleted(false);
        }

        controller.applyFilter(f);
    }

    private void toggleTheme() {
        UIStyle.toggleDarkMode();
        applyTheme();

        controller.onThemeChanged();  // 이미 구현되어 있다고 했던 메서드

        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.repaint();
        }
    }

    private void openAdvancedSearch() {
        Window w = SwingUtilities.getWindowAncestor(this);
        Frame owner = (w instanceof Frame) ? (Frame) w : null;
        new AdvancedSearchDialog(owner, controller);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UIStyle.getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
