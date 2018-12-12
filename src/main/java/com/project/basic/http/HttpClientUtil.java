package com.project.basic.http;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.project.basic.utils.Constants;
import com.project.basic.utils.FastJsonUtil;


/**
 * http操作工具类
 * 
 * @author caozj
 *
 */
public class HttpClientUtil {
	private static final Log logger = LogFactory.getLog(HttpClientUtil.class);

	private static final int timeout = 6000;
	private static final int fileUploadTimeout = 30000;

	/**
	 * 获取一个HttpClient对象
	 */
	public static HttpClient getHttpClient() {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, Constants.DEFAULT_CHARSET);
		return client;
	}

	/**
	 * 发送get请求(参数不能为中文，如果要使用中文参数，请使用post提交)
	 */
	public static String get(String url) {
		return get(url, getHttpClient());
	}

	/**
	 * 发送get请求(参数不能为中文，如果要使用中文参数，请使用post提交)
	 */
	public static String get(String url, Map<String, String> params) {
		return get(url, params, getHttpClient());
	}

	/**
	 * 发送get请求 请求返回字节
	 * 
	 * @param url
	 * @return
	 */
	public static byte[] getBytes(String url) {
		return getBytes(url, getHttpClient());
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @return
	 */
	public static byte[] getBytes(String url, Map<String, String> params) {
		return getBytes(url, params, getHttpClient());
	}

	/**
	 * 发送get请求(参数不能为中文，如果要使用中文参数，请使用post提交)
	 * 
	 * @param url
	 *            地址
	 * @param client
	 * @return
	 */
	public static String get(String url, HttpClient client) {
		// 设置代理服务器地址和端口
		// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
		// 使用 GET 方法 ，如果服务器需要通过 HTTPS 连接，那只需要将下面 URL 中的 http 换成 https
		String response = null;
		HttpMethod method = new GetMethod(url);
		method.setRequestHeader("Content-type", "text/html; charset=" + Constants.DEFAULT_CHARSET);
		try {
			client.executeMethod(method);
			method = dealRedirect(method);
			response = method.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("发情get请求失败", e);
		} finally {
			// 释放连接
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 发送get请求
	 * 
	 * @param url
	 *            地址
	 * @param client
	 * @return
	 */
	public static byte[] getBytes(String url, HttpClient client) {
		// 设置代理服务器地址和端口
		// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
		// 使用 GET 方法 ，如果服务器需要通过 HTTPS 连接，那只需要将下面 URL 中的 http 换成 https
		byte[] response = null;
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
			method = dealRedirect(method);
			response = method.getResponseBody();
		} catch (Exception e) {
			logger.error("发情get请求失败", e);
		} finally {
			// 释放连接
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 发情get请求(参数不能为中文，如果要使用中文参数，请使用post提交)
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param client
	 * @return
	 */
	public static String get(String url, Map<String, String> params, HttpClient client) {
		url = buildUrl(url, params);
		return HttpClientUtil.get(url, client);
	}

	/**
	 * 发情get请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param client
	 * @return
	 */
	public static byte[] getBytes(String url, Map<String, String> params, HttpClient client) {
		url = buildUrl(url, params);
		return HttpClientUtil.getBytes(url, client);
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            地址
	 * @return
	 */
	public static String post(String url) {
		return post(url, getHttpClient());
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            地址
	 * @param client
	 * @return
	 */
	public static String post(String url, HttpClient client) {
		// 设置代理服务器地址和端口
		// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
		// 使用POST方法
		HttpMethod method = new PostMethod(url);
		method.setRequestHeader("Content-type", "text/html; charset=" + Constants.DEFAULT_CHARSET);
		String response = null;
		try {
			client.executeMethod(method);
			method = dealRedirect(method);
			response = method.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("发送post请求失败", e);
		} finally {
			// 释放连接
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @return
	 */
	public static String post(String url, Map<String, String> params) {
		return post(url, params, getHttpClient());
	}

	/**
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String postJosn(String url, Map<String, Object> params) {
		return postJson(url, params, getHttpClient());
	}

	/**
	 * 发送post请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param client
	 * @return
	 */
	public static String post(String url, Map<String, String> params, HttpClient client) {
		String response = null;
		PostMethod method = new PostMethod(url);
		try {
			method.setRequestBody(convertParams(params));
			client.executeMethod(method);
			method = dealPostRedirect(method);
			response = method.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("发送post请求失败", e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 发送post json数据请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param client
	 * @return
	 */
	public static String postJson(String url, Map<String, Object> params, HttpClient client) {
		String response = null;
		PostMethod method = new PostMethod(url);
		method.setRequestHeader("Content-type", "application/json; charset=" + Constants.DEFAULT_CHARSET);
		try {
			String jsonStr = FastJsonUtil.toJSONString(params);
			RequestEntity requestEntity = new StringRequestEntity(jsonStr, "application/json", Constants.DEFAULT_CHARSET);
			method.setRequestEntity(requestEntity);
			client.executeMethod(method);
			method = dealPostRedirect(method);
			response = method.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("发送postJson请求失败", e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 登陆系统,返回的HttpClient对象已经包含登陆cookie，可以直接访问其他请求
	 * 
	 * @param url
	 *            登陆url地址
	 * @param params
	 *            登陆时所需的所有参数
	 * @return
	 */
	public static HttpClient login(String url, Map<String, String> params) {
		HttpClient client = getHttpClient();
		PostMethod post = new PostMethod(url);
		try {
			post.setRequestBody(convertParams(params));
			client.executeMethod(post);
			String resp = post.getResponseBodyAsString();
			logger.info("登陆返回:" + post.getStatusCode() + "," + resp);
		} catch (Exception e) {
			logger.error("登陆系统失败", e);
		} finally {
			post.releaseConnection();
		}
		return client;
	}

	/**
	 * 提交xml
	 * 
	 * @param url
	 *            地址
	 * @param xmlFile
	 *            xml文件
	 * @return
	 */
	public static String postXML(String url, File xmlFile) {
		return postXML(url, xmlFile, getHttpClient());
	}

	/**
	 * 提交xml
	 * 
	 * @param url
	 *            地址
	 * @param xmlFile
	 *            xml文件
	 * @param client
	 * @return
	 */
	public static String postXML(String url, File xmlFile, HttpClient client) {
		String resp = null;
		PostMethod post = new PostMethod(url);
		try {
			RequestEntity requestEntity = new StringRequestEntity(FileUtils.readFileToString(xmlFile), "application/xml", Constants.DEFAULT_CHARSET);
			post.setRequestEntity(requestEntity);
			// 指定请求内容的类型
			post.setRequestHeader("Content-type", "application/xml; charset=" + Constants.DEFAULT_CHARSET);
			client.executeMethod(post);
			resp = post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("提交xml失败", e);
		} finally {
			post.releaseConnection();
		}
		return resp;
	}

	/**
	 * 提交xml
	 * 
	 * @param url
	 *            地址
	 * @param xml
	 *            xml内容
	 * @return
	 */
	public static String postXML(String url, String xml) {
		return postXML(url, xml, getHttpClient());
	}

	/**
	 * 提交xml
	 * 
	 * @param url
	 *            地址
	 * @param xml
	 *            xml内容
	 * @param client
	 * @return
	 */
	public static String postXML(String url, String xml, HttpClient client) {
		String resp = null;
		PostMethod post = new PostMethod(url);
		try {
			RequestEntity requestEntity = new StringRequestEntity(xml, "application/xml", Constants.DEFAULT_CHARSET);
			post.setRequestEntity(requestEntity);
			// 指定请求内容的类型
			post.setRequestHeader("Content-type", "application/xml; charset=" + Constants.DEFAULT_CHARSET);
			client.executeMethod(post);
			resp = post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("提交xml失败", e);
		} finally {
			post.releaseConnection();
		}
		return resp;
	}

	/**
	 * 提交json数据
	 * 
	 * @param url
	 *            地址
	 * @param json
	 *            json内容
	 * @return
	 */
	public static String postJson(String url, String json) {
		return postJson(url, json, getHttpClient());
	}

	/**
	 * 提交json数据
	 * 
	 * @param url
	 *            地址
	 * @param json
	 *            json内容
	 * @param client
	 * @return
	 */
	public static String postJson(String url, String json, HttpClient client) {
		String resp = null;
		PostMethod post = new PostMethod(url);
		try {
			RequestEntity requestEntity = new StringRequestEntity(json, "application/json", Constants.DEFAULT_CHARSET);
			post.setRequestEntity(requestEntity);
			// 指定请求内容的类型
			post.setRequestHeader("Content-type", "application/json; charset=" + Constants.DEFAULT_CHARSET);
			client.executeMethod(post);
			resp = post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("提交xml失败", e);
		} finally {
			post.releaseConnection();
		}
		return resp;
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	public static String fileUpload(String url, String filePath) {
		return fileUpload(url, filePath, getHttpClient());
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param fileMap
	 *            [fileName:filePath]
	 * @return
	 */
	public static String fileUpload(String url, Map<String, String> fileMap) {
		return fileUpload(url, fileMap, getHttpClient());
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param fileMap
	 *            [fileName:filePath]
	 * @param client
	 * @return
	 */
	public static String fileUpload(String url, Map<String, String> fileMap, HttpClient client) {
		client.getHttpConnectionManager().getParams().setConnectionTimeout(fileUploadTimeout);
		PostMethod filePost = new PostMethod(url);
		Part[] parts = new Part[fileMap.size()];
		String resp = null;
		int i = 0;
		try {
			for (Iterator<Map.Entry<String, String>> iterator = fileMap.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
				parts[i] = new FilePart(entry.getKey(), new File(entry.getValue()));
				i++;
			}
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			client.executeMethod(filePost);
			resp = filePost.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("上传文件失败", e);
		} finally {
			filePost.releaseConnection();
		}
		return resp;
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param filePath
	 *            文件路径
	 * @param client
	 * @return
	 */
	public static String fileUpload(String url, String filePath, HttpClient client) {
		client.getHttpConnectionManager().getParams().setConnectionTimeout(fileUploadTimeout);
		File targetFile = new File(filePath);
		PostMethod filePost = new PostMethod(url);
		String resp = null;
		try {
			Part[] parts = new Part[] { new FilePart("file", targetFile) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			client.executeMethod(filePost);
			resp = filePost.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("上传文件失败", e);
		} finally {
			filePost.releaseConnection();
		}
		return resp;
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param filePath
	 *            文件路径
	 * @param params
	 *            参数
	 * @return
	 */
	public static String fileUpload(String url, String filePath, Map<String, String> params) {
		return fileUpload(url, filePath, params, getHttpClient());
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param fileMap
	 *            [fileName:filePath]
	 * @param params
	 *            参数
	 * @return
	 */
	public static String fileUpload(String url, Map<String, String> fileMap, Map<String, String> params) {
		return fileUpload(url, fileMap, params, getHttpClient());
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param fileMap
	 *            [fileName:filePath]
	 * @param params
	 *            参数
	 * @param client
	 * @return
	 */
	public static String fileUpload(String url, Map<String, String> fileMap, Map<String, String> params, HttpClient client) {
		client.getHttpConnectionManager().getParams().setConnectionTimeout(fileUploadTimeout);
		PostMethod filePost = new PostMethod(url);
		String resp = null;
		try {
			Part[] parts = new Part[params.size() + fileMap.size()];
			int index = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				parts[index] = new StringPart(entry.getKey(), entry.getValue(), Constants.DEFAULT_CHARSET);
				index++;
			}
			for (Iterator<Map.Entry<String, String>> iterator = fileMap.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
				parts[index] = new FilePart(entry.getKey(), new File(entry.getValue()));
				index++;
			}
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			client.executeMethod(filePost);
			resp = filePost.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("上传文件失败", e);
		} finally {
			filePost.releaseConnection();
		}
		return resp;
	}

	/**
	 * 文件上传
	 * 
	 * @param url
	 *            地址
	 * @param filePath
	 *            文件路径
	 * @param params
	 *            参数
	 * @param client
	 * @return
	 */
	public static String fileUpload(String url, String filePath, Map<String, String> params, HttpClient client) {
		client.getHttpConnectionManager().getParams().setConnectionTimeout(fileUploadTimeout);
		File targetFile = new File(filePath);
		PostMethod filePost = new PostMethod(url);
		String resp = null;
		try {
			Part[] parts = new Part[params.size() + 1];
			int index = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				parts[index] = new StringPart(entry.getKey(), entry.getValue(), Constants.DEFAULT_CHARSET);
				index++;
			}
			parts[index] = new FilePart("file", targetFile);
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			client.executeMethod(filePost);
			resp = filePost.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("上传文件失败", e);
		} finally {
			filePost.releaseConnection();
		}
		return resp;
	}

	/**
	 * 处理重定向
	 * 
	 * @param method
	 * @return
	 */
	private static HttpMethod dealRedirect(HttpMethod method) throws HttpException, IOException {
		// 检查是否重定向
		int statuscode = method.getStatusCode();
		if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) || (statuscode == HttpStatus.SC_SEE_OTHER)
				|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			// 读取新的 URL 地址
			Header header = method.getResponseHeader("location");
			if (header != null) {
				method.releaseConnection();
				HttpClient client = getHttpClient();
				String newuri = header.getValue();
				if ((newuri == null) || (newuri.equals(""))) {
					newuri = "/";
				}
				HttpMethod redirect = new GetMethod(newuri);
				client.executeMethod(redirect);
				return dealRedirect(redirect);
			} else {
				return method;
			}
		} else {
			return method;
		}
	}

	/**
	 * 处理重定向
	 * 
	 * @param method
	 * @return
	 */
	private static PostMethod dealPostRedirect(PostMethod method) throws HttpException, IOException {
		// 检查是否重定向
		int statuscode = method.getStatusCode();
		if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) || (statuscode == HttpStatus.SC_SEE_OTHER)
				|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			// 读取新的 URL 地址
			Header header = method.getResponseHeader("location");
			if (header != null) {
				method.releaseConnection();
				HttpClient client = getHttpClient();
				String newuri = header.getValue();
				if ((newuri == null) || (newuri.equals(""))) {
					newuri = "/";
				}
				PostMethod redirect = new PostMethod(newuri);
				client.executeMethod(redirect);
				return dealPostRedirect(redirect);
			} else {
				return method;
			}
		} else {
			return method;
		}
	}

	/**
	 * 讲参数拼接到url后
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private static String buildUrl(String url, Map<String, String> params) {
		StringBuilder queryString = new StringBuilder(url);
		if (url.indexOf("?") > -1) {
			queryString.append("&");
		} else {
			queryString.append("?");
		}
		int index = 0;
		int size = params.size();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			queryString.append(entry.getKey() + "=" + entry.getValue());
			if (index != size - 1) {
				queryString.append("&");
			}
			index++;
		}
		return queryString.toString();
	}

	/**
	 * 参数转化
	 * 
	 * @param params
	 *            参数
	 * @return
	 */
	private static NameValuePair[] convertParams(Map<String, String> params) {
		NameValuePair[] paramPair = new NameValuePair[params.size()];
		int index = 0;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			NameValuePair simcard = new NameValuePair(entry.getKey(), entry.getValue());
			paramPair[index] = simcard;
			index++;
		}
		return paramPair;
	}
}
