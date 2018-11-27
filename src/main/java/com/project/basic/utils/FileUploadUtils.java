package com.project.basic.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传工具类
 * 
 */
public class FileUploadUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUploadUtils.class);
    
	/**
	 * 上传文件,进行存储
	 */
	public static List<String> uploadMultipartFileList(MultipartFile[] files,String uploadPath){
		List<String> filePathList = null;
		for(MultipartFile file : files){
			if(file==null||file.isEmpty()){
				return filePathList;
			}else {
			    //文件名称,上传form的name属性
			    String name = file.getName();
			    //文件类型
			    String contentType = file.getContentType();
			    //文件长度
				long size = file.getSize();
				//文件原名
				String originalFileName = file.getOriginalFilename();
				logger.info("文件上传,名称为:{},原名为:{},类型为:{},长度为:{}!",name,originalFileName,contentType,size);
			    
			    filePathList = new ArrayList<String>();
				//使用自定义文件资源库
                //String realPath = PropertityUtils.getContextProperty(uploadPath);
                // 按照日期,生成子目录信息
                //String subPath = "/"+DatetimeUtils.currentDateStr()+"/"+File.pathSeparator;
                String subPath = File.separator+DateTimeUtil.formatDate2Str()+File.separator;
                //originalFileName=DatetimeUtils.currentTimestampStr()+"_"+originalFileName;
				//保存文件的本地全路径信息
                String fullName = uploadPath+subPath+originalFileName;
                File localFile = new File(fullName);
                
				try {
                	//这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
                	org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), localFile);
                	//工具类已经处理
                	/*if(localFile!=null
                	        &&localFile.getParentFile().isDirectory()){
                	    localFile.mkdirs();
                	}
                	org.springframework.util.FileCopyUtils.copy(FileCopyUtils.copyToByteArray(file.getInputStream()), localFile);*/
					
                	//返回该文件的路径
					//String serverPath = Pphconfig.getContextProperty("upload.read.path");
					//全部访问路径
					//pathList.add(serverPath+subPath+originalFileName);
					filePathList.add(fullName);
					logger.info("文件上传,拷贝文件结束,生成的文件全路径为:{}",fullName);
				} catch (Exception e) {
				    logger.error("文件上传,拷贝文件异常!",e);
				}
			}
		}
		return filePathList;
	}
	
    /**
     * 获取请求ip地址信息
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
	
	
}
