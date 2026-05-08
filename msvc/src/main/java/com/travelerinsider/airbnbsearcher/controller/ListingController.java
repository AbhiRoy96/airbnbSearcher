package com.travelerinsider.airbnbsearcher.controller;

import com.travelerinsider.airbnbsearcher.domain.dto.ListingAutoResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.dto.RestPageImpl;
import com.travelerinsider.airbnbsearcher.domain.dto.ListingResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.interfaces.IListingService;
import com.travelerinsider.airbnbsearcher.service.elastic.ListingSyncService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@Slf4j
public class ListingController {

    private final IListingService listingService;
    private final ListingSyncService listingSyncService;
    private final Counter listingsFetchCounter;
    private final Counter listingSearchCounter;
    private final Counter listingAutocompleteCounter;
    private final Counter listingSyncCounter;

    public ListingController(IListingService listingService, ListingSyncService listingSyncService, MeterRegistry meterRegistry) {
        this.listingService = listingService;
        this.listingSyncService = listingSyncService;
        this.listingsFetchCounter = Counter.builder("api.listings.fetch.count")
                .description("Number of times listings are fetched")
                .register(meterRegistry);
        this.listingSearchCounter = Counter.builder("api.listings.search.count")
                .description("Number of times listings are searched")
                .register(meterRegistry);
        this.listingAutocompleteCounter = Counter.builder("api.listings.autocomplete.count")
                .description("Number of times listings autocomplete is called")
                .register(meterRegistry);
        this.listingSyncCounter = Counter.builder("api.listings.sync.count")
                .description("Number of times listings are synced")
                .register(meterRegistry);
    }

    @GetMapping
    @WithSpan("airbnb.api.listings.get-all")
    public ResponseEntity<RestPageImpl<ListingResponseDTO>> getAllListings(Pageable pageable) {
        long start = System.nanoTime();
        log.info("Fetching listings pageNumber={} pageSize={} sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        listingsFetchCounter.increment();
        RestPageImpl<ListingResponseDTO> response = listingService.getAllListings(pageable);
        log.info("Fetched listings pageNumber={} resultCount={} totalElements={} elapsedMs={}",
                response.getNumber(), response.getNumberOfElements(), response.getTotalElements(), elapsedMillis(start));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @WithSpan("airbnb.api.listings.get-by-id")
    public ResponseEntity<ListingResponseDTO> getListingById(@PathVariable Long id) {
        long start = System.nanoTime();
        log.info("Fetching listing with id: {}", id);
        ListingResponseDTO response = listingService.getListingById(id);
        log.info("Fetched listing id={} elapsedMs={}", id, elapsedMillis(start));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @WithSpan("airbnb.api.listings.search")
    public ResponseEntity<List<ListingResponseDTO>> searchListings(@RequestParam String query) {
        long start = System.nanoTime();
        log.info("Searching listings queryLength={}", length(query));
        log.debug("Searching listings query='{}'", query);
        listingSearchCounter.increment();
        List<ListingResponseDTO> response = listingService.searchListings(query);
        log.info("Completed listing search resultCount={} elapsedMs={}", response.size(), elapsedMillis(start));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/autocomplete")
    @WithSpan("airbnb.api.listings.autocomplete")
    public ResponseEntity<List<ListingAutoResponseDTO>> autocomplete(@RequestParam String q) {
        long start = System.nanoTime();
        log.info("Autocomplete listings prefixLength={}", length(q));
        log.debug("Autocomplete listings prefix='{}'", q);
        listingAutocompleteCounter.increment();
        List<ListingAutoResponseDTO> response = listingService.autocomplete(q);
        log.info("Completed listing autocomplete resultCount={} elapsedMs={}", response.size(), elapsedMillis(start));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync")
    @WithSpan("airbnb.api.listings.sync")
    public ResponseEntity<String> syncListings() {
        long start = System.nanoTime();
        log.info("Triggering full re-index of listings to Elasticsearch");
        listingSyncCounter.increment();
        listingSyncService.syncAll();
        log.info("Full re-index request completed elapsedMs={}", elapsedMillis(start));
        return ResponseEntity.ok("Synchronization triggered successfully");
    }

    private int length(String value) {
        return value == null ? 0 : value.length();
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
