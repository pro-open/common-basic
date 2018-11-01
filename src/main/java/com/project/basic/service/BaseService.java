package com.project.basic.service;

import java.util.Date;
import java.util.List;

import tk.mybatis.mapper.entity.Example;

/**
 * service层统一服务接口定义
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年10月26日
 */
public interface BaseService<T> {
    
    /**
     * 查询所有数据
     */
    List<T> selectAll();
    
    /**
     * 用对象查询List数据
     */
    List<T> selectByEntity(T record);
    
    /**
     * 按主键查询数据
     */
    T selectByPrimaryKey(Long id);
    
    /**
     * 按业务code查询数据
     */
    T selectByCode(String code);
    
    /**
     * 分页条件查询
     * @param pageIndex 起始角标
     * @param pageSize  分页条数
     * @param T record 包含删除的历史数据
     * @return  List<T>
     */
    List<T> selectListByPage(T record,int pageIndex, int pageSize);
    
    /**
     * 支持分页的条件查询
     * @param example
     * @param rowBounds
     * @return
     */
    List<T> selectByExampleAndRowBounds(Example example, int pageIndex, int pageSize);
    
    /**
     * 分页条件查询总数
     * @param T record 包含删除的历史数据
     * @return int
     */
    int selectCount(T record);
    
    /**
     * 根据key查询对应的list集合值的集合信息
     * @param key 属性名称
     * @param keyList 属性值集合
     */
    List<T> selectByKeyList(String key, List<String> keyList);
    
    /**
     * 按实体类所有字段插入数据
     */
    int insert(T record);
    
    /**
     * 按实体类所有字段批量插入数据:oracle过时
     */
    int insertList(List<T> records);

    /**
     * 按实体类选择非空字段插入数据
     */
    int insertSelective(T record);

    /**
     * 按主键update所有字段
     */
    int updateByPrimaryKey(T record);
    
    /**
     * 按主键update非空字段
     */
    int updateByPrimaryKeySelective(T record);
    
    /**
     * 按code业务码update所有字段
     */
    int updateByCode(T record);
    
    /**
     * 按code业务码update非空字段
     */
    int updateByCodeSelective(T record);

    /**
     * 按主键删除数据
     */
    int deleteByPrimaryKey(Long id);
    
    /**
     * 按业务code删除数据
     */
    int deleteByCode(String code);

    /**
     * 根据实体对象T,从缓存中查询对应的数据集合
     */
    List<T> selectEntityListFromCache(T record);

    /**
     * 根据实体对象T,从缓存中查询对应的单个数据实体
     */
    T selectEntityFromCache(T record);

    /**
     * 从缓存中查询对应所有数据实体集合
     */
    List<T> selectEntityAllFromCache();

    /**
     * 根据实体对象T,从缓存中清除对应的单个数据实体
     */
    Boolean deleteEntityCache(T record);
    
    /**
     * 根据实体对象T,从缓存中清除对应的数据集合
     */
    Boolean deleteEntityListCache(T record);

    /**
     * 从缓存中清除所有数据实体集合
     */
    Boolean deleteEntityAllCache();

    /**
     * 指定日期记录列表查询
     */
    List<T> selectByCreateDateBetween(Date createDateBegin, Date createDateEnd);

    List<T> selectByCreateDateBetweenAndRowBounds(Date createDateBegin, Date createDateEnd, int pageIndex,int pageSize);

    /**
     * 指定日期记录列表个数查询
     */
    int selectCountByCreateDateBetween(Date createDateBegin, Date createDateEnd);

}
