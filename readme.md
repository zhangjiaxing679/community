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
# 5.相关压测数据

## 5.1 服务器数据

| 服务器类型 | CPU&内存 | 操作系统 |
| --- | --- | --- |
| 阿里云服务器 | 2核(vCPU) 4 GiB | CentOS 7.9 64位 |

## 5.2性能测试结果:（以1min时长统计）

| url | 接口说明 | 并发量 | 平均响应时间 | 95%响应时间 | 99%响应时间 | 吞吐量 | CPU占用率 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| /index | 首页  | 12000 | 140ms | 177ms | 202ms | 210/sec | 62% |
|     |     | 15000 | 286ms | 380ms | 664ms | 240/sec | 70% |
| /index?orderMode=1 | 热门帖子 | 15000 | 155ms | 190ms | 228ms | 250/sec | 60% |
|     |     | 20000 | 165ms | 230ms | 258ms | 318/sec | 70% |





