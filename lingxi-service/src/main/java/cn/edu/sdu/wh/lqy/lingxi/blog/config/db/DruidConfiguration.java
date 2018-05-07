package cn.edu.sdu.wh.lqy.lingxi.blog.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

@Configuration
@ConditionalOnClass({DruidDataSource.class})
@EnableConfigurationProperties(DruidProperties.class)
public class DruidConfiguration {

    private static Logger LOGGER = LoggerFactory.getLogger(DruidConfiguration.class);

    private final DruidProperties properties;

    public DruidConfiguration(DruidProperties properties) {
        this.properties = properties;
    }

    /**
     * Druid数据源
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUserName());
        dataSource.setPassword(properties.getPassword());
        dataSource.setDbType(properties.getType());
        dataSource.setInitialSize(properties.getInitialSize());
        dataSource.setMinIdle(properties.getMinIdle());
        dataSource.setMaxActive(properties.getMaxActive());
        dataSource.setMaxWait(properties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(properties.getValidationQuery());
        dataSource.setTestWhileIdle(properties.getTestWhileIdle());
        dataSource.setTestOnBorrow(properties.getTestOnBorrow());
        dataSource.setTestOnReturn(properties.getTestOnReturn());
        try {
            dataSource.setFilters(properties.getFilters());
        } catch (SQLException e) {
            LOGGER.error("db configuration initialization filter error: ", e);
        }

        return dataSource;
    }

    /**
     * WebStatFilter配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.datasource", name = "web-stat-filter")
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/db/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    /**
     * StatViewServlet配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(new StatViewServlet());
        registrationBean.setEnabled(true);
        registrationBean.addUrlMappings("/db/*");
        registrationBean.addInitParameter("loginUsername", "db");
        registrationBean.addInitParameter("loginPassword", "db");
        registrationBean.addInitParameter("logSlowSql", "true");
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

}
