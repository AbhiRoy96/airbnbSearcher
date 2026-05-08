# Stayfindr — Airbnb Search Frontend

Angular 18 frontend for the Airbnb Searcher Spring Boot API.

## Tech Stack
- **Angular 18** (standalone components)
- **Tailwind CSS** for utility-first styling
- **Work Sans** (Google Fonts) typography

## APIs Used
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/listings?page=0&size=12` | Paginated listings |
| GET | `/api/listings/:id` | Listing detail |
| GET | `/api/listings/search?query=` | Full-text search |
| GET | `/api/listings/autocomplete?q=` | Autocomplete suggestions |

## Getting Started

1. Make sure your Spring Boot API is running on `http://localhost:8080`

2. Install dependencies:
```bash
npm install
```

3. Start the dev server:
```bash
ng serve
```

4. Open [http://localhost:4200](http://localhost:4200)

## Features
- 🔍 Real-time search with autocomplete (debounced 250ms)
- 🖼️ Listing images from `pictureUrl` API field
- 📄 Paginated browse mode
- 🏠 Listing detail page with amenities, stats & booking card
- ⚡ Instant Book badge
- ⭐ Star ratings & review counts
- 💀 Skeleton loading states
- 📱 Fully responsive grid layout
