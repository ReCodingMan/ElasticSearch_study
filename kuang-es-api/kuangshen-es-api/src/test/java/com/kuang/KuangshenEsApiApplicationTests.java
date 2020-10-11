package com.kuang;

import com.alibaba.fastjson.JSON;
import com.kuang.pojo.User;
import com.kuang.utils.ESconst;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

	/**
	 * 批量插入
	 */
	@Test
	void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("10s");

		ArrayList<User> userList = new ArrayList<>();
		userList.add(new User("kuangshen1",3));
		userList.add(new User("kuangshen2",3));
		userList.add(new User("kuangshen3",3));
		userList.add(new User("chengzi1",3));
		userList.add(new User("chengzi2",3));
		userList.add(new User("chengzi3",3));

		// 批处理请求
		for (int i = 0; i < userList.size(); i++) {
			// 批量更新和批量删除，就在这里修改对应的请求就可以了
			bulkRequest.add(new IndexRequest("kuang_index")
					.id(""+(i+1))
					.source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
		}

		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures()); // 是否失败
	}

	/**
	 * 批量查询
	 * SearchRequest		搜索请求
	 * SearchSourceBuilder	条件构造
	 * HighlightBuilder		构建高亮
	 * TermQueryBuilder		精确查询
	 * MatchAllQueryBuilder
	 * xxx QueryBuilder		对应我们看到的命令
	 */
	@Test
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest(ESconst.ES_INDEX);
		// 构建搜索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

		// 查询条件，我们可以使用 QueryBuilders 工具来实现
		// QueryBuilders.termQuery 精确匹配
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "chengzi1");
		sourceBuilder.query(termQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		System.out.println(JSON.toJSONString(searchResponse.getHits()));
		System.out.println("==================================");
		for (SearchHit documentFields : searchResponse.getHits()) {
			System.out.println(documentFields.getSourceAsMap());
		}
	}


}
