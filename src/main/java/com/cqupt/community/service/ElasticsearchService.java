package com.cqupt.community.service;

import com.alibaba.fastjson.JSON;
import com.cqupt.community.aspect.ServiceLogAspect;
import com.cqupt.community.entity.DiscussPost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticsearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

    public void saveDiscussPost(DiscussPost post) throws IOException {
        IndexRequest request = new IndexRequest("discusspost");
        request.id(String.valueOf(post.getId()));
        request.source(JSON.toJSONString(post), XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        logger.info("插入状态：" + index.status());
    }

    public void deleteDiscussPost(int id) throws IOException {
        DeleteRequest deleteIndexRequest = new DeleteRequest("discusspost", String.valueOf(id));
        DeleteResponse delete = restHighLevelClient.delete(deleteIndexRequest, RequestOptions.DEFAULT);
        logger.info("删除状态：" + delete.status());
    }

    public boolean isExistDiscussPost(int id) throws IOException {
        GetRequest getRequest = new GetRequest("discusspost", String.valueOf(id));
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        return exists;
    }

    public List<DiscussPost> searchDiscussPost(String keyword, int offset, int limit) throws IOException, ParseException {
        List<DiscussPost> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("discusspost");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(offset);
        sourceBuilder.size(limit);
        MultiMatchQueryBuilder termQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "content");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

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
            DiscussPost post = new DiscussPost();

            Map<String, Object> tempPost = hit.getSourceAsMap();
            post.setId((Integer) tempPost.get("id"));
            post.setContent((String) tempPost.get("content"));
            post.setTitle((String) tempPost.get("title"));
            post.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse((String)(tempPost.get("createTime"))));
            post.setUserId((Integer) tempPost.get("userId"));
            post.setStatus((Integer) tempPost.get("status"));
            post.setCommentCount((Integer) tempPost.get("commentCount"));
            post.setScore((Double) tempPost.get("score"));
            post.setType((Integer) tempPost.get("type"));
            list.add(post);
        }

        return list;
    }

}
