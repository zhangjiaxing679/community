package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(Model model, @PathVariable("discussPostId") int discussPostId,
                             Comment comment) {
        if (comment == null) {
            model.addAttribute("error", "评论内容不能为空");
            return "site:/discuss-detail";
        }

        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //当评论者与原帖者不为同一个人时，触发评论事件
        if (user.getId() != discussPostService.findDiscussPostById(discussPostId).getUserId()) {
            Event event = new Event()
                    .setTopic(TOPIC_COMMENT)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setData("postId", discussPostId);

            if (comment.getEntityType() == ENTITY_TYPE_POST) {
                DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
                Comment target = commentService.findCommentById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
            eventProducer.fireEvent(event);

            if (comment.getEntityType() == ENTITY_TYPE_POST) {
                //触发发帖事件
                event = new Event()
                        .setTopic(TOPIC_PUBLISH)
                        .setUserId(comment.getUserId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(discussPostId);
                eventProducer.fireEvent(event);
            }
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }
        return "redirect:/discuss/detail/" + discussPostId;

    }
}
