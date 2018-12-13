package com.project.basic.zip;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.basic.utils.Constants;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;



/**
 * zip工具类
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年12月13日
 */
public class ZipUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * 解压zip
	 * 
	 * @param zipFile
	 *            zip文件
	 * @param dest
	 *            解压目录
	 * @param passwd
	 *            解压密码
	 * @throws ZipException
	 */
	public static void unzip(File zipFile, String dest, String passwd) throws ZipException {
		ZipFile zFile = new ZipFile(zipFile); // 首先创建ZipFile指向磁盘上的.zip文件
		zFile.setFileNameCharset(Constants.DEFAULT_CHARSET); // 设置文件名编码
		if (!zFile.isValidZipFile()) { // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
			throw new ZipException("压缩文件不合法,可能被损坏.");
		}
		File destDir = new File(dest); // 解压目录
		if (destDir.isDirectory() && !destDir.exists()) {
			destDir.mkdir();
		}
		if (zFile.isEncrypted() && StringUtils.isNotBlank(passwd)) {
			zFile.setPassword(passwd.toCharArray()); // 设置密码
		}
		zFile.extractAll(dest); // 将文件抽出到解压目录(解压)
	}

	/**
	 * 解压zip
	 * 
	 * @param zipFile
	 *            zip文件
	 * @param dest
	 *            解压目录
	 * @param passwd
	 *            解压密码
	 * @throws ZipException
	 */
	public static void unzip(String zipFile, String dest, String passwd) throws ZipException {
		unzip(new File(zipFile), dest, passwd);
	}

	/**
	 * 解压zip
	 * 
	 * @param zipFile
	 *            zip文件
	 * @param dest
	 *            解压目录
	 * @throws ZipException
	 */
	public static void unzip(File zipFile, String dest) throws ZipException {
		unzip(zipFile, dest, null);
	}

	/**
	 * 解压zip
	 * 
	 * @param zipFile
	 *            zip文件
	 * @param dest
	 *            解压目录
	 * @throws ZipException
	 */
	public static void unzip(String zipFile, String dest) throws ZipException {
		unzip(new File(zipFile), dest);
	}

	/**
	 * 压缩zip
	 * 
	 * @param srcList
	 *            要压缩的文件或者文件夹列表
	 * @param dest
	 *            压缩文件存放路径
	 * @param passwd
	 *            压缩使用的密码
	 * @throws ZipException
	 */
	public static void zip(List<File> srcList, String dest, String passwd) throws ZipException {
		ZipParameters parameters = buildZipParameters(passwd);
		ZipFile zipFile = new ZipFile(dest);
		for (File src : srcList) {
			if (src.isDirectory()) {
				zipFile.addFolder(src, parameters);
			} else {
				zipFile.addFile(src, parameters);
			}
		}
	}

	/**
	 * 压缩zip
	 * 
	 * @param srcList
	 *            要压缩的文件或者文件夹列表
	 * @param dest
	 *            压缩文件存放路径
	 * @throws ZipException
	 */
	public static void zip(List<File> srcList, String dest) throws ZipException {
		zip(srcList, dest, null);
	}

	private static ZipParameters buildZipParameters(String passwd) {
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别
		if (StringUtils.isNotEmpty(passwd)) {
			parameters.setEncryptFiles(true);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式
			parameters.setPassword(passwd.toCharArray());
		}
		return parameters;
	}

	/**
	 * 压缩zip
	 * 
	 * @param src
	 *            要压缩的文件或文件夹路径
	 * @param dest 压缩文件存放路径
	 * @param isCreateDir
	 *            是否在压缩文件里创建目录,仅在压缩文件为目录时有效.
	 *            如果为false,将直接压缩目录下文件到压缩文件.
	 * @param passwd
	 *            压缩使用的密码
	 * @throws ZipException
	 */
	public static void zip(File src, String dest, boolean isCreateDir, String passwd)  {
//	    File destFile=new File(dest);
//		if(destFile.exists()){
//		    destFile.delete();
//		    System.out.println("删除了");
//        }
		try {
            ZipFile zipFile = new ZipFile(dest);
            ZipParameters parameters = buildZipParameters(passwd);
            if (src.isDirectory()) {
            	// 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构
            	if (!isCreateDir) {
            		File[] subFiles = src.listFiles();
            		for (File subFile : subFiles) {
            			if (subFile.isDirectory()) {
            				zipFile.addFolder(subFile, parameters);
            			} else {
            				zipFile.addFile(subFile, parameters);
            			}
            		}
            	} else {
            		zipFile.addFolder(src, parameters);
            	}
            } else {
            	zipFile.addFile(src, parameters);
            }
        } catch (ZipException e) {
            LOGGER.error("压缩文件[{}]信息异常!",dest);
        }
	}

	/**
	 * 压缩zip
	 * 
	 * @param src
	 *            要压缩的文件或文件夹路径
	 * @param dest
	 *            压缩文件存放路径
	 * @param isCreateDir
	 *            是否在压缩文件里创建目录,仅在压缩文件为目录时有效.
	 *            如果为false,将直接压缩目录下文件到压缩文件.
	 * @param passwd
	 *            压缩使用的密码
	 * @throws ZipException
	 */
	public static void zip(String src, String dest, boolean isCreateDir, String passwd) {
		zip(new File(src), dest, isCreateDir, passwd);
	}

	/**
	 * 压缩zip
	 * 
	 * @param src
	 *            要压缩的文件或文件夹路径
	 * @param dest
	 *            压缩文件存放路径
	 * @param isCreateDir
	 *            是否在压缩文件里创建目录,仅在压缩文件为目录时有效.
	 *            如果为false,将直接压缩目录下文件到压缩文件.
	 * @throws ZipException
	 */
	public static void zip(File src, String dest, boolean isCreateDir) {
		zip(src, dest, isCreateDir, null);
	}

	/**
	 * 压缩zip
	 * 
	 * @param src 要压缩的文件或文件夹路径
	 * @param dest 压缩文件存放路径
	 * @param isCreateDir  
	 *                         是否在压缩文件里创建目录,仅在压缩文件为目录时有效.
	 *                         如果为false,将直接压缩目录下文件到压缩文件.
	 * @throws ZipException
	 */
	public static void zip(String src, String dest, boolean isCreateDir) {
		zip(new File(src), dest, isCreateDir, null);
	}
	
}
