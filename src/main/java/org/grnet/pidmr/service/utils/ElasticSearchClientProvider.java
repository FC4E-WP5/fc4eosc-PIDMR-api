package org.grnet.pidmr.service.utils;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@ApplicationScoped
@Startup
public class ElasticSearchClientProvider {

    private RestClient client;

    @ConfigProperty(name = "elasticsearch.host")
    String host;
    @ConfigProperty(name = "elasticsearch.port")
    int port;
    @ConfigProperty(name = "elasticsearch.scheme")
    String scheme;
    @ConfigProperty(name = "elasticsearch.username")
    String username;
    @ConfigProperty(name = "elasticsearch.password")
    String password;
    @ConfigProperty(name = "elasticsearch.path-prefix")
    String pathPrefix;

    @PostConstruct
    void init() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, scheme))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE))
                .setPathPrefix(pathPrefix);

        this.client = builder.build();
    }

    public RestClient getClient() {
        return client;
    }

    @PreDestroy
    void close() {
        try {
            if (client != null) client.close();
        } catch (IOException e) {
            e.printStackTrace(); // or use a logger
        }
    }
}

