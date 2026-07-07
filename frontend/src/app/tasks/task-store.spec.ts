import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';
import { TaskStore } from './task-store';
import { TaskService } from './task.service';
import type { TaskDto } from './task.model';

describe('TaskStore', () => {
  const task: TaskDto = {
    id: 1,
    title: 'Wire the tasks endpoint',
    description: '',
    status: 'todo',
    priority: 'low',
    projectName: null,
    createdAt: '2026-01-01',
    dueDate: null,
    subtasks: [],
  };

  function setup() {
    const getTasks = vi.fn().mockReturnValue(of([task]));
    const updateStatus = vi
      .fn()
      .mockReturnValue(of({ ...task, status: 'done' }));

    TestBed.configureTestingModule({
      providers: [
        TaskStore,
        { provide: TaskService, useValue: { getTasks, updateStatus } },
      ],
    });

    return { store: TestBed.inject(TaskStore), getTasks, updateStatus };
  }

  it('load() fetches the forest and clears loading', () => {
    const { store, getTasks } = setup();

    store.load();

    expect(getTasks).toHaveBeenCalledTimes(1);
    expect(store.tasks()).toEqual([task]);
    expect(store.loading()).toBe(false);
  });

  // 🎯 CANDIDATE TASK 1 — make this pass by implementing changeStatus.
  it('changeStatus() updates via the service and reloads the tree', () => {
    const { store, getTasks, updateStatus } = setup();

    store.load(); // initial load
    store.changeStatus(1, 'done'); // update + reload

    expect(updateStatus).toHaveBeenCalledWith(1, 'done');
    expect(getTasks).toHaveBeenCalledTimes(2); // initial load + reload
  });
});
