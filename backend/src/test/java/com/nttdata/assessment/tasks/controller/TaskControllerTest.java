package com.nttdata.assessment.tasks.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nttdata.assessment.tasks.domain.TaskPriority;
import com.nttdata.assessment.tasks.domain.TaskStatus;
import com.nttdata.assessment.tasks.dto.TaskResponse;
import com.nttdata.assessment.tasks.error.TaskNotFoundException;
import com.nttdata.assessment.tasks.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void returnsTheTaskForestWithNestedSubtasks() throws Exception {
        TaskResponse subtask = new TaskResponse(
                2L, "Set up the monorepo", "Bootstrap the build tooling",
                TaskStatus.DONE, TaskPriority.HIGH, "Platform",
                LocalDate.parse("2026-01-10"), LocalDate.parse("2026-01-20"), List.of());
        TaskResponse root = new TaskResponse(
                1L, "Build the platform", "Epic: platform foundations",
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, "Platform",
                LocalDate.parse("2026-01-05"), LocalDate.parse("2026-04-30"), List.of(subtask));
        when(taskService.findAll()).thenReturn(List.of(root));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("in_progress"))
                .andExpect(jsonPath("$[0].subtasks[0].id").value(2))
                .andExpect(jsonPath("$[0].subtasks[0].status").value("done"));
    }

    @Test
    void returnsProblemDetail404WhenTaskMissing() throws Exception {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Task 99 not found"));
    }
}
