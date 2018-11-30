package com.project.basic.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 基础BASE实体
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
public abstract class BaseEntity extends ToString {
    private static final long serialVersionUID = 5396865564545888757L;
    
    /**
     * 交易ID
     */
//    @Transient
//    private String transactionId;
    
    
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 业务code
     */
    private String code;
    /**
     * 删除标记位0,未删除,1,删除
     */
    @Column(name = "del_flag")
    private Boolean delFlag;
    /**
     * 简单描述信息
     */
    private String remark;
    /**
     * 创建人
     */
    @Column(name = "create_by")
    private String createBy;
    /**
     * 更新人
     */
    @Column(name = "update_by")
    private String updateBy;
    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Timestamp createDate;
    /**
     * 更新时间
     */
    @Column(name = "update_date")
    private Timestamp updateDate;
    
    public BaseEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    @JSONField(serialize=false)
    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 获取简单描述信息
     *
     * @return remark - 简单描述信息
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置简单描述信息
     *
     * @param remark 简单描述信息
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

}
