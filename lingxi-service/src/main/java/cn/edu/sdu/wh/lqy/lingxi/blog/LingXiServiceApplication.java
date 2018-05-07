package cn.edu.sdu.wh.lqy.lingxi.blog;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableTransactionManagement
//@DubboComponentScan("cn.edu.sdu.wh.lqy.lingxi.blog.service.*")
public class LingXiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingXiServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        //修复 Elasticsearch引入Redis后启动报错的Bug：
//        availableProcessors is already set to [8], rejecting [8]
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
