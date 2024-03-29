package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String GetSettingPage() {
        return "site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));

        //限制大小，只能为500kb以下图片
        long fileSizeInBytes = headerImage.getSize();
        long fileSizeInKB = fileSizeInBytes / 1024; // 转换为KB
        if (fileSizeInKB >= 500) {
            model.addAttribute("errorInfo", "请上传小于500KB图片！");
            return "site/setting";
        }
        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }
        //更新当前用户头像的路径（web访问路径）
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //服务器存放路径
        filename = uploadPath + "/" + filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(filename);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {

        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            return "site/setting";
        }

        //判断旧密码是否正确
        String origalPassword = hostHolder.getUser().getPassword();
        String salt = hostHolder.getUser().getSalt();
        String password = CommunityUtil.md5(oldPassword + salt);
        if (!password.equals(origalPassword)) {
            model.addAttribute("oldPasswordMsg", "原密码不正确！");
            return "site/setting";
        }
        //限制新密码长度至少为8位
        String message = FieldUtil.fieldCheck(newPassword);
        if (message != null) {
            model.addAttribute("newPasswordMsg", message);
            return "site/setting";
        }
        //判断新的两次密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordMsg", "两次输入的密码不一致");
            return "site/setting";
        }

        int id = hostHolder.getUser().getId();
        userService.updatePassword(id, CommunityUtil.md5(newPassword + salt));

        return "redirect:/index";
    }

    @ResponseBody
    @RequestMapping(path = "/code", method = RequestMethod.POST)
    public void getCode(String email) {
        userService.code(email);
    }

    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    public String forgetPassword(String email, String code, String password, Model model) {
        Map<String, Object> map = userService.forget(email, code, password);
        System.out.println(map);

        if (map.size() == 0) {
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("codeMsg", map.get("codeMsg"));
            return "site/forget";
        }
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        //用户
        model.addAttribute("user", user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "site/profile";
    }

    //帖子
    @RequestMapping(path = "/post/{userId}", method = RequestMethod.GET)
    public String getPost(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        //帖子总数
        int discussPostRows = discussPostService.findDiscussPostRows(userId);
        model.addAttribute("discussPostRows", discussPostRows);

        //分页信息
        page.setLimit(5);
        page.setPath("/user/post/" + userId);
        page.setRows(discussPostRows);

        //帖子
        List<DiscussPost> list = discussPostService.findDiscussPosts(
                userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);
        return "site/my-post";
    }

    //回复
    @RequestMapping(path = "/reply/{userId}", method = RequestMethod.GET)
    public String getReply(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        //帖子总数
        int discussPostRows = commentService.findCommentCounts(1, userId);
        model.addAttribute("discussPostRows", discussPostRows);

        //分页信息
        page.setLimit(5);
        page.setPath("/user/reply/" + userId);
        page.setRows(discussPostRows);

        //帖子
        List<Comment> list = commentService.findCommentByUserId(
                1, userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (Comment comment : list) {
                int postId = comment.getEntityId();
                Map<String, Object> map = new HashMap<>();
                DiscussPost post = discussPostService.findDiscussPostById(postId);
                map.put("post", post);

                discussPosts.add(map);

            }

        }

        model.addAttribute("discussPosts", discussPosts);
        return "site/my-reply";
    }

}
