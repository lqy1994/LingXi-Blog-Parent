package cn.edu.sdu.wh.lqy.lingxi.blog.controller.web;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Comment;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.ErrorCode;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ICommentService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.IPKit;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.PatternKit;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/lingxi/")
public class CommentController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ICommentService commentService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 评论操作
     */
    @PostMapping(value = "comment")
    @ResponseBody
    public ApiResponse comment(Model model, HttpServletRequest request, HttpServletResponse response,
                               @RequestParam Integer cid, @RequestParam Integer coid, @RequestParam String author,
                               @RequestParam String mail, @RequestParam String url, @RequestParam String text,
                               @RequestParam String _csrf_token) {

        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST);
        }

        String token = redisTemplate.opsForValue().get(TypeEnum.CSRF_TOKEN.getType() + ":" + _csrf_token);
        if (cn.edu.sdu.wh.lqy.lingxi.blog.utils.StringUtils.isNull(token)) {
            return ApiResponse.fail(ErrorCode.BAD_REQUEST);
        }

        if (null == cid || StringUtils.isBlank(text)) {
            return ApiResponse.fail("请输入完整后评论");
        }

        if (StringUtils.isNotBlank(author) && author.length() > 50) {
            return ApiResponse.fail("姓名过长");
        }

        if (StringUtils.isNotBlank(mail) && !TaleUtils.isEmail(mail)) {
            return ApiResponse.fail("请输入正确的邮箱格式");
        }

        if (StringUtils.isNotBlank(url) && !PatternKit.isURL(url)) {
            return ApiResponse.fail("请输入正确的URL格式");
        }

        if (text.length() > 200) {
            return ApiResponse.fail("请输入200个字符以内的评论");
        }

        String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
        String cntStr = redisTemplate.opsForValue().get(TypeEnum.COMMENTS_FREQUENCY.getType() + ":" + val);
        Integer count = 0;

        if (cn.edu.sdu.wh.lqy.lingxi.blog.utils.StringUtils.isNotNull(cntStr)) {
            count = Integer.valueOf(cntStr);
        }

        if (count > 0) {
            return ApiResponse.fail("您发表评论太快了，请过会再试");
        }

        author = TaleUtils.cleanXSS(author);
        text = TaleUtils.cleanXSS(text);

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        Comment comments = new Comment();
        comments.setAuthor(author);
        comments.setArtId(cid);
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(url);
        comments.setContent(text);
        comments.setMail(mail);
        comments.setParent(coid);
        try {
            String result = commentService.insertComment(comments);
            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLEncoder.encode(mail, "UTF-8"), 7 * 24 * 60 * 60, response);
            if (StringUtils.isNotBlank(url)) {
                cookie("tale_remember_url", URLEncoder.encode(url, "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            // 设置对每个文章1分钟可以评论一次
            redisTemplate.opsForValue().set(TypeEnum.COMMENTS_FREQUENCY.getType() + ":" + val, "1", 60, TimeUnit.SECONDS);
            if (!WebConstant.SUCCESS_RESULT.equals(result)) {
                return ApiResponse.fail(result);
            }
            return ApiResponse.ok();
        } catch (Exception e) {
            String msg = "评论发布失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
    }

}
