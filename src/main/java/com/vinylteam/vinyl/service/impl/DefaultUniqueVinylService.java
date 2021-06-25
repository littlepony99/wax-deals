package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUniqueVinylService implements UniqueVinylService {
    private static final String INDEX = "unique_vinyl_index";

    private final UniqueVinylRepository uniqueVinylRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public UniqueVinyl updateOneUniqueVinylAsHavingNoOffer(UniqueVinyl vinyl) {
        if (vinyl.isHasOffers()) {
            return vinyl;
        }
        return uniqueVinylRepository.save(vinyl);
    }

    @Override
    public List<UniqueVinyl> findAll() {
        List<UniqueVinyl> gottenUniqueVinyls = uniqueVinylRepository.findAll();
        log.debug("Resulting list with that amount of unique vinyls from db is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        if (amount <= 0) {
            return new ArrayList<>();
        }
        FunctionScoreQueryBuilder functionScore = QueryBuilders.functionScoreQuery(
                QueryBuilders.termQuery("hasOffers", true),
                ScoreFunctionBuilders.randomFunction());

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(functionScore)
                .withMaxResults(amount)
                .build();

        return getUniqueVinyls(searchQuery);
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        if (matcher == null || matcher.isEmpty()) {
            log.error("Matcher is null, returning empty list.");
            return new ArrayList<>();
        }

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

        List<UniqueVinyl> vinylMatches = getUniqueVinyls(searchQuery);

        if (vinylMatches.isEmpty()) {
            return uniqueVinylRepository.findByFullNameIgnoreCaseContainingAndHasOffers(matcher, true);
        }

        return vinylMatches;
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        if (artist == null || artist.isEmpty()) {
            log.error("Artist is null, returning empty list.");
            return new ArrayList<>();
        }

        return uniqueVinylRepository.findByArtistIgnoreCaseAndHasOffers(artist, true);
    }

    @Override
    public UniqueVinyl findById(String id) {
        if (id == null) {
            log.error("Id is null");
            throw new IllegalArgumentException("Incorrect id value " + id);
        }
        UniqueVinyl uniqueVinyl = uniqueVinylRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No uniqueVinyl with that id in table {'id':{" + id + "}}"));
        log.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", uniqueVinyl);
        return uniqueVinyl;
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
