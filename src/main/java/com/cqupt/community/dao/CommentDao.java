package com.cqupt.community.dao;

import com.cqupt.community.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface CommentDao {

    @Select({"select id, user_id, entity_type, entity_id, target_id, content, status, create_time ",
            "from comment where entity_type = #{entityType} and entity_id = #{entityId} and status = 0 order by create_time asc " +
                    "limit #{offset}, #{limit}"})
    public List<Comment> selectComment(int entityType, int entityId, int offset, int limit);

    @Select({
            "select count(id) from comment where entity_type = #{entityType} and entity_id = #{entityId} and status = 0"
    })
    public int getCommentCount(int entityType, int entityId);

    @Insert({
            "insert into comment(user_id, entity_type, entity_id, target_id, content, status, create_time) ",
            "values(#{userId}, #{entityType}, #{entityId}, #{targetId}, #{content}, #{status}, #{createTime})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int addComment(Comment comment);
}
