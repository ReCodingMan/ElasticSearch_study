package com.kuang;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class KuangshenEsApiApplicationTests {

	//面向对象来操作
	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	@Test
	void contextLoads() throws IOException {
		//1、创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("kuang_index");
		//2、客户端执行请求 IndicesClient，请求后获得响应
		CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

		System.out.println(response);
	}

}
