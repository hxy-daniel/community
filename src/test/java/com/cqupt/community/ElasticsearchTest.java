package com.cqupt.community;

import com.alibaba.fastjson.JSON;
import com.cqupt.community.dao.DiscussPostDao;
import com.cqupt.community.entity.DiscussPost;
import com.cqupt.community.service.DiscussPostService;
import com.cqupt.community.service.ElasticsearchService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ElasticsearchTest {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchRepository elasticsearchRepository;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private DiscussPostDao discussPostDao;

    @Test
    public void testAdd() throws IOException {
        /**
         * 向ES中的索引christy下的type类型中添加一天文档
         */
//        IndexRequest indexRequest = new IndexRequest("christy","user","11");
//        indexRequest.source("{\"name\":\"齐天大圣孙悟空\",\"age\":685,\"bir\":\"1685-01-01\",\"introduce\":\"花果山水帘洞美猴王齐天大圣孙悟空是也！\"," +
//                "\"address\":\"花果山\"}", XContentType.JSON);
//        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
//        System.out.println(indexResponse.status());
        DiscussPost discussPost = discussPostService.selectDiscussPostById(109);
        elasticsearchRepository.save(discussPost);
    }

    @Test
    public void testInsertList() throws IOException {
//        elasticsearchRepository.saveAll(discussPostDao.getDiscussPosts(101, 0, 100));
        List<DiscussPost> discussPosts = discussPostDao.getDiscussPosts(101, 0, 100, 0);
        BulkRequest bulkRequest = new BulkRequest();
        for (DiscussPost post : discussPosts) {
            bulkRequest.add(new IndexRequest("discusspost").source(JSON.toJSONString(post), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    @Test
    public void searchPage() throws IOException {

        SearchRequest searchRequest = new SearchRequest("discusspost");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(1);
        sourceBuilder.size(5);
        MultiMatchQueryBuilder termQueryBuilder = QueryBuilders.multiMatchQuery("因特网寒冬", "title", "content");
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title","因特网寒冬");
        sourceBuilder.query(termQueryBuilder);
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").field("content");
        //多个高亮显示设置为true
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style = 'color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        List<Map<String, Object>> maps = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            //原来的结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮字段 ,将原来的字段换成高亮字段显示
            if(title!=null){
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text fragment : fragments) {
                    newTitle+=fragment;
                }
                //高亮字段替换原来内容
                sourceAsMap.put("title",newTitle);
            }

            HighlightField content = highlightFields.get("content");
            //解析高亮字段 ,将原来的字段换成高亮字段显示
            if(content!=null){
                Text[] fragments = content.fragments();
                String newContent = "";
                for (Text fragment : fragments) {
                    newContent+=fragment;
                }
                //高亮字段替换原来内容
                sourceAsMap.put("content",newContent);
            }

            maps.add(hit.getSourceAsMap());
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Autowired
    private ElasticsearchService elasticsearchService;
    @Test
    public void test() throws IOException, ParseException {
        System.out.println(elasticsearchService.isExistDiscussPost(109));
        List<DiscussPost> discussPosts = elasticsearchService.searchDiscussPost("互联网寒冬", 1, 5);
        for (DiscussPost post : discussPosts) {
            System.out.println(post);
        }
    }

    @Test
    public void testExist() throws IOException {
        System.out.println(elasticsearchService.isExistDiscussPost(282));
    }

    @Test
    public void testDel() throws IOException {
        elasticsearchService.deleteDiscussPost(281);
    }

}
