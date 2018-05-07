package cn.edu.sdu.wh.lqy.lingxi.blog.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
