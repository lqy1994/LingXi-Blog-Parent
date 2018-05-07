package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Comment;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.CommentVoExample;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ICommentService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin/comments")
public class AdminCommentController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCommentController.class);

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ICommentService commentService;

    @GetMapping(value = "")
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit, HttpServletRequest request) {
        User users = this.user(request);
        CommentVoExample commentVoExample = new CommentVoExample();
        commentVoExample.setOrderByClause("coid desc");
        commentVoExample.createCriteria().andAuthorIdNotEqualTo(users.getUid());
        PageInfo<Comment> commentsPaginator = commentService.getCommentsWithPage(commentVoExample, page, limit);
        request.setAttribute("comments", commentsPaginator);
        return RestPageConst.ADMIN_COMMENT_LIST;
    }

    /**
     * 删除一条评论
     *
     * @param coid
     * @return
     */
    @PostMapping(value = "delete")
    @ResponseBody
    public ApiResponse delete(@RequestParam Integer coid) {
        try {
            Comment comments = commentService.getCommentById(coid);
            if (null == comments) {
                return ApiResponse.fail("不存在该评论");
            }
            commentService.delete(coid, comments.getArtId());
        } catch (Exception e) {
            String msg = "评论删除失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

    @PostMapping(value = "status")
    @ResponseBody
    public ApiResponse delete(@RequestParam Integer coid, @RequestParam String status) {
        try {
            Comment comments = commentService.getCommentById(coid);
            if (comments != null) {
                comments.setCoid(coid);
                comments.setStatus(status);
                commentService.update(comments);
            } else {
                return ApiResponse.fail("操作失败");
            }
        } catch (Exception e) {
            String msg = "操作失败";
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

}
