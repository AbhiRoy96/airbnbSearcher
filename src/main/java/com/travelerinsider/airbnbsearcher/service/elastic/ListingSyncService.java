package com.travelerinsider.airbnbsearcher.service.elastic;

import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import com.travelerinsider.airbnbsearcher.repository.ListingRepository;
import com.travelerinsider.airbnbsearcher.repository.elastic.ListingElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingSyncService {

    private final ListingRepository listingRepository;
    private final ListingElasticRepository listingElasticRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional(readOnly = true)
    public void syncAll() {
        log.info("Starting synchronization of listings to Elasticsearch...");
        
        IndexOperations indexOps = elasticsearchOperations.indexOps(ListingDocument.class);
        if (!indexOps.exists()) {
            log.info("Creating index for ListingDocument");
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
        }

        List<Listing> listings = listingRepository.findAll();
        List<ListingDocument> documents = listings.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());
        listingElasticRepository.saveAll(documents);
        log.info("Successfully synchronized {} listings to Elasticsearch.", documents.size());
    }

    public void sync(Listing listing) {
        log.debug("Synchronizing listing with ID: {} to Elasticsearch", listing.getId());
        
        IndexOperations indexOps = elasticsearchOperations.indexOps(ListingDocument.class);
        if (!indexOps.exists()) {
            log.info("Creating index for ListingDocument during single sync");
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
        }

        listingElasticRepository.save(convertToDocument(listing));
    }

    public void delete(Long id) {
        log.debug("Deleting listing with ID: {} from Elasticsearch", id);
        listingElasticRepository.deleteById(id);
    }

    private ListingDocument convertToDocument(Listing listing) {
        return ListingDocument.builder()
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
