package cn.edu.sdu.wh.lqy.lingxi.blog.config.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;

import java.net.InetAddress;


@Configuration
@ConditionalOnClass({ Client.class, TransportClientFactoryBean.class })
@ConditionalOnProperty(prefix = "lingxi.elasticsearch", name = "cluster-name", matchIfMissing = false)
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfiguration {

    private final ElasticsearchProperties properties;

    public ElasticsearchConfiguration(ElasticsearchProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public TransportClient elasticsearchClient() throws Exception {

        Settings settings = Settings.builder()
                .put("cluster.name", properties.getClusterName())
                .put("client.transport.sniff", true)
                .put("thread_pool.search.size", properties.getPoolSize())
                .build();

        InetSocketTransportAddress masterAddress = new InetSocketTransportAddress(
                InetAddress.getByName(properties.getHostName()), properties.getPort());

//        InetSocketTransportAddress slaveAddress = new InetSocketTransportAddress(
//                InetAddress.getByName(properties.getHostName()), properties.getPort()
//        );

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(masterAddress);
//                .addTransportAddress(slaveAddress);

        return client;
    }

}