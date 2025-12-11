package dao;

import model.FilterOptions;
import model.Todo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {

    private final Connection conn;

    public TodoDAO() {
        this.conn = DBConnector.getConnection();
        ensureTodosTableExists();
    }

    private void ensureTodosTableExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS todos (
                  id INT PRIMARY KEY AUTO_INCREMENT,
                  title VARCHAR(255) NOT NULL,
                  description TEXT,
                  date DATE NOT NULL,
                  priority TINYINT NOT NULL,
                  completed TINYINT(1) NOT NULL DEFAULT 0
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("todos 테이블 생성/확인 실패", e);
        }
    }

    private Todo mapRow(ResultSet rs) throws SQLException {
        Todo t = new Todo();
        t.setId(rs.getInt("id"));
        Date d = rs.getDate("date");
        t.setDate(d != null ? d.toLocalDate() : null);
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setPriority(rs.getInt("priority"));
        t.setCompleted(rs.getBoolean("completed"));
        return t;
    }

    public void insert(Todo todo) {
        String sql = "INSERT INTO todos (title, description, date, priority, completed) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, todo.getTitle());
            ps.setString(2, todo.getDescription());
            ps.setDate(3, Date.valueOf(todo.getDate()));
            ps.setInt(4, todo.getPriority());
            ps.setBoolean(5, todo.isCompleted());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    todo.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("insert 실패", e);
        }
    }

    public void update(Todo todo) {
        String sql = "UPDATE todos SET title = ?, description = ?, date = ?, priority = ?, completed = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, todo.getTitle());
            ps.setString(2, todo.getDescription());
            ps.setDate(3, Date.valueOf(todo.getDate()));
            ps.setInt(4, todo.getPriority());
            ps.setBoolean(5, todo.isCompleted());
            ps.setInt(6, todo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update 실패", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM todos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete 실패", e);
        }
    }

    public List<Todo> findByDate(LocalDate date) {
        String sql = "SELECT id, title, description, date, priority, completed FROM todos WHERE date = ? ORDER BY priority, id";
        List<Todo> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByDate 실패", e);
        }
        return list;
    }

    public List<Todo> findByFilter(FilterOptions filter) {
        StringBuilder sb = new StringBuilder(
                "SELECT id, title, description, date, priority, completed FROM todos WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
            sb.append(" AND title LIKE ?");
            params.add("%" + filter.getKeyword().trim() + "%");
        }
        if (filter.getCompleted() != null) {
            sb.append(" AND completed = ?");
            params.add(filter.getCompleted());
        }
        if (filter.getStartDate() != null) {
            sb.append(" AND date >= ?");
            params.add(filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            sb.append(" AND date <= ?");
            params.add(filter.getEndDate());
        }
        if (filter.getPriority() != null) {
            sb.append(" AND priority = ?");
            params.add(filter.getPriority());
        }

        // 🔽 중요도 → 날짜 → id 순
        sb.append(" ORDER BY priority, date, id");

        List<Todo> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                int idx = i + 1;

                if (p instanceof LocalDate ld) {
                    ps.setDate(idx, Date.valueOf(ld));
                } else if (p instanceof Boolean b) {
                    ps.setBoolean(idx, b);
                } else {
                    ps.setObject(idx, p);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByFilter 실패", e);
        }
        return result;
    }

    
    public boolean existsByDate(LocalDate date) {
        String sql = "SELECT 1 FROM todos WHERE date = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsByDate 실패", e);
        }
    }
    
    

}
