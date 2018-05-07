package cn.edu.sdu.wh.lqy.lingxi.blog.config.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ShiroConfiguration {

    @Value("${shiro.cipherkey}")
    private String cipherKey = "U3ByaW5nQmxhZGUAAAAAAA==";

    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("classpath:config/Ehcache.xml"));

        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(cacheManagerFactoryBean.getObject());
        return ehCacheManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/admin/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/login");

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        filterChainDefinitionMap.put("/static/admin/css/**", "anon");
        filterChainDefinitionMap.put("/static/admin/js/**", "anon");
        filterChainDefinitionMap.put("/static/admin/images/**", "anon");
        filterChainDefinitionMap.put("/static/admin/img/**", "anon");
        filterChainDefinitionMap.put("/static/admin/plugins/**", "anon");
        filterChainDefinitionMap.put("/static/admin/fonts/**", "anon");

        filterChainDefinitionMap.put("/static/user/css/**", "anon");
        filterChainDefinitionMap.put("/static/user/img/**", "anon");

        filterChainDefinitionMap.put("/common/error_404", "anon");
        filterChainDefinitionMap.put("/common/error_500", "anon");

        filterChainDefinitionMap.put("/druid/**", "anon");
//        filterChainDefinitionMap.put("/register", "anon");
        filterChainDefinitionMap.put("/error", "anon");
        filterChainDefinitionMap.put("/gifCode", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/lingxi/**", "anon");
        filterChainDefinitionMap.put("/admin/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(SessionManager sessionManager){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm(retryLimitHashedCredentialsMatcher()));
        securityManager.setRememberMeManager(rememberMeManager());
        securityManager.setCacheManager(ehCacheManager());
        securityManager.setSessionManager(sessionManager);

        return securityManager;
    }

    @Bean
    public RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher() {
        RetryLimitHashedCredentialsMatcher matcher = new RetryLimitHashedCredentialsMatcher(ehCacheManager());
        matcher.setHashAlgorithmName(Md5Hash.ALGORITHM_NAME);
        matcher.setStoredCredentialsHexEncoded(true);
        matcher.setHashIterations(6);
        return matcher;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor attributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
//    @ConditionalOnMissingBean
    public ShiroRealm shiroRealm(HashedCredentialsMatcher credentialsMatcher){
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCachingEnabled(true);
        shiroRealm.setCredentialsMatcher(credentialsMatcher);
        return shiroRealm;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey(Base64.decode("U3ByaW5nQmxhZGUAAAAAAA=="));
        return cookieRememberMeManager;
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public SessionDAO sessionDAO() {
        MemorySessionDAO sessionDAO = new MemorySessionDAO();
        return sessionDAO;
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        Collection<SessionListener> listeners = new ArrayList<>();
        listeners.add(new SessionListener() {

            private final AtomicInteger sessionCount = new AtomicInteger(0);

            @Override
            public void onStart(Session session) {
                sessionCount.incrementAndGet();
            }

            @Override
            public void onStop(Session session) {
                sessionCount.decrementAndGet();
            }

            @Override
            public void onExpiration(Session session) {
                sessionCount.decrementAndGet();
            }

        });
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionDAO(sessionDAO());
        return sessionManager;
    }

}
