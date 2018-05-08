package cn.edu.sdu.wh.lqy.lingxi.blog.config.dubbo;//package cn.edu.sdu.wh.lqy.lingxi.blog.config.dubbo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("classpath:dubbo/dubbo-consumer.properties")
@ImportResource({"classpath:dubbo/dubbo-consumer.xml"})
public class DubboConfiguration {
}
