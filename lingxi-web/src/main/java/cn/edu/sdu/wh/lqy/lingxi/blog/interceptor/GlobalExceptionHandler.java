package cn.edu.sdu.wh.lqy.lingxi.blog.interceptor;

import cn.edu.sdu.wh.lqy.lingxi.blog.exception.LingXiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = LingXiException.class)
    public String lingXiException(Exception e) {
        LOGGER.error("LingXi Error: ", e);
        return "comm/error_500";
    }

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e) {
        LOGGER.error("LingXi Error: ", e);
        return "comm/error_404";
    }

}
