package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Attach;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.LogActions;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.TypeEnum;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IAttachService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ILogService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.Commons;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件管理
 */
@Controller
@RequestMapping("admin/attach")
public class AdminAttachController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAttachController.class);

    public static final String CLASSPATH = TaleUtils.getUplodFilePath();

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IAttachService attachService;

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ILogService logService;

    /**
     * 附件页面
     *
     * @param request
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "")
    public String index(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "12") int limit) {
        PageInfo<Attach> attachPaginator = attachService.getAttaches(page, limit);
        request.setAttribute("attachs", attachPaginator);
        request.setAttribute(TypeEnum.ATTACH_URL.getType(), Commons.site_option(TypeEnum.ATTACH_URL.getType(), Commons.site_url()));
        request.setAttribute("max_file_size", WebConstant.MAX_FILE_SIZE / 1024);
        return RestPageConst.ADMIN_ATTACH;
    }

    /**
     * 上传文件接口
     *
     * @param request
     * @return
     */
    @PostMapping(value = "upload")
    @ResponseBody
    public ApiResponse upload(HttpServletRequest request, @RequestParam("file") MultipartFile[] multipartFiles) throws IOException {
        Integer uid = this.getUid(request);
        List<String> errorFiles = new ArrayList<>();
        try {
            for (MultipartFile multipartFile : multipartFiles) {
                String fname = multipartFile.getOriginalFilename();
                if (multipartFile.getSize() <= WebConstant.MAX_FILE_SIZE) {
                    String fkey = TaleUtils.getFileKey(fname);
                    String ftype = TaleUtils.isImage(multipartFile.getInputStream()) ? TypeEnum.IMAGE.getType() : TypeEnum.FILE.getType();
                    File file = new File(CLASSPATH + fkey);
                    try {
                        FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    attachService.save(fname, fkey, ftype, uid);
                } else {
                    errorFiles.add(fname);
                }
            }
        } catch (Exception e) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok(errorFiles);
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ApiResponse delete(@RequestParam Integer id, HttpServletRequest request) {
        try {
            Attach attach = attachService.selectById(id);
            if (null == attach) {
                return ApiResponse.fail("不存在该附件");
            }
            attachService.deleteById(id);
            new File(CLASSPATH + attach.getFkey()).delete();
            logService.insertLog(LogActions.DEL_ARTICLE.getAction(), attach.getFkey(), request.getRemoteAddr(), this.getUid(request));
        } catch (Exception e) {
            String msg = "附件删除失败";
            LOGGER.error(msg, e);
            return ApiResponse.fail(msg);
        }
        return ApiResponse.ok();
    }

}
