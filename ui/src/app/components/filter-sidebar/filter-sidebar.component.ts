import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface SearchFilters {
  propertyType?: string;
  roomType?: string;
  minPrice?: number;
  maxPrice?: number;
}

@Component({
  selector: 'app-filter-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm sticky top-24">
      <div class="flex items-center justify-between mb-6">
        <h3 class="font-bold text-lg text-gray-800">Filters</h3>
        <button (click)="resetFilters()" class="text-sm text-rose-500 font-semibold hover:underline">
          Reset
        </button>
      </div>

      <!-- Property Type -->
      <div class="mb-6">
        <label class="block text-sm font-bold text-gray-700 mb-3">Property Type</label>
        <select [(ngModel)]="filters.propertyType" (change)="applyFilters()"
          class="w-full bg-gray-50 border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-rose-500/20 focus:border-rose-500 transition-all">
          <option value="">All types</option>
          <option *ngFor="let type of propertyTypes" [value]="type">{{ type }}</option>
        </select>
      </div>

      <!-- Room Type -->
      <div class="mb-6">
        <label class="block text-sm font-bold text-gray-700 mb-3">Room Type</label>
        <div class="space-y-2">
          <label *ngFor="let type of roomTypes" class="flex items-center group cursor-pointer">
            <input type="radio" [(ngModel)]="filters.roomType" [value]="type" (change)="applyFilters()"
              class="w-4 h-4 text-rose-500 border-gray-300 focus:ring-rose-500">
            <span class="ml-3 text-sm text-gray-600 group-hover:text-gray-900 transition-colors">
              {{ type || 'All' }}
            </span>
          </label>
        </div>
      </div>

      <!-- Price Range -->
      <div class="mb-6">
        <label class="block text-sm font-bold text-gray-700 mb-3">Price Range</label>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <span class="text-[10px] uppercase font-bold text-gray-400 block mb-1">Min Price</span>
            <div class="relative">
              <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">$</span>
              <input type="number" [(ngModel)]="filters.minPrice" (change)="applyFilters()"
                class="w-full pl-7 pr-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-rose-500">
            </div>
          </div>
          <div>
            <span class="text-[10px] uppercase font-bold text-gray-400 block mb-1">Max Price</span>
            <div class="relative">
              <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">$</span>
              <input type="number" [(ngModel)]="filters.maxPrice" (change)="applyFilters()"
                class="w-full pl-7 pr-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:border-rose-500">
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class FilterSidebarComponent {
  @Output() filterChange = new EventEmitter<SearchFilters>();

  filters: SearchFilters = {
    propertyType: '',
    roomType: '',
    minPrice: undefined,
    maxPrice: undefined
  };

  propertyTypes = [
    'Entire home/apt', 'Private room', 'Hotel room', 'Shared room',
    'Apartment', 'House', 'Condominium', 'Townhouse', 'Loft'
  ];

  roomTypes = ['', 'Entire home/apt', 'Private room', 'Hotel room', 'Shared room'];

  applyFilters() {
    this.filterChange.emit({ ...this.filters });
  }

  resetFilters() {
    this.filters = {
      propertyType: '',
      roomType: '',
      minPrice: undefined,
      maxPrice: undefined
    };
    this.applyFilters();
  }
}
