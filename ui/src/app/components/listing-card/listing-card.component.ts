import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Listing } from '../../models/listing.model';

@Component({
  selector: 'app-listing-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './listing-card.component.html'
})
export class ListingCardComponent {
  @Input() listing!: Listing;
  isImageLoading = true;

  get stars(): number[] {
    const r = Math.round(this.listing.reviewScoresRating ?? 0);
    return Array(Math.min(r, 5)).fill(0);
  }

  get emptyStars(): number[] {
    const r = Math.round(this.listing.reviewScoresRating ?? 0);
    return Array(Math.max(0, 5 - r)).fill(0);
  }

  get formattedPrice(): string {
    return this.listing.price || 'N/A';
  }

  get imageUrl(): string {
    return this.listing.pictureUrl || 'https://placehold.co/400x300/f3f4f6/9ca3af?text=No+Image';
  }

  onImageError(event: Event) {
    (event.target as HTMLImageElement).src = 'https://placehold.co/400x300/f3f4f6/9ca3af?text=No+Image';
    this.isImageLoading = false;
  }

  onImageLoad() {
    this.isImageLoading = false;
  }
}
