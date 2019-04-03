package com.project.basic.excel;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.project.basic.utils.DateTimeUtil;

/**
 * 1.导出文件<不限于excel>下载信息,当前仅用于下载消息头部分封装 2.导如excel2003和2007格式文件
 * 
 * 导出: 入参为: 标题集合:String[] titleArray 对象集合:List<String[]> contentArrayList
 * 求和标记:底部数据求和:sumFlag 求和结果集合:String[] sumArray 导入: 入参为excel对象或InputStream对象:
 * 且根据标记,自动判断是根据其首行或第二行进行字段名匹配:Boolean
 * isFirstRow,true为首行为属性和标题行,false为第一行为标题行,第二行为属性行; 返回值为List<Map
 * <String,String>>集合对:
 * 
 * 2.封装下载文件的头信息,适用于多种格式的文件
 * 
 */
public class FileWriterUtils2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWriterUtils2.class);

    public static String filePathDefaule = "D:\\mytemp\\";//默认目录
    public static String sheetNameDefaule = "DefauleSheet";

    // 标题样式/字体
    private static CellStyle titleStyle = null;
    private static Font titleFont = null;
    // 信息内容行样式/字体
    private static CellStyle contentStyle = null;
    private static Font contentFont = null;
    // 求和行样式
    private static CellStyle sumStyle = null;
    private static Font sumFont = null;
    
   // 创建日期显示格式
    private static CellStyle dateStyle =null;

    /**
     * 简化的方法调用入口
     * 无typeArray,sumFlag和sumArray
     */
    public static String writeExcel2007(String fileName, String[] titleArray, List<String[]> contentArrayList,String sheetName, String filePath) {
        return writeExcel2007(fileName, titleArray, contentArrayList, null, sheetName, false, null, filePath);
    }
    
    /**
     * 
     * 简化的方法调用入口
     * 无typeArray,sumFlag和sumArray为动态传入
     */
    public static String writeExcel2007(String fileName, String[] titleArray, List<String[]> contentArrayList,String sheetName, boolean sumFlag, String[] sumArray, String filePath) {
        return writeExcel2007(fileName, titleArray, contentArrayList, null, sheetName, sumFlag, sumArray, filePath);
    }
    
    /**
     * 2007格式:1048575 Invalid row number (-32768) outside allowable range
     * (0..1048575)
     */
    public static String writeExcel2007(String fileName, String[] titleArray, List<String[]> contentArrayList,Integer typeArray[],String sheetName, boolean sumFlag, String[] sumArray, String filePath) {
        //数据长度校验 
        int titleCount = titleArray==null?0:titleArray.length;
        int typeCount = typeArray==null?0:typeArray.length;
        int sumCount = sumArray==null?0:sumArray.length;
        if(sumFlag){
            if(sumCount<=0||(typeCount>0&&typeCount!=sumCount)||(titleCount>0&&titleCount!=sumCount)){
               throw new RuntimeException("标题行数据/求和行数据/行数据类型初始化长度不一致!"); 
            }
        }else {
            if(typeCount>0&&titleCount>0&&typeCount!=titleCount){
                throw new RuntimeException("标题行数据/求和行数据/行数据类型初始化长度不一致!"); 
             }
        }
        
        if (StringUtils.isBlank(sheetName)) {
            sheetName = sheetNameDefaule;
        }
        if (StringUtils.isBlank(filePath)) {
            filePath = filePathDefaule;
        }
        String finalName = DateTimeUtil.formatDate2Str(new Date(), DateTimeUtil.DATE_TIME_PATTON_3) + ".xlsx";
        if (StringUtils.isNoneBlank(fileName)) {
            finalName = filePath + File.separator + fileName + "-" + finalName;
        } else {
            finalName = filePath + File.separator + finalName;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("生成的最终文件全路径信息为:{}", finalName);
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook(500);
        //设置表格样式
        setXSSFExcelStyle(workbook);
        // Sheet sheet = wb.createSheet(sheetName);
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            LOGGER.info("不存在sheet,新创建!");
        } else {
            LOGGER.info("已经存在sheet了,直接使用!");
        }

        //创建标题行
        createTitleRow(titleArray, workbook, sheet);
        //创建内容行
        createContentRows(contentArrayList, typeArray,workbook, sheet);
        if (sumFlag) {
            //创建求和行
            createSumRow(sumArray, workbook, sheet);
        }

        FileOutputStream fileOut = null;
        //ByteArrayOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(finalName);
            //fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.flush();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("导出excel文件正常结束!");
            }
        } catch (IOException e) {
            LOGGER.error("导出excel文件异常!",e);
        } finally {
            try {
                if(null!=fileOut){
                    fileOut.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭管道流fileOut信息异常!",e);
            }
        }
        return finalName;
    }
    
    /**
     * 简化的方法调用入口
     * 无typeArray,sumFlag和sumArray
     */
    public static ByteArrayOutputStream writeStreamExcel2007(String fileName, String[] titleArray, List<String[]> contentArrayList,String sheetName, String filePath) {
        return writeStreamExcel2007(fileName, titleArray, contentArrayList, null, sheetName, false, null, filePath);
    }
    
    /**
     * 
     * 简化的方法调用入口
     * 无typeArray,sumFlag和sumArray为动态传入
     */
    public static ByteArrayOutputStream writeStreamExcel2007(String fileName, String[] titleArray, List<String[]> contentArrayList,String sheetName, boolean sumFlag, String[] sumArray, String filePath) {
        return writeStreamExcel2007(fileName, titleArray, contentArrayList, null, sheetName, sumFlag, sumArray, filePath);
    }
    
    public static ByteArrayOutputStream writeStreamExcel2007(String fileName, String[] titleArray, List<String[]> contentArrayList,Integer typeArray[],String sheetName, boolean sumFlag, String[] sumArray, String filePath) {
        //数据长度校验 
        int titleCount = titleArray==null?0:titleArray.length;
        int typeCount = typeArray==null?0:typeArray.length;
        int sumCount = sumArray==null?0:sumArray.length;
        if(sumFlag){
            if(sumCount<=0||(typeCount>0&&typeCount!=sumCount)||(titleCount>0&&titleCount!=sumCount)){
                throw new RuntimeException("标题行数据/求和行数据/行数据类型初始化长度不一致!"); 
            }
        }else {
            if(typeCount>0&&titleCount>0&&typeCount!=titleCount){
                throw new RuntimeException("标题行数据/求和行数据/行数据类型初始化长度不一致!"); 
            }
        }
        
        if (StringUtils.isBlank(sheetName)) {
            sheetName = sheetNameDefaule;
        }
        if (StringUtils.isBlank(filePath)) {
            filePath = filePathDefaule;
        }
        String finalName = DateTimeUtil.formatDate2Str(new Date(), DateTimeUtil.DATE_TIME_PATTON_3) + ".xlsx";
        if (StringUtils.isNoneBlank(fileName)) {
            finalName = filePath + File.separator + fileName + "-" + finalName;
        } else {
            finalName = filePath + File.separator + finalName;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("生成的最终文件全路径信息为:{}", finalName);
        }
        
        SXSSFWorkbook workbook = new SXSSFWorkbook(500);
        //设置表格样式
        setXSSFExcelStyle(workbook);
        // Sheet sheet = wb.createSheet(sheetName);
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            LOGGER.info("不存在sheet,新创建!");
        } else {
            LOGGER.info("已经存在sheet了,直接使用!");
        }
        
        //创建标题行
        createTitleRow(titleArray, workbook, sheet);
        //创建内容行
        createContentRows(contentArrayList, typeArray,workbook, sheet);
        if (sumFlag) {
            //创建求和行
            createSumRow(sumArray, workbook, sheet);
        }
        
        //FileOutputStream fileOut = null;
        ByteArrayOutputStream fileOut = null;
        try {
            //fileOut = new FileOutputStream(finalName);
            fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.flush();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("导出excel文件正常结束!");
            }
        } catch (IOException e) {
            LOGGER.error("导出excel文件异常!",e);
        } finally {
            try {
                if(null!=fileOut){
                    fileOut.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭管道流fileOut信息异常!",e);
            }
        }
        return fileOut;
    }

    /**
     * 创建内容行
     */
    private static void createContentRows(List<String[]> contentArrayList,Integer typeArray[],SXSSFWorkbook workBook, Sheet sheet) {
        if(CollectionUtils.isEmpty(contentArrayList)){
            return;
        }
        int lastRowNum = sheet.getLastRowNum();
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (lastRowNum > 0) {
            lastRowNum++;
        } else if (physicalNumberOfRows>=1){
            lastRowNum = physicalNumberOfRows;
        }else{
            lastRowNum = 0;
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("createContentRows当前计算得到的lastRowNum的值为:{}", lastRowNum);
        }
        // 写内容
        int contentCount = contentArrayList.size();
        for (int i = 0; i < contentCount; i++) {
            String[] contents = contentArrayList.get(i);
            if(ArrayUtils.isEmpty(typeArray)){
                typeArray=new Integer[contents.length];
            }
            Row row = sheet.createRow(i + lastRowNum);
            for (int j = 0; j < contents.length; j++) {
                Cell cell = row.createCell(j);
                // 设置内容样式
                if (StringUtils.isBlank(contents[j] )|| StringUtils.equals(contents[j], "null")) {
                    contents[j] = StringUtils.EMPTY;
                }
                if(null==typeArray[j]){
                    typeArray[j]=CELL_TYPE_STRING;
                }
                // 格式化日位置,根据传入的日期标题行标记判断 TODO
                switch (typeArray[j]) {
                case CELL_TYPE_STRING:
                    cell.setCellStyle(contentStyle);
                    cell.setCellValue(new XSSFRichTextString(contents[j]));
                    break;
                case CELL_TYPE_NUMERIC:
                    cell.setCellStyle(contentStyle);
                    cell.setCellValue(new XSSFRichTextString(contents[j]));
                    break;
                case CELL_TYPE_FORMULA:
                    cell.setCellStyle(contentStyle);
                    cell.setCellValue(new XSSFRichTextString(contents[j]));
                    break;
                default:
                    cell.setCellStyle(dateStyle);
                    cell.setCellValue(contents[j]);
                    break;
                }
            }
            /*
             * if(i>0&&i%batchFlushSize==0){
             *  wb.write(fileOut); fileOut.flush();
             * }
             */
        }
    }

    /**
     * 创建标题行
     */
    private static void createTitleRow(String[] titleArray, SXSSFWorkbook workbook, Sheet sheet) {
        if(ArrayUtils.isEmpty(titleArray)){
            return;
        }
        int lastRowNum = sheet.getLastRowNum();
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (lastRowNum > 0) {
            lastRowNum++;
        } else if (physicalNumberOfRows>=1){
            lastRowNum = physicalNumberOfRows;
        }else{
            lastRowNum = 0;
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("createTitleRow当前计算得到的lastRowNum的值为:{}", lastRowNum);
        }
        // 写汇总行
        Row sumRow = sheet.createRow(lastRowNum);
        // 设置行高,设置太小可能被隐藏看不到
        sumRow.setHeight((short) 300);
        // 20像素
        sumRow.setHeightInPoints(20);
        // 写标题
        for (int k = 0; k < titleArray.length; k++) {
            Cell cell = sumRow.createCell(k);
            // 设置标题样式
            cell.setCellStyle(titleStyle);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            // 设置列宽
            sheet.setColumnWidth(k, 5500);
            // 为单元格赋值
            // cell.setCellValue(titleArray[k]);
            // cell.setCellValue(new XSSFRichTextString(titleArray[k]));
            cell.setCellValue(workbook.getCreationHelper().createRichTextString(titleArray[k]));
        }
        // 单元格,CellRangeAddress(firstRow,lastRow,firstCol,lastCol)里的参数分别表示需要合并的单元格起始行，起始列
        // sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleCount-1));
    }

    /**
     * 创建求和行
     */
    private static void createSumRow(String[] sumArray, SXSSFWorkbook workbook, Sheet sheet) {
        if(ArrayUtils.isEmpty(sumArray)){
            return;
        }
        int lastRowNum = sheet.getLastRowNum();
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (lastRowNum > 0) {
            lastRowNum++;
        } else if (physicalNumberOfRows>=1){
            lastRowNum = physicalNumberOfRows;
        }else{
            lastRowNum = 0;
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("createSumRow当前计算得到的lastRowNum的值为:{}", lastRowNum);
        }
        // 写汇总行
        Row sumRow = sheet.createRow(lastRowNum);
        // 设置行高,设置太小可能被隐藏看不到
        sumRow.setHeight((short) 300);
        // 20像素
        sumRow.setHeightInPoints(20);
        // 写标题
        for (int k = 0; k < sumArray.length; k++) {
            Cell cell = sumRow.createCell(k);
            // 设置标题样式
            cell.setCellStyle(sumStyle);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            // 设置列宽
            sheet.setColumnWidth(k, 5500);
            // 为单元格赋值
            // cell.setCellValue(sumArray[k]);
            //cell.setCellValue(new XSSFRichTextString(sumArray[k]));
            cell.setCellValue(workbook.getCreationHelper().createRichTextString(sumArray[k]));
        }

    }
    
//    /**
//     * 创建文档基础信息
//     */
//    private static void buildHSSFWorkbook(SXSSFWorkbook workBook) {
//        if(workBook==null){
//            return;
//        }
//        // 2.创建文档摘要
//        workBook.createDrawingPatriarch();
//        // 3.获取文档信息，并配置
//        DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();
//        // 3.1文档类别
//        dsi.setCategory("项目信息");
//        // 3.2设置文档管理员
//        dsi.setManager("刘保");
//        // 3.3设置组织机构
//        dsi.setCompany("XXX集团");
//        // 4.获取摘要信息并配置
//        SummaryInformation si = workbook.getSummaryInformation();
//        // 4.1设置文档主题
//        si.setSubject("项目信息表");
//        // 4.2.设置文档标题
//        si.setTitle("项目信息");
//        // 4.3 设置文档作者
//        si.setAuthor("XXX集团");
//        // 4.4设置文档备注
//        si.setComments("备注信息暂无");
//    }

    /**
     * 样式初始化设置
     */
    private static void setXSSFExcelStyle(SXSSFWorkbook workBook) {
        // 设置列标题字体，样式
        titleFont = workBook.createFont();
        titleFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);// 加粗
        
        titleStyle = workBook.createCellStyle();
        titleStyle.setFont(titleFont);

        // 设置背景色
        //titleStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        // 自动换行
        //titleStyle.setWrapText(false);
        titleStyle.setWrapText(true);
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
        contentFont = workBook.createFont();
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short) 11);

        contentStyle = workBook.createCellStyle();
        contentStyle.setFont(contentFont);

        // 设置背景色
        XSSFColor xssfColor = new XSSFColor(new Color(220, 230, 241));
        contentStyle.setFillForegroundColor(xssfColor.getIndexed());
        // 自动换行
        contentStyle.setWrapText(false);
        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
        //contentStyle.setAlignment(CellStyle.ALIGN_LEFT);
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        //设置接收换行操作,换行符号为"\n"
        contentStyle.setWrapText(true);
        
        // sum行列样式
        sumFont = workBook.createFont();
        sumFont.setFontName("宋体");
        sumFont.setFontHeightInPoints((short) 11);

        sumStyle = workBook.createCellStyle();
        sumStyle.setFont(sumFont);
        
        // 设置背景色
        sumStyle.setFillForegroundColor(xssfColor.getIndexed());
        // 自动换行
        sumStyle.setWrapText(false);
        sumStyle.setBorderTop(CellStyle.BORDER_THIN);
        sumStyle.setBorderBottom(CellStyle.BORDER_THIN);
        sumStyle.setBorderLeft(CellStyle.BORDER_THIN);
        sumStyle.setBorderRight(CellStyle.BORDER_THIN);
        sumStyle.setAlignment(CellStyle.ALIGN_LEFT);
        sumStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        
        //日期格式初始化:m/d/yy
        dateStyle = workBook.createCellStyle();
        dateStyle.setDataFormat(workBook.getCreationHelper().createDataFormat().getFormat(DateTimeUtil.DATE_PATTON_1));
        //dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(DateTimeUtil.DATE_PATTON_1));
        dateStyle.setFont(contentFont);
        
         // 设置背景色
        dateStyle.setFillForegroundColor(xssfColor.getIndexed());
        // 自动换行
        dateStyle.setWrapText(false);
        dateStyle.setBorderTop(CellStyle.BORDER_THIN);
        dateStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dateStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dateStyle.setBorderRight(CellStyle.BORDER_THIN);
        dateStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dateStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    }
    
    /**
     * 封装下载信息头格式对象
     */
    public static ResponseEntity<byte[]> fillDownloadFileInfo(String fileName, String fileFullPath) {
        HttpHeaders headers =new HttpHeaders();
        ByteArrayOutputStream baOutput = new ByteArrayOutputStream();
        //封装具体的文件流信息即可
        //byte[] byteArray =new byte[]{};
        try {
            File localFile = new File(fileFullPath);
            //byteArray = FileCopyUtils.copyToByteArray(localFile);
            //这里不必处理IO流关闭的问题，因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
//            FileUtils.copyURLToFile(source, destination); TODO
            org.apache.commons.io.FileUtils.copyFile(localFile, baOutput);
//            FileCopyUtils.copy(FileCopyUtils.copyToByteArray(localFile), baOutput);
            //byteArray = baOutput.toByteArray();
            
            //获取文件类型 TODO
            String contentType = Files.probeContentType(Paths.get(localFile.toURI()));
            String suffix = fileFullPath.substring(fileFullPath.lastIndexOf("."));
            switch (contentType) {
            case IMAGE_PNG_VALUE:
                suffix=".png";
                break;
            case IMAGE_JPEG_VALUE:
                suffix=".jpg";
                break;
            case IMAGE_GIF_VALUE:
                suffix=".gif";
                break;
            case APPLICATION_PDF_VALUE:
                suffix=".pdf";
                break;
            case "application/x-zip-compressed":
                suffix=".zip";
                break;
            default:
                //suffix="";
                break;
            }
            if(!StringUtils.contains(fileName, suffix)){
                fileName+=suffix;
            }
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", new String((fileName).getBytes("UTF-8"), "iso-8859-1"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("导出文件,设置头信息编码方式异常!",e);
        }catch (Exception e) {
            LOGGER.error("导出文件,拷贝文件流异常!",e);
        }finally{
             if(null!=baOutput){
             try {
             baOutput.close();
             } catch (IOException e) {
             LOGGER.error("导出文件,关闭文件流异常!",e);
             }
             }
        }

        return new ResponseEntity<byte[]>(baOutput.toByteArray(), headers, HttpStatus.CREATED);
//        return new ResponseEntity<byte[]>(byteArray, headers, HttpStatus.CREATED);
    }
    
    /**
     * 封装下载信息头格式对象
     */
    @Deprecated
    public static ResponseEntity<byte[]> fillDownloadStreamInfo(String fileName, ByteArrayOutputStream outputStream,String suffix) {
        return fillDownloadStreamInfo(fileName, outputStream.toByteArray(), suffix);
    }
    
    public static ResponseEntity<byte[]> fillDownloadStreamInfo(String fileName, byte[] byteBuffer,String suffix) {
        HttpHeaders headers =new HttpHeaders();
//        ByteArrayOutputStream baOutput = new ByteArrayOutputStream();
        //封装具体的文件流信息即可
        try {
            if(!StringUtils.contains(fileName, suffix)&&StringUtils.isNoneBlank(suffix)){
                fileName+=suffix;
            }
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", new String((fileName).getBytes("UTF-8"), "iso-8859-1"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("导出文件,设置头信息编码方式异常!",e);
        }catch (Exception e) {
            LOGGER.error("导出文件,拷贝文件流异常!",e);
        }finally{
//            if(null!=baOutput){
//                try {
//                    baOutput.close();
//                } catch (IOException e) {
//                    LOGGER.error("导出文件,关闭文件流异常!",e);
//                }
//            }
        }
        
        return new ResponseEntity<byte[]>(byteBuffer, headers, HttpStatus.CREATED);
//        return new ResponseEntity<byte[]>(byteBuffer, headers, HttpStatus.CREATED);
    }

    
    public static void setFilePathDefaule(String filePathDefaule) {
        FileWriterUtils2.filePathDefaule = filePathDefaule;
    }

    public static void main(String[] args) {
        String fileName = "数据excel测试表";
        String sheetName = "数据主表";
        String[] titleArray = { "部门", "城市", "日期", "金额" };
        String[] sumArray = { "总计", "---", "---", "99999" };
        Integer typeArray[] = {CELL_TYPE_STRING,CELL_TYPE_STRING,CELL_TYPE_FORMULA, CELL_TYPE_NUMERIC};

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
            FileWriterUtils2.writeExcel2007(fileName, titleArray, contentList,typeArray, sheetName, Boolean.TRUE, sumArray,filePathDefaule);
            TimeUnit.SECONDS.sleep(5);
            FileWriterUtils2.writeExcel2007(fileName, titleArray, contentList, sheetName,filePathDefaule);
            LOGGER.warn("解析excel信息结束...");
        } catch (Exception e) {
            LOGGER.error("解析excel信息异常...", e);
        }

    }

}