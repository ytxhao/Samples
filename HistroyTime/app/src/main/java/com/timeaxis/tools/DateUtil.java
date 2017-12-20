package com.timeaxis.tools;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateUtil {

    public static String formatToNormalStyle(long time) {
        Date date = new Date(time);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }


    public static String formatToCameraProgressStyle(long time) {
        Date date = new Date(time);
        String pattern = "MM/dd HH:mm:ss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatUTCTimeToNormalStyle(long time){
        Date date = new Date(time);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return df.format(date);
    }

    public static String formatToNormalStyleV2(long time) {
        Date date = new Date(time);
        String pattern = "yyyyMMddHHmmss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatToNormalStyleV3(long time) {
        Date date = new Date(time);
        String pattern = "yyyyMMddHHmm";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Date parseNormalDateV2(String strDate) {
        String pattern = "yyyyMMddHHmmss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatHttpParamStyle(long time) {
        Date date = new Date(time);
        String pattern = "yyyyMMdd'T'HHmmss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatNormalTimeStyle(long time) {
        Date date = new Date(time);
        String pattern = "HH:mm:ss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatToEventDateStyle(long time) {
        Date date = new Date(time);
        String pattern = "yyyy-MM-dd";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatToEventTimeStyle(long time) {
        Date date = new Date(time);
        String pattern = "HH:mm";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    // 转换字符串到UTC时间
    public static long parseToUTCTime(String dateStr) {
        long time = 0;
        String pattern = "yyyyMMdd'T'HHmmss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            time = df.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    // 转换K3文件日期
    public static Date getFileDate(String path) {
        String[] pathArray = path.split("/");
        if (pathArray.length >= 5) {
            String date = pathArray[3];
            String time = pathArray[4].split("\\.")[0];
            Date result = parseFileDate(date + " " + time);
            return result;
        }

        return null;
    }

    private static Date parseFileDate(String strDate) {
        String pattern = "yyyy_MM_dd HH_mm_ss";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static String formatToK3Time(Date date) {
        String pattern = "yyyy_MM_dd";
        java.text.DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

	public static long getUtcTimestamp(){
	    // 1、取得本地时间：  
        Calendar cal = Calendar.getInstance() ;
        // 2、取得时间偏移量：  
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：  
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：  
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return cal.getTimeInMillis();
	}

    //比如传入Asia/Shanghai，返回GMT+8:00
    public static String getTimeZoneGMTOffset(String timeZoneId){
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        int offsetHour = timeZone.getRawOffset() / (60 * 60 * 1000);
        int offsetMinute = (timeZone.getRawOffset() / (60 * 1000)) % 60;
        offsetMinute = offsetMinute >= 0 ? offsetMinute : 0-offsetMinute;
        String gmtTimezone = "GMT"
                + (offsetHour >= 0 ? "+"+offsetHour : offsetHour)
                + ":"
                + (offsetMinute < 10 ? "0"+offsetMinute : offsetMinute);
        return gmtTimezone;
    }

    public static Date formatToDateStyle(String dateStr){
        DateFormat dfFolderName = new SimpleDateFormat("yyyy-MM-dd");
        Date returnDate = null;
        try{
            returnDate = dfFolderName.parse(dateStr);
        }catch (ParseException pEx){
        }
        return returnDate;
    }


    public static String formatAlertYearDate(long mTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(mTime);
    }


    public static String getCurrentYYMMDD() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        return sdf.format(System.currentTimeMillis());
    }

    public static long getMillionSeconds(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long millionSeconds = 0;
        try {
            millionSeconds = sdf.parse(dateStr).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds;
    }

    public static long getMillionSecondsFromDay(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        long millionSeconds = 0;
        try {
            millionSeconds = sdf.parse(dateStr).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds;
    }

    public static String formatToMillionSeconds(long mTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(mTime);
    }

    public static String formatToMinuteSecond(long mTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(mTime);
    }

    public static long getBeforeYearOfDay(int i){
        return getTodayMilliSeconds(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000);
    }



    public static String getMillionSecondStr(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        return sdf.format(System.currentTimeMillis());
    }


    // get hour from milliseconds
    public static int getHour(long mTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        return Integer.valueOf(sdf.format(mTime));
    }

    // get week from milliseconds, 0星期天, 6星期六
    public static int getWeek(long mTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTime);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        w = w - 1;
        if(w < 0){
            w = 0;
        }
        return w;
    }


    public static String convertStandardTime2Local(String time) {
        return convertStandardTime2Local(time,false);
    }

    public static String convertStandardTime2Local(String time, boolean useH12) {
        String result = time;
        SimpleDateFormat oldFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat newFormat = new SimpleDateFormat("h:mma");
        try {
            result = newFormat.format(oldFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }




    public static int disparityDays(long dayTimeOne, long dayTimeTwo){
        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        int betweenDays = 0;
        try {
            Date dateOne = sdf.parse(sdf.format(dayTimeOne));
            Date dateTwo = sdf.parse(sdf.format(dayTimeTwo));
            mCalendar.setTime(dateOne);
            dayTimeOne = mCalendar.getTimeInMillis();
            mCalendar.setTime(dateTwo);
            dayTimeTwo = mCalendar.getTimeInMillis();
            betweenDays = (int)((dayTimeTwo - dayTimeOne)/ (1000*3600*24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return betweenDays;
    }

    // 取当天0点的 milliseconds
    public static long getTodayMilliSeconds(){
        return getTodayMilliSeconds(System.currentTimeMillis());
    }

    // 取mTime当前0点的 milliseconds
    public static long getTodayMilliSeconds(long mTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    private static int[] allDaysOfMonth(int year) {
        int[] months = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            months[1]++;
        }
        return months;
    }

}
