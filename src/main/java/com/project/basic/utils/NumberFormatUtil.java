package com.project.basic.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 格式化工具类:多种实现方式
 * 
 * @Author LiuBao
 * @Version 2.0
 * @Date 2018年12月12日
 */
public class NumberFormatUtil {

    // 1、使用 String 类的静态 format()方法 来确定 double 数据类型的精度
    public static String userString(double n,int scale) {
        if(scale<0){
            scale=0;
        }
        return String.format("%."+scale+"f", n);
    }

    /**
     * 注意和BigDecimal.ZERO进行比较的时候,使用compareTo方法,而不能使用equals方法!!!
     */
    // 2、使用 DecimalFormat 对象的 format()方法
    public static String userDecimalFormat(BigDecimal number,int scale) {
        if(number==null){
            number=BigDecimal.ZERO;
        }
        if(BigDecimal.ZERO.compareTo(number)==0){
            return "0";
        }
        if(scale<0){
            scale=0;
        }
        String str="";
        while(str.length()<scale){
            str+="0";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#."+str);
        return decimalFormat.format(number);
    }
//    public static String userDecimalFormat(double n,int scale) {
//        if(scale<0){
//            scale=0;
//        }
//        String str="";
//        while(str.length()<scale){
//            str+="0";
//        }
//        DecimalFormat decimalFormat = new DecimalFormat("#."+str);
//        return decimalFormat.format(n);
//    }

    // 3、使用 BigDecimal 对象的 setScale()方法
    public static double userBigDecimal(double n,int scale) {
        if(scale<0){
            scale=0;
        }
        BigDecimal bigDecimal = new BigDecimal(n);
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    // 4、使用 NumberFormat 对象的 setMaximumFractionDigits()和format()方法
    public static String userNumberFormat(double n,int scale) {
        if(scale<0){
            scale=0;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(scale);
        return numberFormat.format(n);
    }

    // 5、使用 Math 类的静态 round()方法
    public static double userMath(double n,int scale) {
        if(scale<0){
            scale=0;
        }
        return (double) (Math.round(n * Math.pow(10, scale)) / Math.pow(10, scale)*1.0);
    }
    
    public static void main(String[] args) {
        double n=3.5698000;
        int scale=5;
        System.out.println(userString(n,scale));
        System.out.println(userDecimalFormat(BigDecimal .valueOf(n),scale));
        System.out.println(userBigDecimal(n,scale));
        System.out.println(userNumberFormat(n,scale));
        scale=5;
        System.out.println(userMath(n,scale));
    }

}