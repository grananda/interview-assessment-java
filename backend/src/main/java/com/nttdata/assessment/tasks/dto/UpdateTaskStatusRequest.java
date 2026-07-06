package com.nttdata.assessment.tasks.dto;

import com.nttdata.assessment.tasks.domain.TaskStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for PUT /api/tasks/{id}/status.
 */
public record UpdateTaskStatusRequest(
        @NotNull(message = "status is required") TaskStatus status) {
}
