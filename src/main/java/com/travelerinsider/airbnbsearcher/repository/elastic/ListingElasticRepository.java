package com.travelerinsider.airbnbsearcher.repository.elastic;

import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingElasticRepository extends ElasticsearchRepository<ListingDocument, Long> {

    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["name", "description"],
                "operator": "and"
              }
            }
            """)
    List<ListingDocument> findByQuery(String query);

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
                  "name.autocomplete._3gram"
                ]
              }
            }
            """)
    List<ListingDocument> autocomplete(String prefix);
}
