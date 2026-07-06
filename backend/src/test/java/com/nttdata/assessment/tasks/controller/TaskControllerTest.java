package com.nttdata.assessment.tasks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nttdata.assessment.tasks.domain.TaskPriority;
import com.nttdata.assessment.tasks.domain.TaskStatus;
import com.nttdata.assessment.tasks.dto.TaskResponse;
import com.nttdata.assessment.tasks.error.TaskNotFoundException;
import com.nttdata.assessment.tasks.service.TaskService;
import com.nttdata.assessment.tasks.web.StringToTaskStatusConverter;

@WebMvcTest(TaskController.class)
@Import(StringToTaskStatusConverter.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void returnsTheTaskListAsJson() throws Exception {
        when(taskService.findAll(any())).thenReturn(List.of(new TaskResponse(
                1L, "Wire the tasks endpoint", "Return the list",
                TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, "Platform",
                LocalDate.parse("2026-02-01"), LocalDate.parse("2026-02-15"))));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("in_progress"))
                .andExpect(jsonPath("$[0].projectName").value("Platform"));
    }

    @Test
    void returnsProblemDetail404WhenTaskMissing() throws Exception {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Task 99 not found"));
    }
}
