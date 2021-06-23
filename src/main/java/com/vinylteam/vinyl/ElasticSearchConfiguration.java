package com.vinylteam.vinyl;

import com.vinylteam.vinyl.service.converter.CurrencyToStringConverter;
import com.vinylteam.vinyl.service.converter.StringToCurrencyConverter;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Arrays;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.vinylteam.vinyl.dao.elasticsearch")
@PropertySource({"classpath:application.properties"})
public class ElasticSearchConfiguration extends AbstractElasticsearchConfiguration {

    @Value("${spring.data.elasticsearch.cluster-nodes:localhost:9200}")
    private String hostAndPort;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {

        ClientConfiguration clientConfiguration =
                ClientConfiguration
                        .builder()
                        .connectedTo(hostAndPort)
                        .build();

        return RestClients
                .create(clientConfiguration)
                .rest();
    }

    @Override
    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(Arrays.asList(
                new CurrencyToStringConverter(),
                new StringToCurrencyConverter())
        );
    }
}
