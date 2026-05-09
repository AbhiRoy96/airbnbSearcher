package com.travelerinsider.airbnbsearcher.repository.elastic;

import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingElasticRepository extends
        ElasticsearchRepository<ListingDocument, Long>,
        ListingElasticRepositoryCustom  {

    /**
     * Autocomplete using the search_as_you_type sub-field on name.
     * multi_match across all four shingle-aware sub-fields gives prefix
     * + infix token matching at low latency.
     */
    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "type": "bool_prefix",
                "fields": [
                  "name.autocomplete",
                  "name.autocomplete._2gram",
                  "name.autocomplete._3gram",
                  "hostName.autocomplete",
                  "hostName.autocomplete._2gram",
                  "hostName.autocomplete._3gram",
                  "hostLocation.autocomplete",
                  "hostLocation.autocomplete._2gram",
                  "hostLocation.autocomplete._3gram",
                  "neighbourhoodCleansed.autocomplete",
                  "neighbourhoodCleansed.autocomplete._2gram",
                  "neighbourhoodCleansed.autocomplete._3gram"
                ]
              }
            }
            """)
    List<ListingDocument> autocomplete(String prefix, Pageable pageable);
}
