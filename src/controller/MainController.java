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

    private LocalDate currentDate;      // 마지막으로 선택한 날짜
    private FilterOptions lastFilter;   // 마지막 검색/필터

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
	
	public void saveTodo(Todo todo) {
	    if (todo.getId() == 0) {
	        todoDAO.insert(todo);
	    } else {
	        todoDAO.update(todo);
	    }
	
	    // 캘린더에도 반영
	    if (calendarView != null) {
	        calendarView.refresh();
	    }
	
	    // 리스트 갱신
	    if (todo.getDate() != null) {
	        onDateSelected(todo.getDate());
	    } else if (lastFilter != null) {
	        applyFilter(lastFilter);
	    }
	}

	public void deleteTodo(Todo todo) {
	    if (todo == null || todo.getId() == 0) return;

	    todoDAO.delete(todo.getId());

	    // 달력 갱신
	    if (calendarView != null) {
	        calendarView.refresh();
	    }

	    // 현재 뷰 갱신 (필터가 있으면 필터 유지, 없으면 날짜 기준)
	    if (lastFilter != null) {
	        applyFilter(lastFilter);
	    } else if (todo.getDate() != null) {
	        onDateSelected(todo.getDate());
	    } else {
	        // 예비: 전체 조회
	        FilterOptions f = new FilterOptions();
	        listView.showTodos(todoDAO.findByFilter(f));
	    }
	}

    public void updateTodoCompleted(Todo todo, boolean completed) {
        if (todo == null || todo.getId() == 0) return;
        todo.setCompleted(completed);
        todoDAO.update(todo);
        // 화면은 이미 체크박스를 바꿨으니 굳이 다시 로딩 안 해도 됨
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }
    

    public void onThemeChanged() {
        // 달력 다시 그리기 (년도/월 텍스트, 버튼 등 색상 재적용)
        if (calendarView != null) {
            calendarView.refresh();
        }
        // 리스트뷰도 다크/라이트 색 다시 입히기
        if (listView != null) {
            listView.applyTheme();
        }
    }

}
