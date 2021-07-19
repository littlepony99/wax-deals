package com.vinylteam.vinyl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class WaxDealsTestContainersTest {

    @Container
    @Autowired
    private ElasticsearchContainer elasticsearchContainer;

    @Container
    public PostgreSQLContainer container = WaxDealsPostgresqlContainer.getInstance();

    @Test
    void test() {
        assertTrue(elasticsearchContainer.isRunning());
        assertTrue(container.isRunning());
    }
}
