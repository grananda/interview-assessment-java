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

  /**
   * 🎯 CANDIDATE TASK — persist a task's new status, then refresh the tree.
   *
   * Implement it so it calls {@link TaskService.updateStatus} and, on success,
   * reloads the forest via {@link load}. On error, set `error`. Look at
   * {@link load} above for the subscribe pattern used in this codebase.
   */
  changeStatus(id: number, status: TaskStatus): void {
    // TODO (candidate): call taskService.updateStatus(...) and reload on success.
    throw new Error('Not implemented: changeStatus');
  }
}
