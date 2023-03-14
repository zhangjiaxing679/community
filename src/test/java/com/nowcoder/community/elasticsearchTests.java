package com.nowcoder.community;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.SourceConfigBuilders;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class elasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)).build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());
    // And create the API client
    ElasticsearchClient client = new ElasticsearchClient(transport);

    @Test
    public void select() throws IOException{
        //不带高亮的搜索
       SearchResponse<DiscussPost> searchs=client.search(s->s
                .index("discusspost")
               .size(100)
                .query(q->q
                    .term(t->t
                            .field("title")
                            .value(v->v.stringValue("互联网"))
                            .field("content")
                            .value(v->v.stringValue("互联网"))
                    )
                )
                .sort(sort->sort
                    .field(f->f
                                .field("type")
                                .order(SortOrder.Desc)
                                .field("score")
                                .order(SortOrder.Desc)
                                .field("createTime")
                                .order(SortOrder.Desc)))
               .highlight(h->h
                        .fields("title",f->f
                                    .preTags("<em color='red'>").postTags("</em>"))
                       .fields("content",f->f
                               .preTags("<em color='red'>").postTags("</em>"))
                    ), DiscussPost.class);
        System.out.println("查询结果如下");

        System.out.println(searchs.took());
        System.out.println(searchs.hits().total().value());
        searchs.hits().hits().forEach(e->
                System.out.println(e.source().toString()));
        }

    @Test
    public void highlightselect() throws IOException{
        //带高亮的搜索
        SearchResponse<DiscussPost> searchs=client.search(s->s
                .index("discusspost")
                .size(100)
                .query(q->q
                        .term(t->t
                                .field("title")
                                .value(v->v.stringValue("互联网"))
                                .field("content")
                                .value(v->v.stringValue("互联网"))
                        )
                )
                .sort(sort->sort
                        .field(f->f
                                .field("type")
                                .order(SortOrder.Desc)
                                .field("score")
                                .order(SortOrder.Desc)
                                .field("createTime")
                                .order(SortOrder.Desc)))
                .highlight(h->h
                        .fields("title",f->f
                                .preTags("<em color='red'>").postTags("</em>"))
                        .fields("content",f->f
                                .preTags("<em color='red'>").postTags("</em>"))
                ), DiscussPost.class);

        System.out.println("查询结果如下");

        System.out.println(searchs.took());
        System.out.println(searchs.hits().total().value());
        searchs.hits().hits().forEach(e->
                System.out.println(e.source().toString()));
    }
    @Test
    public void testget() throws IOException{
        //查询当前索引的全部文档
            SearchResponse<DiscussPost> searches = client.search(g -> g
                            .index("discusspost")
                    , DiscussPost.class);
            HitsMetadata<DiscussPost> hits = searches.hits();
            for (Hit<DiscussPost> hit : hits.hits()) {
                System.out.println(hit.source());
            }
        }

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(110);
        post.setType(2);
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete() {
//        discussPostRepository.deleteById(231);
        discussPostRepository.deleteAll();
    }

}
