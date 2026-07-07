package com.nttdata.assessment.tasks.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /** Returns the whole task forest: root tasks with their subtasks nested. */
    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskMapper.toForest(taskRepository.findAll());
    }

    /**
     * Returns a single task with its subtree, or raises a domain error when it
     * does not exist.
     */
    @Transactional(readOnly = true)
    public TaskResponse findById(long id) {
        Task root = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return taskMapper.toSubtree(root, taskRepository.findAll());
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setStatus(status);
        Task saved = taskRepository.save(task);
        return taskMapper.toResponse(saved);
    }

    /**
     * 🎯 CANDIDATE TASK 2 — aggregate task statistics using the Stream API.
     *
     * <p>Implement it so it returns a {@link TaskStatsResponse} with:
     * <ul>
     *   <li>{@code total}: the number of tasks (the whole tree counts),</li>
     *   <li>{@code byStatus}: count per status, keyed by the enum's API value
     *       (e.g. {@code "in_progress"}),</li>
     *   <li>{@code byPriority}: count per priority, keyed by its API value.</li>
     * </ul>
     * Hint: {@code taskRepository.findAll()} + {@code Collectors.groupingBy(..., counting())}.
     */
    @Transactional(readOnly = true)
    public TaskStatsResponse getStats() {
        List<Task> all = taskRepository.findAll();

        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(task -> task.getStatus().apiValue(), Collectors.counting()));
        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(task -> task.getPriority().apiValue(), Collectors.counting()));

        return new TaskStatsResponse(all.size(), byStatus, byPriority);
    }
}
