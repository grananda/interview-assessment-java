import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

/** Shape returned by GET /api/health. */
export interface HealthResponse {
  status: string;
  service: string;
  timestamp: string;
}

/** Thin client for the backend health endpoint. */
@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly http = inject(HttpClient);

  getHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>('/api/health');
  }
}
