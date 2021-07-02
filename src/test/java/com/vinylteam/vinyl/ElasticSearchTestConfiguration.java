package com.vinylteam.vinyl;

import com.vinylteam.vinyl.util.TestElasticsearchContainer;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@Configuration
public class ElasticSearchTestConfiguration {

    private static ElasticsearchContainer ES_CONTAINER = new TestElasticsearchContainer();

    @Bean(initMethod = "start")
    public ElasticsearchContainer getElasticSearchTestContainer(){
        return ES_CONTAINER;
    }

    @Bean
    @DependsOn("getElasticSearchTestContainer")
    @Primary
    RestHighLevelClient geLocalElasticsearchClient(@Qualifier("localClient") RestHighLevelClient client) {
        return client;
    }
}
