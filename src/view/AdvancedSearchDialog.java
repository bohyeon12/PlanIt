package view;

import controller.MainController;
import model.FilterOptions;
import util.DateUtils;
import util.UIStyle;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AdvancedSearchDialog extends JDialog {

    private final JTextField keywordField;

    private final JComboBox<Integer> startYearBox;
    private final JComboBox<Integer> startMonthBox;
    private final JComboBox<Integer> startDayBox;

    private final JComboBox<Integer> endYearBox;
    private final JComboBox<Integer> endMonthBox;
    private final JComboBox<Integer> endDayBox;

    private final JRadioButton priorityAll;
    private final JRadioButton priorityHigh;
    private final JRadioButton priorityMedium;
    private final JRadioButton priorityLow;

    private final JRadioButton statusAll;
    private final JRadioButton statusCompleted;
    private final JRadioButton statusNotCompleted;

    private final MainController controller;

    public AdvancedSearchDialog(Frame owner, MainController controller) {
        super(owner, "고급검색", true);
        this.controller = controller;

        setSize(550, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyle.getBackground());

        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UIStyle.getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                int arc = 18;
                int pad = 8;
                int w = getWidth() - pad * 2;
                int h = getHeight() - pad * 2;

                g2.setColor(UIStyle.getCardBackground());
                g2.fillRoundRect(pad, pad, w, h, arc, arc);

                g2.setColor(UIStyle.getCardBorder());
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(pad, pad, w, h, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(card, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        card.add(form, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;
        
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("제목 키워드"), c);
        c.gridx = 1; c.gridy = row;
        keywordField = new JTextField();
        UIStyle.styleTextField(keywordField);
        form.add(keywordField, c);

        LocalDate today = DateUtils.getToday();

        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("시작 날짜"), c);

        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        startDatePanel.setOpaque(false);

        startYearBox = createYearBox(today.getYear());
        startMonthBox = createMonthBox(today.getMonthValue());
        startDayBox = createDayBox(today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        startYearBox.addActionListener(e -> updateDayBox(startYearBox, startMonthBox, startDayBox));
        startMonthBox.addActionListener(e -> updateDayBox(startYearBox, startMonthBox, startDayBox));
        
        UIStyle.styleComboBox(startYearBox);
        UIStyle.styleComboBox(startMonthBox);
        UIStyle.styleComboBox(startDayBox);
        
        startDatePanel.add(startYearBox);
        JLabel yearLabel = new JLabel("년");
        yearLabel.setForeground(UIStyle.getTextSecondary());
        startDatePanel.add(yearLabel);
        startDatePanel.add(startMonthBox);
        JLabel monthLabel = new JLabel("월");
        monthLabel.setForeground(UIStyle.getTextSecondary());
        startDatePanel.add(monthLabel);
        startDatePanel.add(startDayBox);
        JLabel dayLabel = new JLabel("일");
        dayLabel.setForeground(UIStyle.getTextSecondary());
        startDatePanel.add(dayLabel);

        c.gridx = 1; c.gridy = row;
        form.add(startDatePanel, c);

        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("끝 날짜"), c);

        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        endDatePanel.setOpaque(false);

        endYearBox = createYearBox(today.getYear());
        endMonthBox = createMonthBox(today.getMonthValue());
        endDayBox = createDayBox(today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        endYearBox.addActionListener(e -> updateDayBox(endYearBox, endMonthBox, endDayBox));
        endMonthBox.addActionListener(e -> updateDayBox(endYearBox, endMonthBox, endDayBox));
        
        UIStyle.styleComboBox(endYearBox);
        UIStyle.styleComboBox(endMonthBox);
        UIStyle.styleComboBox(endDayBox);

        endDatePanel.add(endYearBox);
        yearLabel = new JLabel("년");
        yearLabel.setForeground(UIStyle.getTextSecondary());
        endDatePanel.add(yearLabel);
        endDatePanel.add(endMonthBox);
        monthLabel = new JLabel("월");
        monthLabel.setForeground(UIStyle.getTextSecondary());
        endDatePanel.add(monthLabel);
        endDatePanel.add(endDayBox);
        dayLabel = new JLabel("일");
        dayLabel.setForeground(UIStyle.getTextSecondary());
        endDatePanel.add(dayLabel);

        c.gridx = 1; c.gridy = row;
        form.add(endDatePanel, c);

        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("중요도"), c);

        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        priorityPanel.setOpaque(false);

        priorityAll = new JRadioButton();
        priorityHigh = new JRadioButton();
        priorityMedium = new JRadioButton();
        priorityLow = new JRadioButton();

        ButtonGroup priorityGroup = new ButtonGroup();
        priorityGroup.add(priorityAll);
        priorityGroup.add(priorityHigh);
        priorityGroup.add(priorityMedium);
        priorityGroup.add(priorityLow);

        priorityAll.setOpaque(false);
        priorityHigh.setOpaque(false);
        priorityMedium.setOpaque(false);
        priorityLow.setOpaque(false);
        priorityAll.setSelected(true);

        priorityPanel.add(createSimpleOption(priorityAll, "전체"));
        priorityPanel.add(createPriorityOption(priorityHigh, "상", UIStyle.getPriorityHighColor()));
        priorityPanel.add(createPriorityOption(priorityMedium, "중", UIStyle.getPriorityMediumColor()));
        priorityPanel.add(createPriorityOption(priorityLow, "하", UIStyle.getPriorityLowColor()));

        c.gridx = 1; c.gridy = row;
        form.add(priorityPanel, c);

        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("상태"), c);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusPanel.setOpaque(false);

        statusAll = new JRadioButton();
        statusCompleted = new JRadioButton();
        statusNotCompleted = new JRadioButton();

        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(statusAll);
        statusGroup.add(statusCompleted);
        statusGroup.add(statusNotCompleted);

        statusAll.setOpaque(false);
        statusCompleted.setOpaque(false);
        statusNotCompleted.setOpaque(false);

        statusAll.setSelected(true);

        statusPanel.add(createSimpleOption(statusAll, "전체"));
        statusPanel.add(createSimpleOption(statusCompleted, "완료"));
        statusPanel.add(createSimpleOption(statusNotCompleted, "미완료"));

        c.gridx = 1; c.gridy = row;
        form.add(statusPanel, c);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton cancelBtn = new JButton("취소");
        JButton searchBtn = new JButton("검색");

        styleFlatButton(cancelBtn);
        stylePrimaryButton(searchBtn);

        cancelBtn.addActionListener(e -> dispose());
        searchBtn.addActionListener(e -> onSearch());

        bottom.add(cancelBtn);
        bottom.add(searchBtn);

        card.add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        l.setForeground(UIStyle.getTextSecondary());
        return l;
    }

    private JPanel createSimpleOption(JRadioButton radio, String label) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panel.setOpaque(false);

        JLabel textLabel = new JLabel(label);
        textLabel.setForeground(UIStyle.getTextSecondary());
        textLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        panel.add(radio);
        panel.add(textLabel);

        return panel;
    }

    private JPanel createPriorityOption(JRadioButton radio, String label, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panel.setOpaque(false);

        JPanel colorCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(2, 2, 12, 12);
            }
        };
        colorCircle.setPreferredSize(new Dimension(16, 16));
        colorCircle.setOpaque(false);

        JLabel textLabel = new JLabel(label);
        textLabel.setForeground(UIStyle.getTextSecondary());
        textLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        panel.add(radio);
        panel.add(textLabel);
        panel.add(colorCircle);

        return panel;
    }

    private void styleFlatButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setForeground(UIStyle.getTextSecondary());
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(UIStyle.getAccent());
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btn.setOpaque(true);
    }

    private JComboBox<Integer> createYearBox(int currentYear) {
        JComboBox<Integer> box = new JComboBox<>();
        for (int y = currentYear - 5; y <= currentYear + 5; y++) {
            box.addItem(y);
        }
        box.setSelectedItem(currentYear);
        box.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        return box;
    }

    private JComboBox<Integer> createMonthBox(int currentMonth) {
        JComboBox<Integer> box = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            box.addItem(m);
        }
        box.setSelectedItem(currentMonth);
        box.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        return box;
    }

    private JComboBox<Integer> createDayBox(int year, int month, int currentDay) {
        JComboBox<Integer> box = new JComboBox<>();
        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            box.addItem(d);
        }
        if (currentDay <= daysInMonth) {
            box.setSelectedItem(currentDay);
        } else {
            box.setSelectedItem(daysInMonth);
        }
        box.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        return box;
    }

    private void updateDayBox(JComboBox<Integer> yBox,
                              JComboBox<Integer> mBox,
                              JComboBox<Integer> dBox) {
        Integer y = (Integer) yBox.getSelectedItem();
        Integer m = (Integer) mBox.getSelectedItem();
        if (y == null || m == null) return;

        Integer selectedDay = (Integer) dBox.getSelectedItem();
        dBox.removeAllItems();

        int daysInMonth = LocalDate.of(y, m, 1).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            dBox.addItem(d);
        }
        if (selectedDay != null && selectedDay <= daysInMonth) {
            dBox.setSelectedItem(selectedDay);
        } else {
            dBox.setSelectedItem(daysInMonth);
        }
    }

    private LocalDate getDateFromCombo(JComboBox<Integer> yBox,
                                       JComboBox<Integer> mBox,
                                       JComboBox<Integer> dBox) {
        Integer y = (Integer) yBox.getSelectedItem();
        Integer m = (Integer) mBox.getSelectedItem();
        Integer d = (Integer) dBox.getSelectedItem();
        if (y == null || m == null || d == null) return null;
        return LocalDate.of(y, m, d);
    }

    private void onSearch() {
        FilterOptions f = new FilterOptions();

        String keyword = keywordField.getText().trim();
        if (!keyword.isEmpty()) {
            f.setKeyword(keyword);
        }

        LocalDate s = getDateFromCombo(startYearBox, startMonthBox, startDayBox);
        LocalDate e = getDateFromCombo(endYearBox, endMonthBox, endDayBox);
        if (s != null) f.setStartDate(s);
        if (e != null) f.setEndDate(e);

        if (priorityHigh.isSelected()) {
            f.setPriority(1);
        } else if (priorityMedium.isSelected()) {
            f.setPriority(2);
        } else if (priorityLow.isSelected()) {
            f.setPriority(3);
        }

        if (statusCompleted.isSelected()) {
            f.setCompleted(true);
        } else if (statusNotCompleted.isSelected()) {
            f.setCompleted(false);
        }

        controller.applyFilter(f);
        dispose();
    }
}
