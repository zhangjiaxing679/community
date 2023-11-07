# 1.项目介绍

本项目主要是基于 Spring Boot + MyBatis + MySQL + Redis  + Kafka 实现的 Java 开发项目。
用于充当校内学生交流的论坛，包含普通用户注册、登录、发帖、评论、点赞、私信；管理员对发帖内容加精、删除、置顶；系统通知等功能。

# 2.项目功能介绍

1.用户模块：对登录用户颁发登录凭证，记录用户登录状态，使用 Interceptor 拦截器拦截需登录的请求，区分游客与登录者权限；使用 Spring Security 对不同角色用户权限进行管理
2.内容模块：使用前缀树算法过滤掉发帖内容的敏感词，使用 AOP 思想统一记录日志和处理异常；使用 Redis 作为缓存,存放登录凭证、验证码、点赞、关注等热点数据
3.通知模块：使用 Kafka 作为消息队列，在用户被点赞、评论、关注后放入异步队列，以系统通知的方式推送给用户
4.全局设置 ：日志管理、异常处理

# 3.目录

```
├─java                      
│  └─com
│      └─nowcoder
│          └─community               
│              ├─actuator                // 监听器
│              ├─annotation              // 自定义注解     
│              ├─aspect                  // 统一记录日志
│              ├─config                  // 配置类
│              ├─controller              // 控制层
│              │  ├─advice               // 异常处理
│              │  └─interceptor          // 拦截器
│              ├─dao                     // 持久化层
│              ├─entity                  // 实体类
│              ├─event                   // Kafka 自定义事件
│              ├─quartz                  // 任务调度
│              ├─service                 // 业务层
│              └─util                    // 常用工具类
└─resources                              //资源
    ├─mapper                             // MySQL 对应的 mapper 文件                                
    ├─static                             // 前端静态资源
    │  ├─css                            
    │  ├─img
    │  └─js
    └─templates                           // 前端模板文件
        ├─error                           // 错误页面
        ├─mail                            // 邮件模板
        └─site                            // 其它前端页面
            └─admin
```



# 4.框架版本

```
jdk 17
MySQL 8.0.26
Redis 3.0
Kafka 3.4.0
Spring Boot 3.0.1

```






