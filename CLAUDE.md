# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PlanIt is a Java Swing-based todo/task management application with calendar integration and MySQL database persistence. The application features a split-view interface with a calendar on the left and a todo list on the right, supporting both light and dark themes.

## Build and Run

This is an Eclipse Java project configured for Java 21.

**Run the application:**
```bash
# From Eclipse: Run Main.java as Java Application
# Or compile and run from command line:
javac -cp "lib/mysql-connector-j-9.5.0.jar" -d bin src/**/*.java
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" main.Main
```

**Database Setup:**
- The application requires a MySQL database connection
- Database credentials are in `src/dao/DBConnector.java:15-17`
- The `todos` table is auto-created on first run by `TodoDAO.ensureTodosTableExists()`
- Default database URL: `jdbc:mysql://nsyun.synology.me:3306/db`

## Architecture

**MVC Pattern:**
- **Model** (`src/model/`): `Todo` entity and `FilterOptions` for search queries
- **View** (`src/view/`): Swing UI components (dialogs, panels, frames)
- **Controller** (`src/controller/`): `MainController` coordinates between views and DAO
- **DAO** (`src/dao/`): `TodoDAO` handles database operations, `DBConnector` manages MySQL connection

**Key Flow:**
1. `Main.java` launches `MainFrame`
2. `MainFrame` creates `TodoDAO`, `MainController`, and wires up `CalendarViewPanel` + `TodoListViewPanel`
3. User interactions (date clicks, filters, CRUD operations) flow through `MainController`
4. Controller calls `TodoDAO` methods and updates both calendar and list views
5. Views always refresh through controller to maintain state consistency

**State Management:**
- `MainController` tracks `currentDate` (last selected date) and `lastFilter` (last search/filter)
- When saving/deleting todos, the controller automatically refreshes the appropriate view based on context
- Calendar and list views are kept in sync through controller callbacks

## UI Components

**Split View Structure:**
- Left: `CalendarViewPanel` - monthly calendar with todo indicators
- Right: `TodoListViewPanel` - filtered/dated todo list
- Top: `SearchPanel` - search and filter controls

**Theme System:**
- All UI colors centralized in `UIStyle.java`
- Toggle dark mode via `UIStyle.toggleDarkMode()`
- After theme change, call `MainController.onThemeChanged()` to refresh all views
- Use `UIStyle.styleTextField()`, `styleComboBox()`, `styleTextArea()`, etc. for consistent styling

**Dialog Components:**
- `TodoFormDialog` - create/edit todos
- `AdvancedSearchDialog` - complex filtering UI
- `ConfirmDialog` - deletion confirmations

## Data Model

**Todo Entity:**
- `id` - primary key (auto-generated)
- `title` - task title (VARCHAR 255, required)
- `description` - detailed description (TEXT, optional)
- `date` - due date (DATE, required)
- `priority` - 1 (high), 2 (medium), 3 (low)
- `completed` - boolean status

**Database Queries:**
- Results always ordered by: `priority` → `date` → `id`
- `TodoDAO.findByDate()` - todos for specific date
- `TodoDAO.findByFilter()` - dynamic query based on `FilterOptions`
- `TodoDAO.existsByDate()` - quick check for calendar indicators

## Controller Patterns

**Creating/Updating Todos:**
```java
controller.saveTodo(todo);  // Auto-detects insert vs update based on id==0
```

**Deleting Todos:**
```java
controller.deleteTodo(todo);  // Refreshes calendar and current view
```

**View Selection:**
```java
controller.onDateSelected(date);  // Shows todos for specific date
controller.applyFilter(filter);   // Shows filtered results
```

**Checking Todo Existence:**
```java
boolean hasTodos = controller.hasTodoOn(date);  // For calendar indicators
```

## Dependencies

- **MySQL Connector/J 9.5.0** (`lib/mysql-connector-j-9.5.0.jar`) - JDBC driver
- **Java 21** - uses text blocks, pattern matching in switch, record patterns
- **Swing** - GUI framework (no external UI libraries)

## Utilities

- `DateUtils.java` - Date formatting and manipulation helpers
- `Validator.java` - Input validation for forms
- `UIStyle.java` - Centralized theme/styling management
