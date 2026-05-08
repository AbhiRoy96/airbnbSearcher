package com.travelerinsider.airbnbsearcher.component;

import com.travelerinsider.airbnbsearcher.service.elastic.ListingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataIndexer {

    private final ListingSyncService listingSyncService;

    @EventListener(ApplicationReadyEvent.class)
    public void indexDataOnStartup() {
        log.info("Application ready. Starting initial indexing of data to Elasticsearch...");
        try {
            listingSyncService.syncAll();
            log.info("Initial indexing completed successfully.");
        } catch (Exception e) {
            log.error("Failed to perform initial indexing: {}", e.getMessage(), e);
        }
    }
}
