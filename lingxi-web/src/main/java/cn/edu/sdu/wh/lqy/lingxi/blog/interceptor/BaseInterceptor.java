package cn.edu.sdu.wh.lqy.lingxi.blog.interceptor;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.Option;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IOptionService;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IUserService;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.AdminCommons;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.Commons;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.IPKit;
import com.alibaba.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 */
@Component
public class BaseInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGE = LoggerFactory.getLogger(BaseInterceptor.class);
    private static final String USER_AGENT = "user-agent";

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IUserService userService;

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}", url = "dubbo://localhost:20880")
    @Autowired
    private IOptionService optionService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Commons commons;

    @Resource
    private AdminCommons adminCommons;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();

        LOGGE.info("UserAgent: {}", request.getHeader(USER_AGENT));
        LOGGE.info("用户访问地址: {}, 来源地址: {}", uri, IPKit.getIpAddrByRequest(request));

        //请求拦截处理
//        User user = TaleUtils.getLoginUser(request);
//        if (user == null) {
//            Integer uid = TaleUtils.getCookieUid(request);
//            if (uid != null) {
//                user = userService.queryUserById(uid);
//                request.getSession().setAttribute(WebConstant.LOGIN_SESSION_KEY, user);
//            }
//        }
//        if (uri.startsWith("/admin") && !uri.startsWith("/admin/login") && null == user) {
//            response.sendRedirect(request.getContextPath() + "/admin/login");
//            return false;
//        }
        //设置get请求的token
//        if (request.getMethod().equals("GET")) {
//            String csrf_token = UUID.UU64();
//            // 默认存储30分钟
//            stringRedisTemplate.opsForValue().set(TypeEnum.CSRF_TOKEN.getType() + ":" + csrf_token,
//                    uri, 30 * 60, TimeUnit.SECONDS);
//            request.setAttribute("_csrf_token", csrf_token);
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        Option ov = optionService.getOptionByName("site_record");
        httpServletRequest.setAttribute("commons", commons);//一些工具类和公共方法
        httpServletRequest.setAttribute("option", ov);
        httpServletRequest.setAttribute("adminCommons", adminCommons);
    }

//    @Override
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//    }
}
