import { Injectable, inject, signal } from '@angular/core';
import { TaskService } from './task.service';
import type { TaskDto, TaskStatus } from './task.model';

/**
 * Signal-based store for the task tree. Provided at the container component so
 * the container and every (recursive) node share the same instance and the
 * same reactive state.
 */
@Injectable()
export class TaskStore {
  private readonly taskService = inject(TaskService);

  readonly tasks = signal<TaskDto[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  /** Loads (or reloads) the whole forest. */
  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks.set(tasks);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load tasks.');
        this.loading.set(false);
      },
    });
  }

  /** Persists a task's new status, then refreshes the tree on success. */
  changeStatus(id: number, status: TaskStatus): void {
    this.taskService.updateStatus(id, status).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Failed to update the task.'),
    });
  }
}
