package com.nttdata.assessment.tasks.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.assessment.tasks.domain.TaskStatus;
import com.nttdata.assessment.tasks.dto.TaskResponse;
import com.nttdata.assessment.tasks.dto.TaskStatsResponse;
import com.nttdata.assessment.tasks.dto.UpdateTaskStatusRequest;
import com.nttdata.assessment.tasks.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET /api/tasks?status=... — the (optionally filtered) task list.
     */
    @GetMapping
    public List<TaskResponse> findAll(@RequestParam Optional<TaskStatus> status) {
        return taskService.findAll(status);
    }

    /**
     * GET /api/tasks/stats — aggregated statistics.
     */
    @GetMapping("/stats")
    public TaskStatsResponse stats() {
        return taskService.getStats();
    }

    /**
     * GET /api/tasks/{id} — a single task or 404 (ProblemDetail) when missing.
     */
    @GetMapping("/{id}")
    public TaskResponse findOne(@PathVariable long id) {
        return taskService.findById(id);
    }

    /**
     * PUT /api/tasks/{id}/status — updates a task's status or 404 when missing.
     */
    @PutMapping("/{id}/status")
    public TaskResponse updateStatus(@PathVariable long id,
                                     @Valid @RequestBody UpdateTaskStatusRequest body) {
        return taskService.updateStatus(id, body.status());
    }
}
