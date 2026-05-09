package com.travelerinsider.airbnbsearcher.domain.interfaces;

import com.travelerinsider.airbnbsearcher.domain.dto.ListingAutoResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.dto.ListingResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.dto.RestPageImpl;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IListingService {
    RestPageImpl<ListingResponseDTO> getAllListings(Pageable pageable);
    ListingResponseDTO getListingById(Long id);
    RestPageImpl<ListingResponseDTO> searchListings(String query, Pageable pageable, String propertyType, String roomType, Double minPrice, Double maxPrice);
    List<ListingAutoResponseDTO> autocomplete(String prefix);
}
