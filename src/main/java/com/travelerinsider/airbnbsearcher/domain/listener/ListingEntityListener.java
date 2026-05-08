package com.travelerinsider.airbnbsearcher.domain.listener;

import com.travelerinsider.airbnbsearcher.domain.model.Listing;
import com.travelerinsider.airbnbsearcher.service.elastic.ListingSyncService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ListingEntityListener {

    private ListingSyncService listingSyncService;

    @Autowired
    public void setListingSyncService(@Lazy ListingSyncService listingSyncService) {
        this.listingSyncService = listingSyncService;
    }

    @PostPersist
    @PostUpdate
    public void onPostPersistOrUpdate(Listing listing) {
        listingSyncService.sync(listing);
    }

    @PostRemove
    public void onPostRemove(Listing listing) {
        listingSyncService.delete(listing.getId());
    }
}
