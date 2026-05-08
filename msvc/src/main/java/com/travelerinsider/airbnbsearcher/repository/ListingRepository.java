package com.travelerinsider.airbnbsearcher.repository;

import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
}
