package model;

import java.time.LocalDate;

public class FilterOptions {

    private String keyword;         // 제목 검색어
    private LocalDate startDate;    // 시작 날짜 (옵션)
    private LocalDate endDate;      // 끝 날짜 (옵션)
    private Boolean completed;      // null: 전체, true: 완료만, false: 미완료만

    private Integer priority;       // null: 전체, 1: 상, 2: 중, 3: 하

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
