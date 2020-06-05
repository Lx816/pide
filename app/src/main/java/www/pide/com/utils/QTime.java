package www.pide.com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QTime {

    public static String QTime() {
        long time = System.currentTimeMillis();
        String timeStamp = null;
        try {
            timeStamp = dateToStamp(stampToDate(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static String QSign(String qTime) {
        String sign = "";
        try {
            sign = DesEcbUtil.encode(URLs.KEY, qTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(time);
        long ts = date.getTime();
        return String.valueOf(ts);
    }
    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp1(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = simpleDateFormat.parse(time);
        long ts = date.getTime();
        return String.valueOf(ts);
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    private static final long MINUTE_SECONDS = 60; //1分钟多少秒
    private static final long HOUR_SECONDS = MINUTE_SECONDS * 60;
    private static final long DAY_SECONDS = HOUR_SECONDS * 24;
    private static final long YEAR_SECONDS = DAY_SECONDS * 365;

//    public static String testPassedTime(long nowMilliseconds, long oldMilliseconds) {
//        long passed = (nowMilliseconds - oldMilliseconds) / 1000;//转为秒
//        if (passed > YEAR_SECONDS) {
//            return passed / YEAR_SECONDS + "年";
//        } else if (passed > DAY_SECONDS) {
//            return passed / DAY_SECONDS + "天";
//        } else if (passed > HOUR_SECONDS) {
//            return passed / HOUR_SECONDS + "小时";
//        } else if (passed > MINUTE_SECONDS) {
//            return passed / MINUTE_SECONDS + "分钟";
//        } else {
//            return passed + "秒";
//        }
//    }
    /**
     *
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String mdays="";
        if(days>0){
            mdays=days + " 天 ";
        }
        String mhours="";
        if(hours>0){
            mhours=hours + " 小时 ";
        }
        String mminutes="";
        if(minutes>0){
            mminutes=minutes + " 分 ";
        }
        String mseconds="";
        if(days>0 || hours>0 || minutes>0){
            return mdays + mhours + mminutes;
        }else {
            if(seconds>0){
                mseconds=seconds + " 秒 ";
            }
            return mseconds;
        }
    }
    public static int testPassedTime1(long begin, long end) {
        return formatDuring1(begin- end);
    }
    public static int formatDuring1(long mss) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        int mminutes = (int)minutes;
        int mhours = (int)hours;
        int mseconds = (int)seconds;
        return mhours*60*60+mminutes*60+mseconds;
    }
    public static int testPassedTime2(long begin, long end) {
        return formatDuring1(begin- end);
    }
    public static int formatDuring2(long mss) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        int mminutes = (int)minutes;
        int mhours = (int)hours;
        return mhours*60+mminutes;
    }
    /**
     *
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return  输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String testPassedTime(long begin, long end) {
        return formatDuring(begin- end);
    }


    /**
     * 获得星期几
     *
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        String Week = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "星期日";
        }
        if (wek == 2) {
            Week += "星期一";
        }
        if (wek == 3) {
            Week += "星期二";
        }
        if (wek == 4) {
            Week += "星期三";
        }
        if (wek == 5) {
            Week += "星期四";
        }
        if (wek == 6) {
            Week += "星期五";
        }
        if (wek == 7) {
            Week += "星期六";
        }
        return Week;
    }


    /**
     * 20 * 字符串转换成日期
     * 21 * @param str
     * 22 * @return date
     * 23
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String StringToDate(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTime(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeG(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeN(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeY(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("MM");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeR(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("dd");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeS(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("HH");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeF(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("mm");
        String s = format1.format(date);
        return s;
    }
    public static String StringToTimeM(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("ss");
        String s = format1.format(date);
        return s;
    }
}
