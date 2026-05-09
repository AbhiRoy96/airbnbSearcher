package com.travelerinsider.airbnbsearcher.repository.elastic;

import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;

public interface ListingElasticRepositoryCustom {
    SearchPage<ListingDocument> search(String query,
                                       String propertyType,
                                       String roomType,
                                       Double minPrice,
                                       Double maxPrice,
                                       Pageable pageable);
}