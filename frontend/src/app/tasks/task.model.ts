/** API contract shared with the Spring Boot backend (values are snake_case). */
export type TaskStatus = 'todo' | 'in_progress' | 'done';
export type TaskPriority = 'low' | 'medium' | 'high';

/** A task node. `subtasks` nests the recursive children (empty for a leaf). */
export interface TaskDto {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  projectName: string | null;
  createdAt: string; // 'YYYY-MM-DD'
  dueDate: string | null;
  subtasks: TaskDto[];
}

/** All statuses, in lifecycle order (for the status <select>). */
export const TASK_STATUSES: TaskStatus[] = ['todo', 'in_progress', 'done'];

/** Flattens a task and all its descendants into a single list. */
export function flatten(task: TaskDto): TaskDto[] {
  return [task, ...task.subtasks.flatMap(flatten)];
}

/**
 * Completion of a subtree: percentage of tasks (the node itself + every
 * descendant) whose status is 'done'. Returns a whole number 0..100.
 */
export function completionPercent(task: TaskDto): number {
  const all = flatten(task);
  const done = all.filter((t) => t.status === 'done').length;
  return Math.round((done / all.length) * 100);
}
