package com.travelerinsider.airbnbsearcher.component;

import com.travelerinsider.airbnbsearcher.service.elastic.ListingSyncService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
    @WithSpan("airbnb.indexer.startup")
    public void indexDataOnStartup() {
        long start = System.nanoTime();
        log.info("Application ready. Starting initial listing index sync to Elasticsearch");
        try {
            listingSyncService.syncAll();
            log.info("Initial listing index sync completed in {} ms", elapsedMillis(start));
        } catch (Exception e) {
            log.error("Initial listing index sync failed after {} ms: {}", elapsedMillis(start), e.getMessage(), e);
        }
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
