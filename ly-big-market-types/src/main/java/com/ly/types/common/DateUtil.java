package com.ly.types.common;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Stream;

import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;

/**
 * @Author admin
 * @create 2024/5/31 10:15
 */
@UtilityClass
@Slf4j
public class DateUtil {
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_M = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_DATETIME_DATETIME_NONE = "yyyyMMddHHmmss";
    public static final String PATTERN_DATETIME_YEAR_NONE = "yyyyMMdd";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_TIME_HHMM00 = "HH:mm:00";
    public static final String PATTERN_YYYYMM = "yyyyMM";
    public static final String PATTERN_YYYY = "yyyy";
    public static final String PATTERN_YYYY_MM = "yyyy-MM";
    public static final String PATTERN_SLASH_YM = "yyyy/MM/dd HH:mm:ss";
    public static final String PATTERN_HHMM00 = "yyyy-MM-dd HH:mm:00";
    public static final String PATTERN_000000 = "yyyy-MM-dd 00:00:00";
    public static final String PATTERN_UTC_DATETIME = "yyyy-MM-dd'T'HH:mm:ss+08:00";
    public static final String PATTERN_UTC_V2_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.000+08:00";
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * java 8 时间格式化
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATETIME).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter DATETIME_FORMATTER_M = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATETIME_M).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_DATE).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_TIME).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter YYYY_MM_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_YYYY_MM).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter PATTERN_HHMM00_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_HHMM00).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter PATTERN_000000_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.PATTERN_000000).withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter TIME_FORMATTER_HHMM00 = DateTimeFormatter.ofPattern(DateUtil.PATTERN_TIME_HHMM00).withZone(ZoneId.systemDefault());

    public static String toIsoStr(Date date) {
        return format(date, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    /**
     * 添加年
     *
     * @param date       时间
     * @param yearsToAdd 添加的年数
     * @return 设置后的时间
     */
    public static Date plusYears(Date date, int yearsToAdd) {
        return DateUtil.set(date, Calendar.YEAR, yearsToAdd);
    }

    /**
     * 添加月
     *
     * @param date        时间
     * @param monthsToAdd 添加的月数
     * @return 设置后的时间
     */
    public static Date plusMonths(Date date, int monthsToAdd) {
        return DateUtil.set(date, Calendar.MONTH, monthsToAdd);
    }

    /**
     * 添加周
     *
     * @param date       时间
     * @param weeksToAdd 添加的周数
     * @return 设置后的时间
     */
    public static Date plusWeeks(Date date, int weeksToAdd) {
        return DateUtil.plus(date, Period.ofWeeks(weeksToAdd));
    }

    /**
     * 添加天
     *
     * @param date      时间
     * @param daysToAdd 添加的天数
     * @return 设置后的时间
     */
    public static Date plusDays(Date date, long daysToAdd) {
        return DateUtil.plus(date, Duration.ofDays(daysToAdd));
    }

    public static String plusDays(String dayStr, long daysToAdd) {
        LocalDate localDate = string2DateDay(dayStr);
        LocalDate plusDay = localDate.plusDays(daysToAdd);
        return plusDay.toString();
    }

    /**
     * 添加小时
     *
     * @param date       时间
     * @param hoursToAdd 添加的小时数
     * @return 设置后的时间
     */
    public static Date plusHours(Date date, long hoursToAdd) {
        return DateUtil.plus(date, Duration.ofHours(hoursToAdd));
    }

    /**
     * 添加分钟
     *
     * @param date         时间
     * @param minutesToAdd 添加的分钟数
     * @return 设置后的时间
     */
    public static Date plusMinutes(Date date, long minutesToAdd) {
        return DateUtil.plus(date, Duration.ofMinutes(minutesToAdd));
    }

    /**
     * 添加秒
     *
     * @param date         时间
     * @param secondsToAdd 添加的秒数
     * @return 设置后的时间
     */
    public static Date plusSeconds(Date date, long secondsToAdd) {
        return DateUtil.plus(date, Duration.ofSeconds(secondsToAdd));
    }

    /**
     * 添加毫秒
     *
     * @param date        时间
     * @param millisToAdd 添加的毫秒数
     * @return 设置后的时间
     */
    public static Date plusMillis(Date date, long millisToAdd) {
        return DateUtil.plus(date, Duration.ofMillis(millisToAdd));
    }

    /**
     * 添加纳秒
     *
     * @param date       时间
     * @param nanosToAdd 添加的纳秒数
     * @return 设置后的时间
     */
    public static Date plusNanos(Date date, long nanosToAdd) {
        return DateUtil.plus(date, Duration.ofNanos(nanosToAdd));
    }

    /**
     * 日期添加时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date plus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.plus(amount));
    }

    /**
     * 减少年
     *
     * @param date  时间
     * @param years 减少的年数
     * @return 设置后的时间
     */
    public static Date minusYears(Date date, int years) {
        return DateUtil.set(date, Calendar.YEAR, -years);
    }

    /**
     * 减少月
     *
     * @param date   时间
     * @param months 减少的月数
     * @return 设置后的时间
     */
    public static Date minusMonths(Date date, int months) {
        return DateUtil.set(date, Calendar.MONTH, -months);
    }

    /**
     * 减少周
     *
     * @param date  时间
     * @param weeks 减少的周数
     * @return 设置后的时间
     */
    public static Date minusWeeks(Date date, int weeks) {
        return DateUtil.minus(date, Period.ofWeeks(weeks));
    }

    /**
     * 减少天
     *
     * @param date 时间
     * @param days 减少的天数
     * @return 设置后的时间
     */
    public static Date minusDays(Date date, long days) {
        return DateUtil.minus(date, Duration.ofDays(days));
    }

    /**
     * 减少小时
     *
     * @param date  时间
     * @param hours 减少的小时数
     * @return 设置后的时间
     */
    public static Date minusHours(Date date, long hours) {
        return DateUtil.minus(date, Duration.ofHours(hours));
    }

    /**
     * 减少分钟
     *
     * @param date    时间
     * @param minutes 减少的分钟数
     * @return 设置后的时间
     */
    public static Date minusMinutes(Date date, long minutes) {
        return DateUtil.minus(date, Duration.ofMinutes(minutes));
    }

    /**
     * 返回两个时间中的差值（分钟）
     */
    public static Long minutesDiff(Date earlyTime, Date lateTime) {
        return Duration.between(LocalDateTime.ofInstant(earlyTime.toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(lateTime.toInstant(), ZoneId.systemDefault())).toMinutes();
    }

    /**
     * 减少秒
     *
     * @param date    时间
     * @param seconds 减少的秒数
     * @return 设置后的时间
     */
    public static Date minusSeconds(Date date, long seconds) {
        return DateUtil.minus(date, Duration.ofSeconds(seconds));
    }

    /**
     * 减少毫秒
     *
     * @param date   时间
     * @param millis 减少的毫秒数
     * @return 设置后的时间
     */
    public static Date minusMillis(Date date, long millis) {
        return DateUtil.minus(date, Duration.ofMillis(millis));
    }

    /**
     * 减少纳秒
     *
     * @param date  时间
     * @param nanos 减少的纳秒数
     * @return 设置后的时间
     */
    public static Date minusNanos(Date date, long nanos) {
        return DateUtil.minus(date, Duration.ofNanos(nanos));
    }

    /**
     * 日期减少时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date minus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.minus(amount));
    }

    /**
     * 设置日期属性
     *
     * @param date          时间
     * @param calendarField 更改的属性
     * @param amount        更改数，-1表示减少
     * @return 设置后的时间
     */
    private static Date set(Date date, int calendarField, int amount) {
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 日期时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return DATETIME_FORMATTER.format(date.toInstant());
    }

    /**
     * 日期时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime00(Date date) {
        if (date == null) {
            return null;
        }
        return PATTERN_HHMM00_FORMATTER.format(date.toInstant());
    }

    /**
     * 日期时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime000000(Date date) {
        if (date == null) {
            return null;
        }
        return PATTERN_000000_FORMATTER.format(date.toInstant());
    }

    /**
     * 日期格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date.toInstant());
    }

    /**
     * 时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatTimeHHMMSS(Date date) {
        if (date == null) {
            return null;
        }
        return TIME_FORMATTER.format(date.toInstant());
    }

    /**
     * 时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatTimeHHMM00(Date date) {
        if (date == null) {
            return null;
        }
        return PATTERN_HHMM00_FORMATTER.format(date.toInstant());
    }

    /**
     * 时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatHHMM00(Date date) {
        if (date == null) {
            return null;
        }
        return TIME_FORMATTER_HHMM00.format(date.toInstant());
    }

    /**
     * 日期格式化
     *
     * @param date    时间
     * @param pattern 表达式
     * @return 格式化后的时间
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(date.toInstant());
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatDateTime(TemporalAccessor temporal) {
        return DATETIME_FORMATTER.format(temporal);
    }

    /**
     * java8 日期时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatDate(TemporalAccessor temporal) {
        return DATE_FORMATTER.format(temporal);
    }

    /**
     * java8 时间格式化
     *
     * @param temporal 时间
     * @return 格式化后的时间
     */
    public static String formatTime(TemporalAccessor temporal) {
        return TIME_FORMATTER.format(temporal);
    }

    /**
     * java8 日期格式化
     *
     * @param temporal 时间
     * @param pattern  表达式
     * @return 格式化后的时间
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(temporal);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static Date parse(String dateStr, String pattern) {
        return DateUtil.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param format  DateTimeFormatter
     * @return 时间
     */
    public static Date parse(String dateStr, DateTimeFormatter format) {
        if (format.getZone() == null) {
            format = format.withZone(ZoneId.systemDefault());
        }
        Instant instant = format.parse(dateStr, Instant::from);
        return Date.from(instant);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static <T> T parse(String dateStr, String pattern, TemporalQuery<T> query) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).parse(dateStr, query);
    }

    /**
     * 时间转 Instant
     *
     * @param dateTime 时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Instant 转 时间
     *
     * @param instant Instant
     * @return Instant
     */
    public static LocalDateTime toDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date Date
     * @return Instant
     */
    public static LocalDateTime toDateTime(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return DateUtil.toDateTime(date.toInstant());
    }

    /**
     * 转换成 date
     *
     * @param dateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(DateUtil.toInstant(dateTime));
    }

    /**
     * 转换成 date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts local date time to Calendar.
     */
    public static Calendar toCalendar(final LocalDateTime localDateTime) {
        return GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));
    }

    /**
     * localDateTime 转换成毫秒数
     *
     * @param localDateTime LocalDateTime
     * @return long
     */
    public static long toMilliseconds(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * localDate 转换成毫秒数
     *
     * @param localDate LocalDate
     * @return long
     */
    public static long toMilliseconds(LocalDate localDate) {
        return toMilliseconds(localDate.atStartOfDay());
    }

    /**
     * 转换成java8 时间
     *
     * @param calendar 日历
     * @return LocalDateTime
     */
    public static LocalDateTime fromCalendar(final Calendar calendar) {
        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }

    /**
     * 转换成java8 时间
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime fromInstant(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime fromDate(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param milliseconds 毫秒数
     * @return LocalDateTime
     */
    public static LocalDateTime fromMilliseconds(final long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
    }

    /**
     * 转换成java8 时间
     *
     * @param seconds 秒数
     * @return LocalDateTime
     */
    public static LocalDateTime fromSeconds(final long seconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault());
    }

    /**
     * 比较2个时间差，跨度比较小
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 时间间隔
     */
    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    /**
     * 比较2个时间差，跨度比较大，年月日为单位
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Period between(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate);
    }

    public static int betweenDayByDay(String startDate, String endDate) {
        Long day = (DateUtil.day2Date(endDate).getTime() - DateUtil.day2Date(startDate).getTime()) / (1000 * 60 * 60 * 24);
        return day.intValue();
    }

    public static int betweenDayByDay(Date startDate, Date endDate) {
        Long day = (DateUtil.day2Date(DateUtil.date2StringYYMMDD(endDate)).getTime() - DateUtil.day2Date(DateUtil.date2StringYYMMDD(startDate)).getTime()) / (1000 * 60 * 60 * 24);
        return day.intValue();
    }

    public static int betweenDayByDate(String startDateStr, String endDateStr) {
        if (StringUtils.isEmpty(startDateStr) || StringUtils.isEmpty(endDateStr)) {
            return NumberUtils.INTEGER_ZERO;
        }

        Date startDate = DateUtil.string2Date(startDateStr);
        Date endDate = DateUtil.string2Date(endDateStr);
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return NumberUtils.INTEGER_ZERO;
        }
        Long day = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
        return day.intValue();
    }

    public static int betweenDayByDate(Date startDate, Date endDate) {
        Long day = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
        return day.intValue();
    }

    public static int durDays(String startDay, String endDay) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        try {
            SimpleDateFormat formatter1 = new SimpleDateFormat(PATTERN_DATE);
            calendar1.setTime(formatter1.parse(startDay));
            calendar2.setTime(formatter1.parse(endDay));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) ((calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000 / 60 / 60 / 24) + 1;
    }

    public static long compareDatesByDay(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime()) / (1000 * 3600 * 24);
    }

    /**
     * 比较2个 时间差
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Duration between(Date startDate, Date endDate) {
        return Duration.between(startDate.toInstant(), endDate.toInstant());
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static LocalDateTime parseDateTime(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return DateUtil.parseDateTime(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public static LocalDateTime parseDateTime(String dateStr, DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public static LocalDateTime parseDateTime(String dateStr) {
        return DateUtil.parseDateTime(dateStr, DateUtil.DATETIME_FORMATTER);
    }

    public static LocalDateTime parseDateTimeByDateDay(String dateStr) {
        return parseDateTime(dateStr + " 00:00:00");
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 表达式
     * @return 时间
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return DateUtil.parseDate(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * 将字符串转换为日期
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public static LocalDate parseDate(String dateStr) {
        return DateUtil.parseDate(dateStr, DateUtil.DATE_FORMATTER);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @param pattern 时间正则
     * @return 时间
     */
    public static LocalTime parseTime(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return DateUtil.parseTime(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr   时间字符串
     * @param formatter DateTimeFormatter
     * @return 时间
     */
    public static LocalTime parseTime(String dateStr, DateTimeFormatter formatter) {
        return LocalTime.parse(dateStr, formatter);
    }

    /**
     * 将字符串转换为时间
     *
     * @param dateStr 时间字符串
     * @return 时间
     */
    public static LocalTime parseTime(String dateStr) {
        return DateUtil.parseTime(dateStr, DateUtil.TIME_FORMATTER);
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static Long convertDateToLong(Date date) {
        LocalDateTime dateTime = DateUtil.toDateTime(date);
        return LocalDateTime.from(dateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Integer parseTimeToInteger(Date date, String pattern) {
        LocalDateTime dateTime = DateUtil.toDateTime(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return Integer.parseInt(formatter.format(dateTime));
    }

    public Boolean isNowBetween(Date start, Date end) {
        return new Date().before(end) && new Date().after(start);
    }

    public Boolean isBetween(Date date, Date start, Date end) {
        return date.before(end) && date.after(start);
    }

    /**
     * 当前时间在当日的某个时间之前
     */
    public static boolean mealIsNowBeforeSpecifiedTime(String time) {
        LocalDateTime tempNow = LocalDateTime.now();
        String date = DateUtil.formatDate(tempNow);
        LocalDateTime endOffComparedTime = DateUtil.parseDateTime(date + " " + time);
        //设置的小时时间
        Integer settingHour = endOffComparedTime.getHour();
//        Integer nowDay = tempNow.getDayOfMonth();
        Integer nowHour = tempNow.getHour();
        DateUtil.plusDays(DateUtil.toDate(tempNow), 1);
        if (settingHour >= 0 && settingHour <= 5 && nowHour > 5) {
            LocalDateTime zeroLocalTimeFor24 = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            //当截止时间是0点到5点质检 而当前时间又在24点前则把截止时间+1
            if (tempNow.isBefore(zeroLocalTimeFor24)) {
                Date endDate = DateUtil.plusDays(DateUtil.toDate(endOffComparedTime), 1);
                endOffComparedTime = DateUtil.toDateTime(endDate);
            }
        }
        return tempNow.isBefore(endOffComparedTime);
    }

    public static String transCnWeek(Integer week) {
        switch (week) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "";
        }
    }

    public static String transCnYmd(LocalDate date) {
        return date.getYear() + "年" + date.getMonthValue() + "月" + date.getDayOfMonth() + "日";
    }

    public static String transCnYm(LocalDate date) {
        return date.getYear() + "年" + date.getMonthValue() + "月";
    }

    public static List<String> listMonth(String month) {
        return listMonth(LocalDate.parse(month));
    }

    public static List<String> listDays(String dayS, String dayE) {
        List<String> list = new ArrayList<>();
        if (dayE.equals(dayS)) {
            list.add(dayS);
            return list;
        }
        LocalDate startDate = LocalDate.parse(dayS);
        LocalDate endDate = LocalDate.parse(dayE);
        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            list.add(f.toString());
        });
        return list;
    }

    public static List<String> listDays(Date start, Date end) {
        List<String> dateDays = Lists.newArrayList();
        LocalDate startDate = dateToLocalDate(start);
        LocalDate endDate = dateToLocalDate(end);
        if (startDate.isAfter(endDate)) {
            return dateDays;
        }
        while (!startDate.isAfter(endDate)) {
            dateDays.add(startDate.toString());
            startDate = startDate.plusDays(1);
        }
        return dateDays;
    }

    public static List<String> listMonth(LocalDate date) {

        List<String> list = new ArrayList<>();
        LocalDate startDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            list.add(f.toString());
        });
        return list;
    }

    public static List<String> listMonths(String monthStart, String monthEnd) {

        List<String> list = new ArrayList<>();
        if (monthStart.equals(monthEnd)) {
            list.add(monthStart);
            return list;
        }
        LocalDate startDate = LocalDate.parse(monthStart + "-01");
        LocalDate endDate = LocalDate.parse(monthEnd + "-01");
        long distance = ChronoUnit.MONTHS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
            list.add(f.format(YYYY_MM_FORMATTER));
        });
        return list;
    }

    public static List<String> listDaysOfMonth(String month) {
        try {
            return listDaysOfMonth(LocalDate.parse(month));
        } catch (Exception e) {
            return listDaysOfMonth(LocalDate.parse(month + "-01"));
        }
    }

    public static List<String> listDaysOfMonth(LocalDate date) {

        List<String> list = new ArrayList<>();
        LocalDate startDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endDate = date.with(TemporalAdjusters.lastDayOfMonth());

        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            list.add(f.toString());
        });
        return list;
    }

    /**
     * 返回距离当前时间后 几个月的天数据
     *
     * @param date
     * @param month
     * @return
     */
    public static List<String> listDaysOfMonth(Date date, int month) {
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -month); //设置为前month月
        Date startDate = calendar.getTime(); //得到前month月的时间
        Calendar calendarEnd = Calendar.getInstance(); //得到日历
        calendarEnd.setTime(date);//把当前时间赋给日历
        calendarEnd.add(Calendar.MONTH, month); //设置为month月
        Date endDate = calendarEnd.getTime(); //得到前month月的时间
        for (; !endDate.before(startDate); ) {
            list.add(date2StringYYMMDD(startDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
        }
        return list;
    }

    /**
     * 返回距离当前时间前几个月的天数据
     *
     * @param date
     * @param month
     * @return
     */
    public static List<String> listBeforeMonthDay(Date date, int month) {
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -month); //设置为前month月
        Date startDate = calendar.getTime(); //得到前month月的时间
        for (; !date.before(startDate); ) {
            list.add(date2StringYYMMDD(startDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
        }
        return list;
    }

    public static List<String> listRangeDateDay(String startDay, String endDay) {
        List<String> list = new ArrayList<>();
        Date start = DateUtil.parse(startDay + " 00:00:00", DateUtil.PATTERN_DATETIME);
        Date end = DateUtil.parse(endDay + " 23:59:59", DateUtil.PATTERN_DATETIME);
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(start);//把当前时间赋给日历
        Date startDate = calendar.getTime();
        for (; !end.before(startDate); ) {
            list.add(date2StringYYMMDD(startDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
        }
        return list;
    }

    public static LocalDateTime string2LocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static Date string2Date(String date) {
        if (Objects.isNull(date)) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return DateUtil.localDateTime2Date(dateTime);
    }

    public static Date string2Date(String date, String pattern) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
        return DateUtil.localDateTime2Date(dateTime);
    }

    public static Date day2Date(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return DateUtil.toDate(localDate);
    }

    public static Date day2Date(String date, String pattern) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        return DateUtil.toDate(localDate);
    }

    public static LocalDate string2DateDay(String date) {
        return LocalDate.parse(date);
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static String nowString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String nowDateDay() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String nowDay(String format) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
    }

    public static LocalDate nowDay() {
        return LocalDate.now();
    }

    public static String nowStringPriHour(Long hours) {
        return LocalDateTime.now().plusHours(hours).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDate();
    }

    public static boolean checkStringTimeDayIsEqual(Date time1, Date time2) {
        if (Objects.isNull(time1) || Objects.isNull(time2)) {
            return false;
        }
        return DateUtil.dateToLocalDateTime(time1).toLocalDate().equals(DateUtil.dateToLocalDateTime(time2).toLocalDate());
    }

    public static String transToStringTime(double time) {
        if (time < 0) {
            return "0.00";
        }
        return String.format("%.2f", (time / 3600));
    }

    public static String transToStringTime(Long time) {
        return transToStringTime((double) time);
    }

    public static String localDateTime2String(LocalDateTime time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(time);
    }

    public static String localDate2String(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return df.format(localDate);
    }

    public static String date2String(Date time) {
        if (Objects.isNull(time)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(time);
    }

    public static String date2StringYYMMDD(Date time) {
        if (Objects.isNull(time)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(time);
    }

    public static Date LocalDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }

    //获取两个时间区间的交集时间
    public static Long checkIntersectionTime(LocalDateTime time1Start, LocalDateTime time1End,
                                             LocalDateTime time2Start, LocalDateTime time2End) {

        if (Objects.isNull(time1End) || Objects.isNull(time2End)) {
            return 0L;
        }
        if (time1End.isBefore(time2Start) || time2End.isBefore(time1Start)) {
            return 0L;
        }
        List<Long> longs = Arrays.asList(
                Math.abs(Duration.between(time2Start, time1End).getSeconds()),
                Math.abs(Duration.between(time1Start, time1End).getSeconds()),
                Math.abs(Duration.between(time2Start, time2End).getSeconds()),
                Math.abs(Duration.between(time1Start, time2End).getSeconds())
        );

        return Collections.min(longs);
    }

    public static LocalDateTime getStatutoryHolidayStart(String day) {
        return DateUtil.string2LocalDateTime(day + " " + "00:00:00");
    }

    public static LocalDateTime getStatutoryHolidayEnd(String day) {
        return DateUtil.string2LocalDateTime(string2DateDay(day).plusDays(1).toString() + " 00:00:00");
    }

    /**
     * 日期格式化
     *
     * @param date    时间
     * @param pattern 表达式
     * @return 格式化后的时间
     */
    public static String dateToString(Date date, String pattern) {
        if (Objects.isNull(date)) {
            return StringUtils.EMPTY;
        }
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(date.toInstant());
    }


    public static String dateToString(Date date) {
        if (Objects.isNull(date)) {
            return StringUtils.EMPTY;
        }
        return dateToString(date, PATTERN_DATETIME);
    }

    public static String getNowDateString(String formatter) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(formatter);
        return dtf.format(LocalDateTime.now());
    }

    /**
     * 日期是否正确
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static boolean isDate(String dateStr, String pattern) {
        try {
            if (pattern.contains("HH:mm:ss")) {
                if (pattern.equals("HH:mm:ss")) {
                    string2Date(LocalDate.now().toString() + " " + dateStr, DateUtil.PATTERN_DATETIME);
                } else {
                    string2Date(dateStr, pattern);
                }
            } else if (pattern.contains("dd")) {
                day2Date(dateStr, pattern);
            } else if (pattern.contains("MM")) {
                YearMonth.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
            } else if (pattern.contains("yyyy")) {
                Year.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static boolean isNotDate(String dateStr, String pattern) {
        return !isDate(dateStr, pattern);
    }

    public static String getCurrentDateYYYYMMDD() {
        Date date = new Date();
        return format(date, "yyyyMMdd");
    }

    public boolean isFormatYmd(String time) {
        DateTimeFormatter ldt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean dateFlag = true;
        try {
            LocalDate.parse(time, ldt);
        } catch (Exception e) {
            dateFlag = false;
        }
        return dateFlag;
    }

    public boolean isFormatYmdHms(String time) {
        DateTimeFormatter ldt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean dateFlag = true;
        try {
            LocalDateTime.parse(time, ldt);
        } catch (Exception e) {
            dateFlag = false;
        }
        return dateFlag;
    }

    public static String localDateTime2DateDayOfFormat(String time) {

        DateTimeFormatter ldt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter ldt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            return LocalDate.parse(time, ldt).toString();
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(time, ldt2).toLocalDate().toString();
            } catch (Exception e1) {
                return time;
            }
        }
    }

    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static Long convertTimeToLong(Date date) {
        String dateString = dateToString(date);
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(dateString, ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取一天的开始时间
     *
     * @param date
     * @return
     */
    public static Date getStartTime(String date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtil.parse(date, "yyyy-MM-dd HH:mm:ss"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的结束时间
     *
     * @param date
     * @return
     */
    public static Date getEndTime(String date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtil.parse(date, "yyyy-MM-dd HH:mm:ss"));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        return localDateTime.with(LocalTime.MIN);
    }

    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        return localDateTime.with(LocalTime.MAX);
    }

    public static Date getStartOfDay(Date date) {
        return localDateTime2Date(getStartOfDay(dateToLocalDateTime(date)));
    }

    public static Date getEndOfDay(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return localDateTime2Date(getEndOfDay(dateToLocalDateTime(date)));
    }

    /**
     * 是否跨天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean crossDay(String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return false;
        }
        return startDate.compareTo(endDate) > 0;
    }

    public static OffsetDateTime convertDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        return OffsetDateTime.of(localDateTime, ZoneId.systemDefault().getRules().getOffset(localDateTime));
    }

    /**
     * 日期时间格式化
     *
     * @param date 时间
     * @return 格式化后的时间
     */
    public static String formatDateMillTime(Date date) {
        if (date == null) {
            return null;
        }
        return DATETIME_FORMATTER_M.format(date.toInstant());
    }

    public static Integer getAgeAccordingToYYYY(String year) {
        try {
            year = year.replaceAll("-", "");
            LocalDate today = LocalDate.now();

            if (year.length() == 6) {
                year = year + "01";
            }
            LocalDate player = LocalDate.from(DateTimeFormatter.ofPattern("yyyyMMdd").parse(year));
            Long years = ChronoUnit.YEARS.between(player, today);
            return years.intValue();
        } catch (Exception e) {
            log.warn("计算年龄错误检查人员生日数据:{}", e.getMessage());
        }
        return 0;
    }

    public int compareDateByDayTo(Date d1, Date d2) {
        String dd1 = DateUtil.format(d1, DateUtil.PATTERN_DATE);
        String dd2 = DateUtil.format(d2, DateUtil.PATTERN_DATE);
        Date ddd1 = DateUtil.parse(dd1 + " 00:00:00", DateUtil.PATTERN_DATETIME);
        Date ddd2 = DateUtil.parse(dd2 + " 00:00:00", DateUtil.PATTERN_DATETIME);
        return ddd1.compareTo(ddd2);
    }

    public Date completionDateSuffix(Date date, String suffix) {
        String yyyyDDmm = DateUtil.format(date, DateUtil.PATTERN_DATE);
        yyyyDDmm = yyyyDDmm + " " + suffix;
        return DateUtil.parse(yyyyDDmm, DateUtil.PATTERN_DATETIME);
    }

    public static Map<Integer, List<String>> weeks(String yearMonthStr) {
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, YYYY_MM_FORMATTER);
        LocalDate firstDay = yearMonth.atEndOfMonth().with(yearMonth).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = yearMonth.atEndOfMonth().with(yearMonth).with(TemporalAdjusters.lastDayOfMonth());
        LocalDate monday = yearMonth.atEndOfMonth().with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        Map<Integer, List<String>> weekMap = new TreeMap<>();
        List<String> list = new ArrayList<>();
        int i = 1;
        while (monday.isBefore(lastDay)) {
            LocalDate temp = monday.plusDays(6);
            list = DateUtil.listDays(monday.toString(), temp.toString());
            weekMap.put(i++, list);
            monday = monday.plusWeeks(1);
            if (!temp.isBefore(lastDay)) {
                break;
            }
        }
        return weekMap;
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return 相差天数
     */
    public static int differentDays(Date from, Date to) {
        if (Objects.isNull(from) || Objects.isNull(to)) {
            log.info("日期异常from：{},to:{}", DateUtil.date2String(from), DateUtil.date2String(to));
            return 0;
        }
        return (int) ((to.getTime() - from.getTime()) / (1000 * 3600 * 24));
    }

    /**
     * 时间格式化成整点或者半点
     *
     * @param date
     * @return
     */
    public static LocalDateTime formatToHourOrHalfHour(Date date) {
        LocalDateTime formatDate = DateUtil.parseDateTime(DateUtil.format(date, "yyyy-MM-dd HH:mm:00"), "yyyy-MM-dd HH:mm:ss");
        LocalDateTime resultDate;
        int minute = formatDate.getMinute();
        if (minute <= 15) {
            resultDate = formatDate.withMinute(0);
        } else if (minute <= 45) {
            resultDate = formatDate.withMinute(30);
        } else {
            resultDate = formatDate.plusHours(1).withMinute(0);
        }
        return resultDate;
    }

    public static Date formatToHourOrHalfHourDate(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return DateUtil.localDateTime2Date(formatToHourOrHalfHour(date));
    }

    public static String lastDayOfMonth(String month) {
        LocalDate lastDate = LocalDate.parse(month + "-01");
        return lastDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    }

    public static String firstDayOfMonth(String month) {
        LocalDate firstDate = LocalDate.parse(month + "-01");
        return firstDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public static String lastDayOfMonth(String month, DateTimeFormatter formatter) {
        LocalDate lastDate = LocalDate.parse(month, formatter);
        return lastDate.with(TemporalAdjusters.lastDayOfMonth()).format(formatter);
    }

    public static String firstDayOfMonth(String month, DateTimeFormatter formatter) {
        LocalDate firstDate = LocalDate.parse(month, formatter);
        return firstDate.with(TemporalAdjusters.firstDayOfMonth()).format(formatter);
    }

    public static String day2Month(String day) {
        Date dayDate = DateUtil.day2Date(day);
        return DateUtil.dateToString(dayDate, DateUtil.PATTERN_YYYY_MM);
    }

    public static String date2Month(Date date) {
        if (Objects.isNull(date)) {
            return StringUtils.EMPTY;
        }
        return DateUtil.dateToString(date, DateUtil.PATTERN_YYYY_MM);
    }

    /*public static DateTime parseStr(String value, String pattern) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(pattern)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            // 为避免夏令时,先转换为date,再转换为DateTime
            Date date = sdf.parse(value);
            return new DateTime(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }*/

    /*public static OffsetDateTime fromString(String text, String pattern) {
        return fromJodaTime(parseStr(text, pattern));
    }*/

    /*public static OffsetDateTime fromJodaTime(DateTime dateTime) {
        return dateTime == null ? null : fromLong(dateTime.getMillis());
    }*/

    public static OffsetDateTime fromLong(Long mills) {
        return new Timestamp(mills).toInstant().atZone(systemDefault()).toOffsetDateTime();
    }

    public static String getYesterday(String date) {
        return LocalDate.parse(date).minusDays(1).toString();
    }

    public static boolean isMinusDaysAfter(String startDay, String endDay, int days) {
        return LocalDate.parse(startDay).isAfter(LocalDate.parse(endDay).minusDays(days));
    }

    public static Long betweenDays(Date start, Date end) {
        return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
    }

    public static Long betweenSeconds(Date start, Date end) {
        if (end != null && start != null) {
            return (end.getTime() - start.getTime()) / (1000);
        }
        return 0L;
    }

    public static Long betweenSeconds(String start, String end) {
        return betweenSeconds(string2Date(start), string2Date(end));
    }

    public static boolean isPastDays(Date signInTime, int day) {
        LocalDate nowTime = LocalDate.now();
        Instant instant = signInTime.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate pastTime = instant.atZone(zoneId).toLocalDate();
        return nowTime.minusDays(day).isAfter(pastTime);
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.compareTo(begin) >= 0 && date.compareTo(end) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当月第一天
     *
     * @param day
     * @return
     */
    public static String getMonthFirstDay(String day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DateUtil.parse(day, DateUtil.PATTERN_DATETIME);
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return format.format(c.getTime());
    }

    /**
     * 获取当月第一天
     *
     * @param dateDay
     * @return
     */
    public static String getMonthFirstDayByDateDay(String dateDay) {
        return firstDayOfMonth(day2Month(dateDay));
    }

    /**
     * 获取当月第一天
     *
     * @param dateDay
     * @return
     */
    public static String getMonthLastDayByDateDay(String dateDay) {
        return lastDayOfMonth(day2Month(dateDay));
    }

    /**
     * 获取当前月最后一天
     *
     * @param day
     * @return
     */
    public static String getMonthLastDay(String day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DateUtil.parse(day, DateUtil.PATTERN_DATETIME);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return format.format(c.getTime());
    }

    public static Date getMonthDateLastDay(String dateDay) {
        Date date = DateUtil.parse(dateDay + " 00:00:00", DateUtil.PATTERN_DATETIME);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    public static Date getMonthDateFirstDay(String dateDay) {
        Date date = DateUtil.parse(dateDay + " 00:00:00", DateUtil.PATTERN_DATETIME);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static Date getWeekLastDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getActualMaximum(Calendar.DAY_OF_WEEK));
        c.set(Calendar.HOUR, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    public static Date getWeekFirstDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static String getNextWeekFirstDay(String date) {
        return LocalDate.parse(date).plusWeeks(1).with(DAY_OF_WEEK, 1).toString();
    }

    public static String getNextWeekFirstDay(LocalDate date) {
        return date.plusWeeks(1).with(DAY_OF_WEEK, 1).toString();
    }

    public static String getNextWeekLastDay(String date) {
        return LocalDate.parse(date).plusWeeks(1).with(DAY_OF_WEEK, 7).toString();
    }

    public static String getPreWeekFirstDay(String date) {
        return LocalDate.parse(date).minusWeeks(1).with(DAY_OF_WEEK, 1).toString();
    }

    public static String getPreWeekLastDay(String date) {
        return LocalDate.parse(date).minusWeeks(1).with(DAY_OF_WEEK, 7).toString();
    }

    public static LocalDate getNowLocalDate() {
        return LocalDate.now();
    }

    public static LocalDateTime getNowLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Description: 判断一个时间是否在一个时间段内 </br>
     *
     * @param nowTimeStr   当前时间 </br>
     * @param startTimeStr 开始时间 </br>
     * @param endTimeStr   结束时间 </br>
     */
    public static boolean isInTime(String nowTimeStr, String startTimeStr, String endTimeStr, String pattern) {
        Date nowTime = time2Date(nowTimeStr, pattern);
        Date startTime = time2Date(startTimeStr, pattern);
        Date endTime = time2Date(endTimeStr, pattern);
        return isEffectiveDate(nowTime, startTime, endTime);
    }

    @SneakyThrows
    public static Date time2Date(String timeStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(timeStr);
    }

    public int getWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    public int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static String formatLocalDate(LocalDate parseDate, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(parseDate);
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return false;
        }
        String date1MonthStr = date2Month(date1);
        String date2MonthStr = date2Month(date2);
        if (StringUtils.isEmpty(date1MonthStr) || StringUtils.isEmpty(date2MonthStr)) {
            return false;
        }
        return date1MonthStr.equals(date2MonthStr);
    }

    public static BigDecimal secondToHour(Long second) {
        return secondToHour(second, 1);
    }

    public static BigDecimal secondToHour(Long second, int scale) {
        return BigDecimal.valueOf(second).divide(BigDecimal.valueOf(60 * 60), scale, RoundingMode.HALF_DOWN);
    }

    public static List<LocalDate> days(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return new ArrayList<>();
        }
        LocalDate from = LocalDate.from(date1.toInstant().atZone(systemDefault()));
        long days = ChronoUnit.DAYS.between(from,
                LocalDate.from(date2.toInstant().atZone(ZoneId.systemDefault())));
        if (days < 1) {
            return Lists.newArrayList(from);
        }
        List<LocalDate> result = new ArrayList<>((int) days);
        for (long i = 0; i < days + 1; i++) {
            LocalDate localDate = from.plusDays(i);
            result.add(localDate);
        }
        return result;
    }

    public static List<String> getLatestTwoMonth() {
        String month1 = DateUtil.format(new Date(), DateUtil.PATTERN_YYYY_MM);
        String month2 = DateUtil.format(DateUtil.minusMonths(new Date(), 1), DateUtil.PATTERN_YYYY_MM);
        List<String> twoMonths = Lists.newArrayList();
        twoMonths.add(month1);
        twoMonths.add(month2);
        return twoMonths;
    }

    public static boolean haveIntersection(LocalDateTime startTime1,
                                           LocalDateTime endTime1,
                                           LocalDateTime startTime2,
                                           LocalDateTime endTime2) {
        return startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1);
    }

    public static boolean haveIntersection(Date startTime1,
                                           Date endTime1,
                                           Date startTime2,
                                           Date endTime2) {
        return startTime1.before(endTime2) && startTime2.before(endTime1);
    }

    public String formatDate(Date startTime, Date endTime) {
        return formatDateWithScale(startTime, endTime, 1);
    }

    public static String formatDateWithScale(Date startTime, Date endTime, int scale) {
        long seconds = Duration.between(dateToLocalDateTime(startTime), dateToLocalDateTime(endTime)).getSeconds();
        long day = seconds / (60 * 60 * 24);
        String hour = BigDecimal.valueOf(seconds % (60 * 60 * 24)).divide(BigDecimal.valueOf(60 * 60), scale, RoundingMode.HALF_DOWN).toString();
        return day + "天" + hour + "小时";
    }

    public static String getLastMonth(String month) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = parse(month + "-01 00:00:00", PATTERN_DATETIME);
        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        date = calendar.getTime();
        return format.format(date);
    }

    public static String getOffsetMonth(String month, Integer offset) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = parse(month + "-01 00:00:00", PATTERN_DATETIME);
        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, offset);
        date = calendar.getTime();
        return format.format(date);
    }

    public static boolean dayBetween(String day, String start, String end) {
        return day.compareTo(start) >= 0 && day.compareTo(end) <= 0;
    }

    public static String localDateTime2String(LocalDateTime time, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.format(time);
    }
}
