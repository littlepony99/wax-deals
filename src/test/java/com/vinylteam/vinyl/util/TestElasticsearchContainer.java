package com.vinylteam.vinyl.util;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class TestElasticsearchContainer extends ElasticsearchContainer {
    private static final String ELASTIC_SEARCH_DOCKER = "elasticsearch:7.9.0";

    private static final String CLUSTER_NAME = "cluster.name";

    private static final String ELASTIC_SEARCH = "elasticsearch";

    public TestElasticsearchContainer() {
        super(DockerImageName.parse(ELASTIC_SEARCH_DOCKER).asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"));
        this.addFixedExposedPort(9200, 9200);
        this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
    }

}
