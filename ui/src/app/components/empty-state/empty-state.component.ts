import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-col items-center justify-center py-20 px-4 text-center">
      <div class="w-24 h-24 bg-gray-50 rounded-full flex items-center justify-center mb-6">
        <svg class="w-12 h-12 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M21 21l-4.35-4.35M17 11A6 6 0 1 1 5 11a6 6 0 0 1 12 0z" />
        </svg>
      </div>
      <h3 class="text-xl font-bold text-gray-800 mb-2">{{ title }}</h3>
      <p class="text-gray-500 max-w-xs mb-8">{{ message }}</p>
      <button *ngIf="showReset" (click)="reset.emit()"
        class="px-6 py-2.5 bg-white border border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 transition-colors shadow-sm">
        Clear all filters
      </button>
    </div>
  `
})
export class EmptyStateComponent {
  @Input() title = 'No results found';
  @Input() message = 'Try adjusting your search or filters to find what you\'re looking for.';
  @Input() showReset = true;
  @Output() reset = new EventEmitter<void>();
}
