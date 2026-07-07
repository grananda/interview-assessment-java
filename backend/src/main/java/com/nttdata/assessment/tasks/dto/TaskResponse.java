package com.nttdata.assessment.tasks.dto;

import java.time.LocalDate;
import java.util.List;

import com.nttdata.assessment.tasks.domain.TaskPriority;
import com.nttdata.assessment.tasks.domain.TaskStatus;

/**
 * API response shape for a task. Immutable record, serialized by Jackson.
 * {@code subtasks} nests the recursive children (empty for a leaf task).
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        String projectName,
        LocalDate createdAt,
        LocalDate dueDate,
        List<TaskResponse> subtasks) {
}
