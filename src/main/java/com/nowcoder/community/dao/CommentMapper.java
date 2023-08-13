package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

    int selectCountByUserId(int entityType, int userId);

    List<Comment> selectCommentByUserId(int entityType, int userId,int offset,int limit);

    List<Comment> selectCommentsByUserId(int entityType, int userId, int offset, int limit);
}
