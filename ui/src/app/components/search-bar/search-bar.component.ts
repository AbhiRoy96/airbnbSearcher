import { Component, EventEmitter, Output, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ListingService } from '../../services/listing.service';
import { AutocompleteSuggestion } from '../../models/listing.model';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-bar.component.html'
})
export class SearchBarComponent implements OnInit, OnDestroy {
  @Output() search = new EventEmitter<string>();
  @Output() clear = new EventEmitter<void>();

  query = '';
  suggestions: AutocompleteSuggestion[] = [];
  showSuggestions = false;
  isLoading = false;

  private input$ = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(private listingService: ListingService) {}

  ngOnInit() {
    this.input$.pipe(
      debounceTime(250),
      distinctUntilChanged(),
      switchMap(q => q.length >= 2 ? this.listingService.autocomplete(q) : of([])),
      takeUntil(this.destroy$)
    ).subscribe(suggestions => {
      this.suggestions = suggestions;
      this.showSuggestions = suggestions.length > 0;
    });
  }

  onInput() {
    this.input$.next(this.query);
    if (!this.query) {
      this.suggestions = [];
      this.showSuggestions = false;
      this.clear.emit();
    }
  }

  onSearch() {
    if (this.query.trim()) {
      this.showSuggestions = false;
      this.search.emit(this.query.trim());
    }
  }

  selectSuggestion(suggestion: AutocompleteSuggestion) {
    this.query = suggestion.name;
    this.showSuggestions = false;
    this.search.emit(suggestion.name);
  }

  clearSearch() {
    this.query = '';
    this.suggestions = [];
    this.showSuggestions = false;
    this.clear.emit();
  }

  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Escape') {
      this.showSuggestions = false;
    }
  }

  onBlur() {
    setTimeout(() => { this.showSuggestions = false; }, 150);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
