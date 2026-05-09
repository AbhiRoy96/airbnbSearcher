import { Component, Input, OnChanges, SimpleChanges, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Listing } from '../../models/listing.model';

declare var L: any;

@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="relative w-full h-[600px] rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
      <div #mapContainer class="w-full h-full bg-gray-50"></div>

      <!-- No Geo Data Overlay -->
      <div *ngIf="!hasGeoData" class="absolute inset-0 bg-white/80 backdrop-blur-sm flex flex-col items-center justify-center p-6 text-center z-[1000]">
        <div class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
          </svg>
        </div>
        <h4 class="font-bold text-gray-800">No location data available</h4>
        <p class="text-sm text-gray-500 max-w-xs">These listings don't have coordinates to show on the map.</p>
      </div>
    </div>
  `,
  styles: [`
    :host ::ng-deep .leaflet-popup-content-wrapper {
      border-radius: 12px;
      padding: 0;
      overflow: hidden;
    }
    :host ::ng-deep .leaflet-popup-content {
      margin: 0;
      width: 200px !important;
    }
  `]
})
export class MapViewComponent implements OnChanges, AfterViewInit {
  @Input() listings: Listing[] = [];
  @ViewChild('mapContainer') mapContainer!: ElementRef;

  private map: any;
  private markers: any[] = [];
  hasGeoData = true;

  ngAfterViewInit() {
    this.initMap();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['listings'] && this.map) {
      this.updateMarkers();
    }
  }

  private initMap() {
    if (typeof L === 'undefined') {
      console.warn('Leaflet (L) is not defined. Make sure to include Leaflet CSS/JS.');
      this.hasGeoData = false;
      return;
    }

    this.map = L.map(this.mapContainer.nativeElement).setView([40.7128, -74.0060], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.updateMarkers();
  }

  private updateMarkers() {
    if (!this.map) return;

    // Clear existing markers
    this.markers.forEach(m => this.map.removeLayer(m));
    this.markers = [];

    const validListings = this.listings.filter(l => l.latitude && l.longitude);
    this.hasGeoData = validListings.length > 0;

    if (validListings.length === 0) return;

    const bounds = L.latLngBounds([]);

    validListings.forEach(listing => {
      const pos = [listing.latitude, listing.longitude];
      const marker = L.marker(pos).addTo(this.map);

      const popupContent = `
        <div class="p-0">
          <img src="${listing.pictureUrl || 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400'}"
               class="w-full h-24 object-cover" />
          <div class="p-3">
            <h5 class="font-bold text-sm truncate">${listing.name}</h5>
            <p class="text-rose-500 font-bold mt-1">${listing.price}</p>
          </div>
        </div>
      `;

      marker.bindPopup(popupContent);
      this.markers.push(marker);
      bounds.extend(pos);
    });

    this.map.fitBounds(bounds, { padding: [50, 50] });
  }
}
