import { Component } from '@angular/core';
import { TaskTree } from './tasks/task-tree/task-tree';

@Component({
  selector: 'app-root',
  imports: [TaskTree],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
