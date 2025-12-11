package controller;

import dao.TodoDAO;
import model.FilterOptions;
import model.Todo;
import view.CalendarViewPanel;
import view.TodoListViewPanel;

import java.time.LocalDate;
import java.util.List;

public class MainController {

    private final TodoDAO todoDAO;

    private CalendarViewPanel calendarView;
    private TodoListViewPanel listView;

    private LocalDate currentDate;
    private FilterOptions lastFilter; 

    public MainController(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public void setCalendarView(CalendarViewPanel calendarView) {
        this.calendarView = calendarView;
    }

    public void setListView(TodoListViewPanel listView) {
        this.listView = listView;
    }

    /* ===== 달력 날짜 클릭 ===== */

    public void onDateSelected(LocalDate date) {
        this.currentDate = date;
        List<Todo> todos = todoDAO.findByDate(date);
        if (listView != null) {
            listView.showTodosForDate(date, todos);
        }
    }

    /* ===== 검색/필터 ===== */

    public void applyFilter(FilterOptions filter) {
        this.currentDate = null;
        this.lastFilter = filter;
        List<Todo> todos = todoDAO.findByFilter(filter);
        if (listView != null) {
            listView.showTodos(todos);
        }
    }

    /* ===== Todo 저장/삭제/완료 ===== */


	public boolean hasTodoOn(LocalDate date) {
	    return todoDAO.existsByDate(date);
	}

	public Integer getHighestPriorityForDate(LocalDate date) {
	    return todoDAO.getHighestPriorityForDate(date);
	}

	public void saveTodo(Todo todo) {
	    if (todo.getId() == 0) {
	        todoDAO.insert(todo);
	    } else {
	        todoDAO.update(todo);
	    }

	    if (calendarView != null) {
	        calendarView.refresh();
	    }

	    if (todo.getDate() != null) {
	        onDateSelected(todo.getDate());
	    } else if (lastFilter != null) {
	        applyFilter(lastFilter);
	    }
	}

	public void deleteTodo(Todo todo) {
	    if (todo == null || todo.getId() == 0) return;

	    todoDAO.delete(todo.getId());

	    if (calendarView != null) {
	        calendarView.refresh();
	    }

	    if (lastFilter != null) {
	        applyFilter(lastFilter);
	    } else if (todo.getDate() != null) {
	        onDateSelected(todo.getDate());
	    } else {
	        FilterOptions f = new FilterOptions();
	        listView.showTodos(todoDAO.findByFilter(f));
	    }
	}

    public void updateTodoCompleted(Todo todo, boolean completed) {
        if (todo == null || todo.getId() == 0) return;
        todo.setCompleted(completed);
        todoDAO.update(todo);
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }
    

    public void onThemeChanged() {
        if (calendarView != null) {
            calendarView.refresh();
        }
        if (listView != null) {
            listView.applyTheme();
        }
    }

}
