package com.kuang.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 狂神的 Spring 两步骤：
 * 1、找对象
 * 2、放到spring中待用
 * 3、如果是springboot 就先分析源码！
 *      xxx AutoConfiguration   xxxProperties
 */
@Configuration // xml -> bean
public class ElasticSearchClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200,"http")));
        return client;
    }
}
