package com.vinylteam.vinyl;

import com.vinylteam.vinyl.service.converter.CurrencyToStringConverter;
import com.vinylteam.vinyl.service.converter.StringToCurrencyConverter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
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
    private String elasticsearchNode;

    @Value("${elasticsearch.host}")
    private String awsElasticSearchHost;

    @Value("${elasticsearch.user}")
    private String awsEsUser;

    @Value("${elasticsearch.password}")
    private String awsEsPassword;

    @Value("${ELASTICSEARCH_HOST:}")
    private String awsElasticsearch;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        if (!awsElasticsearch.isEmpty()) {
            return geAWSRelatedElasticsearchClient();
        } else {
            return geLocalElasticsearchClient();
        }
    }

    @Bean("localClient")
    RestHighLevelClient geLocalElasticsearchClient() {
        ClientConfiguration clientConfiguration =
                ClientConfiguration
                        .builder()
                        .connectedTo(elasticsearchNode)
                        .build();

        return RestClients
                .create(clientConfiguration)
                .rest();
    }

    RestHighLevelClient geAWSRelatedElasticsearchClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(awsEsUser, awsEsPassword));

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticsearchNode/*awsElasticSearchHost*/, 443, "https"))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
        return client;
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
