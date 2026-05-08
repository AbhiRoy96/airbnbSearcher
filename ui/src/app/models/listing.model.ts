export interface Listing {
  id: string;
  name: string;
  description: string;
  pictureUrl: string;
  hostName: string;
  neighbourhood: string;
  latitude: number;
  longitude: number;
  propertyType: string;
  roomType: string;
  accommodates: number;
  bathrooms: number;
  bedrooms: number;
  beds: number;
  amenities: string;
  price: string;
  numberOfReviews: number;
  reviewScoresRating: number;
  instantBookable: boolean;
}

export interface AutocompleteSuggestion {
  id: string;
  name: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements?: number;
  totalPages?: number;
  number?: number;
  size?: number;
  page?: {
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
  };
}
