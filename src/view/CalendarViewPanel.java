package view;

import controller.MainController;
import util.DateUtils;
import util.UIStyle;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CalendarViewPanel extends JPanel {
    private JLabel monthTitle;
    private JLabel yearTitle;
    private JPanel dayPanel;
    private JButton prevBtn;
    private JButton nextBtn;
    private JLabel yearSuffixLabel; 
    private JLabel monthSuffixLabel; 
    private JLabel[] weekdayLabels = new JLabel[7];


    private final int OUTER_MARGIN = 30;
    private final int INNER_PADDING = 20;
    private final int ARC_SIZE = 40;

    private final MainController controller;
    private LocalDate currentMonth;

    public CalendarViewPanel(MainController controller) {
        this.controller = controller;
        this.currentMonth = DateUtils.getToday().withDayOfMonth(1);

        setOpaque(false);
        setLayout(new BorderLayout());

        int totalPadding = OUTER_MARGIN + INNER_PADDING;
        setBorder(BorderFactory.createEmptyBorder(totalPadding, totalPadding, totalPadding, totalPadding));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        titlePanel.setOpaque(false);

        prevBtn = new JButton("<");
        nextBtn = new JButton(">");

        styleNavButton(prevBtn);
        styleNavButton(nextBtn);

        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            renderCalendar();
        });
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            renderCalendar();
        });

        yearTitle = new JLabel(String.valueOf(currentMonth.getYear()));
        yearTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        monthTitle = new JLabel(String.valueOf(currentMonth.getMonthValue()));
        monthTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        yearSuffixLabel = new JLabel("년");
        monthSuffixLabel = new JLabel("월");

        titlePanel.add(prevBtn);
        titlePanel.add(yearTitle);
        titlePanel.add(yearSuffixLabel);
        titlePanel.add(monthTitle);
        titlePanel.add(monthSuffixLabel);
        titlePanel.add(nextBtn);

        add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new GridLayout(1, 7));
        headerPanel.setOpaque(false);

        String[] days = {"일", "월", "화", "수", "목", "금", "토"};

        for (int i = 0; i < days.length; i++) {
            String d = days[i];
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 13));

            weekdayLabels[i] = label;   // ★ 필드에 저장
            headerPanel.add(label);
        }

        centerPanel.add(headerPanel, BorderLayout.NORTH);

        dayPanel = new JPanel(new GridLayout(0, 7));
        dayPanel.setOpaque(false);
        centerPanel.add(dayPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        renderCalendar();
    }

    public void refresh() {
        renderCalendar();    // 날짜 부분 다시 그림
    }
    
    private void styleNavButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(2, 8, 2, 8));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setForeground(UIStyle.getTextPrimary());
    }

    private void renderCalendar() {
	    dayPanel.removeAll();

	    yearTitle.setText(String.valueOf(currentMonth.getYear()));
	    monthTitle.setText(String.valueOf(currentMonth.getMonthValue()));

	    Color primary = UIStyle.getTextPrimary();

	    yearTitle.setForeground(primary);
	    monthTitle.setForeground(primary);
	    yearSuffixLabel.setForeground(primary);
	    monthSuffixLabel.setForeground(primary);

	    styleNavButton(prevBtn);
	    styleNavButton(nextBtn);

	    for (int i = 0; i < weekdayLabels.length; i++) {
	        JLabel label = weekdayLabels[i];
	        if (label == null) continue;

	        if (i == 0) {
	            label.setForeground(UIStyle.getSundayColor());
	        } else if (i == 6) {
	            label.setForeground(UIStyle.getSaturdayColor());
	        } else {
	            label.setForeground(primary);
	        }
	    }

	    int offset = DateUtils.getFirstDayOffset(currentMonth);
	    int daysInMonth = DateUtils.getDaysInMonth(currentMonth);
	    LocalDate today = DateUtils.getToday();

        for (int i = 0; i < offset; i++) {
            dayPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            Integer highestPriority = controller.getHighestPriorityForDate(date);
            boolean isToday = DateUtils.isSameDate(date, today);

            DayButton btn = new DayButton(String.valueOf(day), date, highestPriority, isToday);

            btn.addActionListener(e -> controller.onDateSelected(date));

            dayPanel.add(btn);
        }

        dayPanel.revalidate();
        dayPanel.repaint();
    }

    private static class DayButton extends JButton {
        private final LocalDate date;
        private final Integer highestPriority;
        private final boolean isToday;

        public DayButton(String text, LocalDate date, Integer highestPriority, boolean isToday) {
            super(text);
            this.date = date;
            this.highestPriority = highestPriority;
            this.isToday = isToday;

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(getFont().deriveFont(Font.PLAIN, 13f));

            setForeground(UIStyle.getTextPrimary());

            switch (date.getDayOfWeek()) {
                case SUNDAY -> setForeground(UIStyle.getSundayColor());
                case SATURDAY -> setForeground(UIStyle.getSaturdayColor());
            }
            
            if (isToday) {
                setForeground(UIStyle.getTodayColor());
                setFont(getFont().deriveFont(Font.BOLD));
            }

            if (highestPriority != null) {
                setFont(getFont().deriveFont(Font.BOLD));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (highestPriority != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int d = 8;
                int x = (getWidth() - d) / 2;
                int y = getHeight() - d - 4;

                Color priorityColor;
                if (highestPriority == 1) {
                    priorityColor = UIStyle.getPriorityHighColor();
                } else if (highestPriority == 2) {
                    priorityColor = UIStyle.getPriorityMediumColor();
                } else {
                    priorityColor = UIStyle.getPriorityLowColor();
                }

                g2.setColor(priorityColor);
                g2.fillOval(x, y, d, d);

                g2.setColor(UIStyle.getBackground());
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(x, y, d, d);

                g2.dispose();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rectX = OUTER_MARGIN;
        int rectY = OUTER_MARGIN;
        int rectWidth = getWidth() - (OUTER_MARGIN * 2);
        int rectHeight = getHeight() - (OUTER_MARGIN * 2);

        g2.setColor(UIStyle.getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(UIStyle.getCardBackground());
        g2.fillRoundRect(rectX, rectY, rectWidth, rectHeight, ARC_SIZE, ARC_SIZE);

        g2.setColor(UIStyle.getCardBorder());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(rectX, rectY, rectWidth, rectHeight, ARC_SIZE, ARC_SIZE);

        g2.dispose();
        super.paintComponent(g);
    }
}
