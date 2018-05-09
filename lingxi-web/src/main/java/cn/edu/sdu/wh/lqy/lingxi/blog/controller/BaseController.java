package cn.edu.sdu.wh.lqy.lingxi.blog.controller;

import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.AuthorizingUser;
import cn.edu.sdu.wh.lqy.lingxi.blog.model.Vo.User;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseController {

    public static String THEME = "themes/default";

//    protected MapCache cache = MapCache.single();

    /**
     * 主页的页面主题
     * @param viewName
     * @return
     */
    public String render(String viewName) {
        return THEME + "/" + viewName;
    }

    public BaseController title(HttpServletRequest request, String title) {
        request.setAttribute("title", title);
        return this;
    }

    public BaseController keywords(HttpServletRequest request, String keywords) {
        request.setAttribute("keywords", keywords);
        return this;
    }

    /**
     * 获取请求绑定的登录对象
     * @param request
     * @return
     */
    public AuthorizingUser user(HttpServletRequest request) {
//        return TaleUtils.getLoginUser(request);
        return getCurrentUser();
    }

    public Integer getUid(HttpServletRequest request){
        return Math.toIntExact(this.user(request).getUserId());
    }

    public String render_404() {
        return "comm/error_404";
    }

    /**
     * 设置cookie
     *
     * @param name
     * @param value
     * @param maxAge
     * @param response
     */
    protected void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    protected AuthorizingUser getCurrentUser() {
        return (AuthorizingUser) getSubject().getPrincipal();
    }

    protected Session getSession() {
        return getSubject().getSession();
    }

    protected Session getSession(Boolean flag) {
        return getSubject().getSession(flag);
    }
}
