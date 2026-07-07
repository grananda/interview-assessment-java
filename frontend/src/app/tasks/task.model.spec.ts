import { describe, expect, it } from 'vitest';
import { completionPercent, type TaskDto } from './task.model';

function node(
  id: number,
  status: TaskDto['status'],
  subtasks: TaskDto[] = [],
): TaskDto {
  return {
    id,
    title: `t${id}`,
    description: '',
    status,
    priority: 'low',
    projectName: null,
    createdAt: '2026-01-01',
    dueDate: null,
    subtasks,
  };
}

// 🎯 CANDIDATE TASK 2 — make these pass by implementing completionPercent.
describe('completionPercent', () => {
  it('is 100 when the whole subtree is done', () => {
    const tree = node(1, 'done', [node(2, 'done'), node(3, 'done')]);
    expect(completionPercent(tree)).toBe(100);
  });

  it('counts the node itself plus every descendant', () => {
    // 4 tasks total, 2 of them done -> 50%
    const tree = node(1, 'done', [
      node(2, 'todo', [node(3, 'done')]),
      node(4, 'todo'),
    ]);
    expect(completionPercent(tree)).toBe(50);
  });

  it('rounds to a whole number', () => {
    // 3 tasks, 1 done -> 33
    const tree = node(1, 'done', [node(2, 'todo'), node(3, 'todo')]);
    expect(completionPercent(tree)).toBe(33);
  });
});
