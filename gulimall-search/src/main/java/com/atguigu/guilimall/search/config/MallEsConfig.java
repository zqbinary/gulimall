package com.atguigu.guilimall.search.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MallEsConfig {
    public static final RequestOptions COMMON_OPTIONS;

    //官方建议这个 单例
    //https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/7.17/java-rest-low-usage-requests.html#java-rest-low-usage-request-options
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // builder.addHeader("Authorization", "Bearer " + TOKEN);
        // builder.setHttpAsyncResponseConsumerFactory(
        //         new HttpAsyncResponseConsumerFactory
        //                 .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(@Value("${es.host}") String host) {
        System.out.println("........");
        System.out.println(host);
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, 9200, "http")));

    }
}
