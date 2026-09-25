package com.taskflow.taskflowapi.repository;

import com.taskflow.taskflowapi.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByProjectId(Long projectId);
    //derived query Spring odma generise
    //SELECT * FROM boards WHERE project_id = ?
}
