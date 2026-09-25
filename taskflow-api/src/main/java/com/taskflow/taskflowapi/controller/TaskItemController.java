package com.taskflow.taskflowapi.controller;

import com.taskflow.taskflowapi.dto.CreateTaskItemDto;
import com.taskflow.taskflowapi.dto.TaskItemDto;
import com.taskflow.taskflowapi.dto.UpdateTaskItemDto;
import com.taskflow.taskflowapi.model.Board;
import com.taskflow.taskflowapi.model.TaskItem;
import com.taskflow.taskflowapi.repository.BoardRepository;
import com.taskflow.taskflowapi.repository.TaskItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TaskItemController {

    private final BoardRepository boardRepository;
    private final TaskItemRepository taskItemRepository;

    public TaskItemController(BoardRepository boardRepository, TaskItemRepository taskItemRepository){
        this.boardRepository = boardRepository;
        this.taskItemRepository = taskItemRepository;
    }

    private TaskItemDto toDto(TaskItem taskItem){
        TaskItemDto taskItemDto = new TaskItemDto();
        taskItemDto.setDescription(taskItem.getDescription());
        taskItemDto.setCreatedAt(taskItem.getCreatedAt());
        taskItemDto.setDueDate(taskItem.getDueDate());
        taskItemDto.setBoardId(taskItem.getBoard().getId());
        taskItemDto.setStatus(taskItem.getStatus());

        return taskItemDto;
    }

    //GET /api/boards/{boardId}/tasks
    @GetMapping("/boards/{boardId}/tasks")
    public List<TaskItemDto> getTasksForBoard(@PathVariable Long boardId){
        return taskItemRepository.findByBoardId(boardId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    //GET /api/tasks/{id}
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskItemDto> getById(@PathVariable Long id){
        return taskItemRepository.findById(id)
                .map(task -> ResponseEntity.ok(toDto(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    //POST /api/tasks
    @PostMapping("/tasks")
    public ResponseEntity<TaskItemDto> create(@RequestBody CreateTaskItemDto dto){
        return boardRepository.findById(dto.getBoardId())
                .map(board ->{
                    TaskItem taskItem = new TaskItem();
                    taskItem.setTitle(dto.getTitle());
                    taskItem.setDescription(dto.getDescription());
                    taskItem.setDueDate(dto.getDueDate());
                    taskItem.setBoard(board);

                    TaskItem saved = taskItemRepository.save(taskItem);

                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(saved.getId())
                            .toUri();

                    return ResponseEntity.created(location).body(toDto(saved));
                })
                .orElse(ResponseEntity.badRequest().build());
    }


    // PUT /api/tasks/{id}
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskItemDto> update(@PathVariable Long id, @RequestBody UpdateTaskItemDto dto) {
        return taskItemRepository.findById(id)
                .map(task -> applyUpdate(task, dto))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<TaskItemDto> applyUpdate(TaskItem task, UpdateTaskItemDto dto) {
        task.setTitle(dto.getTitle());
        task.setDueDate(dto.getDueDate());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());

        boolean wantsBoardChange = dto.getBoardId() != null
                && !dto.getBoardId().equals(task.getBoard().getId());

        if (wantsBoardChange) {
            Optional<Board> validBoard = findValidBoardForMove(task, dto.getBoardId());
            if (validBoard.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            task.setBoard(validBoard.get());
        }

        TaskItem updated = taskItemRepository.save(task);
        return ResponseEntity.ok(toDto(updated));
    }

    private Optional<Board> findValidBoardForMove(TaskItem task, Long newBoardId) {
        return boardRepository.findById(newBoardId)
                .filter(board -> board.getProject().getId().equals(task.getBoard().getProject().getId()));
    }
}
