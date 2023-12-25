//package com.nowcoder.community.dao.elasticsearch;
//
//import com.nowcoder.community.entity.DiscussPost;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.elasticsearch.annotations.Highlight;
//import org.springframework.data.elasticsearch.annotations.HighlightField;
//import org.springframework.data.elasticsearch.annotations.HighlightParameters;
//import org.springframework.data.elasticsearch.core.SearchHit;
//import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
//
//    @Highlight(
//            fields = {
//                    @HighlightField(name = "title"),
//                    @HighlightField(name="content")
//            }
////            parameters = @HighlightParameters(
////                    preTags = "<strong><font style='color:red'>",
////                    postTags = "</font></strong>",
////                    fragmentSize = 500,
////                    numberOfFragments = 3
////            )
//    )
//    List<SearchHit<DiscussPost>> findByTitleOrContent(String title,String content,Pageable pageable);
////    SearchHit<DiscussPost> findByTitleOrContent(String title, String content, Pageable pageable);
//
//}
//
//
