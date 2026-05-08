package com.travelerinsider.airbnbsearcher.repository.elastic;

import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingElasticRepository extends ElasticsearchRepository<ListingDocument, Long> {
    List<ListingDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
