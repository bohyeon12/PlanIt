// view/TodoFormDialog.java
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
    // ★ dateField 대신 연/월/일 콤보박스 사용
    // private final JTextField dateField;
    private final JTextArea descArea;
    private final JComboBox<String> priorityBox;
    private final JCheckBox completedBox;

    // ★ 날짜 콤보박스 필드 추가
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


        // 날짜 (연/월/일 콤보박스) ★ 변경 부분
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

        // 연/월 변경 시 일 콤보 갱신
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

        // 중요도
        row++;
        c.gridx = 0; c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(makeLabel("중요도"), c);
        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        priorityBox = new JComboBox<>(new String[]{"상", "중", "하"});
        UIStyle.styleComboBox(priorityBox);
        form.add(priorityBox, c);

        // 완료 여부
        row++;
        c.gridx = 0; c.gridy = row;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(makeLabel("상태"), c);
        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        completedBox = new JCheckBox("완료된 일정입니다");
        completedBox.setOpaque(false);
        completedBox.setForeground(UIStyle.getTextSecondary());
        completedBox.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        form.add(completedBox, c);

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

    // ★ 삭제 버튼용 스타일
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
        UIStyle.styleComboBox(box);  // ★ 여기
        return box;
    }

    private JComboBox<Integer> createMonthBox(int currentMonth) {
        JComboBox<Integer> box = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            box.addItem(m);
        }
        box.setSelectedItem(currentMonth);
        UIStyle.styleComboBox(box);  // ★ 여기
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
        UIStyle.styleComboBox(box);  // ★ 여기
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
        completedBox.setSelected(t.isCompleted());

        int p = t.getPriority();
        int idx = switch (p) {
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            default -> 1;
        };
        priorityBox.setSelectedIndex(idx);
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
        todo.setCompleted(completedBox.isSelected());

        int idx = priorityBox.getSelectedIndex();
        int priority = switch (idx) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            default -> 2;
        };
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
