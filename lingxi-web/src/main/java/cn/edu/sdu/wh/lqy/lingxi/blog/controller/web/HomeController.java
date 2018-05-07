package cn.edu.sdu.wh.lqy.lingxi.blog.controller.web;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 首页
 */
@Controller
@RequestMapping("/lingxi")
public class HomeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://127.0.0.1:20880")
    @Autowired
    private IArticleService articleService;

    /**
     * 首页
     *
     * @return
     */
    @GetMapping(value = {"", "/", "/index"})
    public String index(Model model, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return index(model, 1, limit);
    }

    /**
     * 首页分页
     *
     * @param model  model
     * @param offset 第几页
     * @param limit  每页大小
     * @return 主页
     */
    @GetMapping(value = "/page/{offset}")
    public String index(Model model, @PathVariable("offset") int offset, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        offset = offset < 0 || offset > WebConstant.MAX_PAGE ? 1 : offset;
        PageInfo<Article> articles = articleService.getArticles(offset, limit);
        model.addAttribute("articles", articles);
        if (offset > 1) {
            model.addAttribute("title", "第" + offset + "页");
        }
        return RestPageConst.INDEX;
    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session, response);
    }

}
