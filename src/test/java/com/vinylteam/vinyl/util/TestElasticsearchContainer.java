package com.vinylteam.vinyl.util;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class TestElasticsearchContainer extends ElasticsearchContainer {

    private static TestElasticsearchContainer container;
    private static final String ELASTIC_SEARCH_DOCKER = "elasticsearch:7.12.1";

    private static final String CLUSTER_NAME = "cluster.name";

    private static final String ELASTIC_SEARCH = "elasticsearch";

    public TestElasticsearchContainer() {
        super(DockerImageName.parse(ELASTIC_SEARCH_DOCKER).asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"));
        this.addFixedExposedPort(9200, 9200);
        this.withCreateContainerCmdModifier(cmd -> cmd
                .getHostConfig()
                .withMemory(512000000l)
                .withMemorySwap(512000000l));
        this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
    }

}
