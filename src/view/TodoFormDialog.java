package view;

import controller.MainController;
import model.Todo;
import util.DateUtils;
import util.UIStyle;
import util.Validator;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.*;
import java.time.LocalDate;


public class TodoFormDialog extends JDialog {

    private final JTextField titleField;
    private final JTextArea descArea;

    private final JRadioButton priorityHigh;
    private final JRadioButton priorityMedium;
    private final JRadioButton priorityLow;

    private final JRadioButton statusCompleted;
    private final JRadioButton statusNotCompleted;

    private final JComboBox<Integer> yearBox;
    private final JComboBox<Integer> monthBox;
    private final JComboBox<Integer> dayBox;

    private final MainController controller;
    private Todo todo;

    public TodoFormDialog(Frame owner, MainController controller, Todo todo) {
        this(owner, controller, todo, null);
    }

    public TodoFormDialog(Frame owner, MainController controller, Todo todo, LocalDate defaultDate) {
        super(owner, true);
        this.controller = controller;
        this.todo = todo;

        setTitle(todo == null ? "새 일정 추가" : "일정 수정");
        setSize(420, 380);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout()); // 가운데 카드 하나
        getContentPane().setBackground(UIStyle.getBackground());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UIStyle.getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                int arc = 20;
                int pad = 8;
                int w = getWidth() - pad * 2;
                int h = getHeight() - pad * 2;

                g2.setColor(UIStyle.getCardBackground());
                g2.fillRoundRect(pad, pad, w, h, arc, arc);

                g2.setColor(UIStyle.getCardBorder());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(pad, pad, w, h, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(card, gbc);

        JLabel titleLabel = new JLabel(todo == null ? "새 일정" : "일정 수정");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(UIStyle.getTextPrimary());
        card.add(titleLabel, BorderLayout.NORTH);

        // 폼 영역
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        card.add(form, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;

        c.gridx = 0; c.gridy = row;
        form.add(makeLabel("제목"), c);
        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        
        titleField = new JTextField();
        UIStyle.styleTextField(titleField);
        form.add(titleField, c);

        row++;
        c.gridx = 0; c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(makeLabel("날짜"), c);
        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        LocalDate initialDate;
        if (todo != null && todo.getDate() != null) {
            initialDate = todo.getDate();
        } else if (defaultDate != null) {
            initialDate = defaultDate;
        } else {
            initialDate = DateUtils.getToday();
        }

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        datePanel.setOpaque(false);

        yearBox = createYearBox(initialDate.getYear());
        monthBox = createMonthBox(initialDate.getMonthValue());
        dayBox = createDayBox(initialDate.getYear(), initialDate.getMonthValue(), initialDate.getDayOfMonth());

        yearBox.addActionListener(e -> updateDayBox());
        monthBox.addActionListener(e -> updateDayBox());
        
        datePanel.add(yearBox);
        JLabel yearLabel = new JLabel("년");
        yearLabel.setForeground(UIStyle.getTextSecondary());
        datePanel.add(yearLabel);
        datePanel.add(monthBox);
        JLabel monthLabel = new JLabel("월");
        monthLabel.setForeground(UIStyle.getTextSecondary());
        datePanel.add(monthLabel);
        datePanel.add(dayBox);
        JLabel dayLabel = new JLabel("일");
        dayLabel.setForeground(UIStyle.getTextSecondary());
        datePanel.add(dayLabel);

        form.add(datePanel, c);

        row++;
        c.gridx = 0; c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(makeLabel("중요도"), c);
        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        priorityPanel.setOpaque(false);

        priorityHigh = new JRadioButton();
        priorityMedium = new JRadioButton();
        priorityLow = new JRadioButton();

        ButtonGroup priorityGroup = new ButtonGroup();
        priorityGroup.add(priorityHigh);
        priorityGroup.add(priorityMedium);
        priorityGroup.add(priorityLow);

        priorityHigh.setOpaque(false);
        priorityMedium.setOpaque(false);
        priorityLow.setOpaque(false);

        priorityMedium.setSelected(true);

        priorityPanel.add(createPriorityOption(priorityHigh, "상", UIStyle.getPriorityHighColor()));
        priorityPanel.add(createPriorityOption(priorityMedium, "중", UIStyle.getPriorityMediumColor()));
        priorityPanel.add(createPriorityOption(priorityLow, "하", UIStyle.getPriorityLowColor()));

        form.add(priorityPanel, c);

        row++;
        c.gridx = 0; c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(makeLabel("상태"), c);
        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusPanel.setOpaque(false);

        statusNotCompleted = new JRadioButton("진행 중");
        statusCompleted = new JRadioButton("완료");

        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(statusNotCompleted);
        statusGroup.add(statusCompleted);

        statusNotCompleted.setOpaque(false);
        statusCompleted.setOpaque(false);
        statusNotCompleted.setForeground(UIStyle.getTextSecondary());
        statusCompleted.setForeground(UIStyle.getTextSecondary());
        statusNotCompleted.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        statusCompleted.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

        statusNotCompleted.setSelected(true);

        statusPanel.add(statusNotCompleted);
        statusPanel.add(statusCompleted);

        form.add(statusPanel, c);

        row++;
        c.gridx = 0; c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(makeLabel("내용"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        UIStyle.styleTextArea(descArea);

        JScrollPane scroll = new JScrollPane(descArea);

        UIStyle.styleTextScrollPane(scroll);

        form.add(scroll, c);


        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        if (todo != null && todo.getId() != 0) {
            JButton deleteBtn = new JButton("삭제");
            styleDangerButton(deleteBtn);
            deleteBtn.addActionListener(e -> onDelete());
            bottom.add(deleteBtn);
        }

        JButton cancelBtn = new JButton("취소");
        JButton saveBtn = new JButton("저장");

        styleFlatButton(cancelBtn);
        stylePrimaryButton(saveBtn);

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> onSave());

        bottom.add(cancelBtn);
        bottom.add(saveBtn);

        card.add(bottom, BorderLayout.SOUTH);

        if (todo != null) {
            loadTodo(todo);
        }

        setUndecorated(false);
        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        l.setForeground(UIStyle.getTextSecondary());
        return l;
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
        btn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
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

    private void styleDangerButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(210, 70, 80));
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
        UIStyle.styleComboBox(box);
        return box;
    }

    private JComboBox<Integer> createMonthBox(int currentMonth) {
        JComboBox<Integer> box = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            box.addItem(m);
        }
        box.setSelectedItem(currentMonth);
        UIStyle.styleComboBox(box);
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
        UIStyle.styleComboBox(box);
        return box;
    }


    private void updateDayBox() {
        Integer y = (Integer) yearBox.getSelectedItem();
        Integer m = (Integer) monthBox.getSelectedItem();
        if (y == null || m == null) return;

        Integer selectedDay = (Integer) dayBox.getSelectedItem();
        dayBox.removeAllItems();

        int daysInMonth = LocalDate.of(y, m, 1).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            dayBox.addItem(d);
        }
        if (selectedDay != null && selectedDay <= daysInMonth) {
            dayBox.setSelectedItem(selectedDay);
        } else {
            dayBox.setSelectedItem(daysInMonth);
        }
    }

    private LocalDate getDateFromCombo() {
        Integer y = (Integer) yearBox.getSelectedItem();
        Integer m = (Integer) monthBox.getSelectedItem();
        Integer d = (Integer) dayBox.getSelectedItem();
        if (y == null || m == null || d == null) return null;
        return LocalDate.of(y, m, d);
    }

    private void loadTodo(Todo t) {
        titleField.setText(t.getTitle());
        if (t.getDate() != null) {
            LocalDate d = t.getDate();
            yearBox.setSelectedItem(d.getYear());
            monthBox.setSelectedItem(d.getMonthValue());
            updateDayBox();
            dayBox.setSelectedItem(d.getDayOfMonth());
        }
        descArea.setText(t.getDescription());

        if (t.isCompleted()) {
            statusCompleted.setSelected(true);
        } else {
            statusNotCompleted.setSelected(true);
        }

        int p = t.getPriority();
        switch (p) {
            case 1 -> priorityHigh.setSelected(true);
            case 2 -> priorityMedium.setSelected(true);
            case 3 -> priorityLow.setSelected(true);
            default -> priorityMedium.setSelected(true);
        }
    }

    private void onSave() {
        String title = titleField.getText();

        if (Validator.isEmpty(title)) {
            JOptionPane.showMessageDialog(this, "제목은 필수입니다.");
            return;
        }

        LocalDate date = getDateFromCombo();
        if (date == null) {
            JOptionPane.showMessageDialog(this, "날짜를 선택해주세요.");
            return;
        }

        if (todo == null) {
            todo = new Todo();
        }

        todo.setTitle(title);
        todo.setDescription(descArea.getText());
        todo.setDate(date);
        todo.setCompleted(statusCompleted.isSelected());

        int priority = 2;
        if (priorityHigh.isSelected()) {
            priority = 1;
        } else if (priorityMedium.isSelected()) {
            priority = 2;
        } else if (priorityLow.isSelected()) {
            priority = 3;
        }
        todo.setPriority(priority);

        controller.saveTodo(todo);
        dispose();
    }

    private void onDelete() {
        if (todo == null || todo.getId() == 0) {
            dispose();
            return;
        }

        boolean confirmed = ConfirmDialog.showDeleteConfirm(
                this,
                "이 일정을 삭제할까요?"
        );

        if (confirmed) {
            controller.deleteTodo(todo);
            dispose();
        }
    }
}
