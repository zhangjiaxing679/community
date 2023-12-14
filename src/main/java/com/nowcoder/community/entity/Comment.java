package com.nowcoder.community.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;

//    @NotEmpty(message = "内容不能为空")
    private String content;
    private int status;
    private Date createTime;

//    public int getId() {
//        return id;
//    }
//
//    public int getEntityId() {
//        return entityId;
//    }
//
//    public void setEntityId(int entityId) {
//        this.entityId = entityId;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    public int getEntityType() {
//        return entityType;
//    }
//
//    public void setEntityType(int entityType) {
//        this.entityType = entityType;
//    }
//
//    public int getTargetId() {
//        return targetId;
//    }
//
//    public void setTargetId(int targetId) {
//        this.targetId = targetId;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
//
//    @Override
//    public String toString() {
//        return "Comment{" +
//                "id=" + id +
//                ", userId=" + userId +
//                ", entityType=" + entityType +
//                ", entityId=" + entityId +
//                ", targetId=" + targetId +
//                ", content='" + content + '\'' +
//                ", status=" + status +
//                ", createTime=" + createTime +
//                '}';
//    }
}
