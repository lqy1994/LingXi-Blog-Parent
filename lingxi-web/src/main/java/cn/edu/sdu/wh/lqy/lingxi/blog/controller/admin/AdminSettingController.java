package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.exception.LingXiException;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.BackResponseBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Option;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.LogActions;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ILogService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IOptionService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ISiteService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.GsonUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/setting")
public class AdminSettingController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSettingController.class);

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IOptionService optionService;

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ILogService logService;

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ISiteService siteService;

    /**
     * 系统设置
     */
    @GetMapping(value = "")
    public String setting(HttpServletRequest request) {
        List<Option> voList = optionService.getOptions();
        Map<String, String> options = new HashMap<>();
        voList.forEach((option) -> {
            options.put(option.getName(), option.getValue());
        });
        if (options.get("site_record") == null) {
            options.put("site_record", "");
        }
        request.setAttribute("options", options);
        return RestPageConst.ADMIN_SETTINGS;
    }

    /**
     * 保存系统设置
     */
    @PostMapping(value = "")
    @ResponseBody
    public ApiResponse saveSetting(@RequestParam(required = false) String site_theme, HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> querys = new HashMap<>();
            parameterMap.forEach((key, value) -> {
                querys.put(key, join(value));
            });
            optionService.saveOptions(querys);
            WebConstant.initConfig = querys;
            if (StringUtils.isNotBlank(site_theme)) {
                BaseController.THEME = "themes/" + site_theme;
            }
            logService.insertLog(LogActions.SYS_SETTING.getAction(), GsonUtils.toJsonString(querys), request.getRemoteAddr(), this.getUid(request));
            return ApiResponse.ok();
        } catch (Exception e) {
            String msg = "保存设置失败";
            return ApiResponse.fail(msg);
        }
    }


    /**
     * 系统备份
     *
     * @return
     */
    @PostMapping(value = "backup")
    @ResponseBody
    public ApiResponse backup(@RequestParam String bk_type, @RequestParam String bk_path,
                              HttpServletRequest request) {
        if (StringUtils.isBlank(bk_type)) {
            return ApiResponse.fail("请确认信息输入完整");
        }
        try {
            BackResponseBo backResponse = siteService.backup(bk_type, bk_path, "yyyyMMddHHmm");
            logService.insertLog(LogActions.SYS_BACKUP.getAction(), null, request.getRemoteAddr(), this.getUid(request));
            return ApiResponse.ok(backResponse);
        } catch (Exception e) {
            String msg = "备份失败";
            if (e instanceof LingXiException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return ApiResponse.fail(msg);
        }
    }


    /**
     * 数组转字符串
     *
     * @param arr
     * @return
     */
    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String item = arr[i];
            ret.append(',').append(item);
        }
        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }

}
