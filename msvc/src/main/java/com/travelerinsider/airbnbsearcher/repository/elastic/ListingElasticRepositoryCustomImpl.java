package com.travelerinsider.airbnbsearcher.repository.elastic;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.travelerinsider.airbnbsearcher.domain.elastic.ListingDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListingElasticRepositoryCustomImpl implements ListingElasticRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchPage<ListingDocument> search(String query,
                                              String propertyType,
                                              String roomType,
                                              Double minPrice,
                                              Double maxPrice,
                                              Pageable pageable) {
        var boolBuilder = bool();

        // Main full-text query across key fields with boosts
        boolBuilder.must(multiMatch(m -> m
                .query(query)
                .fields("name^3", "description", "hostName",
                        "hostLocation", "neighbourhood^2", "neighbourhoodCleansed^2")
                .operator(Operator.Or)
                .minimumShouldMatch("75%")
                .fuzziness("AUTO")
        ));

        // Optional keyword filters — only added when param is present
        if (StringUtils.hasText(propertyType)) {
            boolBuilder.filter(term(t -> t
                    .field("propertyType")
                    .value(propertyType)));
            log.debug("Applying propertyType filter='{}'", propertyType);
        }

        if (StringUtils.hasText(roomType)) {
            boolBuilder.filter(term(t -> t
                    .field("roomType")
                    .value(roomType)));
            log.debug("Applying roomType filter='{}'", roomType);
        }

        // Optional price range filter
        if (minPrice != null || maxPrice != null) {
            boolBuilder.filter(range(r -> {
                var rangeBuilder = r.number(n -> {
                    var nb = n.field("price");
                    if (minPrice != null) nb = nb.gte(minPrice);
                    if (maxPrice != null) nb = nb.lte(maxPrice);
                    return nb;
                });
                return rangeBuilder;
            }));
            log.debug("Applying price filter min={} max={}", minPrice, maxPrice);
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        SearchHits<ListingDocument> hits =
                elasticsearchOperations.search(nativeQuery, ListingDocument.class);

        return SearchHitSupport.searchPageFor(hits, pageable);
    }
}