package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.constant.RestPageConst;
import cn.edu.sdu.wh.lqy.lingxi.blog.constant.WebConstant;
import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.exception.LingXiException;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Bo.ApiResponse;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.dto.LogActions;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.ILogService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IUserService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.RSAUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * 用户后台登录/登出
 */
@Controller
@Transactional(rollbackFor = LingXiException.class)
public class AdminLoginController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminLoginController.class);

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IUserService userService;

    //    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private ILogService logService;
    //
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/login")
    public String login(Model model) {

        Map<String, Object> publicKeyMap = RSAUtils.getPublicKeyMap();
        model.addAttribute("publicKeyMap", publicKeyMap);

        return RestPageConst.ADMIN_LOGIN;
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiResponse login(HttpServletRequest request, @RequestParam("userName") String userName,
                             @RequestParam("password") String password, @RequestParam("code") String code,
                             @RequestParam("rememberMe") Boolean rememberMe) {

        if (org.apache.commons.lang.StringUtils.isEmpty(code) || "null".equalsIgnoreCase(code.trim())) {
            return ApiResponse.fail(102, "验证码错误");
        }

        Session session = super.getSession();
        String sessionCode = (String) session.getAttribute("_code");
        if (!code.toLowerCase().equals(sessionCode)) {
            return ApiResponse.fail(102, "验证码错误");
        }
        LOGGER.info("GifCode: " + sessionCode);

        String realPassword = RSAUtils.decryptStringByJs(password);
        LOGGER.info("realPassword:{}", realPassword);

        UsernamePasswordToken token = new UsernamePasswordToken(userName, realPassword);
        token.setRememberMe(rememberMe);

        Subject currentUser = SecurityUtils.getSubject();

        try {
            currentUser.login(token);
            User user = userService.getUserByName(userName);
            session.setAttribute(WebConstant.LOGIN_SESSION_KEY, user);

            logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());

            return ApiResponse.ok(200);
        } catch (UnknownAccountException e) {
            return  ApiResponse.fail(103,"未知账户");
        } catch (DisabledAccountException e) {
            return  ApiResponse.fail(104, "账户冻结，请联系管理员");

        }

    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("/admin/logout")
    public void logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.removeAttribute(WebConstant.LOGIN_SESSION_KEY);
        SecurityUtils.getSubject().logout();
        Cookie cookie = new Cookie(WebConstant.USER_IN_COOKIE, "");
        cookie.setValue(null);
        cookie.setMaxAge(0);// 立即销毁cookie
        cookie.setPath("/");
        response.addCookie(cookie);
        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }

}
