package com.cqupt.community.dao.elasticsearch;

import com.cqupt.community.entity.DiscussPost;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated
public interface DiscussPostRepository extends org.springframework.data.elasticsearch.repository.ElasticsearchRepository<DiscussPost, Integer> {
}
