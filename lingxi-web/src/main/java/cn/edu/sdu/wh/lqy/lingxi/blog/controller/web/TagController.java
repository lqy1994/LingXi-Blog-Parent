package cn.edu.sdu.wh.lqy.lingxi.blog.controller.web;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Article;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.MetaDto;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IMetaService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 标签
 */
@Controller
@RequestMapping("/lingxi/")
public class TagController extends BaseController {

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IMetaService metaService;
//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IArticleService articleService;


    /**
     * 标签页
     *
     * @param name
     * @return
     */
    @GetMapping(value = "tag/{name}")
    public String tags(Model model, @PathVariable String name, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return tags(model, name, 1, limit);
    }

    /**
     * 标签分页
     *
     * @param model
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "tag/{name}/{page}")
    public String tags(Model model, @PathVariable String name, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {

        page = page < 0 || page > WebConstant.MAX_PAGE ? 1 : page;
//        对于空格的特殊处理
        name = name.replaceAll("\\+", " ");
        MetaDto metaDto = metaService.getMeta(TypeEnum.TAG.getType(), name);
        if (null == metaDto) {
            return RestPageConst.ERROR_404;
        }

        PageInfo<Article> articles = articleService.getArticles(metaDto.getMid(), page, limit);
        model.addAttribute("articles", articles);
        model.addAttribute("meta", metaDto);
        model.addAttribute("type", "标签");
        model.addAttribute("keyword", name);
        return RestPageConst.PAGE_CAT;
    }
}
