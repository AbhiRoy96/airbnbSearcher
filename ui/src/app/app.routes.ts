import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ListingDetailComponent } from './components/listing-detail/listing-detail.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'listing/:id', component: ListingDetailComponent },
  { path: '**', redirectTo: '' }
];
