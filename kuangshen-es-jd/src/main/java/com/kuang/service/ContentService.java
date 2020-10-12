package com.kuang.service;

import com.alibaba.fastjson.JSON;
import com.kuang.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 不能直接使用 @Autowired，需要 Spring 容器
     */
//    public static void main(String[] args) throws IOException {
//        new ContentService().parseContent("java");
//    }

    // 1、解析数据放入 es 索引中
    public Boolean parseContent(String keywords) throws IOException {
        List<String> strings = new HtmlParseUtil().parseJD(keywords);

        // 把查询出的数据放入 es 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < strings.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(strings.get(i)), XContentType.JSON));
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
}
