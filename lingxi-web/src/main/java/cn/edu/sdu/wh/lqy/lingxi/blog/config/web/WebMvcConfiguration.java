package cn.edu.sdu.wh.lqy.lingxi.blog.config.web;

import cn.edu.sdu.wh.lqy.lingxi.blog.interceptor.BaseInterceptor;
import cn.edu.sdu.wh.lqy.lingxi.blog.utils.TaleUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {


    @Resource
    private BaseInterceptor baseInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor);
    }

    /**
     * 添加静态资源文件，外部可以直接访问地址
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:"+ TaleUtils.getUplodFilePath()+"upload/");
        super.addResourceHandlers(registry);
    }
}
