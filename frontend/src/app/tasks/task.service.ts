import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import type { TaskDto, TaskStatus } from './task.model';

/** Tasks API client. */
@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/tasks';

  /** GET /api/tasks — the task forest (roots with nested subtasks). */
  getTasks(): Observable<TaskDto[]> {
    return this.http.get<TaskDto[]>(this.baseUrl);
  }

  /** PUT /api/tasks/{id}/status — updates a task's status. */
  updateStatus(id: number, status: TaskStatus): Observable<TaskDto> {
    return this.http.put<TaskDto>(`${this.baseUrl}/${id}/status`, { status });
  }
}
