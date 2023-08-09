package com.nowcoder.community;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class elasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(273));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(227));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(246));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100,0));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("新人不灌水");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteById(231);
//        discussPostRepository.deleteAll();
    }

    @Test
    public void testSelect() {
        //实现查询，查询＋按照指定字段排序
        //分页展示
        //匹配到的内容有标签高亮展示
        //高亮代码重写

//        Query query = new StringQuery("{ \"bool\": { \"should\": [ " +
//        "{ \"match\": { \"title\": \"哈哈\" } }, " +
//        "{ \"match\": { \"content\": \"哈哈\" } } ] } }"
//        );
//        List<SearchHit<DiscussPost>> searchHits=discussPostRepository.findByTitleOrContent("互联网寒冬","互联网寒冬",pageable);
        Sort sort = Sort.by("type").descending()
                .and(Sort.by("score").descending())
                .and(Sort.by("createTime").descending());
        Pageable pageable = PageRequest.of(0, 10, sort);
        List<SearchHit<DiscussPost>> searchHits = discussPostRepository.findByTitleOrContent("互联网寒冬", "互联网寒冬", pageable);


// Process the searchHits content and handle highlighting
        List<DiscussPost> posts = new ArrayList<>();
        for (SearchHit<DiscussPost>searchHit : searchHits) {
            DiscussPost discussPost = searchHit.getContent();
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();

            // Update title and content with highlighted values if available
            if (highlightFields.containsKey("title")) {
                discussPost.setTitle(highlightFields.get("title").get(0));
            }
            if (highlightFields.containsKey("content")) {
                discussPost.setContent(highlightFields.get("content").get(0));
            }

            posts.add(discussPost);
        }

// Create a PageImpl from the processed posts and the total number of elements
        Page<DiscussPost> page = new PageImpl<>(posts, pageable, searchHits.size());

// Now you can work with the page object, such as accessing its content, page information, etc.
        System.out.println("Page content: " + page.getContent());
        System.out.println("Total elements: " + page.getTotalElements());
        System.out.println("Total pages: " + page.getTotalPages());
        System.out.println("Current page: " + page.getNumber());
        System.out.println("Page size: " + page.getSize());

        for (DiscussPost post : page.getContent()) {
            System.out.println(post);
        }

//            //高亮的内容
//            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
//            //将高亮的内容填充到content中
//            searchHit.getContent().setTitle(highlightFields.get("title")==null ? searchHit.getContent().getTitle():highlightFields.get("title").get(0));
//            searchHit.getContent().setContent(highlightFields.get("content")==null ? searchHit.getContent().getContent():highlightFields.get("content").get(0));
            //放到实体类中
//            posts.add(searchHit.getContent());
    }

}
