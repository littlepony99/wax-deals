package com.vinylteam.vinyl.util;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public abstract class AbstractElasticsearchContainerBaseTest {
    static final ElasticsearchContainer CONTAINER;

    static {
        CONTAINER = new TestElasticsearchContainer();
        CONTAINER.start();
    }
}
