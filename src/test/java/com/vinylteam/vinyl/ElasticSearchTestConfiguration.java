package com.vinylteam.vinyl;

import com.github.dockerjava.api.model.PortBinding;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;


@Configuration
public class ElasticSearchTestConfiguration {
    private static final String ELASTIC_SEARCH_DOCKER = "elasticsearch:7.12.1";
    private static final String CLUSTER_NAME = "cluster.name";
    private static final String ELASTIC_SEARCH = "elasticsearch";
    private static ElasticsearchContainer ES_CONTAINER;

    @Bean(name = "elasticSearchContainer")
    public ElasticsearchContainer createESTestContainer() {
        if (ES_CONTAINER == null) {
            ES_CONTAINER = new ElasticsearchContainer(DockerImageName
                    .parse(ELASTIC_SEARCH_DOCKER)
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"));
            ES_CONTAINER.withCreateContainerCmdModifier(cmd -> cmd
                    .getHostConfig()
                    .withPortBindings(PortBinding.parse("9200:9200/tcp"))
                    .withMemory(512000000L)
                    .withMemorySwap(512000000L));
            ES_CONTAINER.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
        }
        if (!ES_CONTAINER.isRunning()) {
            ES_CONTAINER.start();
        }
        return ES_CONTAINER;
    }

    @Bean
    @DependsOn("elasticSearchContainer")
    @Primary
    public RestHighLevelClient getESClient(@Autowired RestHighLevelClient client) {
        return client;
    }

}
