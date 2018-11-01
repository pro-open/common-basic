package com.project.basic.conf;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@Configuration
@EnableTransactionManagement
@Order(Ordered.LOWEST_PRECEDENCE)
@AutoConfigureAfter({MyBatisConfiguration.class })
public class TransactionManagerConfiguration implements TransactionManagementConfigurer{
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagerConfiguration.class);

    @Resource(name="primaryDataSource")
    private DataSource dataSource;
    
    @Bean(name = "transactionManager")
    public PlatformTransactionManager txManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManager.setNestedTransactionAllowed(true);
        return transactionManager;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        LOGGER.debug("初始化MultiTransactionManagerConfiguration执行结束了");
        return txManager();
    }
    
}