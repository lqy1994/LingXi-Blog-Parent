package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.exception.LingXiException;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.StatisticsBo;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.*;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.LogActions;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ILogService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ISiteService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IUserService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.GsonUtils;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.PasswordUtils;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 后台管理首页
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = LingXiException.class)
public class AdminIndexController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminIndexController.class);

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ISiteService siteService;

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ILogService logService;

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IUserService userService;

    /**
     * 页面跳转
     *
     * @return
     */
    @GetMapping(value = {"", "/", "/index"})
    public String index(Model model) {
        LOGGER.info("Enter admin index method");
        List<Comment> comments = siteService.recentComments(5);
        List<Article> articles = siteService.recentContents(5);
        StatisticsBo statistics = siteService.getStatistics();
        // 取最新的20条日志
        List<Log> logs = logService.getLogs(1, 5);

        model.addAttribute("comments", comments);
        model.addAttribute("articles", articles);
        model.addAttribute("statistics", statistics);
        model.addAttribute("logs", logs);
        LOGGER.info("Exit admin index method");
        return RestPageConst.ADMIN_INDEX;
    }

    /**
     * 个人设置页面
     */
    @GetMapping(value = "profile")
    public String profile() {
        return RestPageConst.ADMIN_PROFILE;
    }


    /**
     * 保存个人信息
     */
    @PostMapping(value = "/profile")
    @ResponseBody
    public ApiResponse saveProfile(@RequestParam String screenName, @RequestParam String email, HttpServletRequest request, HttpSession session) {

        if (StringUtils.isNotBlank(screenName) && StringUtils.isNotBlank(email)) {
            User temp = new User();
            temp.setUid(this.getUid(request));
            temp.setScreenName(screenName);
            temp.setEmail(email);
            userService.updateByUid(temp);
            logService.insertLog(LogActions.UP_INFO.getAction(), GsonUtils.toJsonString(temp), request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            User original = (User) session.getAttribute(WebConstant.LOGIN_SESSION_KEY);
            original.setScreenName(screenName);
            original.setEmail(email);
            session.setAttribute(WebConstant.LOGIN_SESSION_KEY, original);
        }
        return ApiResponse.ok();
    }

    /**
     * 修改密码
     */
    @PostMapping(value = "/password")
    @ResponseBody
    public ApiResponse upPwd(@RequestParam String oldPassword, @RequestParam String password, HttpServletRequest request, HttpSession session) {
        User user = TaleUtils.getLoginUser(request);
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            return ApiResponse.fail("请确认信息输入完整");
        }

        if (!user.getPassword().equals(TaleUtils.MD5encode(user.getUsername() + oldPassword))) {
            return ApiResponse.fail("旧密码错误");
        }
        if (password.length() < 6 || password.length() > 14) {
            return ApiResponse.fail("请输入6-14位密码");
        }

        try {
            User tempUser = new User();
            tempUser.setUid(user.getUid());
            String encodePwd = PasswordUtils.getMd5(user.getPassword(), user.getUsername(), user.getSalt());
//            String pwd = TaleUtils.MD5encode(user.getUsername() + password);
            tempUser.setPassword(encodePwd);
            userService.updateByUid(tempUser);
            logService.insertLog(LogActions.UP_PWD.getAction(), null, request.getRemoteAddr(), this.getUid(request));

            //更新session中的数据
            User original = (User) session.getAttribute(WebConstant.LOGIN_SESSION_KEY);
            original.setPassword(encodePwd);
            session.setAttribute(WebConstant.LOGIN_SESSION_KEY, original);
            return ApiResponse.ok();
        } catch (Exception e) {
            String msg = "密码修改失败";
            if (e instanceof LingXiException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return ApiResponse.fail(msg);
        }
    }
}
