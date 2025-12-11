// view/AdvancedSearchDialog.java
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

    // 시작 날짜 콤보박스
    private final JComboBox<Integer> startYearBox;
    private final JComboBox<Integer> startMonthBox;
    private final JComboBox<Integer> startDayBox;

    // 끝 날짜 콤보박스
    private final JComboBox<Integer> endYearBox;
    private final JComboBox<Integer> endMonthBox;
    private final JComboBox<Integer> endDayBox;

    private final JComboBox<String> priorityBox;
    private final JComboBox<String> statusBox;  
    
    private final MainController controller;

    public AdvancedSearchDialog(Frame owner, MainController controller) {
        super(owner, "고급검색", true);
        this.controller = controller;

        setSize(420, 320);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIStyle.getBackground());

        // ===== 카드 패널 =====
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

        // ===== 폼 영역 =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        card.add(form, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // 1) 키워드
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("제목 키워드"), c);

        keywordField = new JTextField();
        keywordField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        c.gridx = 1; c.gridy = row;
        keywordField.setForeground(UIStyle.getTextPrimary());
        keywordField.setBackground(
                UIStyle.isDarkMode()
                        ? new Color(40, 44, 52)
                        : Color.WHITE
        );
        keywordField.setCaretColor(UIStyle.getTextPrimary());
        keywordField.setBorder(BorderFactory.createLineBorder(
                UIStyle.isDarkMode() ? new Color(90, 90, 110) : new Color(200, 200, 200)
        ));
        form.add(keywordField, c);

        // 오늘 날짜 기준
        LocalDate today = DateUtils.getToday();

        // 2) 시작 날짜 (연/월/일 콤보)
        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("시작 날짜"), c);

        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        startDatePanel.setOpaque(false);

        startYearBox = createYearBox(today.getYear());
        startMonthBox = createMonthBox(today.getMonthValue());
        startDayBox = createDayBox(today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        // 연/월 변경 시 일 콤보 갱신
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

        // 3) 끝 날짜 (연/월/일 콤보)
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

        // 4) 중요도
        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("중요도"), c);

        priorityBox = new JComboBox<>(new String[]{"전체", "상", "중", "하"});
        c.gridx = 1; c.gridy = row;
        UIStyle.styleComboBox(priorityBox);
        form.add(priorityBox, c);

        // 5) 상태 (완료/미완료)
        row++;
        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("상태"), c);

        statusBox = new JComboBox<>(new String[]{"전체", "완료만", "미완료만"});
        c.gridx = 1; c.gridy = row;
        UIStyle.styleComboBox(statusBox);
        form.add(statusBox, c);

        // ===== 버튼 영역 (카드 안 SOUTH에 배치) =====
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

        card.add(bottom, BorderLayout.SOUTH);  // ★ 카드 안에 넣음

        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        l.setForeground(UIStyle.getTextSecondary());
        return l;
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

    // 연 콤보: 현재 년도를 가운데로 ±5년 범위
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

        // 1) 키워드
        String keyword = keywordField.getText().trim();
        if (!keyword.isEmpty()) {
            f.setKeyword(keyword);
        }

        // 2) 날짜 범위
        LocalDate s = getDateFromCombo(startYearBox, startMonthBox, startDayBox);
        LocalDate e = getDateFromCombo(endYearBox, endMonthBox, endDayBox);
        if (s != null) f.setStartDate(s);
        if (e != null) f.setEndDate(e);

        // 3) 중요도
        int pIdx = priorityBox.getSelectedIndex();
        if (pIdx == 1)      f.setPriority(1);   // 상
        else if (pIdx == 2) f.setPriority(2);   // 중
        else if (pIdx == 3) f.setPriority(3);   // 하
        // 0 = 전체 → null

        // 4) 상태 (완료/미완료)
        int sIdx = statusBox.getSelectedIndex();
        if (sIdx == 1) {          // 완료만
            f.setCompleted(true);
        } else if (sIdx == 2) {   // 미완료만
            f.setCompleted(false);
        } // 0 = 전체 → null

        controller.applyFilter(f);
        dispose();
    }
}
