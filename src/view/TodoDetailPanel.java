package view;

import controller.MainController;
import model.Todo;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoDetailPanel extends JPanel {

    private final MainController controller;

    private LocalDate currentDate;
    private List<Todo> currentTodos = new ArrayList<>();

    private final DefaultTableModel tableModel;
    private final JTable table;

    public TodoDetailPanel(MainController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("선택된 날짜의 일정");
        JButton addBtn = new JButton("이 날짜에 일정 추가");
        addBtn.addActionListener(e -> openFormForCurrentDate());
        top.add(title, BorderLayout.WEST);
        top.add(addBtn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        String[] cols = {"완료", "날짜", "제목", "우선순위"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return Object.class;
            }
        };
        table = new JTable(tableModel);

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() != TableModelEvent.UPDATE) return;
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col != 0 || row < 0 || row >= currentTodos.size()) return;

                Boolean val = (Boolean) tableModel.getValueAt(row, 0);
                Todo t = currentTodos.get(row);
                controller.updateTodoCompleted(t, val != null && val);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < currentTodos.size()) {
                        Todo t = currentTodos.get(row);
                        openEditForm(t);
                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
    }

    public void setTodos(List<Todo> todos) {
        currentTodos = new ArrayList<>(todos);
        tableModel.setRowCount(0);
        for (Todo t : currentTodos) {
            tableModel.addRow(new Object[]{
                    t.isCompleted(),
                    t.getDate(),
                    t.getTitle(),
                    t.getPriority()
            });
        }
    }

    private void openFormForCurrentDate() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (!(window instanceof Frame frame)) return;

        LocalDate date = (currentDate != null) ? currentDate : LocalDate.now();
        new TodoFormDialog(frame, controller, null, date);
    }

    private void openEditForm(Todo todo) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (!(window instanceof Frame frame)) return;

        new TodoFormDialog(frame, controller, todo, todo.getDate());
    }
}
