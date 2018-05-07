package cn.edu.sdu.wh.lqy.lingxi.blog.config.db;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("cn.edu.sdu.wh.lqy.lingxi.blog.mapper")
public class MybatisPlusConfiguration {

    /**
     * mybatis-plus SQL执行效率插件
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    /**
     * mybatis-plus分页插件<br>
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLocalPage(true);// 开启 PageHelper 的支持

//        List<ISqlParser> sqlParserList = new ArrayList<>();
//        TenantSqlParser tenantSqlParser = new TenantSqlParser();
//        tenantSqlParser.setTenantHandler(new TenantHandler() {
//            @Override
//            public Expression getTenantId() {
//                return new LongValue(1L);
//            }
//
//            @Override
//            public String getTenantIdColumn() {
//                return "tenant_id";
//            }
//
//            @Override
//            public boolean doTableFilter(String tableName) {
//                // 这里可以判断是否过滤表
//                /*
//                if ("user".equals(tableName)) {
//                    return true;
//                }*/
//                return false;
//            }
//        });
//
//
//        sqlParserList.add(tenantSqlParser);
//        paginationInterceptor.setSqlParserList(sqlParserList);
        // 以下过滤方式与 @SqlParser(filter = true) 注解等效
//        paginationInterceptor.setSqlParserFilter(new ISqlParserFilter() {
//            @Override
//            public boolean doFilter(MetaObject metaObject) {
//                MappedStatement ms = PluginUtils.getMappedStatement(metaObject);
//                // 过滤自定义查询此时无租户信息约束【 麻花藤 】出现
//                if ("com.baomidou.springboot.mapper.UserMapper.selectListBySQL".equals(ms.getId())) {
//                    return true;
//                }
//                return false;
//            }
//        });
        return paginationInterceptor;
    }

//    @Bean
//    public MetaObjectHandler metaObjectHandler(){
//        return new MyMetaObjectHandler();
//    }

//    /**
//     * 注入主键生成器
//     */
//    @Bean
//    public IKeyGenerator keyGenerator(){
//        return new H2KeyGenerator();
//    }
//
//    /**
//     * 注入sql注入器
//     */
//    @Bean
//    public ISqlInjector sqlInjector(){
//        return new LogicSqlInjector();
//    }
}