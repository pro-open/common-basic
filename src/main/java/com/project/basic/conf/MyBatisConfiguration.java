package com.project.basic.conf;


import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.github.pagehelper.PageHelper;

import tk.mybatis.mapper.autoconfigure.SpringBootVFS;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@Configuration
@MapperScan(value={"com.project.pms.dao.mapper"})
@AutoConfigureAfter({DataSourceMysqlConfig.class })
public class MyBatisConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisConfiguration.class);

    @Value("${mybatis.config-location:/mybatis-config.xml}")
    private Resource configLocation;
    
    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;
    
    @Value("${mybatis.typeAliasesPackage}")
    private String typeAliasesPackage;
    
    @Value("${mybatis.typeHandlersPackage}")
    private String typeHandlersPackage;
    
    @Autowired
    private PagehelperProperties pagehelperProperties;
    
    @Autowired
    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource)  {
        //解决打包为jar后的注解@Alias无法识别的问题
        VFS.addImplClass(SpringBootVFS.class);
        //下面spring.boot这个不可以!!!
        //VFS.addImplClass(org.mybatis.spring.boot.autoconfigure.SpringBootVFS.class);
        final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setConfigLocation(configLocation);
        sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        sessionFactoryBean.setTypeHandlersPackage(typeHandlersPackage);
        //sessionFactoryBean.setTypeHandlers(new UploadStatusEnumHandler[]{new UploadStatusEnumHandler(UploadStatus.class)});
        //sessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});  
        
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            sessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
            SqlSessionFactory sqlSessionFactory = sessionFactoryBean.getObject();
            LOGGER.debug("初始化SqlSessionFactory执行结束了");
            return sqlSessionFactory;
        } catch (Exception e) {
            LOGGER.error("初始化SqlSessionFactory异常",e);
            throw new RuntimeException("初始化ResourcePatternResolver信息异常!",e);
        }
    }
    
    /**
     * 配置mybatis的分页插件pageHelper
     * @return
     */
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();  
        Properties props = new Properties();  
        props.setProperty("reasonable", pagehelperProperties.getReasonable());  
        props.setProperty("supportMethodsArguments", pagehelperProperties.getSupportMethodsArguments());  
        props.setProperty("returnPageInfo", pagehelperProperties.getReturnPageInfo());  
        props.setProperty("params", pagehelperProperties.getParams());  
        
        props.setProperty("offsetAsPageNum","true");
        props.setProperty("rowBoundsWithCount","true");
        //指定为MySQL数据库
        props.setProperty("helperDialect","mysql");
        
        pageHelper.setProperties(props);  
        return pageHelper;
    }
    
    @Lazy
    @Autowired
    @Scope("singleton")
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        LOGGER.debug("SqlSessionTemplate初始化完成...");
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    @Value("${spring.datasource.filters}")
    private String filters;
    
    @Value("${druid.login.username}")
    private String loginUsername;
    
    @Value("${druid.login.password}")
    private String loginPassword;
    
    @Value("${druid.parameter.exclusions}")
    private String parameterExclusions;
    
    @Value("${druid.service.patterns}")
    private String servicePatterns;
    
    @Value("${druid.url.mappings}")
    private String urlMappings;
    
    @Value("${druid.reset.enable}")
    private String resetEnable;
    
    @Value("${druid.profile.enable}")
    private String profileEnable;
    
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> reg = new ServletRegistrationBean<>();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings(urlMappings);
        reg.addInitParameter("loginUsername", loginUsername);
        reg.addInitParameter("loginPassword", loginPassword);
        //reg.addInitParameter("allow", "127.0.0.1,10.67.26.113");
        //reg.addInitParameter("deny", "10.0.12.26");
        reg.addInitParameter("resetEnable",resetEnable);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", parameterExclusions);
        filterRegistrationBean.addInitParameter("profileEnable", profileEnable);
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return filterRegistrationBean;
    }
    
}

