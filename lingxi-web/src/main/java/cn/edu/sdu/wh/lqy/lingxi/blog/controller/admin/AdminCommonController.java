package cn.edu.sdu.wh.lqy.lingxi.blog.controller.admin;

import cn.edu.sdu.wh.lqy.lingxi.blog.controller.BaseController;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.vcode.Captcha;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.vcode.GifCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class AdminCommonController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCommonController.class);

    @GetMapping(value = "/gifCode")
    public void getGifCode(HttpServletResponse response, HttpServletRequest request, Model model) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");

            Captcha captcha = new GifCaptcha(146, 33, 4);
            captcha.out(response.getOutputStream());
//            Session session = super.getSession();
            HttpSession session = request.getSession();
            session.removeAttribute("_code");
            session.setAttribute("_code", captcha.text().toLowerCase());

        } catch (Exception e) {
            LOGGER.error("generate gif code error! ", e);
        }
    }
}
