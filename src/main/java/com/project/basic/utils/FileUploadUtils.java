package com.project.basic.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传工具类
 * 
 */
public class FileUploadUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(FileUploadUtils.class);
    
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
				if(LOGGER.isInfoEnabled()){
				    LOGGER.info("文件上传,名称为:{},原名为:{},类型为:{},长度为:{}!",name,originalFileName,contentType,size);
                }
			    
			    filePathList = new ArrayList<String>();
				//使用自定义文件资源库
                //String realPath = PropertityUtils.getContextProperty(uploadPath);
                // 按照日期,生成子目录信息
                //String subPath = "/"+DatetimeUtils.currentDateStr()+"/"+File.pathSeparator;
                String subPath = File.separator+DateTimeUtil.formatDate2Str(DateTimeUtil.DATE_PATTON_3)+File.separator;
                //originalFileName=DatetimeUtils.currentTimestampStr()+"_"+originalFileName;
				//保存文件的本地全路径信息
                String fullName = uploadPath+subPath+originalFileName;
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("文件上传,拷贝文件结束,生成的文件全路径为:{}",fullName);
                }
                File localFile = new File(fullName);
                //返回该文件的路径
                //String serverPath = Pphconfig.getContextProperty("upload.read.path");
                //全部访问路径
                //pathList.add(serverPath+subPath+originalFileName);
                filePathList.add(fullName);
                
				try {
                	//这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
                	org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), localFile);
                	//工具类已经处理
                	/*if(localFile!=null
                	        &&localFile.getParentFile().isDirectory()){
                	    localFile.mkdirs();
                	}
                	org.springframework.util.FileCopyUtils.copy(FileCopyUtils.copyToByteArray(file.getInputStream()), localFile);*/
				} catch (Exception e) {
				    LOGGER.error("文件上传,拷贝文件异常!",e);
				}
			}
		}
		return filePathList;
	}
	
	/**
	 * 上传批量文件,并返回附件信息对象列表
	 */
	public static List<Map<String,String>> uploadMultipartFileListMap(MultipartFile[] files,String uploadPath){
	    List<Map<String,String>> resultMapList = new ArrayList<>();
	    for(MultipartFile file : files){
	        if(file==null/*||file.isEmpty()*/){
	            return resultMapList;
	        }else {
	            //文件名称,上传form的name属性
	            String formName = file.getName();
	            //文件类型
	            String contentType = file.getContentType();
	            //文件长度
	            long size = file.getSize();
	            //文件原名
	            String fileOrgiName = file.getOriginalFilename();
	            if(LOGGER.isInfoEnabled()){
	                LOGGER.info("文件上传,名称为:{},原名为:{},类型为:{},长度为:{}!",formName,fileOrgiName,contentType,size);
                }
	            
	            //使用自定义文件资源库
	            //String realPath = PropertityUtils.getContextProperty(uploadPath);
	            // 按照日期,生成子目录信息
	            //String subPath = "/"+DatetimeUtils.currentDateStr()+"/"+File.pathSeparator;
	            String subPath = File.separator+DateTimeUtil.formatDate2Str(DateTimeUtil.DATE_PATTON_3)+File.separator;
	            //originalFileName=DatetimeUtils.currentTimestampStr()+"_"+originalFileName;
	            //保存文件的本地全路径信息
	            //全部访问路径
	            //pathList.add(serverPath+subPath+originalFileName);
	            //生成新的不重复的文件名，仅用于保存使用
	            String fileLocalName=fileOrgiName;
	            @SuppressWarnings("unused")
                int suffixIndex=-1;
	            if((suffixIndex=fileOrgiName.lastIndexOf("."))>=0){
	                //fileLocalName=UUIDGenerator.generate()+fileOrgiName.substring(suffixIndex);
	            }
	            String fileLocalPath = uploadPath+subPath+fileLocalName;
	            if(LOGGER.isInfoEnabled()){
	                LOGGER.info("文件上传,拷贝文件结束,生成的文件全路径为:{}",fileLocalPath);
	            }
	            File localFile = new File(fileLocalPath);

                Map<String,String> paramMap=new HashMap<>();
                paramMap.put("formName", formName);
                paramMap.put("size", String.valueOf(size));
                paramMap.put("fileLocalName", fileLocalName);
                paramMap.put("fileUrl", subPath+ fileLocalName);
                
                paramMap.put("fileShowName", fileOrgiName);
                paramMap.put("contentType", contentType);
                paramMap.put("fileLocalPath", fileLocalPath);
                
	            try {
	                //这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
	                org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), localFile);
	                //工具类已经处理
	                /*if(localFile!=null
                	        &&localFile.getParentFile().isDirectory()){
                	    localFile.mkdirs();
                	}
                	org.springframework.util.FileCopyUtils.copy(FileCopyUtils.copyToByteArray(file.getInputStream()), localFile);*/
	            } catch (Exception e) {
	                LOGGER.error("文件上传,拷贝文件异常!",e);
	            } finally {
	                resultMapList.add(paramMap);
                }
	        }
	    }
	    return resultMapList;
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
