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
 * 🎯 CANDIDATE TASK — completion of a subtree.
 *
 * Return the percentage of tasks (the node itself + every descendant) whose
 * status is 'done', as a whole number 0..100. The `flatten` helper above gives
 * you the node plus all its descendants; count how many are 'done' and round.
 *
 * Currently returns 0 as a placeholder so the UI renders while unimplemented.
 */
export function completionPercent(task: TaskDto): number {
  // TODO (candidate): compute the real percentage using `flatten(task)`.
  return 0;
}
