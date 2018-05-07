package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.MetaDto;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IMetaService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("admin/category")
public class AdminCategoryController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCategoryController.class);

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IMetaService metasService;

    @GetMapping(value = "")
    public String index(HttpServletRequest request) {
        List<MetaDto> categories = metasService.getMetaList(TypeEnum.CATEGORY.getType(), null, WebConstant.MAX_POSTS);
        List<MetaDto> tags = metasService.getMetaList(TypeEnum.TAG.getType(), null, WebConstant.MAX_POSTS);
        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        return RestPageConst.ADMIN_CATEGORY;
    }

    @PostMapping(value = "save")
    @ResponseBody
    public ApiResponse saveCategory(@RequestParam String cname, @RequestParam Integer mid) {
        try {
            metasService.saveMeta(TypeEnum.CATEGORY.getType(), cname, mid);
        } catch (Exception e) {
            String msg = "分类保存失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ApiResponse delete(@RequestParam int mid) {
        try {
            metasService.delete(mid);
        } catch (Exception e) {
            String msg = "删除失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

}
