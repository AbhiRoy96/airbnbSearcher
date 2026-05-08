package com.travelerinsider.airbnbsearcher.service.impl;

import com.travelerinsider.airbnbsearcher.domain.constants.AppConstants;
import com.travelerinsider.airbnbsearcher.domain.dto.ListingAutoResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import com.travelerinsider.airbnbsearcher.domain.dto.ListingResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.exceptions.ResourceNotFoundException;
import com.travelerinsider.airbnbsearcher.domain.interfaces.IListingService;
import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import com.travelerinsider.airbnbsearcher.repository.ListingRepository;
import com.travelerinsider.airbnbsearcher.repository.elastic.ListingElasticRepository;
import com.travelerinsider.airbnbsearcher.domain.dto.RestPageImpl;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Observed
public class ListingServiceImpl implements IListingService {

    private final ListingRepository listingRepository;
    private final ListingElasticRepository listingElasticRepository;

    @Override
    @Cacheable(value = "listings", key = "#pageable")
    public RestPageImpl<ListingResponseDTO> getAllListings(Pageable pageable) {
        log.debug("Finding all listings from repository");
        Page<ListingResponseDTO> page = listingRepository.findAll(pageable)
                .map(this::mapToDTO);
        return new RestPageImpl<>(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    @Override
    @Cacheable(value = "listing", key = "#id")
    public ListingResponseDTO getListingById(Long id) {
        log.debug("Finding listing with id: {} from repository", id);
        return listingRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.error("Listing with id: {} not found", id);
                    return new ResourceNotFoundException(AppConstants.LISTING_NOT_FOUND + id);
                });
    }

    @Override
    @Cacheable(value = "listingSearch", key = "#query")
    public List<ListingResponseDTO> searchListings(String query) {
        log.debug("Searching listings with query: {} from Elasticsearch", query);
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return listingElasticRepository.findByQuery(query)
                .stream()
                .map(this::mapDocumentToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "listingAutocomplete", key = "#prefix")
    public List<ListingAutoResponseDTO> autocomplete(String prefix) {
        log.debug("Autocomplete listings with prefix: {}", prefix);
        if (prefix == null || prefix.isBlank()) {
            return List.of();
        }
        return listingElasticRepository.autocomplete(prefix)
                .stream()
                .map(this::mapDocumentToAutoDTO)
                .collect(Collectors.toList());
    }

    private ListingAutoResponseDTO mapDocumentToAutoDTO(ListingDocument doc) {
        return ListingAutoResponseDTO.builder()
                .id(String.valueOf(doc.getId()))
                .name(doc.getName())
                .build();
    }

    private ListingResponseDTO mapDocumentToDTO(ListingDocument doc) {
        return ListingResponseDTO.builder()
                .id(String.valueOf(doc.getId()))
                .name(doc.getName())
                .description(doc.getDescription())
                .pictureUrl(doc.getPictureUrl())
                .hostName(doc.getHostName())
                .neighbourhood(doc.getNeighbourhood())
                .latitude(doc.getLatitude())
                .longitude(doc.getLongitude())
                .propertyType(doc.getPropertyType())
                .roomType(doc.getRoomType())
                .accommodates(doc.getAccommodates())
                .bathrooms(doc.getBathrooms())
                .bedrooms(doc.getBedrooms())
                .beds(doc.getBeds())
                .amenities(doc.getAmenities())
                .price(doc.getPrice())
                .numberOfReviews(doc.getNumberOfReviews())
                .reviewScoresRating(doc.getReviewScoresRating())
                .instantBookable(doc.getInstantBookable())
                .build();
    }

    private ListingResponseDTO mapToDTO(Listing listing) {
        return ListingResponseDTO.builder()
                .id(String.valueOf(listing.getId()))
                .name(listing.getName())
                .description(listing.getDescription())
                .pictureUrl(listing.getPictureUrl())
                .hostName(listing.getHostName())
                .neighbourhood(listing.getNeighbourhood())
                .latitude(listing.getLatitude())
                .longitude(listing.getLongitude())
                .propertyType(listing.getPropertyType())
                .roomType(listing.getRoomType())
                .accommodates(listing.getAccommodates())
                .bathrooms(listing.getBathrooms())
                .bedrooms(listing.getBedrooms())
                .beds(listing.getBeds())
                .amenities(listing.getAmenities())
                .price(listing.getPrice())
                .numberOfReviews(listing.getNumberOfReviews())
                .reviewScoresRating(listing.getReviewScoresRating())
                .instantBookable(listing.getInstantBookable())
                .build();
    }
}
