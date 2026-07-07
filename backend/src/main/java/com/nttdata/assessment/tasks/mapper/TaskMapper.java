package com.nttdata.assessment.tasks.mapper;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Component;

import com.nttdata.assessment.tasks.domain.Task;
import com.nttdata.assessment.tasks.dto.TaskResponse;

/**
 * Maps domain {@link Task} entities to API {@link TaskResponse} DTOs.
 */
@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getProject() != null ? task.getProject().getName() : null,
                LocalDate.ofInstant(task.getCreatedAt(), ZoneOffset.UTC),
                task.getDueDate());
    }

    /**
     * Maps a list preserving order. Uses the Stream API.
     */
    public List<TaskResponse> toResponses(List<Task> tasks) {
        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }
}
