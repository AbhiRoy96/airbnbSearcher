import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListingService } from '../../services/listing.service';
import { Listing } from '../../models/listing.model';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { ListingCardComponent } from '../../components/listing-card/listing-card.component';
import { EmptyStateComponent } from '../../components/empty-state/empty-state.component';
import { FilterSidebarComponent, SearchFilters } from '../../components/filter-sidebar/filter-sidebar.component';
import { MapViewComponent } from '../../components/map-view/map-view.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    SearchBarComponent,
    ListingCardComponent,
    EmptyStateComponent,
    FilterSidebarComponent,
    MapViewComponent
  ],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  listings: Listing[] = [];
  isLoading = false;
  isSearching = false;
  error: string | null = null;
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  searchQuery = '';

  viewMode: 'grid' | 'map' = 'grid';
  activeFilters: SearchFilters = {};

  constructor(private listingService: ListingService) {}

  ngOnInit() {
    this.loadListings();
  }

  loadListings(page: number = 0) {
    this.isLoading = true;
    this.error = null;
    this.listingService.getAllListings(page, 12).subscribe({
      next: (response) => {
        this.listings = response.content;
        this.totalPages = response.totalPages ?? response.page?.totalPages ?? 0;
        this.totalElements = response.totalElements ?? response.page?.totalElements ?? 0;
        this.currentPage = response.number ?? response.page?.number ?? 0;
        this.isLoading = false;
      },
      error: () => {
        this.error = 'Failed to load listings. Make sure the backend is running on port 8080.';
        this.isLoading = false;
      }
    });
  }

  onSearch(query: string, page: number = 0) {
    this.searchQuery = query;
    this.isSearching = true;
    this.error = null;
    this.listingService.searchListings(query, page, 12, this.activeFilters).subscribe({
      next: (response) => {
        this.listings = response.content;
        this.totalPages = response.totalPages ?? response.page?.totalPages ?? 0;
        this.totalElements = response.totalElements ?? response.page?.totalElements ?? 0;
        this.currentPage = response.number ?? response.page?.number ?? 0;
        this.isSearching = false;
      },
      error: () => {
        this.error = 'Search failed. Please try again.';
        this.isSearching = false;
      }
    });
  }

  onFilterChange(filters: SearchFilters) {
    this.activeFilters = filters;
    this.onSearch(this.searchQuery, 0);
  }

  onClear() {
    this.searchQuery = '';
    this.activeFilters = {};
    this.loadListings();
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      if (this.searchQuery) {
        this.onSearch(this.searchQuery, page);
      } else {
        this.loadListings(page);
      }
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  get pageNumbers(): number[] {
    const range: number[] = [];
    const start = Math.max(0, this.currentPage - 2);
    const end = Math.min(this.totalPages - 1, this.currentPage + 2);
    for (let i = start; i <= end; i++) range.push(i);
    return range;
  }

  get skeletons(): number[] {
    return Array(12).fill(0);
  }
}
