import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ListingService } from '../../services/listing.service';
import { Listing } from '../../models/listing.model';

@Component({
  selector: 'app-listing-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './listing-detail.component.html'
})
export class ListingDetailComponent implements OnInit {
  listing: Listing | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private listingService: ListingService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    this.listingService.getListingById(id!).subscribe({
      next: (listing) => {
        this.listing = listing;
        this.isLoading = false;
      },
      error: () => {
        this.error = 'Listing not found or server unavailable.';
        this.isLoading = false;
      }
    });
  }

  get amenitiesList(): string[] {
    if (!this.listing?.amenities) return [];
    try {
      const cleaned = this.listing.amenities.replace(/^\[|\]$/g, '');
      return cleaned.split(',').map(a => a.trim().replace(/^"|"$/g, '')).filter(Boolean).slice(0, 12);
    } catch {
      return [];
    }
  }

  get imageUrl(): string {
    return this.listing?.pictureUrl || 'https://placehold.co/800x500/f3f4f6/9ca3af?text=No+Image';
  }

  onImageError(event: Event) {
    (event.target as HTMLImageElement).src = 'https://placehold.co/800x500/f3f4f6/9ca3af?text=No+Image';
  }
}
