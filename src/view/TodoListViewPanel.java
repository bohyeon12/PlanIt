// view/TodoListViewPanel.java
package view;

import controller.MainController;
import model.Todo;
import util.DateUtils;
import util.UIStyle;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoListViewPanel extends JPanel {

    private JPanel listContainer;
    private JButton addButton;
    private JLabel titleLabel;
    private JPanel scrollWrapper;
    private JPanel btnPanel;
    private JScrollPane scrollPane;

    private final int OUTER_MARGIN = 30;
    private final int INNER_PADDING = 20;
    private final int ARC_SIZE = 40;

    private final MainController controller;
    private List<Todo> currentTodos = new ArrayList<>();
    private LocalDate currentDateForNew = null;

    public TodoListViewPanel(MainController controller) {
        this.controller = controller;

        setOpaque(false);
        setLayout(new BorderLayout());

        int totalPadding = OUTER_MARGIN + INNER_PADDING;
        setBorder(BorderFactory.createEmptyBorder(totalPadding, totalPadding, totalPadding, totalPadding));

        // 제목
        titleLabel = new JLabel("Todo List");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // 리스트 컨테이너
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);

        scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.add(scrollPane);
        scrollWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        add(scrollWrapper, BorderLayout.CENTER);

        // 추가 버튼
        addButton = new JButton("+ 새 할 일 목록 추가하기");
        addButton.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        addButton.setContentAreaFilled(false);
        addButton.setFocusPainted(false);
        addButton.setBorder(new RoundedBorder(20));
        addButton.setPreferredSize(new Dimension(220, 45));
        addButton.addActionListener(e -> openNewTodoDialog());

        btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnPanel.add(addButton);

        add(btnPanel, BorderLayout.SOUTH);

        // ★ 테마 색 한 번 적용
        applyTheme();
    }

    public void applyTheme() {
        // 카드 영역 전체는 paintComponent에서 그림 → 여기선 내부 컴포넌트만
        titleLabel.setForeground(UIStyle.getTextPrimary());

        Color cardBg = UIStyle.getCardBackground();
        Color borderColor = UIStyle.getCardBorder();

        listContainer.setBackground(cardBg);
        scrollWrapper.setBackground(cardBg);
        scrollPane.getViewport().setBackground(cardBg);

        btnPanel.setBackground(cardBg);
        addButton.setForeground(UIStyle.getTextSecondary());

        // 리스트 아이템들의 텍스트 색도 다시 맞춰주려면 재렌더링
        // (currentTodos를 기준으로 다시 뿌리기)
        setTodoList(currentTodos, currentDateForNew != null);
    }

    private void openNewTodoDialog() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (!(w instanceof Frame frame)) return;

        LocalDate baseDate = (currentDateForNew != null)
                ? currentDateForNew
                : DateUtils.getToday();

        new TodoFormDialog(frame, controller, null, baseDate);
    }

    // 날짜별 보기
    public void showTodosForDate(LocalDate date, List<Todo> todos) {
        this.currentDateForNew = date;
        setTodoList(todos, true);
    }

    // 검색/필터 보기
    public void showTodos(List<Todo> todos) {
        this.currentDateForNew = null;
        setTodoList(todos, false);
    }

    private void setTodoList(List<Todo> todos, boolean showDateInItem) {
        this.currentTodos = new ArrayList<>(todos);
        listContainer.removeAll();

        for (Todo t : currentTodos) {
            JPanel item = createTodoItemPanel(t, showDateInItem);
            listContainer.add(item);
            listContainer.add(Box.createVerticalStrut(5));
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createTodoItemPanel(Todo todo, boolean showDate) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(UIStyle.getCardBackground());
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // 왼쪽 동그라미
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (todo.isCompleted()) {
                    g2.setColor(UIStyle.getAccent());
                    g2.fillOval(2, 10, 22, 22);
                } else {
                    g2.setColor(UIStyle.getTextSecondary());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(2, 10, 22, 22);
                }
            }
        };
        circle.setPreferredSize(new Dimension(30, 40));
        circle.setBackground(UIStyle.getCardBackground());

        // 텍스트
        StringBuilder sb = new StringBuilder();
        if (showDate && todo.getDate() != null) {
            sb.append("[").append(DateUtils.dateToUiString(todo.getDate())).append("] ");
        }
        sb.append(todo.getTitle());

        final JLabel textLabel = new JLabel(sb.toString());
        textLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        textLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.getCardBorder()));
        textLabel.setForeground(todo.isCompleted()
                ? UIStyle.getTextSecondary()
                : UIStyle.getTextPrimary()
        );

        // 동그라미 클릭 → 완료 토글
        circle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean newVal = !todo.isCompleted();
                todo.setCompleted(newVal);
                controller.updateTodoCompleted(todo, newVal);
                circle.repaint();
                textLabel.setForeground(newVal ? UIStyle.getTextSecondary() : UIStyle.getTextPrimary());
            }
        });

        // 더블클릭 → 수정
        MouseAdapter editListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    openEditTodoDialog(todo);
                }
            }
        };
        itemPanel.addMouseListener(editListener);
        textLabel.addMouseListener(editListener);

        itemPanel.add(circle, BorderLayout.WEST);
        itemPanel.add(textLabel, BorderLayout.CENTER);

        return itemPanel;
    }

    private void openEditTodoDialog(Todo todo) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (!(w instanceof Frame frame)) return;
        new TodoFormDialog(frame, controller, todo, todo.getDate());
    }

    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UIStyle.getTextSecondary());
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
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
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(rectX, rectY, rectWidth, rectHeight, ARC_SIZE, ARC_SIZE);

        g2.dispose();
        super.paintComponent(g);
    }
}
