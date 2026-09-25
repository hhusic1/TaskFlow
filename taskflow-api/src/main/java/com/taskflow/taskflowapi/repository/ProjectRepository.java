package com.taskflow.taskflowapi.repository;

import com.taskflow.taskflowapi.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
