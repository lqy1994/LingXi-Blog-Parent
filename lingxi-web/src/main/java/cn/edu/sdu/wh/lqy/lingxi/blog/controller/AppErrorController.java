package cn.edu.sdu.wh.lqy.lingxi.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局错误页面处理
 */
@Controller
public class AppErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * Web页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();
        switch (status) {
//            case 403:
//                return "comm/403";
            case 404:
                return "comm/error_404";
            case 500:
                return "comm/error_500";
        }

        return "index";
    }

    /**
     * 除Web页面外的错误处理，比如Json/XML等
     */
//    @RequestMapping(value = ERROR_PATH)
//    @ResponseBody
//    public ApiResponse errorApiHandler(HttpServletRequest request) {
//        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
//
//        WebRequest webRequest = new ServletWebRequest();
//        Map<String, Object> attr = errorAttributes.getErrorAttributes(requestAttributes, false);
//        int status = getStatus(request);
//
//        return ApiResponse.of(status, String.valueOf(attr.getOrDefault("message", "error")));
//    }

//    private int getStatus(HttpServletRequest request) {
//        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
//        if (status != null) {
//            return status;
//        }
//
//        return 500;
//    }
}