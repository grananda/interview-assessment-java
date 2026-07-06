package com.nttdata.assessment.tasks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nttdata.assessment.tasks.domain.Task;
import com.nttdata.assessment.tasks.domain.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * The {@code @EntityGraph} eagerly fetches the project in a single query,
     * avoiding the N+1 problem when mapping the list to DTOs.
     */
    @Override
    @EntityGraph(attributePaths = "project")
    List<Task> findAll();

    @EntityGraph(attributePaths = "project")
    List<Task> findByStatus(TaskStatus status);

    @Override
    @EntityGraph(attributePaths = "project")
    Optional<Task> findById(Long id);
}
