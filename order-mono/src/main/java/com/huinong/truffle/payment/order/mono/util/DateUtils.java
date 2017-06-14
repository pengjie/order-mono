package com.huinong.truffle.payment.order.mono.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public final class DateUtils {

    public static String datePattern = "yyyy-MM-dd HH:mm:ss";

    public static String datePattern2 = "yyyy-MM-dd HH:mm";

    public static String datePattern3 = "yyyyMMdd";

    public final static String datePattern4 = "yyyyMMddHHmmss";

    public final static String datePattern5 = "yyyy-MM-dd";

    public final static String datePatternDBTimestamp = "yyyy-MM-dd HH:mm:ss.S";
    
    
    
    /**
     * 要用到的DATE Format的定义[yyyy-MM-dd HH:mm:ss]
     */
    public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 要用到的DATE Format的定义[yyyyMMddHHmmss]
     */
    public final static String DATE_FORMAT_SESSION = "yyyyMMddHHmmss"; // 年/月/日 时:分:秒


    /**
     * 要用到的DATE Format的定义[yyyy-MM-dd]
     */
    public static final String DATE_FORMAT_DATEONLY = "yyyy-MM-dd";


        /**
     * Global SimpleDateFormat object
     */
    public static SimpleDateFormat sdfDateOnly = new SimpleDateFormat(
            DATE_FORMAT_DATEONLY);

    public static SimpleDateFormat sdfDateTime = new SimpleDateFormat(
            DATE_FORMAT_DATETIME);            
    /**
     * 日期格式为yyyyMMddHHMMss
     */
    private static SimpleDateFormat sdfDateTimes = new SimpleDateFormat(
            DATE_FORMAT_SESSION);


    /**
     * 根据指定的Format转化String到Date
     * @author jingyul
     * @param strDate
     * @param strFmt DATE_FORMAT_DATEONLY or  DATE_FORMAT_DATETIME
     * @return
     */
    public static Date formateDate(String strDate, String strFmt) {
        if (strFmt.equals(DATE_FORMAT_DATETIME)) { //  "YYYY/MM/DD HH24:MI:SS"
            return formateDate(strDate, DateUtils.sdfDateTime);
        } else if(strFmt.equals(DATE_FORMAT_SESSION)){  //YYYYMMDDHHMI
            return formateDate(strDate,DateUtils.sdfDateTimes);
        } else if (strFmt.equals(DATE_FORMAT_DATEONLY)) { //YYYY/MM/DD
            return formateDate(strDate, DateUtils.sdfDateOnly);
        } else if (!StringUtils.isEmpty(strFmt)) {
            return formateDate(strDate, new SimpleDateFormat(strFmt));
        } else {
            return null;
        }
    }

    public static Date formateDate(String strDate, SimpleDateFormat formatter) {
        java.util.Date dt = null;
        try {
            dt = formatter.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            dt = null;
        }
        return dt;
    }

    /**
     * 利用缺省的Date格式(YYYY/MM/DD)转化String到Date
     * @param  sDate Date string
     * @return
     * @since 1.0
     * @history
     */
    public static java.util.Date formateDate(String sDate) {
        return formateDate(sDate, DateUtils.sdfDateOnly);
    }

    /**
     * 指定时间加若干秒
     *
     * @param oldDate
     * @param seconds
     * @return 相加后的时间
     */
    public static Date addDate(Date oldDate, int seconds) {
        Calendar cdTime = Calendar.getInstance();
        cdTime.setTime(oldDate);
        cdTime.add(Calendar.SECOND, seconds);
        return cdTime.getTime();
    }

    public static Date StringToDate(String datePattern, String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        try {
            if (dateString != null && !"".equals(dateString)) {
                return sdf.parse(dateString);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String dateToString(String datePattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        if (null != date) {
            return sdf.format(date);
        } else {
            return " ";
        }
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy/MM/dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDateDay(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     *
     * @return
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     *
     * @param day
     * @return
     */
    public static Date getLastDate(long day) {
        Date date = new Date();
        long date_3_hm = date.getTime() - 3600000 * 34 * day;
        Date date_3_hm_date = new Date(date_3_hm);
        return date_3_hm_date;
    }

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getStringToday(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateString = formatter.format(date);
        return dateString;
    }
    
    public static String getStrToday(String format) {
    	Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在小时
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 得到指定时间的年
     */
    public static int getYear(Date date) {
        Date currentTime = date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String year = dateString.substring(0, 4);
        return Integer.valueOf(year);
    }

    public static int getYear(Date date, int to) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        if (null != date) {
            String dateString = formatter.format(date);
            String year = dateString.substring(0, 4);
            return Integer.valueOf(year) + to;
        } else {
            return 0;
        }
    }

    /**
     * 得到当前时间的年
     */
    public static int getNowYear() {
        Date currentTime = new Date();
        return getYear(currentTime);
    }

    /**
     * 得到指定时间的月
     */
    public static int getMonth(Date date) {
        Date currentTime = date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String mongth = dateString.substring(5, 7);
        return Integer.valueOf(mongth);
    }

    public static int getDay(Date date) {
        Date currentTime = date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String day = dateString.substring(8, 10);
        return Integer.valueOf(day);
    }

    /**
     * 得到当前时间的月
     */
    public static int getNowMonth() {
        Date currentTime = new Date();
        return getMonth(currentTime);
    }

    /**
     * 得到现在分钟
     *
     * @return
     */
    public static String getTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat
     *            yyyyMMddhhmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }

    public static Date getNextDay(Date nowDate, Integer addDay) {
        try {
            String nowDateStr = dateToStr(nowDate);
            nowDate = strToDate(nowDateStr);
            long myTime = (nowDate.getTime() / 1000) + addDay * 24 * 60 * 60;
            nowDate.setTime(myTime * 1000);
            return nowDate;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否润年
     *
     * @param ddate
     * @return
     */
    public static boolean isLeapYear(String ddate) {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }

    /**
     * 返回美国时间格式 26 Apr 2006
     *
     * @param str
     * @return
     */
    public static String getEDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        String j = strtodate.toString();
        String[] k = j.split(" ");
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dat
     * @return
     */
    public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    /**
     * 判断二个时间是否在同一个周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }

    /**
     * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
     *
     * @param sdate
     * @param num
     * @return
     */
    public static String getWeek(String sdate, String num) {
        // 再转换为时间
        Date dd = DateUtils.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if (num.equals("1")) // 返回星期一所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        else if (num.equals("2")) // 返回星期二所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        else if (num.equals("3")) // 返回星期三所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        else if (num.equals("4")) // 返回星期四所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        else if (num.equals("5")) // 返回星期五所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        else if (num.equals("6")) // 返回星期六所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        else if (num.equals("0")) // 返回星期日所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = DateUtils.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static String getWeekStr(String sdate) {
        String str = "";
        str = DateUtils.getWeek(sdate);
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        long day = 0l;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
        }
        return day;
    }

    /**
     * 判断date是否大于或者小于当前时间一天
     * @param date
     * @return
     */
    public static boolean isRightTime(String date){
        boolean b = false;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        long day = 0l;
        Date nowTime = new Date();
        try {
            date1 = myFormatter.parse(date.trim());
            day = (date1.getTime() - nowTime.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day>=0 || day ==-1)
            b = true;
        return b;
    }

    /**
     * 判断date1是否大于date2
     * @param date1,date2
     * @return
     */
    public static boolean checkDate1WhetherBigerDate2(Date date1, Date date2){
        String d1 = dateToString(DateUtils.datePattern5,date1);
        String d2 = dateToString(DateUtils.datePattern5,date2);
        try {
            date1 = DateUtils.StringToDate(DateUtils.datePattern5, d1);
            date2 = DateUtils.StringToDate(DateUtils.datePattern5, d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean b = false;
        long time = 0l;
        time = date1.getTime() - date2.getTime();
        if (time>0)
            b = true;
        return b;
    }

    public static void main(String[] args){
    	/*
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = DateUtils.StringToDate(DateUtils.datePattern, "2014-12-17 23:59:59");
            d2 =DateUtils.StringToDate(DateUtils.datePattern, "2014-12-16 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean flag = checkDate1WhetherBigerDate2(d1,d2);
        System.out.println("==========="+flag);

        DecimalFormat df=(DecimalFormat)DecimalFormat.getInstance();
        df.applyLocalizedPattern("00000000000");
        for(int i=1;i<5;i++){
            System.out.println(df.format(i));
        }
        String tStr = String.valueOf(System.currentTimeMillis());
        System.out.println(tStr);
        tStr = String.valueOf(System.currentTimeMillis());
        System.out.println(tStr);
        tStr = String.valueOf(System.currentTimeMillis());
        System.out.println(tStr);
        */
    	
    	Date d3= getNextDay(new Date(),-1);
    	System.out.println(formatDateyyyymmdd(d3));
    	String str1="11:40";
    	String str2="11:30";
    	System.out.println("测试时间差:"+getTwoHour(str1,str2));

    }
    /**
     * 判断date是否大于或者小于当前时间一天
     * @param date
     * @return
     */
    public static boolean isRightTime(Date date){
        return isRightTime(dateToStr(date));
    }

    /**
     * 形成如下的日历 ， 根据传入的一个时间返回一个结构 星期日 星期一 星期二 星期三 星期四 星期五 星期六 下面是当月的各个时间
     * 此函数返回该日历第一行星期日所在的日期
     *
     * @param sdate
     * @return
     */
    public static String getNowMonth(String sdate) {
        // 取该时间所在月的一号
        sdate = sdate.substring(0, 8) + "01";

        // 得到这个月的1号是星期几
        Date date = DateUtils.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int u = c.get(Calendar.DAY_OF_WEEK);
        String newday = DateUtils.getNextDay(sdate, (1 - u) + "");
        return newday;
    }

    /**
     * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
     *
     * @param k
     *            表示是取几位随机数，可以自己定
     */

    public static String getNo(int k) {

        return getUserDate("yyyyMMddhhmmss") + getRandom(k);
    }

    /**
     * 返回一个随机数
     *
     * @param i
     * @return
     */
    public static String getRandom(int i) {
        Random jjj = new Random();
        // int suiJiShu = jjj.nextInt(9);
        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }

    /**
     * doc:给定的日期字符串；
     * 给定日期的样式
     * 例如：20111231000000    yyyyMMddHHmmss
     * 返回日期类型
     * */
    public static Date GetDate(String date, String date_format) {
        Date fDate = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(date_format);
            fDate = formatter.parse(date);
        } catch (Exception e) {
            fDate = null;
        }
        return fDate;
    }

    public static String opDate(Date date, int to) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(2, to);
        gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
        return DateUtils.formatDateyyyymmddhhmmss(gc.getTime());
    }

    public static String formatDateyyyymmddhhmmss(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern4);
        return sdf.format(date);
    }

    public static String formatDateyyyymmdd(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern3);
        return sdf.format(date);
    }

    /**
     * doc:给定的日期字符串；
     * 给定日期的样式
     * 例如：20120717000000    yyyyMMddHHmmss
     * 返回类型yyyy-MM-dd
     * */
    public static String formatStringDate(String sdate) {
        if (sdate == null || sdate.equals("") || sdate.length() < 8) {
            return "";
        }
        sdate = sdate.substring(0, 4) + "-" + sdate.substring(4, 6) + "-" + sdate.substring(6, 8);
        return sdate;
    }

    public static boolean equalsDate(Date countDate, Date countDate1) {
        String str1 = DateUtils.dateToStr(countDate);
        String str2 = DateUtils.dateToStr(countDate1);
        Date date1 = DateUtils.strToDate(str1);
        Date date2 = DateUtils.strToDate(str2);

        if(date1.compareTo(date2)==0)
            return true;
        if (str1.trim().equals(str2.trim()))
            return true;
        return false;
    }

    public static Date dbTimestampStringToDate(String sDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternDBTimestamp);
        try {
            return sdf.parse(sDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dbTimestampDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternDBTimestamp);
        return sdf.format(date);
    }
    
    /**
     * 计算 day 天后的时间
     * 
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
    
    /**
     * 时间戳数据转Str
     * @param str
     * @return
     */
    public static String long2Str(String str){
    	long timeInMilliseconds = Long.parseLong(str);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliseconds);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        return formatter.format(date);
    }
}
