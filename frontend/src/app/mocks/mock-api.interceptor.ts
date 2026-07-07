import {
  HttpErrorResponse,
  HttpInterceptorFn,
  HttpResponse,
} from '@angular/common/http';
import { delay, of, throwError } from 'rxjs';
import type { TaskDto, TaskStatus } from '../tasks/task.model';

/**
 * Mocks the tasks API entirely in the browser so the frontend works without a
 * backend (e.g. on StackBlitz, where the Java server cannot run).
 *
 * It is a functional interceptor: it short-circuits the matching requests and
 * lets everything else through via `next(req)`. The service and components are
 * untouched — they keep calling `/api/tasks` as if the backend were there.
 *
 * State is kept in a module-level tree so a status change (PUT) persists and is
 * reflected by the subsequent list reload (GET). It resets on a full reload.
 */

const LATENCY_MS = 250;
const STATUS_URL = /^\/api\/tasks\/(\d+)\/status$/;

// In-memory forest, mirroring the backend's DataSeeder.
const forest: TaskDto[] = seedForest();

export const mockApiInterceptor: HttpInterceptorFn = (req, next) => {
  // GET /api/tasks — return the whole forest.
  if (req.method === 'GET' && req.url === '/api/tasks') {
    return of(new HttpResponse({ status: 200, body: structuredClone(forest) })).pipe(
      delay(LATENCY_MS),
    );
  }

  // PUT /api/tasks/{id}/status — update the status and echo the task.
  const statusMatch = req.method === 'PUT' && STATUS_URL.exec(req.url);
  if (statusMatch) {
    const id = Number(statusMatch[1]);
    const status = (req.body as { status: TaskStatus }).status;
    const task = findById(forest, id);

    if (!task) {
      return throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
            url: req.url,
            error: { title: 'Task not found', detail: `Task ${id} not found` },
          }),
      ).pipe(delay(LATENCY_MS));
    }

    task.status = status;
    return of(new HttpResponse({ status: 200, body: structuredClone(task) })).pipe(
      delay(LATENCY_MS),
    );
  }

  return next(req);
};

function findById(nodes: TaskDto[], id: number): TaskDto | null {
  for (const node of nodes) {
    if (node.id === id) {
      return node;
    }
    const found = findById(node.subtasks, id);
    if (found) {
      return found;
    }
  }
  return null;
}

function seedForest(): TaskDto[] {
  const task = (
    id: number,
    title: string,
    description: string,
    status: TaskStatus,
    priority: TaskDto['priority'],
    projectName: string,
    createdAt: string,
    dueDate: string | null,
    subtasks: TaskDto[] = [],
  ): TaskDto => ({
    id,
    title,
    description,
    status,
    priority,
    projectName,
    createdAt,
    dueDate,
    subtasks,
  });

  return [
    task(
      1,
      'Build the platform',
      'Epic: platform foundations',
      'in_progress',
      'high',
      'Platform',
      '2026-01-05',
      '2026-04-30',
      [
        task(
          2,
          'Set up the monorepo',
          'Bootstrap the build tooling',
          'done',
          'high',
          'Platform',
          '2026-01-10',
          '2026-01-20',
          [
            task(
              3,
              'Configure Turborepo',
              'Pipelines and remote caching',
              'done',
              'medium',
              'Platform',
              '2026-01-11',
              null,
            ),
          ],
        ),
        task(
          4,
          'Wire the tasks endpoint',
          'Return the task list from the service',
          'in_progress',
          'medium',
          'Platform',
          '2026-02-01',
          '2026-02-15',
        ),
      ],
    ),
    task(
      5,
      'Ship the mobile app',
      'Epic: mobile MVP',
      'todo',
      'high',
      'Mobile app',
      '2026-03-01',
      '2026-06-30',
      [
        task(
          6,
          'Design the task list screen',
          'Angular standalone component',
          'todo',
          'high',
          'Mobile app',
          '2026-03-20',
          null,
          [
            task(
              7,
              'Add pull-to-refresh',
              'Refresh the list on gesture',
              'todo',
              'low',
              'Mobile app',
              '2026-04-02',
              '2026-04-20',
            ),
          ],
        ),
      ],
    ),
  ];
}
