package com.nttdata.assessment.tasks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nttdata.assessment.tasks.domain.Task;
import com.nttdata.assessment.tasks.domain.TaskStatus;
import com.nttdata.assessment.tasks.dto.TaskResponse;
import com.nttdata.assessment.tasks.dto.TaskStatsResponse;
import com.nttdata.assessment.tasks.error.TaskNotFoundException;
import com.nttdata.assessment.tasks.mapper.TaskMapper;
import com.nttdata.assessment.tasks.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Returns the task list, optionally filtered by status.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> findAll(Optional<TaskStatus> status) {
        List<Task> tasks = status
                .map(taskRepository::findByStatus)
                .orElseGet(taskRepository::findAll);

        return taskMapper.toResponses(tasks);
    }

    /**
     * Returns a single task or raises a domain error when it does not exist.
     */
    @Transactional(readOnly = true)
    public TaskResponse findById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toResponse(task);
    }

    /**
     * 🎯 CANDIDATE TASK 1 — update a task's status.
     *
     * <p>Implement it so it:
     * <ul>
     *   <li>loads the task by id (see {@link #findById(long)} for the pattern),</li>
     *   <li>raises {@link TaskNotFoundException} when it does not exist,</li>
     *   <li>updates the status, persists it and returns the mapped {@link TaskResponse}.</li>
     * </ul>
     * The repository, entity setter, DTO, mapper and exception already exist.
     */
    @Transactional
    public TaskResponse updateStatus(long id, TaskStatus status) {
        throw new UnsupportedOperationException("TODO (candidate): implement updateStatus");
    }

    /**
     * 🎯 CANDIDATE TASK 2 — aggregate task statistics using the Stream API.
     *
     * <p>Implement it so it returns a {@link TaskStatsResponse} with:
     * <ul>
     *   <li>{@code total}: the number of tasks,</li>
     *   <li>{@code byStatus}: count per status, keyed by the enum's API value
     *       (e.g. {@code "in_progress"}),</li>
     *   <li>{@code byPriority}: count per priority, keyed by its API value.</li>
     * </ul>
     * Hint: {@code taskRepository.findAll()} + {@code Collectors.groupingBy(..., counting())}.
     */
    @Transactional(readOnly = true)
    public TaskStatsResponse getStats() {
        throw new UnsupportedOperationException("TODO (candidate): implement getStats with the Stream API");
    }
}
