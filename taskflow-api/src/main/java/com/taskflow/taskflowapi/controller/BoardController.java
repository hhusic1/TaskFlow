package com.taskflow.taskflowapi.controller;

import com.taskflow.taskflowapi.dto.BoardDto;
import com.taskflow.taskflowapi.dto.CreateBoardDto;
import com.taskflow.taskflowapi.dto.UpdateBoardDto;
import com.taskflow.taskflowapi.model.Board;
import com.taskflow.taskflowapi.repository.BoardRepository;
import com.taskflow.taskflowapi.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BoardController {


    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;

    public BoardController(BoardRepository boardRepository, ProjectRepository projectRepository){
        this.boardRepository = boardRepository;
        this.projectRepository = projectRepository;
    }

    //GET /api/boards
    @GetMapping("/boards")
    public List<BoardDto> getAll(){
        return boardRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    //GET /api/boards/"{id}"
    @GetMapping("/boards/{id}")
    public ResponseEntity<BoardDto> getById(@PathVariable Long id){
        return boardRepository.findById(id)
                .map(board -> ResponseEntity.ok(toDto(board)))
                .orElse(ResponseEntity.notFound().build());
    }
    //POST /api/boards
    @PostMapping("/boards")
    public ResponseEntity<BoardDto> create(@RequestBody CreateBoardDto boardDto){
        return projectRepository.findById(boardDto.getProjectId())
                .map(project -> {
                   Board board = new Board();
                   board.setName(boardDto.getName());
                   board.setProject(project);

                   Board saved = boardRepository.save(board);

                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("{id}")
                            .buildAndExpand(saved.getId())
                            .toUri();

                   return ResponseEntity.created(location).body(toDto(saved));
                })
                .orElse(ResponseEntity.badRequest().build());

    }
    //PUT /api/boards/{id}
    @PutMapping("boards/{id}")
    public ResponseEntity<BoardDto> update(@PathVariable Long id, @RequestBody UpdateBoardDto boardDto){
        return boardRepository.findById(id).map(board ->{
            board.setName(boardDto.getName());
            Board updated = boardRepository.save(board);
            return ResponseEntity.ok(toDto(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    //DELETE /api/boards/"{id}"
    @DeleteMapping("/boards/{id}")
    public ResponseEntity<BoardDto> delete(@PathVariable Long id){
        if(!boardRepository.existsById(id))
            return ResponseEntity.notFound().build();

        boardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private BoardDto toDto(Board board){
        BoardDto boardDto = new BoardDto();
        boardDto.setId(board.getId());
        boardDto.setName(board.getName());
        boardDto.setProjectId(board.getProject().getId());

        return boardDto;
    }
}
