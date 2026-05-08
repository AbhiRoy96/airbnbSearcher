package com.travelerinsider.airbnbsearcher.service.impl;

import com.travelerinsider.airbnbsearcher.domain.constants.AppConstants;
import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import com.travelerinsider.airbnbsearcher.domain.dto.ListingResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.exceptions.ResourceNotFoundException;
import com.travelerinsider.airbnbsearcher.domain.interfaces.IListingService;
import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import com.travelerinsider.airbnbsearcher.repository.ListingRepository;
import com.travelerinsider.airbnbsearcher.repository.elastic.ListingElasticRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<ListingResponseDTO> getAllListings(Pageable pageable) {
        log.debug("Finding all listings from repository");
        return listingRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Override
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
    public List<ListingResponseDTO> searchListings(String query) {
        log.debug("Searching listings with query: {} from Elasticsearch", query);
        return listingElasticRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapDocumentToDTO)
                .collect(Collectors.toList());
    }

    private ListingResponseDTO mapDocumentToDTO(ListingDocument doc) {
        return ListingResponseDTO.builder()
                .id(doc.getId())
                .listingUrl(doc.getListingUrl())
                .scrapeId(doc.getScrapeId())
                .lastScraped(doc.getLastScraped())
                .source(doc.getSource())
                .name(doc.getName())
                .description(doc.getDescription())
                .neighborhoodOverview(doc.getNeighborhoodOverview())
                .pictureUrl(doc.getPictureUrl())
                .hostId(doc.getHostId())
                .hostUrl(doc.getHostUrl())
                .hostName(doc.getHostName())
                .hostSince(doc.getHostSince())
                .hostLocation(doc.getHostLocation())
                .hostAbout(doc.getHostAbout())
                .hostResponseTime(doc.getHostResponseTime())
                .hostResponseRate(doc.getHostResponseRate())
                .hostAcceptanceRate(doc.getHostAcceptanceRate())
                .hostIsSuperhost(doc.getHostIsSuperhost())
                .hostThumbnailUrl(doc.getHostThumbnailUrl())
                .hostPictureUrl(doc.getHostPictureUrl())
                .hostNeighbourhood(doc.getHostNeighbourhood())
                .hostListingsCount(doc.getHostListingsCount())
                .hostTotalListingsCount(doc.getHostTotalListingsCount())
                .hostVerifications(doc.getHostVerifications())
                .hostHasProfilePic(doc.getHostHasProfilePic())
                .hostIdentityVerified(doc.getHostIdentityVerified())
                .neighbourhood(doc.getNeighbourhood())
                .neighbourhoodCleansed(doc.getNeighbourhoodCleansed())
                .neighbourhoodGroupCleansed(doc.getNeighbourhoodGroupCleansed())
                .latitude(doc.getLatitude())
                .longitude(doc.getLongitude())
                .propertyType(doc.getPropertyType())
                .roomType(doc.getRoomType())
                .accommodates(doc.getAccommodates())
                .bathrooms(doc.getBathrooms())
                .bathroomsText(doc.getBathroomsText())
                .bedrooms(doc.getBedrooms())
                .beds(doc.getBeds())
                .amenities(doc.getAmenities())
                .price(doc.getPrice())
                .minimumNights(doc.getMinimumNights())
                .maximumNights(doc.getMaximumNights())
                .minimumMinimumNights(doc.getMinimumMinimumNights())
                .maximumMinimumNights(doc.getMaximumMinimumNights())
                .minimumMaximumNights(doc.getMinimumMaximumNights())
                .maximumMaximumNights(doc.getMaximumMaximumNights())
                .minimumNightsAvgNtm(doc.getMinimumNightsAvgNtm())
                .maximumNightsAvgNtm(doc.getMaximumNightsAvgNtm())
                .calendarUpdated(doc.getCalendarUpdated())
                .hasAvailability(doc.getHasAvailability())
                .availability30(doc.getAvailability30())
                .availability60(doc.getAvailability60())
                .availability90(doc.getAvailability90())
                .availability365(doc.getAvailability365())
                .calendarLastScraped(doc.getCalendarLastScraped())
                .numberOfReviews(doc.getNumberOfReviews())
                .numberOfReviewsLtm(doc.getNumberOfReviewsLtm())
                .numberOfReviewsL30d(doc.getNumberOfReviewsL30d())
                .availabilityEoy(doc.getAvailabilityEoy())
                .numberOfReviewsLy(doc.getNumberOfReviewsLy())
                .estimatedOccupancyL365d(doc.getEstimatedOccupancyL365d())
                .estimatedRevenueL365d(doc.getEstimatedRevenueL365d())
                .firstReview(doc.getFirstReview())
                .lastReview(doc.getLastReview())
                .reviewScoresRating(doc.getReviewScoresRating())
                .reviewScoresAccuracy(doc.getReviewScoresAccuracy())
                .reviewScoresCleanliness(doc.getReviewScoresCleanliness())
                .reviewScoresCheckin(doc.getReviewScoresCheckin())
                .reviewScoresCommunication(doc.getReviewScoresCommunication())
                .reviewScoresLocation(doc.getReviewScoresLocation())
                .reviewScoresValue(doc.getReviewScoresValue())
                .license(doc.getLicense())
                .instantBookable(doc.getInstantBookable())
                .calculatedHostListingsCount(doc.getCalculatedHostListingsCount())
                .calculatedHostListingsCountEntireHomes(doc.getCalculatedHostListingsCountEntireHomes())
                .calculatedHostListingsCountPrivateRooms(doc.getCalculatedHostListingsCountPrivateRooms())
                .calculatedHostListingsCountSharedRooms(doc.getCalculatedHostListingsCountSharedRooms())
                .reviewsPerMonth(doc.getReviewsPerMonth())
                .build();
    }

    private ListingResponseDTO mapToDTO(Listing listing) {
        return ListingResponseDTO.builder()
                .id(listing.getId())
                .listingUrl(listing.getListingUrl())
                .scrapeId(listing.getScrapeId())
                .lastScraped(listing.getLastScraped())
                .source(listing.getSource())
                .name(listing.getName())
                .description(listing.getDescription())
                .neighborhoodOverview(listing.getNeighborhoodOverview())
                .pictureUrl(listing.getPictureUrl())
                .hostId(listing.getHostId())
                .hostUrl(listing.getHostUrl())
                .hostName(listing.getHostName())
                .hostSince(listing.getHostSince())
                .hostLocation(listing.getHostLocation())
                .hostAbout(listing.getHostAbout())
                .hostResponseTime(listing.getHostResponseTime())
                .hostResponseRate(listing.getHostResponseRate())
                .hostAcceptanceRate(listing.getHostAcceptanceRate())
                .hostIsSuperhost(listing.getHostIsSuperhost())
                .hostThumbnailUrl(listing.getHostThumbnailUrl())
                .hostPictureUrl(listing.getHostPictureUrl())
                .hostNeighbourhood(listing.getHostNeighbourhood())
                .hostListingsCount(listing.getHostListingsCount())
                .hostTotalListingsCount(listing.getHostTotalListingsCount())
                .hostVerifications(listing.getHostVerifications())
                .hostHasProfilePic(listing.getHostHasProfilePic())
                .hostIdentityVerified(listing.getHostIdentityVerified())
                .neighbourhood(listing.getNeighbourhood())
                .neighbourhoodCleansed(listing.getNeighbourhoodCleansed())
                .neighbourhoodGroupCleansed(listing.getNeighbourhoodGroupCleansed())
                .latitude(listing.getLatitude())
                .longitude(listing.getLongitude())
                .propertyType(listing.getPropertyType())
                .roomType(listing.getRoomType())
                .accommodates(listing.getAccommodates())
                .bathrooms(listing.getBathrooms())
                .bathroomsText(listing.getBathroomsText())
                .bedrooms(listing.getBedrooms())
                .beds(listing.getBeds())
                .amenities(listing.getAmenities())
                .price(listing.getPrice())
                .minimumNights(listing.getMinimumNights())
                .maximumNights(listing.getMaximumNights())
                .minimumMinimumNights(listing.getMinimumMinimumNights())
                .maximumMinimumNights(listing.getMaximumMinimumNights())
                .minimumMaximumNights(listing.getMinimumMaximumNights())
                .maximumMaximumNights(listing.getMaximumMaximumNights())
                .minimumNightsAvgNtm(listing.getMinimumNightsAvgNtm())
                .maximumNightsAvgNtm(listing.getMaximumNightsAvgNtm())
                .calendarUpdated(listing.getCalendarUpdated())
                .hasAvailability(listing.getHasAvailability())
                .availability30(listing.getAvailability30())
                .availability60(listing.getAvailability60())
                .availability90(listing.getAvailability90())
                .availability365(listing.getAvailability365())
                .calendarLastScraped(listing.getCalendarLastScraped())
                .numberOfReviews(listing.getNumberOfReviews())
                .numberOfReviewsLtm(listing.getNumberOfReviewsLtm())
                .numberOfReviewsL30d(listing.getNumberOfReviewsL30d())
                .availabilityEoy(listing.getAvailabilityEoy())
                .numberOfReviewsLy(listing.getNumberOfReviewsLy())
                .estimatedOccupancyL365d(listing.getEstimatedOccupancyL365d())
                .estimatedRevenueL365d(listing.getEstimatedRevenueL365d())
                .firstReview(listing.getFirstReview())
                .lastReview(listing.getLastReview())
                .reviewScoresRating(listing.getReviewScoresRating())
                .reviewScoresAccuracy(listing.getReviewScoresAccuracy())
                .reviewScoresCleanliness(listing.getReviewScoresCleanliness())
                .reviewScoresCheckin(listing.getReviewScoresCheckin())
                .reviewScoresCommunication(listing.getReviewScoresCommunication())
                .reviewScoresLocation(listing.getReviewScoresLocation())
                .reviewScoresValue(listing.getReviewScoresValue())
                .license(listing.getLicense())
                .instantBookable(listing.getInstantBookable())
                .calculatedHostListingsCount(listing.getCalculatedHostListingsCount())
                .calculatedHostListingsCountEntireHomes(listing.getCalculatedHostListingsCountEntireHomes())
                .calculatedHostListingsCountPrivateRooms(listing.getCalculatedHostListingsCountPrivateRooms())
                .calculatedHostListingsCountSharedRooms(listing.getCalculatedHostListingsCountSharedRooms())
                .reviewsPerMonth(listing.getReviewsPerMonth())
                .build();
    }
}
