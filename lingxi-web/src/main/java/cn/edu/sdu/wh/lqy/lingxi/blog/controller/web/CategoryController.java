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


@Controller
@RequestMapping("/lingxi/")
public class CategoryController extends BaseController {

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IMetaService metaService;
    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IArticleService articleService;

    /**
     * 分类页
     *
     * @return
     */
    @GetMapping(value = "category/{keyword}")
    public String categories(Model model, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return categories(model, keyword, 1, limit);
    }

    @GetMapping(value = "category/{keyword}/{page}")
    public String categories(Model model, @PathVariable String keyword,
                             @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConstant.MAX_PAGE ? 1 : page;
        MetaDto metaDto = metaService.getMeta(TypeEnum.CATEGORY.getType(), keyword);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<Article> articlePage = articleService.getArticles(metaDto.getMid(), page, limit);

        model.addAttribute("articles", articlePage);
        model.addAttribute("meta", metaDto);
        model.addAttribute("type", "分类");
        model.addAttribute("keyword", keyword);
        return RestPageConst.PAGE_CAT;
    }
}
