//package com.project.basic.office;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.poi.util.IOUtils;
//import org.apache.xerces.impl.dv.util.Base64;
//
//import com.caozj.framework.util.freemarker.FreemarkerUtil;
//import com.caozj.model.constant.ConstantData;
//
//import freemarker.core.ParseException;
//import freemarker.template.MalformedTemplateNameException;
//import freemarker.template.TemplateException;
//import freemarker.template.TemplateNotFoundException;
//
///**
// * 复杂的word操作工具类(包括使用freemark语法解析word，插入表格，插入图表，插入图片等)
// * 
// * @author caozj
// *
// */
//public class WordUtil {
//
//  /**
//   * 使用freemarker语法解析word(word必须先使用xml格式另存为)
//   * 
//   * @param content word文件的xml格式内容
//   * @param param 参数
//   * @return
//   * @throws TemplateNotFoundException
//   * @throws MalformedTemplateNameException
//   * @throws ParseException
//   * @throws IOException
//   * @throws TemplateException
//   */
//  public static String parseByFreemarker(String content, Map<String, Object> param)
//      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
//      TemplateException {
//    content = content.replaceAll("<", "@lt;").replaceAll(">", "@gt;");
//    content = content.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//    String word = FreemarkerUtil.parseContent(content, param);
//    word = word.replaceAll("@lt;", "<").replaceAll("@gt;", ">");
//    return word;
//  }
//
//  /**
//   * 使用freemarker语法解析word(word必须先使用xml格式另存为)
//   * 
//   * @param inWord xml格式的输入word
//   * @param outWord xml格式的输出word
//   * @param param 参数
//   * @throws TemplateNotFoundException
//   * @throws MalformedTemplateNameException
//   * @throws ParseException
//   * @throws IOException
//   * @throws TemplateException
//   */
//  public static void parseByFreemarker(File inWord, File outWord, Map<String, Object> param)
//      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
//      TemplateException {
//    String content = FileUtils.readFileToString(inWord, ConstantData.DEFAULT_CHARSET);
//    String word = parseByFreemarker(content, param);
//    FileUtils.writeStringToFile(outWord, word);
//  }
//
//  /**
//   * 使用普通字符串替换word的xml内容
//   * 
//   * @param content xml格式的输入word内容
//   * @param params 替换参数
//   * @return
//   */
//  public static String replaceText(String content, Map<String, String> params) {
//    for (Map.Entry<String, String> entry : params.entrySet()) {
//      content = content.replace("${" + entry.getKey() + "}", entry.getValue());
//    }
//    return content;
//  }
//
//  /**
//   * 获取图片要插入到word的数据
//   * 
//   * @param file 图片文件地址
//   * @return
//   * @throws FileNotFoundException
//   * @throws IOException
//   */
//  public static String getImageForWord(String file) throws FileNotFoundException, IOException {
//    byte[] data = IOUtils.toByteArray(new FileInputStream(file));
//    return Base64.encode(data);
//  }
//
//  /**
//   * 获取图片要插入到word的数据
//   * 
//   * @param in 图片输入流
//   * @return
//   * @throws FileNotFoundException
//   * @throws IOException
//   */
//  public static String getImageForWord(InputStream in) throws FileNotFoundException, IOException {
//    byte[] data = IOUtils.toByteArray(in);
//    return Base64.encode(data);
//  }
//
//  /**
//   * 替换word里的图片占位符
//   * 
//   * @param wordContent word内容
//   * @param key 占位符，在word中为@{image_key}
//   * @param imageContent 图片内容
//   * @return
//   */
//  public static String replaceImage(String wordContent, String key, String imageContent) {
//    wordContent = wordContent.replace("@{image_" + key + "}", imageContent);
//    return wordContent;
//  }
//
//  /**
//   * 替换word里的表格占位符
//   * 
//   * @param wordContent word内容
//   * @param key 占位符，在word中为@{table_key}
//   * @param rows 行数据
//   * @param header 表头
//   * @return
//   * @throws TemplateNotFoundException
//   * @throws MalformedTemplateNameException
//   * @throws ParseException
//   * @throws IOException
//   * @throws TemplateException
//   */
//  public static String replaceTable(String wordContent, String key, List<List<String>> rows,
//      List<String> header) throws TemplateNotFoundException, MalformedTemplateNameException,
//      ParseException, IOException, TemplateException {
//    Map<String, Object> param = new HashMap<>();
//    param.put("rows", rows);
//    param.put("header", header);
//    String tableContent = FreemarkerUtil.parseFile("word/table.ftl", param);
//    wordContent = wordContent.replace("@{table_" + key + "}", tableContent);
//    return wordContent;
//  }
//
//}
