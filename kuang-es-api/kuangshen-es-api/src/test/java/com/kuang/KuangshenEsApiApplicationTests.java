package com.kuang;

import com.alibaba.fastjson.JSON;
import com.kuang.pojo.User;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
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

	/**
	 * 测试添加文档
	 */
	@Test
	void testAddDocument() throws IOException {
		//1、创建对象
		User user = new User("chengzi", 28);
		//2、创建请求
		IndexRequest request = new IndexRequest("kuang_index");

		// 测试 put /kuang_index/_doc/1
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(1));
		request.timeout("1s");

		// 将我们的数据放入请求 json
		request.source(JSON.toJSONString(user), XContentType.JSON);

		// 客户端发送请求，获取响应的结果
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());
	}

	/**
	 * 测试获取文档，判断是否存在
	 */
	@Test
	void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("kuang_index", "1");

		// 不获取返回的 _source 的上下文了
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");

		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	/**
	 * 获得文档的信息
	 */
	@Test
	void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("kuang_index", "1");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		// 打印文档的内容
		System.out.println(getResponse.getSourceAsString());
		System.out.println(getResponse);
	}

	/**
	 * 更新文档的信息
	 */
	@Test
	void testUpdateDocument() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("kuang_index", "1");
		updateRequest.timeout("1s");

		// 打印文档的内容
		User user = new User("狂神说Java", 18);
		updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

		System.out.println(updateResponse.status());
	}

	/**
	 * 删除文档的信息
	 */
	@Test
	void testDeleteDocument() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("kuang_index", "1");
		deleteRequest.timeout("1s");

		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println(deleteResponse.status());
	}

}
