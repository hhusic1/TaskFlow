package com.taskflow.taskflowapi.controller;


import com.taskflow.taskflowapi.dto.CreateProjectDto;
import com.taskflow.taskflowapi.dto.ProjectDto;
import com.taskflow.taskflowapi.model.Project;
import com.taskflow.taskflowapi.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {


    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }


    //GET /api/projects
    @GetMapping
    public List<ProjectDto> getAll() {
        return projectRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // GET /api/projects/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(project -> ResponseEntity.ok(toDto(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    //POST /api/projects
    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestBody CreateProjectDto dto){
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());

        Project saved = projectRepository.save(project);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(toDto(saved));
    }

    // PUT /api/projects/{id}
    @PutMapping("projects/{id}")
    public ResponseEntity<ProjectDto> udpate(@PathVariable Long id, @RequestBody CreateProjectDto dto){
        return projectRepository.findById(id)
                .map( project -> {
                    project.setName(dto.getName());
                    project.setDescription(dto.getDescription());
                    Project updated = projectRepository.save(project);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/projects/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ProjectDto> delete(@PathVariable Long id) {
        if(!projectRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        projectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Pomoćna metoda za mapiranje Entity -> DTO
    private ProjectDto toDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        return dto;
    }

}
