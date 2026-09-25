package com.taskflow.taskflowapi.repository;

import com.taskflow.taskflowapi.model.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByBoardId(Long boardId);
}
