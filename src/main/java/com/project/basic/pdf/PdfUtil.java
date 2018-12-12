package com.project.basic.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * pdf工具类
 * 
 * @Author  LiuBao
 * @Version 2.0
 * @Date 2018年12月11日
 */
@SuppressWarnings("unused")
public class PdfUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfUtil.class);

	private static Font keyfont = null;// 设置字体大小
	private static Font textfont = null;// 设置字体大小
	private static int maxWidth = 520;

	static {
		BaseFont bfChinese;
		try {
			bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			keyfont = new Font(bfChinese, 8, Font.BOLD);// 设置字体大小
			textfont = new Font(bfChinese, 8, Font.NORMAL);// 设置字体大小
		} catch (Exception e) {
			LOGGER.error("pdf初始化中文字体出错", e);
		}
	}

	/**
	 * 将内容写到pdf中
	 */
	public static void writPdf(File file, String content) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			writePdf(out, content);
		} catch (Exception e) {
			LOGGER.error("导出pdf内容失败", e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 将内容写到pdf中
	 */
	public static void writePdf(OutputStream out, String content) throws DocumentException {
		Document document = new Document();// 建立一个Document对象
		document.setPageSize(PageSize.A4);// 设置页面大小
		PdfWriter.getInstance(document, out);
		document.open();
		document.add(new Paragraph(content, textfont));
		document.close();
	}

	/**
	 * 导出pdf的table表格
	 * @param file 文件
	 * @param table 表格内容
	 */
	public static void exportTable(File file, List<List<String>> table) throws DocumentException, FileNotFoundException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			exportTable(out, table);
		} catch (Exception e) {
			LOGGER.error("导出pdf表格失败", e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 导出pdf的table表格
	 * @param out 输出流
	 * @param table 表格内容
	 */
	public static void exportTable(OutputStream out, List<List<String>> table) throws DocumentException {
		Document document = new Document();// 建立一个Document对象
		document.setPageSize(PageSize.A4);// 设置页面大小
		PdfWriter.getInstance(document, out);
		document.open();
		int columns = table.get(0).size();
		PdfPTable pdfTable = createTable(columns);
		for (List<String> row : table) {
			for (String cellContent : row) {
				pdfTable.addCell(createCell(cellContent, keyfont, Element.ALIGN_CENTER));
			}
		}
		document.add(pdfTable);
		document.close();
	}

	private static PdfPCell createCell(String value, Font font, int align) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setPhrase(new Phrase(value, font));
		return cell;
	}

	private static PdfPCell createCell(String value, Font font) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPhrase(new Phrase(value, font));
		return cell;
	}

    private static PdfPCell createCell(String value, Font font, int align, int colspan) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		cell.setPhrase(new Phrase(value, font));
		return cell;
	}

	private static PdfPCell createCell(String value, Font font, int align, int colspan, boolean boderFlag) {
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		cell.setPhrase(new Phrase(value, font));
		cell.setPadding(3.0f);
		if (!boderFlag) {
			cell.setBorder(0);
			cell.setPaddingTop(15.0f);
			cell.setPaddingBottom(8.0f);
		}
		return cell;
	}

	private static PdfPTable createTable(int colNumber) {
		PdfPTable table = new PdfPTable(colNumber);
		try {
			table.setTotalWidth(maxWidth);
			table.setLockedWidth(true);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setBorder(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table;
	}

	private static PdfPTable createTable(float[] widths) {
		PdfPTable table = new PdfPTable(widths);
		try {
			table.setTotalWidth(maxWidth);
			table.setLockedWidth(true);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setBorder(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return table;
	}

	private static PdfPTable createBlankTable() {
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(0);
		table.addCell(createCell("", keyfont));
		table.setSpacingAfter(20.0f);
		table.setSpacingBefore(20.0f);
		return table;
	}

}
