package cn.edu.sdu.wh.lqy.lingxi.blog;

import cn.edu.sdu.wh.lqy.lingxi.blog.base.BaseApplicationTests;
import cn.edu.sdu.wh.lqy.lingxi.blog.service.IArticleService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.google.gson.Gson;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class DubboConsumerTest extends BaseApplicationTests {

//    @Reference(version = "1.0.0", application = "${dubbo.application.id}",
//            url = "dubbo://172.26.226.232:20880")
    @Autowired
    private IArticleService articleService;

    @Autowired
    private Gson gson;

    @Test
    public void test() {


        System.out.println(articleService == null);

        System.out.println(articleService.getClass().getName());

        System.out.println(gson.toJson(articleService.getArticle("18")));
    }

    @Test
    public void testRestReq() {
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject("http://localhost:8088/lingxi/", String.class, 6);
        System.out.println(response);
    }
}
