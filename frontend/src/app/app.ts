import { Component, OnInit, inject, signal } from '@angular/core';
import { HealthService } from './health.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  private readonly healthService = inject(HealthService);

  protected readonly status = signal<'checking' | 'connected' | 'error'>(
    'checking',
  );
  protected readonly detail = signal<string>('');

  ngOnInit(): void {
    this.healthService.getHealth().subscribe({
      next: (health) => {
        this.status.set('connected');
        this.detail.set(`${health.service} · ${health.status}`);
      },
      error: () => {
        this.status.set('error');
        this.detail.set('Could not reach the backend at /api/health');
      },
    });
  }
}
