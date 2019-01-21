package com.project.basic.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.project.basic.dao.entity.BaseEntity;
import com.project.basic.dao.entity.Cacheable;
import com.project.basic.exception.BaseServiceException;
import com.project.basic.service.BaseService;
import com.project.basic.service.IRedisService;
import com.project.basic.utils.Constants;
import com.project.basic.utils.DateTimeUtil;
import com.project.basic.utils.MyMapper;
import com.project.basic.utils.UUIDGenerator;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * service层统一基础服务接口实现
 * 
 * @Author LiuBao
 * @Version 2.0 2017年4月11日
 */
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);

    private Class<T> entityClass;
    private String cacheAllKey;
    private MyMapper<T> myMapper;
    
    @Resource(name="iRedisService")
    @SuppressWarnings("rawtypes")
    private IRedisService iRedisService;
    
    @Value("${serial.number.length:3}")
    private int serialNumberLength;
    
    @Value("${redis.cache.prefix:dev}")
    private String cachePrefix;
    
    @SuppressWarnings("unchecked")
    public BaseServiceImpl(final MyMapper<T> myMapper ) {
        Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        this.entityClass = (Class<T>) types[0];
        this.myMapper = myMapper;
        this.cacheAllKey="selectAll_"+entityClass.getName();
    }
    
    @Override
    public List<T> selectAll() {
        return myMapper.selectAll();
    }
    
    @Override
    public List<T> selectByEntity(T record) {
        return selectByEntity(record, Boolean.FALSE);
    }
    
    @Override
    public List<T> selectByEntity(T record,Boolean isDistinct) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(record));
        }
        List<T> resultList =new ArrayList<>();
        List<T> recordList = myMapper.select(record);
        if(isDistinct&&CollectionUtils.isNotEmpty(recordList)){
          //推荐:JDK8写法
            recordList.stream().forEach(
                    p -> {
                        if (!resultList.contains(p)) {
                            resultList.add(p);
                        }
                    }
            );
        }else{
            //resultList.addAll(recordList);
            return recordList;
        }
        return resultList;
    }

    @Override
    public T selectByPrimaryKey(Long id) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(id));
        }
        return myMapper.selectByPrimaryKey(id);
    }

    @Override
    public T selectByCode(String code) {
        try {
            T record = entityClass.newInstance();
            record.setCode(code);
            return myMapper.selectOne(record);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("查询方法selectByCode:[{}]异常:{}",code,e);
            throw new BaseServiceException("数据查询异常,code="+code+"!");
        }
    }

    @Override
    public List<T> selectListByPage(T record, Integer pageIndex, Integer pageSize) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息:pageIndex为:{},pageSize为:{}",pageIndex,pageSize);
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(record));
        }
        String orderBy=null;
        if(record instanceof BaseEntity){
            orderBy=" ID DESC";
        }
        PageHelper.startPage(pageIndex, pageSize,orderBy); 
        return myMapper.selectByRowBounds(record, makeRowBounds(pageIndex, pageSize));
    }
    
    @Override
    public Integer selectByExampleCount(Map<String,String> paramMap){
        if(MapUtils.isEmpty(paramMap)){
            throw new BaseServiceException("请求参数属性Map不能为空!");
        }
        Example example = makeExample(paramMap);
        if(example==null){
            return null;
        }
        return myMapper.selectCountByExample(example);
    }
    
    /**
     * 构造Example查询条件
     */
    @Override
    public Example makeExample(Map<String, String> paramMap) {
        Example example = new Example(entityClass);
        Criteria criteria = example.createCriteria();
        Boolean delFlag=null;
        for (Map.Entry<String,String> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(StringUtils.isAnyBlank(key,value)){
                continue;
            }
            key=key.trim();
            value=value.trim();
            if(StringUtils.equalsIgnoreCase("order", key)){
                //example.setOrderByClause("id DESC");
                example.setOrderByClause(value);
            }else if(StringUtils.equalsIgnoreCase("delFlag", key)){
                delFlag=Boolean.valueOf(value);
                criteria.andEqualTo(key,delFlag);
            } else{
                if(StringUtils.contains(value, ",")){
                    criteria.andIn(key, Arrays.asList(StringUtils.split(value,",")));
                }else {
                    if(StringUtils.contains(key, "Equal")){
                        criteria.andEqualTo(key.replace("Equal", ""),value);
                    } else if(StringUtils.contains(key, "Ignore")){
                        criteria.andNotEqualTo(key.replace("Ignore", ""),value);
                    }
                }
            }
        }
        if(delFlag==null){
            criteria.andEqualTo("delFlag",Boolean.FALSE);
        }
        return example;
    }

    @Override
    public List<T> selectByExample(Map<String,String> paramMap , Integer pageIndex, Integer pageSize){
        if(MapUtils.isEmpty(paramMap)){
            throw new BaseServiceException("请求参数属性Map不能为空!");
        }
        Example example = makeExample(paramMap);
        if(example==null){
            return null;
        }
        return myMapper.selectByExampleAndRowBounds(example , makeRowBounds(pageIndex, pageSize));
    }
    
    @Override
    public List<T> selectByExampleAndRowBounds(Example example, Integer pageIndex, Integer pageSize) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息:pageIndex为:{},pageSize为:{}",pageIndex,pageSize);
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(example));
        }
        return myMapper.selectByExampleAndRowBounds(example, makeRowBounds(pageIndex, pageSize));
    }
    
    @Override
    public List<T> selectByCreateDateBetween(Date createDateBegin,Date createDateEnd) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息:createDateBegin为:{},createDateEnd为:{}",createDateBegin,createDateEnd);
        }
        if(createDateBegin==null||createDateEnd==null){
            return null;
        }
        /*if(createDateBegin.after(createDateEnd)){
            Timestamp tmp=createDateBegin;
            createDateBegin=createDateEnd;
            createDateEnd=tmp;
        }*/
        Date createDateArray[]=new Date[]{createDateBegin,createDateEnd};
        Arrays.sort(createDateArray);
        createDateBegin=createDateArray[0];
        createDateEnd=createDateArray[1];
        Example example = new Example(entityClass);
        example.setOrderByClause("id");
//        List<String> values=new ArrayList<>();
//        values.add(UploadStatus.INIT.getKey());
//        values.add(UploadStatus.APPROVED.getKey());
        example.createCriteria()
//        .andIn("uploadStatus", values)
        //.orIn("uploadStatus", values)
        .andBetween("createDate", createDateBegin, createDateEnd)
        .andEqualTo("delFlag",Boolean.FALSE);
        return myMapper.selectByExample(example);
    }
    
    @Override
    public Integer selectCountByCreateDateBetween(Date createDateBegin,Date createDateEnd) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息:createDateBegin为:{},createDateEnd为:{}",createDateBegin,createDateEnd);
        }
        if(createDateBegin.after(createDateEnd)){
            Date tmp=createDateBegin;
            createDateBegin=createDateEnd;
            createDateEnd=tmp;
        }
        Example example = new Example(entityClass);
        example.setCountProperty("id");
//        List<String> values=new ArrayList<>();
//        values.add(UploadStatus.INIT.getKey());
//        values.add(UploadStatus.APPROVED.getKey());
        example.createCriteria()
//        .andIn("uploadStatus", values)
        .andBetween("createDate", createDateBegin, createDateEnd)
        .andEqualTo("delFlag",Boolean.FALSE);
        return myMapper.selectCountByExample(example);
    }
    
    @Override
//    @Transactional(propagation=Propagation.REQUIRED,readOnly = true)
    public List<T> selectByCreateDateBetweenAndRowBounds(Date createDateBegin,Date createDateEnd, Integer pageIndex, Integer pageSize) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息:createDateBegin为:{},createDateEnd为:{}",createDateBegin,createDateEnd);
        }
        if(createDateBegin==null||createDateEnd==null){
            return null;
        }
        if(createDateBegin.after(createDateEnd)){
            Date tmp=createDateBegin;
            createDateBegin=createDateEnd;
            createDateEnd=tmp;
        }
//        Date createDateArray[]=new Date[]{createDateBegin,createDateEnd};
//        Arrays.sort(createDateArray);
//        createDateBegin=createDateArray[0];
//        createDateEnd=createDateArray[1];
        Example example = new Example(entityClass);
        example.setOrderByClause("id");
//        List<String> values=new ArrayList<>();
//        values.add(UploadStatus.INIT.getKey());
//        values.add(UploadStatus.APPROVED.getKey());
        example.createCriteria()
//        .andIn("uploadStatus", values)
        //.orIn("uploadStatus", values)
        .andBetween("createDate", createDateBegin, createDateEnd)
        .andEqualTo("delFlag",Boolean.FALSE);
        return selectByExampleAndRowBounds(example, pageIndex, pageSize);
    }
    
    @Override
    public List<T> selectByKeyList(String key, List<String> keyList){
        if(StringUtils.isBlank(key)){
            throw new BaseServiceException("请求参数属性key不能为空!");
        }
        if(CollectionUtils.isEmpty(keyList)){
            return null;
        }
        //推荐:JDK8写法,去重操作
        keyList = keyList.stream().distinct().collect(Collectors.toList());
        Example example = new Example(entityClass);
        example.setOrderByClause("id DESC");
        example.createCriteria().andIn(key, keyList)
        .andEqualTo("delFlag",Boolean.FALSE);
        return myMapper.selectByExample(example);
    }

    @Override
    public Integer selectCount(T record) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(record));
        }
        return myMapper.selectCount(record);
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer insert(T record) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("插入条件信息为:{}",JSON.toJSONString(record));
        }
        Integer result = myMapper.insert(fillInsertBaseEntity(record));
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)) {
            String jsonString = JSON.toJSONString(record);
            putToRedisCache(redisKey, jsonString);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("保存数据到redis缓存:{}", jsonString);
            }
            /*if (isExistRedisCache(redisKey)) {
                deleteRedisCache(redisKey);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("insert.清空redis缓存数据redisKey:{}", redisKey);
                }
            }*/
        }
        return result;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer insertList(List<T> records) {
        Integer insertBatch = myMapper.insertList(fillInsertBaseEntitys(records));
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("批量插入条件结果,更新记录条数为:{}条!",insertBatch);
        }
        return insertBatch;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer insertSelective(T record) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("插入条件信息为:{}",JSON.toJSONString(record));
        }
        Integer result = myMapper.insertSelective(fillInsertBaseEntity(record));
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)) {
            String jsonString = JSON.toJSONString(record);
            putToRedisCache(redisKey, jsonString);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("保存数据到redis缓存:{}", jsonString);
            }
            /*if (isExistRedisCache(redisKey)) {
                deleteRedisCache(redisKey);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("insertSelective.清空redis缓存数据redisKey:{}", redisKey);
                }
            }*/
        }
        return result;
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer updateByPrimaryKey(T record) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("更新条件信息为:{}",JSON.toJSONString(record));
        }
        Integer result = myMapper.updateByPrimaryKey(record);
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)) {
            if (isExistRedisCache(redisKey)) {
                deleteRedisCache(redisKey);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("updateByPrimaryKey.清空redis缓存数据redisKey:{}", redisKey);
                }
            }/* else {
                String jsonString = JSON.toJSONString(record);
                putToRedisCache(redisKey, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("保存数据到redis缓存:{}", jsonString);
                }
            }*/
        }
        return result;
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer updateByPrimaryKeySelective(T record) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("更新条件信息为:{}",JSON.toJSONString(record));
        }
        Integer result = myMapper.updateByPrimaryKeySelective(record);
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)) {
            if (isExistRedisCache(redisKey)) {
                deleteRedisCache(redisKey);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("updateByPrimaryKeySelective.清空redis缓存数据redisKey:{}", redisKey);
                }
            } /*else {
                String jsonString = JSON.toJSONString(record);
                putToRedisCache(redisKey, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("保存数据到redis缓存:{}", jsonString);
                }
            }*/
        }
        return result;
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer updateByCode(T record) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("更新条件信息为:{}",JSON.toJSONString(record));
        }
        String code=record.getCode();
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo("code", code);
        //record.setCode(null);
        Integer result = myMapper.updateByExample(record, example);
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)) {
            if (isExistRedisCache(redisKey)) {
                deleteRedisCache(redisKey);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("updateByCode.清空redis缓存数据redisKey:{}", redisKey);
                }
            } /*else {
                String jsonString = JSON.toJSONString(record);
                putToRedisCache(redisKey, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("保存数据到redis缓存:{}", jsonString);
                }
            }*/
        }
        return result;
    }
    
    @Override
    public /*<V extends Cacheable>*/ Boolean deleteEntityCache(T record) {
        Boolean result =Boolean.FALSE;
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("清除缓存条件为:{}",JSON.toJSONString(record));
        }
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)&&isExistRedisCache(redisKey)) {
            result = deleteRedisCache(redisKey);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteEntityCache.redis清除缓存redisKey={},结果为:{}", redisKey,result);
        }
        return result;
    }
    
    @Override
    public /*<V extends Cacheable>*/ Boolean deleteEntityListCache(T record) {
        Boolean result =Boolean.FALSE;
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("清除缓存条件为:{}",JSON.toJSONString(record));
        }
        String redisKey = null;
        if(record==null){
            redisKey=cacheAllKey;
        }else if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
            if(StringUtils.isNotBlank(redisKey)){
                redisKey="selectList_"+redisKey;
            }
        }
        if (StringUtils.isNotBlank(redisKey)&&isExistRedisCache(redisKey)) {
            result = deleteRedisCache(redisKey);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteEntityListCache.redis清除缓存redisKey={},结果为:{}", redisKey,result);
        }
        return result;
    }
    
    @Override
    public Boolean deleteEntityAllCache() {
        Boolean result =Boolean.FALSE;
        String redisKey = cacheAllKey;
        if (StringUtils.isNotBlank(redisKey)&&isExistRedisCache(redisKey)) {
            result = deleteRedisCache(redisKey);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deleteEntityAllCache.redis清除缓存redisKey={},结果为:{}", redisKey,result);
        }
        return result==null?Boolean.FALSE:result;
    }
    
    @Override
    public /*<V extends Cacheable>*/ T selectEntityFromCache(T record) {
        T result =null;
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(record));
        }
        String redisKey = null;
        Boolean isCachIgnore = true;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
            isCachIgnore = ((Cacheable) record).isCachIgnore();
        }
        if (StringUtils.isNotBlank(redisKey)&&!isCachIgnore&&isExistRedisCache(redisKey)) {
            result = getEntityFromRedisCache(redisKey, entityClass);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("selectEntityFromCache.查询redis缓存redisKey={},结果为:{}", redisKey,result);
            }
            if(result!=null){
                return result;
            }
        }
        //查询并更新缓存
        if (StringUtils.isNotBlank(redisKey)&&!isCachIgnore) {
            //result = myMapper.selectOne(record);
            List<T> resultList = myMapper.select(record);
            if(CollectionUtils.isNotEmpty(resultList)){
                result=resultList.get(0);
            }
            if(result!=null){
                String jsonString = JSON.toJSONString(result);
                putToRedisCache(redisKey, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("selectEntityFromCache保存数据到redis缓存:{}", jsonString);
                }
            }
        }
        return result;
    }
    
    @Override
    public /*<V extends Cacheable>*/ List<T> selectEntityListFromCache(T record) {
        List<T> result =null;
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("查询条件信息为:{}",JSON.toJSONString(record));
        }
        String redisKey = null;
        Boolean isCachIgnore = Boolean.TRUE;
        if(record==null){
            redisKey=cacheAllKey;
            isCachIgnore = Boolean.FALSE;
        }else if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
            if(StringUtils.isNotBlank(redisKey)){
                redisKey="selectList_"+redisKey;
            }
            isCachIgnore = ((Cacheable) record).isCachIgnore();
        }
        if (StringUtils.isNotBlank(redisKey)&&!isCachIgnore&&isExistRedisCache(redisKey)) {
            result=getEntityListFromRedisCache(redisKey, entityClass);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("selectEntityListFromCache.查询redis缓存redisKey={},结果为:{}", redisKey,result);
            }
            if(result!=null){
                return result;
            }
        }
        //查询并更新缓存
        if(record==null){
            try {
                record = entityClass.newInstance();
                record.setDelFlag(Boolean.FALSE);
                result = myMapper.select(record);  
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("方法selectEntityListFromCache:异常:",e);
                throw new BaseServiceException("数据selectEntityListFromCache查询异常!");
            }
            //result = myMapper.selectAll();
        }else if (StringUtils.isNotBlank(redisKey)&&!isCachIgnore) {
            record.setDelFlag(Boolean.FALSE);
            result = myMapper.select(record);
        }
        if(CollectionUtils.isNotEmpty(result)){
            String jsonString = JSON.toJSONString(result);
            if(StringUtils.isNotBlank(redisKey)){
                putToRedisCache(redisKey, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("selectEntityListFromCache保存数据到redis缓存:{}", jsonString);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<T> selectEntityAllFromCache() {
        List<T> result =null;
        String redisKey =cacheAllKey;
        if (StringUtils.isNotBlank(redisKey)&&isExistRedisCache(redisKey)) {
            result=getEntityListFromRedisCache(redisKey, entityClass);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("selectEntityAllFromCache.查询redis缓存redisKey={},结果为:{}", redisKey,result);
            }
            if(result!=null){
                return result;
            }
        }
        //查询并更新缓存
        if (StringUtils.isNotBlank(redisKey)) {
            //result = myMapper.selectAll();  
            try {
                T record = entityClass.newInstance();
                record.setDelFlag(Boolean.FALSE);
                result = myMapper.select(record);  
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("方法selectEntityAllFromCache:异常:",e);
                throw new BaseServiceException("数据selectEntityAllFromCache查询异常!");
            }
        }
        if(CollectionUtils.isNotEmpty(result)){
            String jsonString = JSON.toJSONString(result);
            putToRedisCache(redisKey, jsonString);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("selectEntityAllFromCache保存数据到redis缓存:{}", jsonString);
            }
        }
        return result;
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer updateByCodeSelective(T record) {
        String code=record.getCode();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("更新条件信息为:{}",JSON.toJSONString(record));
        }
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo("code", code);
        record.setCode(null);
        Integer result = myMapper.updateByExampleSelective(record, example);
        String redisKey = null;
        if (record instanceof Cacheable) {
            redisKey = ((Cacheable) record).getRedisKey();
        }
        if (StringUtils.isNotBlank(redisKey)) {
            if (isExistRedisCache(redisKey)) {
                deleteRedisCache(redisKey);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("updateByCodeSelective.清空redis缓存数据redisKey:{}", redisKey);
                }
            } /*else {
                String jsonString = JSON.toJSONString(record);
                putToRedisCache(redisKey, jsonString);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("保存数据到redis缓存:{}", jsonString);
                }
            }*/
        }
        return result;
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer deleteByPrimaryKey(Long id) {
        Integer result = 0;
        try {
            T record = selectByPrimaryKey(id);
            if(record==null){
                return result;
            }
            record.setDelFlag(Boolean.TRUE);
//            T record = entityClass.newInstance();
//            record.setId(id);
//            record.setDelFlag(Boolean.TRUE);
            result = myMapper.updateByPrimaryKeySelective(record);
            //Integer result = myMapper.deleteByPrimaryKey(id);
            return result;
        } catch (Exception e) {
            LOGGER.error("方法deleteByPrimaryKey:[{}]异常:{}",id,e);
            throw new BaseServiceException("数据查询异常,id="+id+"!");
        }
    }
    
    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer deleteByPrimaryKeys(Long[] ids) {
        if(ArrayUtils.isEmpty(ids)){
            return 0;
        }
        Integer result = 0;
        try {
            T record = entityClass.newInstance();
            record.setId(null);
            record.setDelFlag(Boolean.TRUE);
            Example example = new Example(entityClass);
            example.createCriteria()
                        .andIn("id", Arrays.asList(ids))
                        .andEqualTo("delFlag", Boolean.FALSE) ;
            result = myMapper.updateByExampleSelective(record, example);
            return result;
        } catch (Exception e) {
            LOGGER.error("方法deleteByPrimaryKey:[{}]异常:{}",ids,e);
            throw new BaseServiceException("数据查询异常,id="+ids+"!");
        }
    }

    @Override
    //@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class,readOnly = false)
    public Integer deleteByCode(String code) {
        try {
            Example example = new Example(entityClass);
            T newInstance = entityClass.newInstance();
            newInstance.setCode(code);
            newInstance.setDelFlag(Boolean.TRUE);
            example.createCriteria().andEqualTo("code", code);
            newInstance.setCode(null);
            Integer result = myMapper.updateByExampleSelective(newInstance, example);
            //example.createCriteria().andEqualTo("code", code);
            //Integer result = myMapper.deleteByExample(example);
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("方法deleteByCode:[{}]异常:{}",code,e);
            throw new BaseServiceException("数据查询异常,code="+code+"!");
        }
    }
    
    /**
     * 生成全局的自增序列号
     * @param @NotNull projectKey 项目key前缀 不可为空!
     * @param redisKey  模块key名称 可为空
     */
    @Override
    public synchronized String generateSerialNumber(String projectKey,String redisKey) {
        return generateSerialNumber(projectKey, redisKey, null);
    }
    
    @Override
    public synchronized String generateSerialNumber(String projectKey,String redisKey,Integer length) {
        if(StringUtils.isBlank(projectKey)){
            throw new BaseServiceException("-1", "参数信息projectKey不能为空!");
        }
        String cacheKey=cachePrefix+":"+projectKey;
        if(StringUtils.isNoneBlank(redisKey)){
            cacheKey+=":"+redisKey;
        }
        Long serialNumber = iRedisService.incrementBy(cacheKey);
        return projectKey+DateTimeUtil.formatDate2Str(DateTimeUtil.DATE_PATTON_4)+formatSerialNumber(serialNumber,length);
    }
    
    @Override
    public synchronized String getSerialNumber(String projectKey,String redisKey) {
        return getSerialNumber(projectKey, redisKey, 3);
    }
    
    @Override
    public synchronized String getSerialNumber(String projectKey,String redisKey,Integer length) {
        if(StringUtils.isBlank(projectKey)){
            return null;
        }
        String cacheKey=cachePrefix+":"+projectKey;
        if(StringUtils.isNoneBlank(redisKey)){
            cacheKey+=":"+redisKey;
        }
        String string = iRedisService.getString(cacheKey);
        Long serialNumber = StringUtils.isBlank(string)?0:Long.valueOf(string);
        return projectKey+DateTimeUtil.formatDate2Str(DateTimeUtil.DATE_PATTON_4)+formatSerialNumber(serialNumber,length);
    }
    
    /**
     * 当前设置位数为8位数
     */
    private String formatSerialNumber(Long serialNumber,Integer length) {
        if(serialNumber ==null||serialNumber<0){
            serialNumber=0L;
        }
        if(length==null){
            length=this.serialNumberLength;
        }
        String serialNumberFormat=String.valueOf(serialNumber);
        if (StringUtils.isBlank(serialNumberFormat)) {
            serialNumberFormat="0000";
        }
        
        if(serialNumberFormat.length()>length){
            serialNumberFormat=serialNumberFormat.substring(serialNumberFormat.length()-length,serialNumberFormat.length());
        }
        while(serialNumberFormat.length()<length){
            serialNumberFormat="0"+serialNumberFormat;
        }
        return serialNumberFormat;
    }

    /**
     * insert的时候,填充基本属性空值信息
     */
    protected <E extends BaseEntity> E fillInsertBaseEntity(E record) {
        if (StringUtils.isBlank(record.getCode())) {
            record.setCode(UUIDGenerator.generate());
        }
        if (record.getDelFlag() == null) {
            record.setDelFlag(Boolean.FALSE);
        }
        if (DateTimeUtil.isDatetimeZero(record.getUpdateDate())) {
            record.setUpdateDate(DateTimeUtil.currentTimestamp());
        }
        if (DateTimeUtil.isDatetimeZero(record.getCreateDate())) {
            record.setCreateDate(DateTimeUtil.currentTimestamp());
        }
        return record;
    }

    /**
     * 批量填充空属性信息
     */
    private List<T> fillInsertBaseEntitys(List<T> records) {
        if (CollectionUtils.isNotEmpty(records)) {
            for (T record : records) {
                fillInsertBaseEntity(record);
            }
        }
        return records;
    }

    protected <V> void copyProperties(V dest, Object orig) {
        try {
            BeanUtils.copyProperties(dest,orig);
        } catch (IllegalAccessException e) {
            LOGGER.error("属性拷贝方法copyProperties异常:[orig={}]信息异常:{}",orig,e);
            dest=null;
        } catch (InvocationTargetException e) {
            LOGGER.error("属性拷贝方法copyProperties异常:[orig={}]信息异常:{}",orig,e);
            dest=null;
        }
        if(dest!=null){
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("属性拷贝方法转换非空实体结果为:{}",JSON.toJSONString(dest));
            }
        }
        //return dest;
    }
    
    /**
     * 参数 pageIndex pageSize Limit   comment
     *          0               2               0,2     第一页(已转换为1记录)
     *          1               2               0,2     第一页
     *          2               2               2,2     第二页
     *          3               2               4,2     第二页
     *          ......................................................
     *          6               2               (6-1)*2,2     第二页
     *          ......................................................
     *          m               n               (m-1)*n,n     第m页
     */
    protected RowBounds makeRowBounds(Integer pageIndex, Integer pageSize) {
        pageIndex = pageIndex < 1 ? 1 : pageIndex;
        pageSize = (pageSize <= 0) ? 10 : pageSize;
        pageSize = pageSize > Constants.BATCH_SIZE_MAX ? Constants.BATCH_SIZE_MAX : pageSize;
        //pageSize = (pageSize <= 0 || pageSize > 1000) ? 10 : pageSize;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("分页参数为:【pageIndex=" + pageIndex + ",pageSize=" + pageSize + "】");
        }
        return new RowBounds((pageIndex - 1) * pageSize, pageSize);
    }

    @SuppressWarnings("unchecked")
    protected void putToRedisCache(String key, Object value) {
        iRedisService.set(key, value);
    }

    @SuppressWarnings("unchecked")
    protected Boolean deleteRedisCache(String key) {
        boolean result=true;
        iRedisService.remove(key);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    protected Boolean isExistRedisCache(String key) {
        Boolean result =iRedisService.exists(key);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("从isExistRedisCache查询结构[key={}],计算得到的result为:{}", key,result );
        }
        return result==null?Boolean.FALSE:result;
    }

    protected String getFromRedisCache(String key) {
        String value = iRedisService.getString(key);
        return value;
    }

    protected T getEntityFromRedisCache(String key, Class<T> clazz) {
        String value = iRedisService.getString(key);
        T result = JSON.parseObject(value, clazz);
        return result;
    }
    
    protected List<T> getEntityListFromRedisCache(String key, Class<T> clazz) {
        String value = iRedisService.getString(key);
        if (value != null) {
            return JSON.parseArray(value, clazz);
        }
        return null;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
}
