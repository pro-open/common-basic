package com.project.basic.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日期处理工具类
 * 
 * @Author  LiuBao
 * @Version 2.0
 *   2017年4月10日
 */
public class DateTimeUtil {

	private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
	/**
	 * 静态常量
	 */
	public static final String DATE_PATTON_1 = "yyyy-MM-dd";
	public static final String DATE_PATTON_2 = "yyyy/MM/dd";
	public static final String DATE_PATTON_3 = "yyyyMMdd";
	public static final String DATE_PATTON_4 = "yyMMdd";
	public static final String DATE_PATTON_5 = "yyyy";
	public static final String DATE_TIME_PATTON_1 = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_PATTON_2 = "yyyy/MM/dd HH:mm:ss";
	public static final String DATE_TIME_PATTON_3 = "yyyyMMddHHmmss";
	public static final String DATE_TIME_PATTON_4 = "yyyy-MM-dd HH:mm:ss:SSS";
//    public static final Date DATETIME_ZERO = formatStr2Date("1970-01-01");
//    public static final Timestamp TIMESTAMP_ZERO = formatStr2Timestamp("1970-01-01 10:00:00");
    public static final String DATETIME_ZERO = "1970-01-01";
    public static final String TIMESTAMP_ZERO = "1970-01-01 00:00:00";
    public static final String DATE_TIME_PATTON_1_REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
    
	public static final int C_ONE_SECOND = 1000;
	public static final int C_ONE_MINUTE = 60 * C_ONE_SECOND;
	public static final long C_ONE_HOUR = 60 * C_ONE_MINUTE;
	public static final long C_ONE_DAY = 24 * C_ONE_HOUR;

	public static final int DATE_SECOND = Calendar.SECOND;// 秒
	public static final int DATE_MINUTE = Calendar.MINUTE;// 分钟
	public static final int DATE_HOUR = Calendar.HOUR;// 小时
	public static final int DATE_DAY = Calendar.DATE;// 日
	public static final int DATE_MONTH = Calendar.MONTH;// 月
	public static final int DATE_YEAR = Calendar.YEAR;// 年
	
	static SimpleDateFormat date_time_patton_4_format = new SimpleDateFormat(DATE_TIME_PATTON_4);
	static SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTON_1);
	static SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTON_3);
	
	public static void main(String[] args) {
        System.out.println(isDatetimeZero(parseDatetime(TIMESTAMP_ZERO)));
        System.out.println(isTimestampZero(new Timestamp(parseDatetime(DATETIME_ZERO, DATE_PATTON_1).getTime())));
    }

	public static String formatDay() {
		return formatter.format(new Date());
	}

	/**
	 * 转换当前时间为默认格式
	 * 
	 * @return
	 */
	public static String formatDate2Str() {
		return formatDate2Str(new Date());
	}

	/**
	 * 获取毫秒级别时间
	 * 
	 * @return
	 */
	public static String formatStr4Date() {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTON_4);
		return formatter.format(new Date());
	}

	public static String formatStr4Date(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTON_4);

		return formatter.format(date);
	}

	/**
	 * 转换指定时间为默认格式
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate2Str(Date date) {
		return formatDate2Str(date, DATE_TIME_PATTON_1);
	}

	/**
	 * 转换当前日期为指定格式
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate2Str(String format) {
		return formatDate2Str(new Date(), format);
	}

	/**
	 * 转换指定时间为指定格式
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate2Str(Date date, String format) {
		if (date == null) {
			return null;
		}

		if (format == null || format.equals("")) {
			format = DATE_TIME_PATTON_1;
		}
		SimpleDateFormat sdf = getSimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 转换默认格式的时间为Date
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date formatStr2Date(String dateStr) {
		return formatStr2Date(dateStr,DATE_PATTON_1);
	}
	
	public static Timestamp formatStr2Timestamp(String timestamp) {
	    if(StringUtils.isBlank(timestamp)){
	        return null;
	    }
        try {
            return new Timestamp(sdf.parse(timestamp).getTime());
        } catch (Exception ex) {
            logger.error("\"" + timestamp + "\" is invalid,"
                    + " it should be in pattern " + " \""
                    + DATE_TIME_PATTON_1 + "\"", ex);
        }
        return null;
	}

	/**
	 * 转换指定格式的时间为Date
	 * 
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date formatStr2Date(String dateStr, String format) {
		if (dateStr == null) {
			return null;
		}

		if (format == null || format.equals("")) {
			format = DATE_PATTON_1;
		}
		SimpleDateFormat sdf = getSimpleDateFormat(format);
		return sdf.parse(dateStr, new ParsePosition(0));
	}

	/**
	 * 转换默认格式的时间为指定格式时间
	 * 
	 * @param dateStr
	 * @param defineFormat
	 * @return
	 */
	public static String formatDefault2Define(String dateStr,
			String defineFormat) {
		return formatSource2Target(dateStr, DATE_TIME_PATTON_1, defineFormat);
	}

	/**
	 * 转换源格式的时间为目标格式时间
	 * 
	 * @param dateStr
	 * @param sourceFormat
	 * @param targetFormat
	 * @return
	 */
	public static String formatSource2Target(String dateStr,
			String sourceFormat, String targetFormat) {
		Date date = formatStr2Date(dateStr, sourceFormat);
		return formatDate2Str(date, targetFormat);
	}

	/**
	 * 计算当前月份的最大天数
	 * 
	 * @return
	 */
	public static int findMaxDayInMonth() {
		return findMaxDayInMonth(0, 0);
	}

	/**
	 * 计算指定日期月份的最大天数
	 * 
	 * @param date
	 * @return
	 */
	public static int findMaxDayInMonth(Date date) {
		if (date == null) {
			return 0;
		}
		return findMaxDayInMonth(date2Calendar(date));
	}

	/**
	 * 计算指定日历月份的最大天数
	 * 
	 * @param calendar
	 * @return
	 */
	public static int findMaxDayInMonth(Calendar calendar) {
		if (calendar == null) {
			return 0;
		}

		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 计算当前年某月份的最大天数
	 * 
	 * @param month
	 * @return
	 */
	public static int findMaxDayInMonth(int month) {
		return findMaxDayInMonth(0, month);
	}

	/**
	 * 计算某年某月份的最大天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int findMaxDayInMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		if (year > 0) {
			calendar.set(Calendar.YEAR, year);
		}

		if (month > 0) {
			calendar.set(Calendar.MONTH, month - 1);
		}

		return findMaxDayInMonth(calendar);
	}

	/**
	 * Calendar 转换为 Date
	 * 
	 * @param calendar
	 * @return
	 */
	public static Date calendar2Date(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * Date 转换为 Calendar
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar date2Calendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 拿到默认格式的SimpleDateFormat
	 * 
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat() {
		return getSimpleDateFormat(null);
	}

	/**
	 * 拿到指定输出格式的SimpleDateFormat
	 * 
	 * @param format
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat(String format) {
		SimpleDateFormat sdf;
		if (format == null) {
			sdf = new SimpleDateFormat(DATE_TIME_PATTON_1);
		} else {
			sdf = new SimpleDateFormat(format);
		}

		return sdf;
	}

	/**
	 * 获得当前月份的第一天。
	 * 
	 * @return 当前月份的第一天。
	 */
	public static java.sql.Date getFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		return new java.sql.Date(cal.getTime().getTime());
	}
	
	/**
	 * 获得 指定日期月份的第一天。
	 */
	public static Date getFirstDayOfMonth(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}

	/**
	 * 获得当前月份的第一天并且按照yyyy-MM-dd格式化为字符串
	 * 
	 * @return 当前月份的第一天按照yyyy-MM-dd格式化的字符串
	 */
	public static String getFirstDayOfMonthAsString(String format) {
		java.sql.Date date = getFirstDayOfMonth();
		if (null != format && format.length() > 0) {
			return formatDate2Str(date, format);
		} else {
			return formatDate2Str(date);
		}
	}
	
	/**
	 * 获得指定月份的最后第一天。
	 */
	public static Date getLastDayOfMonth(Date date) {
	    Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        //calendar.add(Calendar.DATE, -1);
        calendar.add(Calendar.MILLISECOND, -1);
	    return calendar.getTime();
	}

	/**
	 * 获得当前月份的最后第一天。
	 * 
	 * @return 当前月份的最后第一天。
	 */
	public static java.sql.Date getLastDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return new java.sql.Date(cal.getTime().getTime());
	}

	/**
	 * 获得当前月份的最后一天并且按照yyyy-MM-dd格式化为字符串
	 * 
	 * @return 当前月份的最后一天按照yyyy-MM-dd格式化的字符串
	 */
	public static String getLastDayOfMonthAsString(String format) {
		java.sql.Date date = getLastDayOfMonth();
		if (null != format && format.length() > 0) {
			return formatDate2Str(date, format);
		} else {
			return formatDate2Str(date);
		}
	}

	/**
	 * 计算当天是该年中的第几周
	 * 
	 * @return
	 */
	public static int findWeeksNoInYear() {
		return findWeeksNoInYear(new Date());
	}

	/**
	 * 计算指定日期是该年中的第几周
	 * 
	 * @param date
	 * @return
	 */
	public static int findWeeksNoInYear(Date date) {
		if (date == null) {
			return 0;
		}
		return findWeeksNoInYear(date2Calendar(date));
	}

	/**
	 * 计算指定日历是该年中的第几周
	 * 
	 * @param calendar
	 * @return
	 */
	public static int findWeeksNoInYear(Calendar calendar) {
		if (calendar == null) {
			return 0;
		}
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 计算一年中的第几星期是几号
	 * 
	 * @param year
	 * @param weekInYear
	 * @param dayInWeek
	 * @return
	 */
	public static Date findDateInWeekOnYear(int year, int weekInYear,
			int dayInWeek) {
		Calendar calendar = Calendar.getInstance();
		if (year > 0) {
			calendar.set(Calendar.YEAR, year);
		}

		calendar.set(Calendar.WEEK_OF_YEAR, weekInYear);
		calendar.set(Calendar.DAY_OF_WEEK, dayInWeek);

		return calendar.getTime();
	}

	/**
	 * 相对于当前日期的前几天(days < 0)或者后几天(days > 0)
	 * 
	 * @param days
	 * @return
	 */
	public static Date dayBefore2Date(int days) {
		Date date = new Date();
		return dayBefore2Object(days, null, date);
	}
	
	public static Date dayBefore2Date(Date date,int days) {
	    Calendar calendar = Calendar.getInstance();
        if (days == 0) {
            return date;
        }
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
	    return calendar.getTime();
	}
	
	public static Date dateTimeEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
	
	/**
	 * 相对于当前日期的前几天(days < 0)或者后几天(days > 0)
	 * 
	 * @param days
	 * @return
	 */
	public static String dayBefore2Str(int days) {
		String string = formatDate2Str();
		return dayBefore2Object(days, null, string);
	}
	
	public static Date date2Zero(Date date) {
	    if(date==null){
	        return null;
	    }
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}

	/**
	 * 相对于当前日期的前几天(days < 0)或者后几天(days > 0)
	 * 
	 * @param days
	 * @param format
	 * @param instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T dayBefore2Object(int days,
			String format, T instance) {
		Calendar calendar = Calendar.getInstance();
		if (days == 0) {
			return null;
		}

		if (format == null || format.equals("")) {
			format = DATE_TIME_PATTON_1;
		}

		calendar.add(Calendar.DATE, days);
		if (instance instanceof Date) {
			return (T) calendar.getTime();
		} else if (instance instanceof String) {
			return (T) formatDate2Str(calendar2Date(calendar), format);
		}
		return null;
	}

	/**
	 * 相对于指定日期的前几天(days < 0)或者后几天(days > 0)
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date defineDayBefore2Date(Date date, int days) {
		Date dateInstance = new Date();
		return defineDayBefore2Object(date, days, null, dateInstance);
	}

	/**
	 * 相对于指定日期的前几天(days < 0)或者后几天(days > 0)
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static String defineDayBefore2Str(Date date, int days) {
		String stringInstance = formatDate2Str();
		return defineDayBefore2Object(date, days, null, stringInstance);
	}

	/**
	 * 相对于指定日期的前几天(days < 0)或者后几天(days > 0)
	 * 
	 * @param <T>
	 * @param date
	 * @param days
	 * @param format
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T defineDayBefore2Object(Date date,
			int days, String format, T instance) {
		if (date == null || days == 0) {
			return null;
		}

		if (format == null || format.equals("")) {
			format = DATE_TIME_PATTON_1;
		}

		Calendar calendar = date2Calendar(date);
		calendar.add(Calendar.DATE, days);
		if (instance instanceof Date) {
			return (T) calendar.getTime();
		} else if (instance instanceof String) {
			return (T) formatDate2Str(calendar2Date(calendar), format);
		}
		return null;
	}

	/**
	 * 相对于当前日期的前几月(months < 0)或者后几月(months > 0)时间
	 * 
	 * @param months
	 * @return
	 */
	public static Date monthBefore2Date(int months) {
		Date date = new Date();
		return monthBefore2Object(months, null, date);
	}

	/**
	 * 相对于当前日期的前几月(months < 0)或者后几月(months > 0)时间
	 * 
	 * @param months
	 * @return
	 */
	public static String monthBefore2Str(int months) {
		String string = formatDate2Str();
		return monthBefore2Object(months, null, string);
	}

	/**
	 * 相对于当前日期的前几月(months < 0)或者后几月(months > 0)时间
	 * 
	 * @param <T>
	 * @param months
	 * @param format
	 * @param instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T monthBefore2Object(int months,
			String format, T instance) {
		if (months == 0) {
			return null;
		}

		if (format == null || format.equals("")) {
			format = DATE_TIME_PATTON_1;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, months);

		if (instance instanceof Date) {
			return (T) calendar.getTime();
		} else if (instance instanceof String) {
			return (T) formatDate2Str(calendar2Date(calendar), format);
		}

		return null;
	}

	/**
	 * 相对于指定日期的前几月(months < 0)或者后几月(months > 0)时间
	 * 
	 * @param date
	 * @param months
	 * @return
	 */
	public static Date defineMonthBefore2Date(Date date, int months) {
		Date dateInstance = new Date();
		return defineMonthBefore2Object(date, months, null, dateInstance);
	}

	/**
	 * 相对于指定日期的前几月(months < 0)或者后几月(months > 0)时间
	 * 
	 * @param date
	 * @param months
	 * @return
	 */
	public static String defineMonthBefore2Str(Date date, int months) {
		String stringInstance = formatDate2Str();
		return defineMonthBefore2Object(date, months, null, stringInstance);
	}

	/**
	 * 相对于指定日期的前几月(months < 0)或者后几月(months > 0)时间
	 * 
	 * @param <T>
	 * @param date
	 * @param months
	 * @param format
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T defineMonthBefore2Object(Date date,
			int months, String format, T instance) {
		if (months == 0) {
			return null;
		}

		if (format == null || format.equals("")) {
			format = DATE_TIME_PATTON_1;
		}

		Calendar calendar = date2Calendar(date);
		calendar.add(Calendar.MONTH, months);

		if (instance instanceof Date) {
			return (T) calendar.getTime();
		} else if (instance instanceof String) {
			return (T) formatDate2Str(calendar2Date(calendar), format);
		}

		return null;
	}

	/**
	 * 计算两个日期直接差的天数
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int caculate2Days(Date firstDate, Date secondDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstDate);
		int dayNum1 = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(secondDate);
		int dayNum2 = calendar.get(Calendar.DAY_OF_YEAR);
		return Math.abs(dayNum1 - dayNum2);
	}

	/**
	 * 计算两个日期直接差的天数
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int caculate2Days(String firstString, String secondString) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date firstDate = null;
		Date secondDate = null;

		// Rewriter
		// Rewrite Date：2015-12-22 By ZouYongle Case firstDate secondDate
		// Possible null pointer Start1：
		/*
		 * try { firstDate = df.parse(firstString); secondDate =
		 * df.parse(secondString); } catch (Exception e) { // 日期型字符串格式错误 } int
		 * nDay = (int) ((secondDate.getTime() - firstDate.getTime()) / (24 * 60
		 * * 60 * 1000));
		 */
		// End1：
		// Added by
		// Add date：2015-12-22 Start2：
		int nDay = 0;
		try {
			firstDate = df.parse(firstString);
			secondDate = df.parse(secondString);
			nDay = (int) ((secondDate.getTime() - firstDate.getTime()) / (24 * 60 * 60 * 1000));
		} catch (Exception e) {
			// 日期型字符串格式错误
			e.printStackTrace();
		}
		// End2：
		return nDay;
	}

	/**
	 * 计算两个日期直接差的天数
	 * 
	 * @param firstCalendar
	 * @param secondCalendar
	 * @return
	 */
	public static int caculate2Days(Calendar firstCalendar,
			Calendar secondCalendar) {
		if (firstCalendar.after(secondCalendar)) {
			Calendar calendar = firstCalendar;
			firstCalendar = secondCalendar;
			secondCalendar = calendar;
		}

		long calendarNum1 = firstCalendar.getTimeInMillis();
		long calendarNum2 = secondCalendar.getTimeInMillis();
		return Math.abs((int) ((calendarNum1 - calendarNum2) / C_ONE_DAY));
	}

	/**
	 * 计算两个日期直接差的时间，小时
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static boolean caculate24hours(Date start, Date end) {
		if ((end.getTime() - start.getTime()) < (24 * 60 * 60 * 1000)) {
			return false;
		} else {
			return true;
		}
	}

	// 打印两个时间差 -- 用于简单性能测试
	public static String PrintTimeGap(String str, Date start, Date end) {
		if (str == null || "".equals(str))
			str = "";
		long l = end.getTime() - start.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long n = (l - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min
				* 60 * 1000 - s * 1000);
		return str + "\r\n耗时为 : " + day + "天" + hour + "小时" + min + "分" + s
				+ "秒" + n + "毫秒";
	}

	/**
	 *  计算时间差
	 * @param start
	 * @param end
	 * @return
	 */
	public static long calculTimeGap(String start, String end) {
		SimpleDateFormat dfs = new SimpleDateFormat(DATE_TIME_PATTON_1);
		long between = 0;
		try {
			java.util.Date startTime = dfs.parse(start);
			java.util.Date endTime = dfs.parse(end);
			// 除以1000是为了转换成秒
			between = (endTime.getTime() - startTime.getTime()) / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// long day1=between/(24*3600);
		// long hour1=between%(24*3600)/3600;
		// long minute1=between%3600/60;
		// long second1=between%60/60;
		// System.out.println(""+day1+"天"+hour1+"小时"+minute1+"分"+second1+"秒");
		return Math.abs(between);
	}

	// 时间戳转化为String
	public static String timeStamp2String(long timeStamp) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTON_1);
		String d = format.format(timeStamp);
		return d;
	}

	
	public static long string2TimeStamp(String time) throws ParseException {
		
		Date date  = date_time_patton_4_format.parse(time);
		return  date.getTime();
	}
	
	public static long getUnixTime(String time,String format) {
	    if (StringUtils.isBlank(format)) {
            format = DATE_PATTON_1;
        }
	    SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
	    Date date=null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
	    return  date.getTime()/1000;
	}
	
	public static String getNextNDay(String format,int days){
        if (StringUtils.isBlank(format)) {
            format = DATE_PATTON_1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        SimpleDateFormat simpleDateFormat = getSimpleDateFormat(format);
        String formatDate = simpleDateFormat.format(calendar.getTime());
        if(logger.isInfoEnabled()){
            logger.info("格式化getNextNDay[format={},days={}]得到的日期为:{}",format,days,formatDate);
        }
	    return formatDate ;
	}
	
	public static String getNextDayEarly() {
	    return  getNextNDay(DATE_PATTON_1,1);
	}
	
	public static long getNextDayEarlyUnixTime() {
	    return getUnixTime(getNextDayEarly(), DATE_PATTON_1);
	}
	
	// 时间戳转化为Date
	public static Date timeStamp2Date(long timeStamp) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTON_1);
		String d = format.format(timeStamp);
		Date date = null;
		try {
			date = format.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	// Date转化为时间戳
	public static long date2TimeStamp(Date date) {
		long timeStamp = date.getTime();
		return timeStamp;
	}

	public static Date addDateSeconds(Date date, int s) {
		if (date == null||s==0)
			return date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, s);// 24小时制
		date = cal.getTime();
		cal = null;
		return date;
	}

	public static long caculate2Seconds(Date firstDate, Date secondDate) {
		long firstDateL = firstDate.getTime();
		long secondDateL = secondDate.getTime();
		long diff = Math.abs(firstDateL - secondDateL);
		return diff / 1000;
	}
	
	public static long caculate2MilliSecond(Date firstDate, Date secondDate) {
	    long firstDateL = firstDate.getTime();
	    long secondDateL = secondDate.getTime();
	    long diff = Math.abs(firstDateL - secondDateL);
	    return diff;
	}

	public static long caculate2MilliSecond(String startDate, String endDate) {
		long startDateL = formatStr2Date(startDate, DATE_TIME_PATTON_4)
				.getTime();
		long endDateL = formatStr2Date(endDate, DATE_TIME_PATTON_4).getTime();
		long diff = Math.abs(startDateL - endDateL);
		return diff;
	}

	/**
	 * 
	 * 向前调整时间
	 *
	 * @Title: adjustBeforeDate
	 * @date Feb 3, 2016 2:16:17 PM
	 * @author 邹永乐
	 * @modifier
	 * @modifydate
	 * @param sourceDate
	 *            需要调整的源时间,空则为当前时间
	 * @param adjustType
	 *            调整时间的类型
	 * @param adjustValue
	 *            需要调整时间的值，注意值为正数
	 * @return
	 */
	public static Date adjustBeforeDate(Date sourceDate, int adjustType,
			int adjustValue) {
		if (sourceDate == null) {
			sourceDate = new Date();// 设置默认时间为当前时间
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sourceDate);
		calendar.add(adjustType, adjustValue > 0 ? adjustValue * -1
				: adjustValue);
		return calendar.getTime();
	}

	/**
	 * 
	 * 整时间
	 *
	 * @Title: adjustDate
	 * @date Feb 3, 2016 2:17:14 PM
	 * @author 邹永乐
	 * @modifier
	 * @modifydate
	 * @param sourceDate
	 *            需要调整的源时间,空则为当前时间
	 * @param adjustType
	 *            调整时间的类型
	 * @param adjustValue
	 *            需要调整时间的值，正数向后调整，负数为向前调整
	 * @return
	 */
	public static Date adjustDate(Date sourceDate, int adjustType,
			int adjustValue) {
		if (sourceDate == null) {
			sourceDate = new Date();// 设置默认时间为当前时间
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sourceDate);
		calendar.add(adjustType, adjustValue);
		return calendar.getTime();
	}

	/**
	 * 
	 * 向前调整的时间
	 *
	 * @Title: adjustBeforeDate
	 * @date Feb 3, 2016 2:18:26 PM
	 * @author 邹永乐
	 * @modifier
	 * @modifydate
	 * @param sourceDate
	 *            需要调整的源时间,空则为当前时间
	 * @param adjustYear
	 *            向前调整的年，正数
	 * @param adjustMonth
	 *            向前调整的月，正数
	 * @param adjustDate
	 *            向前调整的日，正数
	 * @param adjustHour
	 *            向前调整的时，正数
	 * @param adjustMinute
	 *            向前调整的分，正数
	 * @param adjustSecond
	 *            向前调整的秒，正数
	 * @return
	 */
	public static Date adjustBeforeDate(Date sourceDate, Integer adjustYear,
			Integer adjustMonth, Integer adjustDate, Integer adjustHour,
			Integer adjustMinute, Integer adjustSecond) {
		if (sourceDate == null) {
			sourceDate = new Date();// 设置默认时间为当前时间
		}
		Calendar calendar = Calendar.getInstance();
		if (adjustYear != null) {
			calendar.add(Calendar.YEAR, adjustYear > 0 ? (adjustYear * -1)
					: adjustYear);
		}
		if (adjustMonth != null) {
			calendar.add(Calendar.MONTH, adjustMonth > 0 ? (adjustMonth * -1)
					: adjustYear);
		}
		if (adjustDate != null) {
			calendar.add(Calendar.DATE, adjustDate > 0 ? (adjustDate * -1)
					: adjustYear);
		}
		if (adjustHour != null) {
			calendar.add(Calendar.HOUR, adjustHour > 0 ? (adjustHour * -1)
					: adjustYear);
		}
		if (adjustMinute != null) {
			calendar.add(Calendar.MINUTE,
					adjustMinute > 0 ? (adjustMinute * -1) : adjustYear);
		}
		if (adjustSecond != null) {
			calendar.add(Calendar.SECOND,
					adjustSecond > 0 ? (adjustSecond * -1) : adjustYear);
		}
		return calendar.getTime();
	}

	/**
	 * 
	 * 向前调整的时间
	 *
	 * @Title: adjustDate
	 * @date Feb 3, 2016 2:18:26 PM
	 * @author 邹永乐
	 * @modifier
	 * @modifydate
	 * @param sourceDate
	 *            需要调整的源时间,空则为当前时间
	 * @param adjustYear
	 *            向前调整的年，正数
	 * @param adjustMonth
	 *            向前调整的月，正数
	 * @param adjustDate
	 *            向前调整的日，正数
	 * @param adjustHour
	 *            向前调整的时，正数
	 * @param adjustMinute
	 *            向前调整的分，正数
	 * @param adjustSecond
	 *            向前调整的秒，正数
	 * @return
	 */
	public static Date adjustDate(Date sourceDate, Integer adjustYear,
			Integer adjustMonth, Integer adjustDate, Integer adjustHour,
			Integer adjustMinute, Integer adjustSecond) {
		if (sourceDate == null) {
			sourceDate = new Date();// 设置默认时间为当前时间
		}
		Calendar calendar = Calendar.getInstance();
		if (adjustYear != null) {
			calendar.add(Calendar.YEAR, adjustYear);
		}
		if (adjustMonth != null) {
			calendar.add(Calendar.MONTH, adjustMonth);
		}
		if (adjustDate != null) {
			calendar.add(Calendar.DATE, adjustDate);
		}
		if (adjustHour != null) {
			calendar.add(Calendar.HOUR, adjustHour);
		}
		if (adjustMinute != null) {
			calendar.add(Calendar.MINUTE, adjustMinute);
		}
		if (adjustSecond != null) {
			calendar.add(Calendar.SECOND, adjustSecond);
		}
		return calendar.getTime();
	}
	
	public static boolean isTimestampZero(Timestamp timestamp) {
	    Timestamp TIMESTAMP_ZERO = formatStr2Timestamp(DateTimeUtil.TIMESTAMP_ZERO);
		if (timestamp != null ) {
            return !TIMESTAMP_ZERO.before(timestamp);
        }
		return true;
    }
	
	/**
	 * 校验字符串是否为指定格式的数据
	 * 
	 * @param datetime
	 * @param pattern
	 * @return
	 */
	public static boolean isDatetimePattern(String datetime,String pattern) {
		if(StringUtils.isBlank(datetime)){
			return false;
		}
		return datetime.matches(pattern);
	}
	
	public static boolean isDatetimePattern(String datetime) {
		return isDatetimePattern(datetime,DATE_TIME_PATTON_1_REGEX);
	}
	
    public static Date parseDatetime(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTON_1);
        try {
            return sdf.parse(datetime);
        } catch (Exception ex) {
            logger.error("\"" + datetime + "\" is invalid,"
                    + " it should be in pattern " + " \""+ DATE_TIME_PATTON_1 + "\"", ex);
        }
        return null;
    }
    
    public static Date parseDatetime(String datetime, String parttern) {
        parttern = (StringUtils.isBlank(parttern)) ? DATE_TIME_PATTON_1: parttern;
        SimpleDateFormat sdf = new SimpleDateFormat(parttern);
        try {
            return sdf.parse(datetime);
        } catch (Exception ex) {
            logger.error("\"" + datetime + "\" is invalid,"
                    + " it should be in pattern " + " \""+ parttern + "\"", ex);
        }
        return null;
    }

    public static boolean isDatetimeZero(Date datetime) {
        Date DATETIME_ZERO = formatStr2Date(DateTimeUtil.DATETIME_ZERO);
        if (datetime != null) {
        	if (datetime instanceof Timestamp) {
        		return isTimestampZero((Timestamp) datetime);
        	}else{
        		return !DATETIME_ZERO.before(datetime);
        	}
        }
        return true;
    }
    
    public static Timestamp currentSTimestamp() {
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
        }
        return currentTimestamp();
    }
    
    public static Timestamp currentS1Timestamp() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return currentTimestamp();
    }
    
    public static Date currentSDate() {
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
        }
    	return currentDate();
    }
    
    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    public static Date currentDate() {
        return new Date(System.currentTimeMillis());
    }
    
	/**
	 * 
	 * @Description: 由日期返回yyyyMMddHHmmss格式的字符串
	 * @author shenchao
	 * @Created 2017年4月17日下午1:08:05
	 * @since 1.0
	 * @param date
	 * @return
	 */
	public static String dateToNumber(Date date) {
		if (date == null)
			return null;

		SimpleDateFormat simpleDateFormat =
			new SimpleDateFormat("yyyyMMddHHmmss");
		return simpleDateFormat.format(date);
	}
	 /**
     * 由yyyyMMdd格式的字符串返回日期
     *
     * @param date
     * @return
     */
    public static Date stringToDate(String string) {
        if (StringUtils.isBlank(string))
            return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
        		DATE_PATTON_3);
        try {
            return simpleDateFormat.parse(string);
        } catch (ParseException e) {
            logger.error("由yyyyMMdd格式的字符串返回日期格式转换出错"+e);
        }
        return null;
    }
	 /**
	  * 
	  * @Description: 由yyyyMM字符串返回日期
	  * @author shenchao
	  * @Created 2016年11月7日下午5:33:43
	  * @since 1.0
	  * @param string
	  * @return
	  * @throws CommonException
	  */
	public static Date stringToDate2(String string) {
		if (string == null)
			return null;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		try {
			return simpleDateFormat.parse(string);
		} catch (ParseException e) {
			logger.error("由yyyyMM格式的字符串返回日期格式转换出错" + e);
		}
		return null;
	}
	 
	/**
	 * 对日期进行增减操作
	 */
	public static Date addDayOfMonth(Date value, int ndays) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(value);
		gc.add(GregorianCalendar.DAY_OF_MONTH, ndays);
		return gc.getTime();
	}
	/**
	 * 由日期返回 dd 格式的字符串
	 *
	 * @param date
	 * @return
	 */
	public static String getDay(Date date) {
		if (date == null)
			return null;
		return dateToNumber2(date).substring(6, 8);
	}

	/**
	 * 由日期返回 mm 格式的字符串
	 *
	 * @param date
	 * @return
	 */
	public static String getMonth(Date date) {
		if (date == null)
			return null;
		return dateToNumber2(date).substring(4, 6);
	}

	/**
	 * 由日期返回 yyyy 格式的字符串
	 *
	 * @param date
	 * @return
	 */
	public static String getYear(Date date) {
		if (date == null)
			return null;
		return dateToNumber2(date).substring(0, 4);
	}
    /**
	 * 由日期返回yyyyMMdd格式的字符串
	 * @param date
	 * @return
	 */
	public static String dateToNumber2(Date date) {
		if (date == null)
			return null;

		SimpleDateFormat simpleDateFormat =
			new SimpleDateFormat(DATE_PATTON_3);
		return simpleDateFormat.format(date);
	}
	
	/**
     * 计算两个日期相隔的月数(不足整月的算一个月)
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getMonthsBetween(Date startDate, Date endDate) {
        Calendar calendarStartDate = Calendar.getInstance();
        Calendar calendarEndDate = Calendar.getInstance();

        // 设日历为相应日期
        calendarStartDate.setTime(startDate);
        calendarEndDate.setTime(endDate);
        if (startDate.after(endDate)) {
            Calendar swap = calendarStartDate;
            calendarStartDate = calendarEndDate;
            calendarEndDate = swap;
        }

        int months = calendarEndDate.get(Calendar.MONTH)
                - calendarStartDate.get(Calendar.MONTH)
                + (calendarEndDate.get(Calendar.YEAR) - calendarStartDate
                        .get(Calendar.YEAR)) * 12;

        if (getEndDateByMonths(startDate, months).compareTo(endDate) < 0)
            months += 1;

        return months;
    }
    
	/**
	 * 计算两个日期相隔的月数(不足整月的不算一个月)
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getMonthsBetween2(Date startDate, Date endDate) {
		Calendar calendarStartDate = Calendar.getInstance();
		Calendar calendarEndDate = Calendar.getInstance();

		// 设日历为相应日期
		calendarStartDate.setTime(startDate);
		calendarEndDate.setTime(endDate);
		if (startDate.after(endDate)) {
			Calendar swap = calendarStartDate;
			calendarStartDate = calendarEndDate;
			calendarEndDate = swap;
		}

		int months = calendarEndDate.get(Calendar.MONTH)
				- calendarStartDate.get(Calendar.MONTH)
				+ (calendarEndDate.get(Calendar.YEAR) - calendarStartDate
						.get(Calendar.YEAR)) * 12;

		if (getEndDateByMonths(startDate, months).compareTo(endDate) > 0)
			months -= 1;

		return months;
	}
	
	/**
	 * 查询两个日期对应的月份的差值
	 */
	public static int getMonthsBetween3(Date startDate, Date endDate) {
        Calendar calendarStartDate = Calendar.getInstance();
        Calendar calendarEndDate = Calendar.getInstance();

        // 设日历为相应日期
        calendarStartDate.setTime(startDate);
        calendarEndDate.setTime(endDate);
        if (startDate.after(endDate)) {
            Calendar swap = calendarStartDate;
            calendarStartDate = calendarEndDate;
            calendarEndDate = swap;
        }

        int months = calendarEndDate.get(Calendar.MONTH)
                - calendarStartDate.get(Calendar.MONTH)
                + (calendarEndDate.get(Calendar.YEAR) - calendarStartDate
                        .get(Calendar.YEAR)) * 12;
        return months;
    }
	
	/**
     * 根据起始日和相隔月数计算终止日
     *
     * @param startDate
     * @param months
     * @return
     */
    public static Date getEndDateByMonths(Date startDate, int months) {
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.setTime(startDate);
        calendarStartDate.add(Calendar.MONTH, months);

        return calendarStartDate.getTime();
    }
    
    /**
     * 增加年份后的日期
     * @author zhaopeng
     * 2017年7月27日 下午4:57:32
     */
    public static Date getEndDateByYears(Date startDate, int years) {
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.setTime(startDate);
        calendarStartDate.add(Calendar.YEAR, years);
        return calendarStartDate.getTime();
    }
    /**
     * 增加年份相隔天数
     * @author zhaopeng
     * 2017年7月27日 下午4:49:25
     */
    public static int daysBetweenYear(Date date ,int year) throws ParseException { 
    	Date endDateByYears = getEndDateByYears(date, year);//增加年份后的日期
    	int daysBetween = daysBetween(date, endDateByYears);
		return daysBetween;
    }
    /**
     * 增加月份相隔天数
     * @author zhaopeng
     * 2017年7月27日 下午4:49:25
     */
    public static int daysBetweenMonths(Date date ,int months) throws ParseException { 
    	Date endDateByMonths = getEndDateByMonths(date, months);//增加月份后的日期
    	int daysBetween = daysBetween(date, endDateByMonths);
		return daysBetween;
    }
    
  /**
   * 两个日期相隔天数
   * @author zhaopeng
   * 2017年7月27日 下午4:39:52
 * @throws ParseException 
   */
   public static int daysBetween(Date smdate,Date bdate) throws ParseException   {    
       SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
       smdate=sdf.parse(sdf.format(smdate));  
       bdate=sdf.parse(sdf.format(bdate));  
       Calendar cal = Calendar.getInstance();    
       cal.setTime(smdate);    
       long time1 = cal.getTimeInMillis();                 
       cal.setTime(bdate);    
       long time2 = cal.getTimeInMillis();         
       long between_days=(time2-time1)/(1000*3600*24);  
      return Integer.parseInt(String.valueOf(between_days));           
   }
   
   /**
    * 获取caculateDate日期months个月后的月份的最大日期值
    */
    public static Date getMaxDateOfMonths(Date caculateDate, int months) {
        if(caculateDate==null){
            return caculateDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(caculateDate);
        calendar.set(Calendar.DAY_OF_MONTH , 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, months+1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }
    
    public static Date getFirstDateOfMonths(Date caculateDate, int months) {
        if(caculateDate==null){
            return caculateDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(caculateDate);
        calendar.set(Calendar.DAY_OF_MONTH , 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }
    
}