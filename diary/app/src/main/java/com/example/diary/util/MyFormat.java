package com.example.diary.util;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFormat {
    /**
     * 拼接并格式化时间 HH:mm:ss
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static String timeFormat(int hour,int minute,int second){
        if (hour>23||hour<0||minute>59||minute<0||second>59||second<0) {
            return null;
        }
        String result;
        //结果
        String strHour = String.valueOf(hour);
        //int类型转string类型
        String strMinute = String.valueOf(minute);
        String strSecond = String.valueOf(second);
        //时间格式化
        if (hour<10) {
            strHour = "0" + strHour;
            //小时小于10，显示01，02.....
        }
        if (minute<10) {
            strMinute = "0" + strMinute;
        }
        if (second<10) {
            strSecond = "0" + strSecond;
        }
        result = strHour + ":" + strMinute + ":" + strSecond;
        //返回时间
        return result;
    }

    /**
     * 拼接并格式化时间 HH:mm
     * @param hour
     * @param minute
     * @return
     */
    public static String timeFormat(int hour, int minute){
        //时间格式函数重载
        if (hour>23||hour<0||minute>59||minute<0) {
            return null;
        }
        String result;
        String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);
        if (hour<10) {
            strHour = "0" + strHour;
        }
        if (minute<10) {
            strMinute = "0" + strMinute;
        }
        result = strHour + ":" + strMinute;
        return result;
    }

    /*** 日期转字符串*/
    @SuppressLint("SimpleDateFormat")
    public static String myDateFormat(Date date, @Nullable DateFormatType dateFormatType){
        //定义是否可为空指针@Nullable 如果可以传入NULL值，则标记为@Nullable，如果不可以，则标注为@Nonnull。
        //时间为date类型的格式化

        SimpleDateFormat dateFormat;
        //SimpleDateFormat格式化时间
        if (dateFormatType == null){
            dateFormatType = DateFormatType.NORMAL_TIME;
        }
        //dateFormatType != null
        switch (dateFormatType){
            case NORMAL_TIME:
                //枚举类里的每种可能
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            case REMOVE_YEAR_TIME:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case NORMAL_DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case REMOVE_YEAR_DATE:
                dateFormat = new SimpleDateFormat("MM-dd");
                break;
            case SPECIAL_TYPE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
        }
        return dateFormat.format(date);
        //把date按照上面的可能 格式化 并返回
    }

    /**
     * 字符串转日期
     * @param str
     * @return DATE
     */
    @SuppressLint("SimpleDateFormat")
    public static Date myDateFormat(String str,@Nullable DateFormatType dateFormatType){
        //时间为String类型的格式化
        SimpleDateFormat dateFormat;
        if (dateFormatType == null){
            dateFormatType = DateFormatType.NORMAL_TIME;
        }
        switch (dateFormatType){
            case NORMAL_TIME:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
            case REMOVE_YEAR_TIME:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case NORMAL_DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case REMOVE_YEAR_DATE:
                dateFormat = new SimpleDateFormat("MM-dd");
                break;
            case SPECIAL_TYPE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
        }
        try {
            return dateFormat.parse(str);
            //如果执行正确
            //SimpleDateFormat中的parse方法可以把sting类型的字符串转换成特定格式的date类型
        } catch (ParseException e) {
            return null;
            //如果出了异常
        }
    }

    /**
     * 字符串转日期
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param dateFormatType
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Date myDateFormat(Integer year,
                                    Integer month,
                                    Integer day,
                                    Integer hour,
                                    Integer minute,
                                    @Nullable DateFormatType dateFormatType){
        //myDateFormat方法重载
        //Integer是java为int提供的封装类 Integer可以区分出未赋值和值为0的区别，int则无法表达出未赋值的情况
        SimpleDateFormat dateFormat;
        String str;
        if (dateFormatType == null){
            dateFormatType = DateFormatType.NORMAL_TIME;
        }
        switch (dateFormatType){
            case REMOVE_YEAR_TIME:
                dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                str = month+"-"+day+" "+hour+":"+minute;
                break;
            case NORMAL_DATE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                str = year+"-"+month+"-"+day;
                break;
            case REMOVE_YEAR_DATE:
                dateFormat = new SimpleDateFormat("MM-dd");
                str = month+"-"+day;
                break;
            case SPECIAL_TYPE:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                str = year+"-"+month+"-"+day+"-"+hour+"-"+minute;
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                str = year+"-"+month+"-"+day+" "+hour+":"+minute;
                break;
        }
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /* 得到时间字符串，判断是否去除年份
    去除规则，如果年份是当年则去除*/
    public static String getTimeStr(Date date){
        int nowYear = new MyTimeGetter(new Date(System.currentTimeMillis())).getYear();
        //将当前系统时间 拆分后，获取年
        int targetYear = new MyTimeGetter(date).getYear();
        //得到时间字符串  拆分后，获取年
        if (nowYear == targetYear){
            // 是当前年份 去除年份
            return myDateFormat(date,DateFormatType.REMOVE_YEAR_TIME);
        }
        return myDateFormat(date,DateFormatType.NORMAL_TIME);
    }
}
