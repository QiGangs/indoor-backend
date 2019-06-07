package com.inlocate.backend.util;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	public static final long m_second = 1000;
	public static final long m_minute = m_second * 60;
	public static final long m_hour = m_minute * 60;
	public static final long m_day = m_hour * 24;

	public DateUtil() {
	}

	public static final String currentTimeFull() {
		return format(new Date(), "yyyyMMddHHmmss");
	}

	public static final Date currentDate() {
		return new Date();
	}

	public static final Timestamp currentTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static final String currentDatestr() {
		return formatDate(currentDate());
	}

	public static final String currentTimestr() {
		return formatTimestamp(currentTime());
	}

	public static final String currentCNdatestr() {
		return formatCNDate(currentDate()) + " " + getCnWeek(currentDate());
	}

	public static final String currentENdatestr(String pattern) {
		return formatEn(currentDate(), pattern) + "   " + getEnWeek(currentDate());
	}

	public static final int nextMonth() {
		String next = format(new Date(), "M");
		int nextMonth = Integer.parseInt(next) + 1;
		if (nextMonth == 13)
			return 1;
		return nextMonth;
	}

	/**
	 * parse date using default pattern yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static final Date parseDate(String strDate) {
		Date date = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = dateFormat.parse(strDate);
			return date;
		} catch (Exception pe) {
			return null;
		}
	}

	public static final Timestamp parseTimestamp(String strDate) {
		try {
			Timestamp result = Timestamp.valueOf(strDate);
			return result;
		} catch (Exception pe) {
			return null;
		}
	}

	/**
	 * @param strDate
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static final Date parseDate(String strDate, String pattern) {
		SimpleDateFormat df = null;
		Date date = null;
		df = new SimpleDateFormat(pattern);
		try {
			date = df.parse(strDate);
			return date;
		} catch (Exception pe) {
			return null;
		}
	}

	/**
	 * @param aDate
	 * @return formated date by yyyy-MM-dd
	 */
	public static final <T extends Date> String formatDate(T date) {
		if (date == null)
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static final <T extends Date> String formatCNDate(T date) {
		if (date == null)
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		return dateFormat.format(date);
	}

	/**
	 * @param aDate
	 * @return formated time by HH:mm:ss
	 */
	public static final <T extends Date> String formatTime(T date) {
		if (date == null)
			return null;
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		return timeFormat.format(date);
	}

	/**
	 * @param aDate
	 * @return formated time by yyyy-MM-dd HH:mm:ss
	 */
	public static final String formatTimestamp(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return timestampFormat.format(date);
	}

	/**
	 * @param aDate
	 * @param pattern:
	 *            Date format pattern
	 * @return
	 */
	public static final <T extends Date> String format(T date, String pattern) {
		if (date == null)
			return null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(pattern, java.util.Locale.CHINA);
			String result = df.format(date);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static final <T extends Date> String formatEn(T date, String pattern) {
		if (date == null)
			return null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(pattern, java.util.Locale.US);
			String result = df.format(date);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param original
	 * @param days
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param mill
	 * @return original+day+hour+minutes+seconds+millseconds
	 */
	public static final <T extends Date> T addTime(T original, int days, int hours, int minutes, int seconds) {
		if (original == null)
			return null;
		long newTime = original.getTime() + m_day * days + m_hour * hours + m_minute * minutes + m_second * seconds;
		T another = (T) original.clone();
		another.setTime(newTime);
		return another;
	}

	public static final <T extends Date> T addDay(T original, int days) {
		if (original == null)
			return null;
		long newTime = original.getTime() + m_day * days;
		T another = (T) original.clone();
		another.setTime(newTime);
		return another;
	}

	public static final <T extends Date> T addHour(T original, int hours) {
		if (original == null)
			return null;
		long newTime = original.getTime() + m_hour * hours;
		T another = (T) original.clone();
		another.setTime(newTime);
		return another;
	}

	public static final <T extends Date> T addMinute(T original, int minutes) {
		if (original == null)
			return null;
		long newTime = original.getTime() + m_minute * minutes;
		T another = (T) original.clone();
		another.setTime(newTime);
		return another;
	}

	public static final <T extends Date> T addSecond(T original, int second) {
		if (original == null)
			return null;
		long newTime = original.getTime() + m_second * second;
		T another = (T) original.clone();
		another.setTime(newTime);
		return another;
	}

	/**
	 * @param day
	 * @return for example ,1997/01/02 22:03:00,return 1997/01/02 00:00:00.0
	 */
	public static final <T extends Date> T getBeginningTimeOfDay(T day) {
		if (day == null)
			return null;
		// new Date(0)=Thu Jan 01 08:00:00 CST 1970
		String strDate = formatDate(day);
		Long mill = parseDate(strDate).getTime();
		T another = (T) day.clone();
		another.setTime(mill);
		return another;
	}

	/**
	 * @param day
	 * @return for example ,1997/01/02 22:03:00,return 1997/01/03 23:59:59.999
	 */
	public static final <T extends Date> T getLastTimeOfDay(T day) {
		if (day == null)
			return null;
		Long mill = getBeginningTimeOfDay(day).getTime() + m_day - 1;
		T another = (T) day.clone();
		another.setTime(mill);
		return another;
	}

	/**
	 * 09:00:00,09:07:00 ---> 9:00,9:7:00
	 * 
	 * @param time
	 * @return
	 */
	public static final String formatTime(String time) {
		if (time == null)
			return null;
		time = StringUtils.trim(time);
		if (StringUtils.isBlank(time))
			throw new IllegalArgumentException("时间格式有错误！");
		time = time.replace('：', ':');
		String[] times = time.split(":");
		String result = "";
		if (times[0].length() < 2)
			result += "0" + times[0] + ":";
		else
			result += times[0] + ":";
		if (times.length > 1) {
			if (times[1].length() < 2)
				result += "0" + times[1];
			else
				result += times[1];
		} else {
			result += "00";
		}
		java.sql.Timestamp.valueOf("2001-01-01 " + result + ":00");
		return result;
	}

	public static boolean isTomorrow(Date date) {
		if (date == null)
			return false;
		if (formatDate(addTime(new Date(), 1, 0, 0, 0)).equals(formatDate(date)))
			return true;
		return false;
	}

	/***
	 * @param date
	 * @return 1,2,3,4,5,6,7
	 */
	private static int[] chweek = new int[] { 0, 0, 1, 2, 3, 4, 5, 6 };

	public static Integer getWeek(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return chweek[c.get(Calendar.DAY_OF_WEEK)];
	}

	private static String[] cnweek = new String[] { "", "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private static String[] enweek = new String[] { "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };
	private static String[] enshortweek = new String[] { "", "Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat" };
	private static String[] cnSimpleweek = new String[] { "", "日", "一", "二", "三", "四", "五", "六" };
	private static String[] cnTimeRange = new String[] { "AM", "AM", "AM", "AM", "AM", "AM", "AM", "AM", "AM", "AM",
			"AM", "AM", "PM", "PM", "PM", "PM", "PM", "PM", "PM", "PM", "PM", "PM", "PM", "PM" };

	public static String getCnWeek(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return cnweek[c.get(Calendar.DAY_OF_WEEK)];
	}

	public static String getEnWeek(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return enweek[c.get(Calendar.DAY_OF_WEEK)];
	}

	public static String getShortEnWeek(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return enshortweek[c.get(Calendar.DAY_OF_WEEK)];
	}

	public static String getCnSimpleWeek(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return cnSimpleweek[c.get(Calendar.DAY_OF_WEEK)];
	}

	public static String getTimeRange(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return cnTimeRange[c.get(Calendar.HOUR_OF_DAY)];
	}

	public static Integer getMonth(Date date) {
		if (date == null)
			return null;
		String month = format(date, "M");
		return Integer.parseInt(month);
	}

	public static Integer getCurrentDay() {
		return getDay(new Date());
	}

	public static Integer getCurrentMonth() {
		return getMonth(new Date());
	}

	public static Integer getCurrentYear() {
		return getYear(new Date());
	}

	public static Integer getYear(Date date) {
		if (date == null)
			return null;
		String year = DateUtil.format(date, "yyyy");
		return Integer.parseInt(year);
	}

	public static Integer getDay(Date date) {
		if (date == null)
			return null;
		String year = DateUtil.format(date, "d");
		return Integer.parseInt(year);
	}

	public static String getCurDateStr() {
		return DateUtil.formatDate(new Date());
	}

	public boolean isAfter(Date date) {
		if (date.after(new Date())) {
			return true;
		}
		return false;
	}

	/**
	 * 获取date所在月份的星期为weektype且日期在date之后（或等于）的所有日期
	 * 
	 * @param weektype
	 * @return
	 */
	public static List<Date> getWeekDateList(Date date, String weektype) {
		int curMonth = getMonth(date);
		int week = Integer.parseInt(weektype);
		int curWeek = getWeek(date);
		int sub = (7 + week - curWeek) % 7;
		Date next = addDay(date, sub);
		List<Date> result = new ArrayList<Date>();
		while (getMonth(next) == curMonth) {
			result.add(next);
			next = addDay(next, 7);
		}
		return result;
	}

	/**
	 * 获取date之后(包括date)的num个星期为weektype日期（不限制月份）
	 * 
	 * @param weektype
	 * @return
	 */
	public static List<Date> getWeekDateList(Date date, String weektype, int num) {
		int week = Integer.parseInt(weektype);
		int curWeek = getWeek(date);
		List<Date> result = new ArrayList<Date>();
		int sub = (7 + week - curWeek) % 7;
		Date next = addDay(date, sub);
		for (int i = 0; i < num; i++) {
			result.add(next);
			next = addDay(next, 7);
		}
		return result;
	}

	/**
	 * 获取date所在星期的周一至周日的日期
	 * 
	 * @param date
	 * @return
	 */
	public static List<Date> getCurWeekDateList(Date date) {
		int curWeek = getWeek(date);
		List<Date> dateList = new ArrayList<Date>();
		for (int i = 1; i <= 7; i++)
			dateList.add(DateUtil.addDay(date, -curWeek + i));
		return dateList;
	}

	public static Date getCurDate() {
		return getBeginningTimeOfDay(new Date());
	}

	/**
	 * 获取日期所在月份的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthFirstDay(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	public static Date getMonthLastDay(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	public static Date getWeekFirstDay(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, 1);
		return c.getTime();
	}

	public static Date getWeekLastDay(Date date) {
		return DateUtil.addDay(getWeekFirstDay(date), 6);
	}

	public static Date getYearFirstDay(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_YEAR, 1);
		return c.getTime();
	}

	public static Date getYearLastDay(Date date) {
		if (date == null)
			return null;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_YEAR, 365);
		return c.getTime();
	}

	public static String formatDate(int days) {
		return formatDate(addDay(new Date(), days));
	}

	public static Timestamp getBeginningTimestamp() {
		return getBeginningTimeOfDay(new Timestamp(System.currentTimeMillis()));
	}

	public static Integer getHour(Date date) {
		if (date == null)
			return null;
		String hour = format(date, "H");
		return Integer.parseInt(hour);
	}

	public static String getTimeDesc(Timestamp time) {
		if (time == null)
			return "";
		String timeContent;
		Long ss = System.currentTimeMillis() - time.getTime();
		Long minute = ss / 60000;
		if (minute < 1)
			minute = 1L;
		if (minute >= 60) {
			Long hour = minute / 60;
			if (hour >= 24) {
				if (hour > 720)
					timeContent = "1月前";
				else if (hour > 168 && hour <= 720)
					timeContent = (hour / 168) + "周前";
				else
					timeContent = (hour / 24) + "天前";
			} else {
				timeContent = hour + "小时前";
			}
		} else {
			timeContent = minute + "分钟前";
		}
		return timeContent;
	}
	
	public static String getTimeDesc1(Timestamp time) {
		if (time == null)
			return "";
		String timeContent;
		Long ss = System.currentTimeMillis() - time.getTime();
		Long minute = ss / 60000;
		if (minute < 1)
			minute = 1L;
		if (minute >= 60) {
			Long hour = minute / 60;
			if (hour >= 24) {
				if (hour > 720)
					timeContent = "1月前";
				else if (hour > 168 && hour <= 720)
					timeContent = (hour / 168) + "周前";
				else
					timeContent = (hour / 24) + "天前";
			} else {
				timeContent = hour + "小时前";
			}
		} else if(minute >= 5){
			timeContent = minute + "分钟前";
		}else {
			timeContent="";
		}
		return timeContent;
	}

	/**
	 * author: bob date: 20100729 截取日期, 去掉年份 param: date1 eg. 传入"1986-07-28", 返回
	 * 07-28
	 */
	public static String getMonthAndDay(Date date) {
		return formatDate(date).substring(5);
	}

	public static Date getMillDate() {
		return new Date();
	}

	public static Timestamp getMillTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 时间差：day1-day2
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static final <T extends Date> String getDiffStr(T day1, T day2) {
		if (day1 == null || day2 == null)
			return "---";
		long diff = day1.getTime() - day2.getTime();
		long sign = diff / Math.abs(diff);
		diff = Math.abs(diff) / 1000;
		long hour = diff / 3600;
		long minu = diff % 3600 / 60;
		long second = diff % 60;
		return (sign < 0 ? "-" : "+") + (hour == 0 ? "" : hour + "小时") + (minu == 0 ? "" : minu + "分")
				+ (second == 0 ? "" : second + "秒");
	}

	/**
	 * 时间差（秒）：day1-day2
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static final <T extends Date> long getDiffSecond(T day1, T day2) {
		if (day1 == null || day2 == null)
			return 0;
		long diff = day1.getTime() - day2.getTime();
		long sign = diff / Math.abs(diff);
		diff = Math.abs(diff) / 1000;
		return sign * diff;
	}

	/**
	 * 时间差（分钟）：day1-day2
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static final <T extends Date> double getDiffMinu(T day1, T day2) {
		if (day1 == null || day2 == null)
			return 0;
		long diff = day1.getTime() - day2.getTime();
		long sign = diff / Math.abs(diff);
		diff = Math.abs(diff) / 1000;
		return Math.round(diff * 1.0d * 10 / 6.0) / 100.0 * sign;// 两位小数
	}

	/**
	 * 时间差（小时）：day1-day2
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static final <T extends Date> double getDiffHour(T day1, T day2) {
		if (day1 == null || day2 == null)
			return 0;
		long diff = day1.getTime() - day2.getTime();
		long sign = diff / Math.abs(diff);
		diff = Math.abs(diff) / 1000;
		return Math.round(diff * 1.0d / 3.6) / 1000.0 * sign;// 三位小数
	}

	public static final <T extends Date> int getDiffDay(T day1, T day2) {
		if (day1 == null || day2 == null)
			return 0;
		long diff = day1.getTime() - day2.getTime();
		diff = Math.abs(diff) / 1000;
		return Math.round(diff / (3600 * 24));
	}

	public static final <T extends Date> int getNegativeDiffDay(T day1, T day2) { // 区分正负
		if (day1 == null || day2 == null)
			return 0;
		long diff = day1.getTime() - day2.getTime();
		diff = diff / 1000;
		return Math.round(diff / (3600 * 24));
	}

	public static boolean isAfterOneHour(Date date, String time) {
		String datetime = formatDate(date) + " " + time + ":00";
		if (addHour(parseTimestamp(datetime), -1).after(getMillTimestamp())) {
			return true;
		}
		return false;
	}

	public static boolean isValidDate(String fyrq) {
		try {
			DateUtil.parseDate(fyrq);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final <T extends Date> String getAgendaDate(Date t, String pattern) {
		if (t == null)
			return "";
		try {
			Long paramsTimes = getBeginningTimeOfDay(t).getTime();
			if (getCurDate().getTime() == paramsTimes)
				return "今天";
			if (addDay(getCurDate(), 1).getTime() == paramsTimes)
				return "明天";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(t);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 当前时间点是否是上午
	 * 
	 * @param time
	 * @return
	 */
	public static final String isAmOrPm(String time) {
		if (StringUtils.isBlank(time))
			return null;
		Date date = parseDate(time, "HH:mm");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (c.get(Calendar.AM_PM) == 0)
			return "am";
		return "pm";
	}

	public static final Timestamp parseDate2Timestamp(Date date) {
		return parseTimestamp(formatTimestamp(date));
	}

	/**
	 * Jul 19, 2012 10:31:36 AM
	 * 
	 * @author bob
	 * @desc 增加/减少一天
	 */
	public static final String addDay(String day, int days) {
		Date daydate = parseDate(day);
		Date newdate = addDay(daydate, days);
		return formatDate(newdate);
	}

	public static final String getCurrentYearMonthDay() {
		return DateUtil.getCurrentYear().toString() + DateUtil.getCurrentMonth().toString() + DateUtil.getCurrentDay().toString();
	}


	public static void main(String[] args) {
		System.out.println(DateUtil.getCurrentYear());
	}
}
