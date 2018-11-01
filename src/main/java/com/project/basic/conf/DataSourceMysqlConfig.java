package com.project.basic.conf;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@Configuration
public class DataSourceMysqlConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceMysqlConfig.class);
    
    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> datasourceType;
    
    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        LOGGER.debug("初始化primaryDataSource执行了。。。");
        return DataSourceBuilder.create().type(datasourceType).build();
    }

}