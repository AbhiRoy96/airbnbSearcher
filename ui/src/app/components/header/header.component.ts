import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../search-bar/search-bar.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, SearchBarComponent],
  templateUrl: './header.component.html',
})
export class HeaderComponent {
  @Output() search = new EventEmitter<string>();
  @Output() clearSearch = new EventEmitter<void>();
  router = inject(Router);

  onSearch(query: string) { this.search.emit(query); }
  onClear() { this.clearSearch.emit(); }
  goHome() { this.router.navigate(['/']); this.clearSearch.emit(); }
}
