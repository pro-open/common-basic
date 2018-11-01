package com.project.basic.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.project.basic.dao.entity.ToString;


/**
 * pagehelper属性配置信息
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
@Configuration
@ConfigurationProperties(prefix = "pagehelper")
public class PagehelperProperties extends ToString{
    private static final long serialVersionUID = -7401765684522825957L;
    private String reasonable;
    private String supportMethodsArguments;
    private String returnPageInfo;
    private String params;

    public String getReasonable() {
        return reasonable;
    }

    public void setReasonable(String reasonable) {
        this.reasonable = reasonable;
    }

    public String getSupportMethodsArguments() {
        return supportMethodsArguments;
    }

    public void setSupportMethodsArguments(String supportMethodsArguments) {
        this.supportMethodsArguments = supportMethodsArguments;
    }

    public String getReturnPageInfo() {
        return returnPageInfo;
    }

    public void setReturnPageInfo(String returnPageInfo) {
        this.returnPageInfo = returnPageInfo;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

}
