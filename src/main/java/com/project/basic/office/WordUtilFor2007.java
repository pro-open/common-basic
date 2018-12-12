package com.project.basic.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

/**
 * word工具类
 * 
 * @author caozj
 * 
 */
public class WordUtilFor2007 {

	/**
	 * 获取word文档的内容
	 * 
	 * @param in
	 *            输入流
	 * @return 读取Word中的内容
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException {
		HWPFDocument hdt = new HWPFDocument(in);
		return hdt.getText().toString();
	}

	/**
	 * 获取word文档的内容
	 * 
	 * @param file
	 *            word文档
	 * @return
	 */
	public static String read(File file) {
		FileInputStream in = null;
		String content = null;
		try {
			in = new FileInputStream(file);
			content = read(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}
		return content;
	}

	/**
	 * 替换word里的字符串
	 * 
	 * @param range
	 *            word对象
	 * @param params
	 *            参数
	 */
	public static void replaceAll(Range range, Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			range.replaceText("${" + entry.getKey() + "}", entry.getValue());
		}
	}

	/**
	 * 将输入流里的word替换字符串之后写到输出流
	 * 
	 * @param out
	 *            输出流
	 * @param in
	 *            输入流
	 * @param params
	 *            替换参数
	 * @throws IOException
	 */
	public static void write(OutputStream out, InputStream in, Map<String, String> params) throws IOException {
		HWPFDocument hdt = new HWPFDocument(in);
		Range range = hdt.getRange();
		replaceAll(range, params);
		hdt.write(out);
	}

	/**
	 * 将原word替换字符串之后写到新的word文档中
	 * 
	 * @param out
	 *            输出word
	 * @param in
	 *            输入word
	 * @param params
	 *            替换参数
	 */
	public static void write(File out, File in, Map<String, String> params) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(in);
			fo = new FileOutputStream(out);
			write(fo, fi, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fi);
			IOUtils.closeQuietly(fo);
		}
	}
}
