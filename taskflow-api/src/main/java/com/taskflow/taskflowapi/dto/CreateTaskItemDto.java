package com.taskflow.taskflowapi.dto;

import com.taskflow.taskflowapi.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateTaskItemDto {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long boardId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
}
