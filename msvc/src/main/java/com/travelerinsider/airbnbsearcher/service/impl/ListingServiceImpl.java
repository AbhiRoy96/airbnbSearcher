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
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ListingServiceImpl implements IListingService {

    private final ListingRepository listingRepository;
    private final ListingElasticRepository listingElasticRepository;

    @Override
    @Cacheable(value = "listings", key = "#pageable")
    @WithSpan("airbnb.service.listings.get-all")
    public RestPageImpl<ListingResponseDTO> getAllListings(Pageable pageable) {
        long start = System.nanoTime();
        log.debug("Loading listings from repository pageNumber={} pageSize={} sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<ListingResponseDTO> page = listingRepository.findAll(pageable)
                .map(this::mapToDTO);
        log.debug("Loaded listings pageNumber={} resultCount={} totalElements={} elapsedMs={}",
                page.getNumber(), page.getNumberOfElements(), page.getTotalElements(), elapsedMillis(start));
        return new RestPageImpl<>(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    @Override
    @Cacheable(value = "listing", key = "#id")
    @WithSpan("airbnb.service.listings.get-by-id")
    public ListingResponseDTO getListingById(Long id) {
        long start = System.nanoTime();
        log.debug("Finding listing with id: {} from repository", id);
        ListingResponseDTO response = listingRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.warn("Listing with id={} not found elapsedMs={}", id, elapsedMillis(start));
                    return new ResourceNotFoundException(AppConstants.LISTING_NOT_FOUND + id);
                });
        log.debug("Loaded listing id={} elapsedMs={}", id, elapsedMillis(start));
        return response;
    }

    @Override
    @Cacheable(
            value = "listingSearch",
            key = "{#query, #pageable.pageNumber, #pageable.pageSize, #propertyType, #roomType, #minPrice, #maxPrice}"
    )
    @WithSpan("airbnb.service.listings.search")
    public RestPageImpl<ListingResponseDTO> searchListings(String query, Pageable pageable,
                                                           String propertyType, String roomType,
                                                           Double minPrice, Double maxPrice) {
        long start = System.nanoTime();
        log.debug("Searching listings from Elasticsearch queryLength={} pageNumber={} pageSize={}",
                length(query), pageable.getPageNumber(), pageable.getPageSize());

        if (query == null || query.isBlank()) {
            log.debug("Skipping listing search for blank query elapsedMs={}", elapsedMillis(start));
            return new RestPageImpl<>(List.of(), pageable, 0L);
        }

        SearchPage<ListingDocument> searchPage = listingElasticRepository.search(
                query, propertyType, roomType, minPrice, maxPrice, pageable);

        List<ListingResponseDTO> results = searchPage.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(this::mapDocumentToDTO)
                .collect(Collectors.toList());

        log.debug("Completed Elasticsearch listing search resultCount={} totalElements={} elapsedMs={}",
                results.size(), searchPage.getTotalElements(), elapsedMillis(start));

        return new RestPageImpl<>(results, pageable, searchPage.getTotalElements());
    }

    @Override
    @Cacheable(value = "listingAutocomplete", key = "#prefix")
    @WithSpan("airbnb.service.listings.autocomplete")
    public List<ListingAutoResponseDTO> autocomplete(String prefix) {
        long start = System.nanoTime();
        log.debug("Autocompleting listings from Elasticsearch prefixLength={}", length(prefix));
        if (prefix == null || prefix.isBlank()) {
            log.debug("Skipping listing autocomplete for blank prefix elapsedMs={}", elapsedMillis(start));
            return List.of();
        }
        List<ListingAutoResponseDTO> results = listingElasticRepository.autocomplete(prefix, PageRequest.of(0, 20))
                .stream()
                .map(doc -> mapDocumentToAutoDTO(doc, prefix))
                .collect(Collectors.toList());
        log.debug("Completed Elasticsearch listing autocomplete resultCount={} elapsedMs={}",
                results.size(), elapsedMillis(start));
        return results;
    }

    private ListingAutoResponseDTO mapDocumentToAutoDTO(ListingDocument doc, String prefix) {
        String type = "property name";
        String lowerPrefix = prefix.toLowerCase();

        if (doc.getHostName() != null && doc.getHostName().toLowerCase().contains(lowerPrefix)) {
            type = "host";
        } else if ((doc.getHostLocation() != null && doc.getHostLocation().toLowerCase().contains(lowerPrefix)) ||
                   (doc.getNeighbourhoodCleansed() != null && doc.getNeighbourhoodCleansed().toLowerCase().contains(lowerPrefix))) {
            type = "neighbourhood";
        }

        return ListingAutoResponseDTO.builder()
                .id(String.valueOf(doc.getId()))
                .name(doc.getName())
                .type(type)
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

    private int length(String value) {
        return value == null ? 0 : value.length();
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
