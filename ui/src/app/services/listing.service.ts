import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Listing, AutocompleteSuggestion, PageResponse } from '../models/listing.model';

@Injectable({
  providedIn: 'root'
})
export class ListingService {
  private readonly baseUrl = 'http://localhost:8080/api/listings';

  constructor(private readonly http: HttpClient) {}

  getAllListings(page: number = 0, size: number = 12): Observable<PageResponse<Listing>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Listing>>(this.baseUrl, { params });
  }

  getListingById(id: string): Observable<Listing> {
    console.log(`Fetching listing with ID: ${id} from ${this.baseUrl}/${id}`);
    return this.http.get<Listing>(`${this.baseUrl}/${id}`);
  }

  searchListings(query: string, page: number = 0, size: number = 12, filters: any = {}): Observable<PageResponse<Listing>> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page)
      .set('size', size);

    if (filters.propertyType) params = params.set('propertyType', filters.propertyType);
    if (filters.roomType) params = params.set('roomType', filters.roomType);
    if (filters.minPrice) params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice) params = params.set('maxPrice', filters.maxPrice);

    return this.http.get<PageResponse<Listing>>(`${this.baseUrl}/search`, { params });
  }

  autocomplete(q: string): Observable<AutocompleteSuggestion[]> {
    const params = new HttpParams().set('q', q);
    return this.http.get<AutocompleteSuggestion[]>(`${this.baseUrl}/autocomplete`, { params });
  }
}
