package com.travelerinsider.airbnbsearcher.controller;

import com.travelerinsider.airbnbsearcher.domain.dto.ListingAutoResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.dto.ListingResponseDTO;
import com.travelerinsider.airbnbsearcher.domain.interfaces.IListingService;
import com.travelerinsider.airbnbsearcher.service.elastic.ListingSyncService;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Slf4j
@Observed
public class ListingController {

    private final IListingService listingService;
    private final ListingSyncService listingSyncService;

    @GetMapping
    public ResponseEntity<Page<ListingResponseDTO>> getAllListings(Pageable pageable) {
        log.info("Fetching all listings with pageable: {}", pageable);
        return ResponseEntity.ok(listingService.getAllListings(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> getListingById(@PathVariable Long id) {
        log.info("Fetching listing with id: {}", id);
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ListingResponseDTO>> searchListings(@RequestParam String query) {
        log.info("Searching listings with query: {}", query);
        return ResponseEntity.ok(listingService.searchListings(query));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<ListingAutoResponseDTO>> autocomplete(@RequestParam String q) {
        log.info("Autocomplete listings with prefix: {}", q);
        return ResponseEntity.ok(listingService.autocomplete(q));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncListings() {
        log.info("Triggering full re-index of listings to Elasticsearch");
        listingSyncService.syncAll();
        return ResponseEntity.ok("Synchronization triggered successfully");
    }
}
