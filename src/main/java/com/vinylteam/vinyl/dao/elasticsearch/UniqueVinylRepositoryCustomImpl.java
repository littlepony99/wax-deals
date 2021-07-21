package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UniqueVinylRepositoryCustomImpl implements UniqueVinylRepositoryCustom {
    private static final String INDEX = "unique_vinyl_index";

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<UniqueVinyl> findByFilter(String matcher) {
        log.info("Search with query {}", matcher);

        QueryBuilder queryBuilder =
                QueryBuilders.boolQuery()
                        .must(QueryBuilders
                                .matchQuery("fullName", matcher)
                                .fuzziness(Fuzziness.AUTO)
                        )
                        .must(QueryBuilders
                                .termQuery("hasOffers", true));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        return getUniqueVinyls(searchQuery);
    }

    @Override
    public List<UniqueVinyl> findRandom(int amount) {
        FunctionScoreQueryBuilder functionScore = QueryBuilders.functionScoreQuery(
                QueryBuilders.termQuery("hasOffers", true),
                ScoreFunctionBuilders.randomFunction());

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(functionScore)
                .withMaxResults(amount)
                .build();

        return getUniqueVinyls(searchQuery);
    }

    List<UniqueVinyl> getUniqueVinyls(Query searchQuery) {
        SearchHits<UniqueVinyl> productHits =
                elasticsearchOperations
                        .search(searchQuery, UniqueVinyl.class,
                                IndexCoordinates.of(INDEX));

        List<UniqueVinyl> vinylMatches = new ArrayList<>();
        productHits.forEach(searchHit -> vinylMatches.add(searchHit.getContent()));
        return vinylMatches;
    }

}
