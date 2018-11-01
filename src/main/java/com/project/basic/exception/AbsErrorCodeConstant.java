package com.project.basic.exception;

/**
 * 异常码常量定义类
 * 
 * @Author  LiuBao
 * @Version 2.0
 *   2017年3月29日
 */
public abstract class AbsErrorCodeConstant{
	
    public static final String ERROR = "-1";
  //成功码
    public static final String ZERO = "0";
    //默认提示文案信息
    public static final String SUCCESS = "00000";
    //服务端信息异常
    public static final String FAILURE = "99999";
    //服务端正忙,请稍后重试
    public static final String ERROR_CODE_DEFAULT= "99998";
    
    //服务端异常响应码
    public static final String ERROR_CODE_302 = "error.302";
    public static final String ERROR_CODE_400 = "error.400";
    public static final String ERROR_CODE_401 = "error.401";
    public static final String ERROR_CODE_404 = "error.404";
    public static final String ERROR_CODE_405 = "error.405";
    public static final String ERROR_CODE_500 = "error.500";
    
  //基础信息相关异常信息 10000-19999 
    //请求参数属性转换异常!
    public static final String ERROR_CODE_10000 = "10000";
    //调用方法的Mapper文件不存在!
    public static final String ERROR_CODE_10001 = "10001";
    //数据库异常,请稍候重试!
    public static final String ERROR_CODE_10002 = "10002";
    //操作数据库出现异常:字段重复/有外键关联等!
    public static final String ERROR_CODE_10003= "10003";
    //IP未被授权
    public static final String ERROR_CODE_10004= "10004";
  //请求IP访问过于频繁
    public static final String ERROR_CODE_10005= "10005";
    //数据上传失败
    public static final String ERROR_CODE_10006= "10006";
    
    //文件上传等公共服务异常信息  20000-29999
    //文件上传成功(仅提示文案信息)!
    public static final String SUCCESS_CODE_20000 = "20000";
    //上传文件信息为空
    public static final String ERROR_CODE_20001 = "20001";
    //上传文件信息返回路径为空!
    public static final String ERROR_CODE_20002 = "20002";
    //上传文件信息失败!
    public static final String ERROR_CODE_20003 = "20003";
    //上传文件信息太大,请不要超过{0}
    public static final String ERROR_CODE_20004 = "20004";
    
    //服务端其他异常响应码 99000-99998
    //请求参数信息{}不能为空!
    public static final String ERROR_CODE_99000= "99000";
    //请求参数信息不正确!
    public static final String ERROR_CODE_99001= "99001";
    //请求JSON数据格式不正确!
    public static final String ERROR_CODE_99002= "99002";
    //请求参数绑定失败!
    public static final String ERROR_CODE_99003= "99003";
    //请求参数校验失败!
    public static final String ERROR_CODE_99004= "99004";
    //请求方法{0}不支持!
    public static final String ERROR_CODE_99005= "99005";
    //请求类型ContentType:{0}不支持!
    public static final String ERROR_CODE_99006= "99006";
    //请求参数信息[{0}]不正确!
    public static final String ERROR_CODE_99007= "99007";
    //当前请求数据[{0}]相关信息不存在!
    public static final String ERROR_CODE_99008= "99008";
    //只能每月1日[{0}]更新更新上月的漏报数据!
    public static final String ERROR_CODE_99009= "99009";
	
  //其他Http请求相关错误码定义 91000-91999
    //获取响应信息失败
    public static final String ERROR_CODE_91000 = "91000";
    //Http请求连接失败
    public static final String ERROR_CODE_91001= "91001";
    //Http请求连接超时
    public static final String ERROR_CODE_91002 = "91002";
    //主机信息解析失败
    public static final String ERROR_CODE_91003 = "91003";
    //Http请求响应异常
    public static final String ERROR_CODE_91004 = "91004";
    //Http网络连接异常
    public static final String ERROR_CODE_91005 = "91005";
    //SSL信息初始化异常
    public static final String ERROR_CODE_91006 = "91006";
    //URL信息编解码异常
    public static final String ERROR_CODE_91007 = "91007";
    //Https协议解析失败
    public static final String ERROR_CODE_91008 = "91008";
    //Http请求响应数据为空
    public static final String ERROR_CODE_91009 = "91009";
	
}
