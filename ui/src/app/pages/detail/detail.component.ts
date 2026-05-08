import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ListingService, ListingResponseDTO } from '../../services/listing.service';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detail.component.html',
})
export class DetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private listingService = inject(ListingService);

  listing = signal<ListingResponseDTO | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Fetching listing with ID:', id);
    this.listingService.getListingById(id!).subscribe({
      next: (data) => { 
        this.listing.set(data);
        console.log('Fetched listing:', data); 
        this.loading.set(false); },
      error: () => { this.error.set('Listing not found.'); this.loading.set(false); }
    });
  }

  get amenitiesList(): string[] {
    const raw = this.listing()?.amenities || '[]';
    try {
      return raw.replace(/[{}"]/g, '').split(',').map(s => s.trim()).filter(Boolean);
    } catch { return []; }
  }

  get rating(): string {
    const r = this.listing()?.reviewScoresRating;
    return r ? (r / 20).toFixed(1) : 'New';
  }

  get fallbackImage(): string {
    return `https://picsum.photos/seed/${this.listing()?.id}/800/500`;
  }

  onImgError(event: Event) {
    (event.target as HTMLImageElement).src = this.fallbackImage;
  }

  goBack() { this.router.navigate(['/']); }
}
