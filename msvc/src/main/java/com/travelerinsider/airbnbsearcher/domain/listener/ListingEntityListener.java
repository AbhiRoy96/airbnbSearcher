package com.travelerinsider.airbnbsearcher.domain.listener;

import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import com.travelerinsider.airbnbsearcher.service.elastic.ListingSyncService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class ListingEntityListener {

    private ListingSyncService listingSyncService;

    @Autowired
    public void setListingSyncService(@Lazy ListingSyncService listingSyncService) {
        log.info("Wiring ListingSyncService into ListingEntityListener");
        this.listingSyncService = listingSyncService;
    }

    @PostPersist
    @PostUpdate
    @WithSpan("airbnb.jpa.listing.after-save")
    public void onPostPersistOrUpdate(Listing listing) {
        log.debug("Listing JPA save callback received for id={}", listing.getId());
        listingSyncService.sync(listing);
    }

    @PostRemove
    @WithSpan("airbnb.jpa.listing.after-remove")
    public void onPostRemove(Listing listing) {
        log.debug("Listing JPA remove callback received for id={}", listing.getId());
        listingSyncService.delete(listing.getId());
    }
}
