package com.nttdata.assessment.tasks.bootstrap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.nttdata.assessment.tasks.domain.Project;
import com.nttdata.assessment.tasks.domain.Task;
import com.nttdata.assessment.tasks.domain.TaskPriority;
import com.nttdata.assessment.tasks.domain.TaskStatus;
import com.nttdata.assessment.tasks.repository.ProjectRepository;
import com.nttdata.assessment.tasks.repository.TaskRepository;

/**
 * Seeds demo data on startup (the in-memory DB is empty on every boot).
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DataSeeder(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (taskRepository.count() > 0) {
            return;
        }

        Project platform = projectRepository.save(new Project("Platform"));
        Project mobile = projectRepository.save(new Project("Mobile app"));

        taskRepository.saveAll(List.of(
                new Task("Set up the monorepo", "Bootstrap the build tooling",
                        TaskStatus.DONE, TaskPriority.HIGH,
                        Instant.parse("2026-01-10T09:00:00Z"), LocalDate.parse("2026-01-20"), platform),
                new Task("Wire the tasks endpoint", "Return the task list from the service",
                        TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM,
                        Instant.parse("2026-02-01T12:30:00Z"), LocalDate.parse("2026-02-15"), platform),
                new Task("Cover the service with tests", "Mock the repository",
                        TaskStatus.TODO, TaskPriority.MEDIUM,
                        Instant.parse("2026-03-15T08:15:00Z"), LocalDate.parse("2026-03-30"), platform),
                new Task("Design the task list screen", "Angular standalone component",
                        TaskStatus.TODO, TaskPriority.HIGH,
                        Instant.parse("2026-03-20T10:00:00Z"), null, mobile),
                new Task("Add pull-to-refresh", "Refresh the list on gesture",
                        TaskStatus.TODO, TaskPriority.LOW,
                        Instant.parse("2026-04-02T14:45:00Z"), LocalDate.parse("2026-04-20"), mobile)));
    }
}
