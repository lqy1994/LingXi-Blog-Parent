package cn.edu.sdu.wh.lqy.lingxi.blog.config;

import cn.edu.sdu.wh.lqy.lingxi.blog.BaseApplicationTests;
import cn.edu.sdu.wh.lqy.lingxi.blog.mapper.ArticleMapper;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.gson.Gson;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConfigTest extends BaseApplicationTests {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private TransportClient elasticSearchClient;

    @Test
    public void testSearch() {
        Article article = articleMapper.selectByPrimaryKey(18);
        System.out.println(gson.toJson(article));
    }

    @Test
    public void testEsClient() {
        SearchRequestBuilder requestBuilder = elasticSearchClient.prepareSearch().setQuery(QueryBuilders.matchAllQuery());

        System.out.println(requestBuilder);

        SearchResponse searchResponse = requestBuilder.get();

        System.out.println(searchResponse.toString());
    }

}
