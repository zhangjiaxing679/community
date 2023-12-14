package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int Status;
    private Date expired;

//    public int getId() {
//        return id;
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
//    public String getTicket() {
//        return ticket;
//    }
//
//    public void setTicket(String ticket) {
//        this.ticket = ticket;
//    }
//
//    public int getStatus() {
//        return Status;
//    }
//
//    public void setStatus(int status) {
//        Status = status;
//    }
//
//    public Date getExpired() {
//        return expired;
//    }
//
//    public void setExpired(Date expired) {
//        this.expired = expired;
//    }
//
//    @Override
//    public String toString() {
//        return "LoginTicket{" +
//                "id=" + id +
//                ", userId=" + userId +
//                ", ticket='" + ticket + '\'' +
//                ", Status=" + Status +
//                ", expired=" + expired +
//                '}';
//    }
}
