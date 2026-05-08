package com.travelerinsider.airbnbsearcher.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthRequest;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch._types.HealthStatus;
import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import com.travelerinsider.airbnbsearcher.repository.ListingRepository;
import com.travelerinsider.airbnbsearcher.repository.elastic.ListingElasticRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private static final int BATCH_SIZE = 200;
    /** Max time to wait for the primary shard to become active after index creation. */
    private static final int SHARD_WAIT_TIMEOUT_SECONDS = 60;

    private final ListingRepository listingRepository;
    private final ListingElasticRepository listingElasticRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * Full re-index: drops and recreates the index, waits for the primary shard
     * to be active (yellow/green), then streams all Postgres rows in batches of
     * {@value BATCH_SIZE} to stay well under ES's 100 MB bulk limit.
     */
    @Transactional(readOnly = true)
    @WithSpan("airbnb.elasticsearch.sync-all")
    public void syncAll() {
        long start = System.nanoTime();
        log.info("Starting synchronization of listings to Elasticsearch");

        IndexOperations indexOps = elasticsearchOperations.indexOps(ListingDocument.class);
        if (indexOps.exists()) {
            log.info("Deleting existing Elasticsearch listings index before full sync");
            indexOps.delete();
            log.info("Deleted existing Elasticsearch listings index");
        } else {
            log.info("Elasticsearch listings index does not exist; creating it for first sync");
        }
        log.info("Creating Elasticsearch listings index with ListingDocument mapping");
        indexOps.createWithMapping();
        log.info("Created Elasticsearch listings index with ListingDocument mapping");

        // Wait for the primary shard to be allocated before writing.
        // Without this, bulk requests arrive before ES finishes shard allocation
        // and every document fails with "primary shard is not active".
        waitForYellow("listings");

        long total = listingRepository.count();
        int pages = (int) Math.ceil((double) total / BATCH_SIZE);
        log.info("Indexing {} listings in {} batches of {}", total, pages, BATCH_SIZE);

        int indexed = 0;
        for (int page = 0; page < pages; page++) {
            long batchStart = System.nanoTime();
            Page<Listing> batch = listingRepository.findAll(PageRequest.of(page, BATCH_SIZE));
            List<ListingDocument> docs = batch.getContent().stream()
                    .map(this::convertToDocument)
                    .collect(Collectors.toList());
            listingElasticRepository.saveAll(docs);
            indexed += docs.size();
            log.info("Indexed listings batch={}/{} batchSize={} indexed={}/{} elapsedMs={}",
                    page + 1, pages, docs.size(), indexed, total, elapsedMillis(batchStart));
        }
        log.info("Successfully synchronized {} listings to Elasticsearch in {} ms", indexed, elapsedMillis(start));
    }

    /**
     * Blocks until the given index reaches at least yellow health (primary shard active),
     * using ES's cluster health wait_for_status API with a server-side timeout.
     */
    @WithSpan("airbnb.elasticsearch.wait-for-index")
    private void waitForYellow(String indexName) {
        long start = System.nanoTime();
        log.info("Waiting up to {}s for index '{}' primary shard to become active...",
                SHARD_WAIT_TIMEOUT_SECONDS, indexName);
        try {
            HealthResponse health = elasticsearchClient.cluster().health(
                    HealthRequest.of(h -> h
                            .index(indexName)
                            .waitForStatus(HealthStatus.Yellow)
                            .timeout(t -> t.time(SHARD_WAIT_TIMEOUT_SECONDS + "s"))
                    )
            );
            log.info("Index '{}' cluster health: {} timedOut={} elapsedMs={}",
                    indexName, health.status(), health.timedOut(), elapsedMillis(start));
            if (health.timedOut()) {
                throw new IllegalStateException(
                        "Timed out waiting for index '" + indexName + "' to reach yellow health. " +
                        "Check ES logs for shard allocation issues.");
            }
        } catch (Exception e) {
            log.error("Failed while waiting for index '{}' readiness after {} ms: {}",
                    indexName, elapsedMillis(start), e.getMessage(), e);
            throw new RuntimeException("Failed while waiting for index '" + indexName + "' readiness", e);
        }
    }

    @WithSpan("airbnb.elasticsearch.sync-one")
    public void sync(Listing listing) {
        long start = System.nanoTime();
        log.debug("Synchronizing listing id={} to Elasticsearch", listing.getId());

        IndexOperations indexOps = elasticsearchOperations.indexOps(ListingDocument.class);
        if (!indexOps.exists()) {
            log.info("Creating Elasticsearch listings index during single listing sync");
            indexOps.createWithMapping();
        }

        listingElasticRepository.save(convertToDocument(listing));
        log.debug("Synchronized listing id={} to Elasticsearch elapsedMs={}", listing.getId(), elapsedMillis(start));
    }

    @WithSpan("airbnb.elasticsearch.delete-one")
    public void delete(Long id) {
        long start = System.nanoTime();
        log.debug("Deleting listing id={} from Elasticsearch", id);
        listingElasticRepository.deleteById(id);
        log.debug("Deleted listing id={} from Elasticsearch elapsedMs={}", id, elapsedMillis(start));
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

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
