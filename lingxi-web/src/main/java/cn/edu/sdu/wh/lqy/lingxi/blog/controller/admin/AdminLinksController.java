package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Meta;
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
@RequestMapping("admin/links")
public class AdminLinksController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminLinksController.class);

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IMetaService metaService;

    @GetMapping(value = "")
    public String index(HttpServletRequest request) {
        List<Meta> metas = metaService.getMetas(TypeEnum.LINK.getType());
        request.setAttribute("links", metas);
        return RestPageConst.ADMIN_LINKS;
    }

    @PostMapping(value = "save")
    @ResponseBody
    public ApiResponse saveLink(@RequestParam String title, @RequestParam String url,
                                @RequestParam String logo, @RequestParam Integer mid,
                                @RequestParam(value = "sort", defaultValue = "0") int sort) {
        try {
            Meta metas = new Meta();
            metas.setName(title);
            metas.setThumbnail(url);
            metas.setDescription(logo);
            metas.setSort(sort);
            metas.setType(TypeEnum.LINK.getType());
            if (null != mid) {
                metas.setMid(mid);
                metaService.update(metas);
            } else {
                metaService.saveMeta(metas);
            }
        } catch (Exception e) {
            String msg = "友链保存失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ApiResponse delete(@RequestParam int mid) {
        try {
            metaService.delete(mid);
        } catch (Exception e) {
            String msg = "友链删除失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

}
