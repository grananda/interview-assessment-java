package com.nttdata.assessment.tasks.dto;

import java.time.LocalDate;

import com.nttdata.assessment.tasks.domain.TaskPriority;
import com.nttdata.assessment.tasks.domain.TaskStatus;

/**
 * API response shape for a task. Immutable record, serialized by Jackson.
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        String projectName,
        LocalDate createdAt,
        LocalDate dueDate) {
}
