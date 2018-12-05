package com.project.basic.excel;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.basic.utils.DateTimeUtil;


/**
 * 生成excel2003和2007格式文件
 */
public class ExcelWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);
    
    // 标题字体
     private static Font titleFont = null; 
    // 标题样式
     private static CellStyle titleStyle = null;
    // 行信息内容样式
     private static CellStyle contentStyle = null;
    //private static final int batchFlushSize = JdbcBaseConfig.batchSize;
     public static final int batchFlushSize = 10000/**10*/;
    public static final String filePath = "D:\\mytemp\\";
    //public static final String filePath = "/haojie/loanFront/";

    public static void main(String[] args) {
        String filename = "数据excel测试表";
        String sheetName = "数据主表";
        String[] titleStrs = { "部门", "城市", "日期", "金额" };

        List<String[]> contentList = new ArrayList<String[]>();
        String[] contents1 = { "财务部", "北京", "2013-07-25", "1000.25" };
        String[] contents2 = { "销售部", "深圳", "2013-08-01", "0.099" };
        String[] contents3 = { "产品部", "天津", "2013-11-17", "18888888" };
        String[] contents4 = { "市场部", "上海", "2013-12-10", "5658978987.135454" };
        contentList.add(contents1);
        contentList.add(contents2);
        contentList.add(contents3);
        contentList.add(contents4);
        
        try {
            LOGGER.warn("解析excel信息开始...");
            ExcelWriter.writeExcel2007(titleStrs, contentList, filename,sheetName,filePath);
        	LOGGER.warn("解析excel信息结束...");
        } catch (IOException e) {
            LOGGER.error("解析excel信息异常...",e);
        }
    }

    /**
     * 2007格式:1048575
     * Invalid row number (-32768) outside allowable range (0..1048575)
     */
    public static void writeExcel2007(String[] titleStrs, List<String[]> contentList,String filename,String sheetName,String filePath) throws IOException {
        if(StringUtils.isBlank(filePath)){
            filePath=ExcelWriter.filePath;
        }
        FileOutputStream fileOut = new FileOutputStream(filePath+filename+"-"+DateTimeUtil.formataDay()+".xlsx");
        
        SXSSFWorkbook wb = new SXSSFWorkbook(500);
         // 执行样式初始化
         setXSSFExcelStyle(wb);
         // TODO 修改位置
         //XSSFSheet sheet = wb.createSheet(sheetName);
         Sheet sheet= wb.getSheet(sheetName);
         if(sheet==null){
             sheet = wb.createSheet(sheetName);
             LOGGER.info("不存在sheet,新创建!");
         }else{
            LOGGER.info("已经存在sheet了!");
         }
         
         int titleCount = titleStrs.length;
//         XSSFRow titleRowHeader = sheet.createRow((short) 0);
//         //设置行高,设置太小可能被隐藏看不到
//         titleRowHeader.setHeight((short)300);
//         // 20像素
//         titleRowHeader.setHeightInPoints(20);
//         XSSFCell titleHeaderCell = titleRowHeader.createCell((short) 0);
//         titleHeaderCell.setCellStyle(titleStyle);
//         titleHeaderCell.setCellType(XSSFCell.CELL_TYPE_STRING);
//         titleHeaderCell.setCellValue(new XSSFRichTextString(sheetName)); 
//         titleHeaderCell.setCellValue(wb.getCreationHelper().createRichTextString(sheetName));
//         
//         //单元格,CellRangeAddress(firstRow,lastRow,firstCol,lastCol)里的参数分别表示需要合并的单元格起始行，起始列 
//         sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleCount-1));  
         
         Row titleRow = sheet.createRow((short)0);
         //设置行高,设置太小可能被隐藏看不到
         titleRow.setHeight((short)300);
         // 20像素
        titleRow.setHeightInPoints(20);
        // 写标题
        for (int k = 0; k < titleCount; k++) {
             Cell cell = titleRow.createCell((short) k); 
             // 设置标题样式
            cell.setCellStyle(titleStyle);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            // 设置列宽
            sheet.setColumnWidth((short) k, (short) 5500);
            // 为单元格赋值
            //cell.setCellValue(titleStrs[k]);
            //cell.setCellValue(new XSSFRichTextString(titleStrs[k])); 
            cell.setCellValue(wb.getCreationHelper().createRichTextString(titleStrs[k]));
        }
        
        // 写内容
        int contentCount = contentList.size();
        for (int i = 0; i < contentCount; i++) {
            String[] contents = contentList.get(i);
             //XSSFRow row = sheet.createRow((short)(i + 1)); 
            Row row = sheet.createRow(i + 1); 
            //XSSFRow row = sheet.createRow(i + 1); 
            for (int j = 0; j < titleCount; j++) {
                 Cell cell = row.createCell((short) j); 
                 // 设置内容样式
                cell.setCellStyle(contentStyle);
                if (contents[j] == null || contents[j].equals("null")) {
                    contents[j] = "";
                }
                // 格式化日位置 TODO 
                if (false) {
                //if (true) {
                     //XSSFCellStyle style = wb.createCellStyle();
                     contentStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(DateTimeUtil.DATE_TIME_PATTON_1));
                     cell.setCellStyle(contentStyle);
                    cell.setCellValue(contents[j]);
                } else {
                    cell.setCellValue(new XSSFRichTextString(contents[j]));
                }
            }
            /*if(i>0&&i%batchFlushSize==0){
                wb.write(fileOut);
                fileOut.flush();
            }*/
        }
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    /**
     * 样式初始化设置
     */
    private static void setXSSFExcelStyle(SXSSFWorkbook workBook) {
        // 设置列标题字体，样式
        titleFont = workBook.createFont();
        titleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
        
        // 标题列样式
        titleStyle = workBook.createCellStyle();
        titleStyle.setFont(titleFont);
        
      //设置背景色
        titleStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());  
        // 自动换行
        titleStyle.setWrapText(false);  
        // 设置边框
        titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);  
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);  
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  
        titleStyle.setBorderRight(CellStyle.BORDER_THIN);  
        titleStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());  
        titleStyle.setBorderLeft(CellStyle.BORDER_THIN);  
        titleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());  
        titleStyle.setBorderTop(CellStyle.BORDER_THIN);  
        titleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());  
        titleStyle.setBorderBottom(CellStyle.BORDER_THIN);  
        titleStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());  
        
        // 内容列样式
        Font contentStyleFont = workBook.createFont();
        contentStyleFont.setFontName("宋体");  
        contentStyleFont.setFontHeightInPoints((short) 11); 
        
        contentStyle = workBook.createCellStyle();
        contentStyle.setFont(contentStyleFont);
        
        //设置背景色
//        contentStyle.setFillForegroundColor(new XSSFColor( new Color(220, 230, 241)));  
        XSSFColor xssfColor = new XSSFColor( new Color(220, 230, 241));
        contentStyle.setFillForegroundColor(xssfColor.getIndexed());  
        // 自动换行
        contentStyle.setWrapText(false);  
        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
        contentStyle.setAlignment(CellStyle.ALIGN_LEFT);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    }

}
