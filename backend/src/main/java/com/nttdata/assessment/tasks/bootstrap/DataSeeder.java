package com.nttdata.assessment.tasks.bootstrap;

import java.time.Instant;
import java.time.LocalDate;

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
 * Seeds a small nested demo tree on startup (the in-memory DB is empty on
 * every boot). Two root tasks, each with subtasks up to three levels deep.
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

        // Root 1: Platform
        Task buildPlatform = save(new Task("Build the platform", "Epic: platform foundations",
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH,
                Instant.parse("2026-01-05T09:00:00Z"), LocalDate.parse("2026-04-30"), platform), null);

        Task setupMonorepo = save(new Task("Set up the monorepo", "Bootstrap the build tooling",
                TaskStatus.DONE, TaskPriority.HIGH,
                Instant.parse("2026-01-10T09:00:00Z"), LocalDate.parse("2026-01-20"), platform), buildPlatform);

        save(new Task("Configure Turborepo", "Pipelines and remote caching",
                TaskStatus.DONE, TaskPriority.MEDIUM,
                Instant.parse("2026-01-11T09:00:00Z"), null, platform), setupMonorepo);

        save(new Task("Wire the tasks endpoint", "Return the task list from the service",
                TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM,
                Instant.parse("2026-02-01T12:30:00Z"), LocalDate.parse("2026-02-15"), platform), buildPlatform);

        // Root 2: Mobile app
        Task shipMobile = save(new Task("Ship the mobile app", "Epic: mobile MVP",
                TaskStatus.TODO, TaskPriority.HIGH,
                Instant.parse("2026-03-01T09:00:00Z"), LocalDate.parse("2026-06-30"), mobile), null);

        Task designScreen = save(new Task("Design the task list screen", "Angular standalone component",
                TaskStatus.TODO, TaskPriority.HIGH,
                Instant.parse("2026-03-20T10:00:00Z"), null, mobile), shipMobile);

        save(new Task("Add pull-to-refresh", "Refresh the list on gesture",
                TaskStatus.TODO, TaskPriority.LOW,
                Instant.parse("2026-04-02T14:45:00Z"), LocalDate.parse("2026-04-20"), mobile), designScreen);
    }

    private Task save(Task task, Task parent) {
        task.setParent(parent);
        return taskRepository.save(task);
    }
}
