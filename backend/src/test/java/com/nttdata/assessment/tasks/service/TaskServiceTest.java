package com.nttdata.assessment.tasks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nttdata.assessment.tasks.domain.Task;
import com.nttdata.assessment.tasks.domain.TaskPriority;
import com.nttdata.assessment.tasks.domain.TaskStatus;
import com.nttdata.assessment.tasks.dto.TaskResponse;
import com.nttdata.assessment.tasks.dto.TaskStatsResponse;
import com.nttdata.assessment.tasks.error.TaskNotFoundException;
import com.nttdata.assessment.tasks.mapper.TaskMapper;
import com.nttdata.assessment.tasks.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(taskRepository, new TaskMapper());
    }

    private Task sampleTask(TaskStatus status, TaskPriority priority) {
        return new Task("Wire the tasks endpoint", "Return the task list",
                status, priority, Instant.parse("2026-02-01T12:30:00Z"),
                LocalDate.parse("2026-02-15"), null);
    }

    @Test
    void findByIdRaisesDomainErrorWhenMissing() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    // 🎯 CANDIDATE TASK 1 — these fail until updateStatus is implemented.
    @Nested
    class UpdateStatus {

        @Test
        void updatesStatusAndReturnsMappedDto() {
            Task task = sampleTask(TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM);
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            TaskResponse result = service.updateStatus(1L, TaskStatus.DONE);

            assertThat(result.status()).isEqualTo(TaskStatus.DONE);
        }

        @Test
        void raisesDomainErrorWhenTaskMissing() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateStatus(99L, TaskStatus.DONE))
                    .isInstanceOf(TaskNotFoundException.class);
        }
    }

    // 🎯 CANDIDATE TASK 2 — these fail until getStats is implemented.
    @Nested
    class GetStats {

        @Test
        void aggregatesCountsByStatusAndPriority() {
            when(taskRepository.findAll()).thenReturn(List.of(
                    sampleTask(TaskStatus.DONE, TaskPriority.HIGH),
                    sampleTask(TaskStatus.DONE, TaskPriority.LOW),
                    sampleTask(TaskStatus.TODO, TaskPriority.HIGH)));

            TaskStatsResponse stats = service.getStats();

            assertThat(stats.total()).isEqualTo(3);
            assertThat(stats.byStatus())
                    .containsEntry("done", 2L)
                    .containsEntry("todo", 1L);
            assertThat(stats.byPriority())
                    .containsEntry("high", 2L)
                    .containsEntry("low", 1L);
        }
    }
}
