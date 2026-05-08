import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-skeleton-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col gap-3">
      <div class="skeleton rounded-xl aspect-square w-full"></div>
      <div class="skeleton h-4 w-3/4 rounded-md"></div>
      <div class="skeleton h-3 w-1/2 rounded-md"></div>
      <div class="skeleton h-3 w-1/3 rounded-md"></div>
      <div class="skeleton h-4 w-2/5 rounded-md"></div>
    </div>
  `,
})
export class SkeletonCardComponent {}
