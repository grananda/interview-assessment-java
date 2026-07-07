package com.nttdata.assessment.tasks.mapper;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nttdata.assessment.tasks.domain.Task;
import com.nttdata.assessment.tasks.dto.TaskResponse;

/**
 * Maps domain {@link Task} entities to API {@link TaskResponse} DTOs, nesting
 * subtasks recursively.
 *
 * <p>The tree is built <b>in memory from a flat list</b> (one query, no N+1):
 * the tasks are grouped by their parent id with the Stream API and then the
 * children are attached recursively. Because we map to DTOs that only point
 * downwards, there is no risk of the parent↔child Jackson infinite recursion.
 */
@Component
public class TaskMapper {

    /** Sentinel key for root tasks (real ids start at 1). */
    private static final long ROOT = 0L;

    /** Builds the full forest: root tasks with their subtasks nested. */
    public List<TaskResponse> toForest(List<Task> allTasks) {
        return buildChildren(ROOT, groupByParent(allTasks));
    }

    /** Builds a single task's subtree from the full task set. */
    public TaskResponse toSubtree(Task root, List<Task> allTasks) {
        return toResponse(root, groupByParent(allTasks));
    }

    /** Maps a single task on its own, without its subtree (empty subtasks). */
    public TaskResponse toResponse(Task task) {
        return map(task, List.of());
    }

    private Map<Long, List<Task>> groupByParent(List<Task> allTasks) {
        return allTasks.stream().collect(Collectors.groupingBy(this::parentKey));
    }

    /** Reading the parent's id off the lazy proxy does not hit the database. */
    private long parentKey(Task task) {
        return task.getParent() == null ? ROOT : task.getParent().getId();
    }

    private List<TaskResponse> buildChildren(long parentId, Map<Long, List<Task>> byParent) {
        return byParent.getOrDefault(parentId, List.of()).stream()
                .map(task -> toResponse(task, byParent))
                .toList();
    }

    private TaskResponse toResponse(Task task, Map<Long, List<Task>> byParent) {
        return map(task, buildChildren(task.getId(), byParent));
    }

    private TaskResponse map(Task task, List<TaskResponse> subtasks) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getProject() != null ? task.getProject().getName() : null,
                LocalDate.ofInstant(task.getCreatedAt(), ZoneOffset.UTC),
                task.getDueDate(),
                subtasks);
    }
}
