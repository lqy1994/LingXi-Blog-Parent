package cn.edu.sdu.wh.lqy.lingxi.blog.controller.web;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.CommentBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Meta;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.BrowseSearch;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.browse.RangeValueBlock;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.ArticleDTO;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.search.ServiceMultiResult;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.search.ServiceResult;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.*;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文章
 */
@Controller
@RequestMapping("/lingxi/")
public class ArticleController extends BaseController {

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IArticleService articleService;
    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IMetaService metaService;
    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IArticleMateService articleMateService;
    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ISearchService searchService;
    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ICommentService commentService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 文章详情页
     *
     * @param request 请求
     * @param id      文章主键
     * @return
     */
    @GetMapping(value = {"article/{id}", "article/{id}.html"})
    public String getArticle(Model model, @PathVariable String id, HttpServletRequest request) {
        Article article = articleService.getArticle(id);
        if (null == article || "draft".equals(article.getStatus())) {
            return this.render_404();
        }
        model.addAttribute("article", article);
        model.addAttribute("is_post", true);
        completeArticle(model, request, article);
        updateArticleHit(article.getId(), article.getHits());
        return RestPageConst.ART_POST;
    }

    /**
     * 文章页(预览)
     *
     * @param request 请求
     * @param id      文章主键
     * @return
     */
    @GetMapping(value = {"article/{id}/preview", "article/{id}.html"})
    public String articlePreview(Model model, HttpServletRequest request, @PathVariable String id) {
        Article contents = articleService.getArticle(id);
        if (null == contents) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(model, request, contents);
        updateArticleHit(contents.getId(), contents.getHits());
        return RestPageConst.ART_POST;
    }

    /**
     * 抽取公共方法
     *
     * @param request
     * @param contents
     */
    private void completeArticle(Model model, HttpServletRequest request, Article contents) {
        if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getId(), Integer.parseInt(cp), 6);
            model.addAttribute("comments", commentsPaginator);
        }
    }

    /**
     * 更新文章的点击率
     *
     * @param articleId
     * @param dbHits    数据库中存储的点击数
     */
    private void updateArticleHit(Integer articleId, Integer dbHits) {

        if (dbHits == null) {
            dbHits = 0;
        }

        String hitStr = valueOperations().get("article" + ":" + "hits");
        Integer hits = 0;
        if (StringUtils.isNotNull(hitStr)) {
            hits = Integer.valueOf(hitStr);
        }

        hits++;

        if (hits >= WebConstant.HIT_EXCEED) {
            Article article = new Article();
            article.setId(articleId);
            article.setHits(dbHits + hits);
            articleService.updateArticleById(article);
            valueOperations().set("article" + ":" + "hits", 1 + "");
        } else {
            valueOperations().set("article" + ":" + "hits", hits + "");
        }
    }


    @GetMapping("/browser")
    public String articleBrowser(Model model, @ModelAttribute BrowseSearch searchBody) {
        ServiceMultiResult<ArticleDTO> serviceMultiResult = articleService.query(searchBody);
        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("articles", serviceMultiResult.getResult());

        List<Meta> metas = metaService.getMetas(TypeEnum.CATEGORY.getType());
        model.addAttribute("categories", metas);

        model.addAttribute("searchBody", searchBody);
        model.addAttribute("hitsBlocks", RangeValueBlock.HITS_BLOCK);
        model.addAttribute("wordCntBlocks", RangeValueBlock.WORDCNT_BLOCK);
        model.addAttribute("currentHitsBlock", RangeValueBlock.matchHits(searchBody.getHitsBlock()));
        model.addAttribute("currentWordCntBlock", RangeValueBlock.matchWordCnt(searchBody.getWordCntBlock()));
        return RestPageConst.ARTICLE_BROWSER;
    }

    /**
     * 自动补全接口
     */
    @GetMapping("article/autocomplete")
    @ResponseBody
    public ApiResponse autoComplete(@RequestParam(value = "prefix") String prefix) {
        if (prefix.isEmpty()) {
            return ApiResponse.fail(400, "Bad Request");
        }
        ServiceResult<List<String>> result = searchService.suggest(prefix);
        return ApiResponse.ofSuccess(result.getResult());
    }

    public ValueOperations<String, String> valueOperations() {
        return redisTemplate.opsForValue();
    }
}
