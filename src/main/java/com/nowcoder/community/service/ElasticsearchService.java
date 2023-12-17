package com.nowcoder.community.service;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;

    public void saveDiscussPost(DiscussPost post){
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        Sort sort = Sort.by("type").descending()
                .and(Sort.by("score").descending())
                .and(Sort.by("createTime").descending());
        Pageable pageable= PageRequest.of(current,limit,sort);
        List<SearchHit<DiscussPost>> searchHits=discussPostRepository.findByTitleOrContent(keyword,keyword,pageable);

//        遍历返回的内容进行处理
//        List<DiscussPost> posts = new ArrayList<>();
//        for(SearchHit<DiscussPost> searchHit:searchHits){
//            //高亮的内容
//            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
//            //将高亮的内容填充到content中
//            searchHit.getContent().setTitle(highlightFields.get("title")==null ? searchHit.getContent().getTitle():highlightFields.get("title").get(0));
//            searchHit.getContent().setContent(highlightFields.get("content")==null ? searchHit.getContent().getContent():highlightFields.get("content").get(0));
//            //放到实体类中
//            posts.add(searchHit.getContent());
//        }
        List<DiscussPost> posts = new ArrayList<>();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPost discussPost = searchHit.getContent();
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();

            // Fill the title and content with highlighted values if available
            if (highlightFields.containsKey("title")) {
                discussPost.setTitle(highlightFields.get("title").get(0));
            }
            if (highlightFields.containsKey("content")) {
                discussPost.setContent(highlightFields.get("content").get(0));
            }

            posts.add(discussPost);
        }
//        Page<DiscussPost> page = new PageImpl<>(posts, pageable, searchHits.getTotalHits());
//        return page;
        System.out.println(searchHits.size());
        Page<DiscussPost> page = new PageImpl<>(posts, pageable, searchHits.size());
        return page;
    }
}
