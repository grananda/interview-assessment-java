import { Component, OnInit, inject } from '@angular/core';
import { TaskStore } from '../task-store';
import { TaskNode } from '../task-node/task-node';

/** Loads the task forest and renders one recursive TaskNode per root. */
@Component({
  selector: 'app-task-tree',
  imports: [TaskNode],
  providers: [TaskStore],
  templateUrl: './task-tree.html',
  styleUrl: './task-tree.scss',
})
export class TaskTree implements OnInit {
  protected readonly store = inject(TaskStore);

  ngOnInit(): void {
    this.store.load();
  }
}
