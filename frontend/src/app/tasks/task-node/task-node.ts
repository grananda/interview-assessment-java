import {Component, computed, inject, input, signal} from '@angular/core';
import {
  completionPercent,
  TASK_STATUSES,
  type TaskDto,
  type TaskStatus,
} from '../task.model';
import {TaskStore} from '../task-store';

/**
 * Renders one task and, recursively, its subtasks. A component is always in its
 * own template scope, so it can reference its own selector (`app-task-node`)
 * without importing itself — that is what draws the tree of arbitrary depth.
 */
@Component({
  selector: 'app-task-node',
  templateUrl: './task-node.html',
  styleUrl: './task-node.scss',
})
export class TaskNode {
  private readonly store = inject(TaskStore);

  readonly task = input.required<TaskDto>();

  protected readonly expanded = signal(true);
  protected readonly statuses = TASK_STATUSES;

  /** % of this subtree (self + descendants) that is done. */
  protected readonly completion = computed(() => completionPercent(this.task()));

  protected toggle(): void {
    this.expanded.update((value) => !value);
  }

  protected onSelect(status: TaskStatus): void {
    if (status !== this.task().status) {
      this.store.changeStatus(this.task().id, status);
    }
  }
}
