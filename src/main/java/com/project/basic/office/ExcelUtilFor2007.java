package com.project.basic.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel工具类
 * 
 * @author caozj
 * 
 */
public class ExcelUtilFor2007 {

	/**
	 * 生成Excel
	 * 
	 * @param excel
	 *            Excel文件对象
	 * @param sheetName
	 *            工作区名称
	 * @param rows
	 *            所有的行数据
	 * @param widthList
	 *            列宽
	 * @param heightList
	 *            行高
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void write(File excel, String sheetName, List<List<String>> rows, List<Integer> widthList,
			List<Short> heightList) throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = buidWorkbook(sheetName, rows, widthList, heightList);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(excel);
			workbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 生成Excel
	 * 
	 * @param out
	 *            输出流
	 * @param sheetName
	 *            工作区名称
	 * @param rows
	 *            所有的行数据
	 * @param widthList
	 *            列宽
	 * @param heightList
	 *            行高
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void write(OutputStream out, String sheetName, List<List<String>> rows, List<Integer> widthList,
			List<Short> heightList) throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = buidWorkbook(sheetName, rows, widthList, heightList);
		workbook.write(out);
	}

	private static XSSFWorkbook buidWorkbook(String sheetName, List<List<String>> rows, List<Integer> widthList,
			List<Short> heightList) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		if (widthList != null && widthList.size() > 0) {
			int columnSize = widthList.size();
			for (int c = 0; c < columnSize; c++) {
				sheet.setColumnWidth(c, widthList.get(c));
			}
		}
		int rowNum = rows.size();
		for (int i = 0; i < rowNum; i++) {
			XSSFRow row = sheet.createRow(i);
			if (heightList != null) {
				row.setHeight(heightList.get(i));
			}
			List<String> rowContent = rows.get(i);
			int columnLength = rowContent.size();
			for (int k = 0; k < columnLength; k++) {
				XSSFCell cell = row.createCell(k);
				cell.setCellValue(rowContent.get(k));
			}
		}
		return workbook;
	}

	/**
	 * 生成Excel
	 * 
	 * @param out
	 *            输出流
	 * @param sheetName
	 *            工作区名称
	 * @param rows
	 *            所有的行数据
	 * @param widthList
	 *            列宽
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void write(OutputStream out, String sheetName, List<List<String>> rows, List<Integer> widthList)
			throws FileNotFoundException, IOException {
		write(out, sheetName, rows, widthList, null);
	}

	/**
	 * 生成Excel
	 * 
	 * @param excel
	 *            Excel文件对象
	 * @param sheetName
	 *            工作区名称
	 * @param rows
	 *            所有的行数据
	 * @param widthList
	 *            列宽
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void write(File excel, String sheetName, List<List<String>> rows, List<Integer> widthList)
			throws FileNotFoundException, IOException {
		write(excel, sheetName, rows, widthList, null);
	}

	/**
	 * 生成Excel
	 * 
	 * @param out
	 *            输出流
	 * @param sheetName
	 *            工作区名称
	 * @param rows
	 *            所有的行数据
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void write(OutputStream out, String sheetName, List<List<String>> rows) throws FileNotFoundException,
			IOException {
		write(out, sheetName, rows, null, null);
	}

	/**
	 * 生成Excel
	 * 
	 * @param excel
	 *            Excel文件对象
	 * @param sheetName
	 *            工作区名称
	 * @param rows
	 *            所有的行数据
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void write(File excel, String sheetName, List<List<String>> rows) throws FileNotFoundException,
			IOException {
		write(excel, sheetName, rows, null, null);
	}

	/**
	 * 读取Excel返回内容
	 * 
	 * @param excel
	 *            Excel文件对象
	 * @param sheetIndex
	 *            工作区索引
	 * @return
	 */
	public static List<List<String>> read(File excel, int sheetIndex) {
		FileInputStream file = null;
		try {
			file = new FileInputStream(excel);
			return read(file, sheetIndex);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(file);
		}
		return new ArrayList<List<String>>(0);
	}

	/**
	 * 读取Excel返回内容
	 * 
	 * @param excel
	 *            Excel文件对象
	 * @return
	 */
	public static List<List<String>> read(File excel) {
		return read(excel, 0);
	}

	/**
	 * 读取Excel返回内容
	 * 
	 * @param in
	 *            输入流
	 * @param sheetIndex
	 *            工作区索引
	 * @return
	 * @throws IOException
	 */
	public static List<List<String>> read(InputStream in, int sheetIndex) throws IOException {
		List<List<String>> rows = new ArrayList<List<String>>();
		XSSFWorkbook wb = new XSSFWorkbook(in);
		XSSFSheet sh = wb.getSheetAt(sheetIndex);
		for (int i = 0; true; i++) {
			XSSFRow ro = sh.getRow(i);
			if (ro == null) {
				break;
			}
			List<String> row = new ArrayList<String>();
			for (int j = 0; true; j++) {
				XSSFCell cell = ro.getCell(j);
				if (cell == null) {
					break;
				}
				row.add(cell.toString());
			}
			rows.add(row);
		}
		return rows;
	}

	/**
	 * 读取Excel返回内容
	 * 
	 * @param in
	 *            输入流
	 * @return
	 * @throws IOException
	 */
	public static List<List<String>> read(InputStream in) throws IOException {
		return read(in, 0);
	}

}
