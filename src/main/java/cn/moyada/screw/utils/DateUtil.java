package cn.moyada.screw.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 * Created by xueyikang on 2016/11/22.
 */
public final class DateUtil {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取今天凌晨时间
     * @return 时间字符
     */
    public static String yesterday2Str(){
        return LocalDate.now().minusDays(1).format(DATE_FORMATTER);
    }

    public static String day2Str(){
        return LocalDate.now().format(DATE_FORMATTER);
    }


    /**
     * 获取今天凌晨时间
     * @return 时间字符
     */
    public static String now2Str(){
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    /**
     * 获取今天凌晨时间
     * @return 时间对象
     */
    public static Date getDay(){
        return Date.from(LocalDate.now().atStartOfDay().atZone(ZONE_ID).toInstant());
    }

    /**
     * 获取当前时间
     * @return 时间对象
     */
    public static Date nowDate(){
        return Date.from(LocalDateTime.now().atZone(ZONE_ID).toInstant());
    }

    /**
     * 获取当前时间
     * @return 时间对象
     */
    public static Timestamp nowTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }


    public static java.sql.Date date2Date(Date data){
        if(null == data) {
            return null;
        }
        return new java.sql.Date(data.getTime());
    }

    public static Date date2Date(java.sql.Date data){
        if(null == data) {
            return null;
        }
        return new Date(data.getTime());
    }

    public static java.sql.Date date2Date(LocalDate localDate){
        return java.sql.Date.valueOf(localDate);
    }
}
