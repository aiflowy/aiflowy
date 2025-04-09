/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.aiflowy.common.util;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class DateUtil {

    public static String datePatternWithoutDividing = "yyyyMMdd";
    public static String datePattern = "yyyy-MM-dd";
    public static final String dateMinutePattern = "yyyy-MM-dd HH:mm";
    public static final String dateMinutePattern2 = "yyyy-MM-dd'T'HH:mm";
    public static String datetimePattern = "yyyy-MM-dd HH:mm:ss";
    public static final String dateMillisecondPattern = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String dateCSTPattern = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static String dateChinesePattern = "yyyy年MM月dd日";
    public static String datetimeChinesePattern = "yyyy年MM月dd日 HH时mm分ss秒";

    private static final String[] WEEKS = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    private static final ThreadLocal<HashMap<String, SimpleDateFormat>> TL = ThreadLocal.withInitial(() -> new HashMap<>());

    private static final Map<String, DateTimeFormatter> dateTimeFormatters = new ConcurrentHashMap<>();

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        DateTimeFormatter ret = dateTimeFormatters.get(pattern);
        if (ret == null) {
            ret = DateTimeFormatter.ofPattern(pattern);
            dateTimeFormatters.put(pattern, ret);
        }
        return ret;
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat ret = TL.get().get(pattern);
        if (ret == null) {
            if (dateCSTPattern.equals(pattern)) {
                ret = new SimpleDateFormat(dateCSTPattern, Locale.US);
            } else {
                ret = new SimpleDateFormat(pattern);
            }
            TL.get().put(pattern, ret);
        }
        return ret;
    }


    public static String toDateString(Date date) {
        return toString(date, datePattern);
    }


    public static String toDateMinuteString(Date date) {
        return toString(date, dateMinutePattern);
    }

    public static String toDateTimeString(Date date) {
        return toString(date, datetimePattern);
    }


    public static String toDateMillisecondString(Date date) {
        return toString(date, dateMillisecondPattern);
    }


    public static String toString(Date date, String pattern) {
        return date == null ? null : getSimpleDateFormat(pattern).format(date);
    }


    public static String toString(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(getDateTimeFormatter(pattern));
    }

    public static String toString(LocalDate localDate, String pattern) {
        return localDate.format(getDateTimeFormatter(pattern));
    }

    public static String toString(LocalTime localTime, String pattern) {
        return localTime.format(getDateTimeFormatter(pattern));
    }


    public static Date parseDate(Object value) {
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        if (value instanceof Timestamp) {
            return new Date(((Timestamp) value).getTime());
        }
        if (value instanceof LocalDate) {
            return DateUtil.toDate((LocalDate) value);
        }
        if (value instanceof LocalDateTime) {
            return DateUtil.toDate((LocalDateTime) value);
        }
        if (value instanceof LocalTime) {
            return DateUtil.toDate((LocalTime) value);
        }
        String s = value.toString();
        if (StringUtil.isNumeric(s)) {
            return new Date(Long.parseLong(s));
        }
        return DateUtil.parseDate(s);
    }


    public static Date parseDate(String dateString) {
        if (StringUtil.noText(dateString)) {
            return null;
        }
        dateString = dateString.trim();
        try {
            SimpleDateFormat sdf = getSimpleDateFormat(getPattern(dateString));
            try {
                return sdf.parse(dateString);
            } catch (ParseException ex) {
                //2022-10-23 00:00:00.0
                int lastIndexOf = dateString.lastIndexOf(".");
                if (lastIndexOf == 19) {
                    return parseDate(dateString.substring(0, lastIndexOf));
                }

                //2022-10-23 00:00:00,0
                lastIndexOf = dateString.lastIndexOf(",");
                if (lastIndexOf == 19) {
                    return parseDate(dateString.substring(0, lastIndexOf));
                }

                //2022-10-23 00:00:00 000123
                lastIndexOf = dateString.lastIndexOf(" ");
                if (lastIndexOf == 19) {
                    return parseDate(dateString.substring(0, lastIndexOf));
                }

                if (dateString.contains(".") || dateString.contains("/")) {
                    dateString = dateString.replace(".", "-").replace("/", "-");
                    return sdf.parse(dateString);
                } else {
                    throw ex;
                }
            }
        } catch (ParseException ex) {
            throw new IllegalArgumentException("The date format is not supported for the date string: " + dateString);
        }
    }


    private static String getPattern(String dateString) {
        int length = dateString.length();
        if (length == datetimePattern.length()) {
            return datetimePattern;
        } else if (length == datePattern.length()) {
            return datePattern;
        } else if (length == dateMinutePattern.length()) {
            if (dateString.contains("T")) {
                return dateMinutePattern2;
            }
            return dateMinutePattern;
        } else if (length == dateMillisecondPattern.length()) {
            return dateMillisecondPattern;
        } else if (length == datePatternWithoutDividing.length()) {
            return datePatternWithoutDividing;
        } else if (length == dateCSTPattern.length()) {
            return dateCSTPattern;
        } else {
            throw new IllegalArgumentException("The date format is not supported for the date string: " + dateString);
        }
    }


    public static Date parseDate(String dateString, String pattern) {
        if (StringUtil.noText(dateString)) {
            return null;
        }
        try {
            return getSimpleDateFormat(pattern).parse(dateString.trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("The date format is not supported for the date string: " + dateString);
        }
    }


    public static LocalDateTime parseLocalDateTime(String localDateTimeString, String pattern) {
        return LocalDateTime.parse(localDateTimeString, getDateTimeFormatter(pattern));
    }

    public static LocalDate parseLocalDate(String localDateString, String pattern) {
        return LocalDate.parse(localDateString, getDateTimeFormatter(pattern));
    }


    public static LocalTime parseLocalTime(String localTimeString, String pattern) {
        return LocalTime.parse(localTimeString, getDateTimeFormatter(pattern));
    }


    /**
     * java.util.Date --> java.time.LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        // java.sql.Date 不支持 toInstant()，需要先转换成 java.util.Date
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * java.util.Date --> java.time.LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // java.sql.Date 不支持 toInstant()，需要先转换成 java.util.Date
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    /**
     * java.util.Date --> java.time.LocalTime
     */
    public static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        // java.sql.Date 不支持 toInstant()，需要先转换成 java.util.Date
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }

    /**
     * java.time.LocalDateTime --> java.util.Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * java.time.LocalDate --> java.util.Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * java.time.LocalTime --> java.util.Date
     */
    public static Date toDate(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * java.time.LocalTime --> java.util.Date
     */
    public static Date toDate(LocalDate localDate, LocalTime localTime) {
        if (localDate == null) {
            return null;
        }

        if (localTime == null) {
            localTime = LocalTime.of(0, 0, 0);
        }

        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    /**
     * 任意一天的开始时间
     *
     * @return date
     */
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 任意一天的结束时间
     *
     * @return date
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * 获取今天的开始时间
     *
     * @return
     */
    public static Date getStartOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    /**
     * 获取昨天的开始时间
     *
     * @return
     */
    public static Date getStartOfYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getStartOfToday().getTime() - 3600L * 24 * 1000);
        return cal.getTime();
    }


    /**
     * 获取最近 7 天的开始时间
     *
     * @return
     */
    public static Date getStartOfNearest7Days() {
        return getStartOfNearestDays(7);
    }


    /**
     * 获取最近 N 天的开始时间
     *
     * @param days
     * @return
     */
    public static Date getStartOfNearestDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getStartOfToday().getTime() - 3600L * 24 * 1000 * days);
        return cal.getTime();
    }


    /**
     * 获取今天的结束数据
     *
     * @return
     */
    public static Date getEndOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    /**
     * 获取 本周 的开始时间
     *
     * @return
     */
    public static Date getStartOfThisWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取 本周 的结束时间
     *
     * @return
     */
    public static Date getEndOfThisWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(getStartOfThisWeek());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }


    /**
     * 获取 本月 的开始时间
     *
     * @return
     */
    public static Date getStartOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获取 本月 的结束时间
     *
     * @return
     */
    public static Date getEndOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }


    /**
     * 获取上个月的开始时间
     *
     * @return
     */
    public static Date getStartOfLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfThisMonth());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }


    /**
     * 获取 本季度 的开始时间
     *
     * @return
     */
    public static Date getStartOfThisQuarter() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        if (currentMonth <= 3) {
            cal.set(Calendar.MONTH, 0);
        } else if (currentMonth <= 6) {
            cal.set(Calendar.MONTH, 3);
        } else if (currentMonth <= 9) {
            cal.set(Calendar.MONTH, 6);
        } else if (currentMonth <= 12) {
            cal.set(Calendar.MONTH, 9);
        }
        cal.set(Calendar.DATE, 0);

        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }


    /**
     * 获取 本季度的 结束时间
     *
     * @return
     */
    public static Date getEndOfThisQuarter() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfThisQuarter());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }


    /**
     * 获取 季度 的开始时间
     *
     * @param quarterNumber
     * @return
     */
    public static Date getStartOfQuarter(int quarterNumber) {
        if (quarterNumber < 1 || quarterNumber > 4) {
            throw new IllegalArgumentException("quarterNumber must equals 1,2,3,4");
        }
        Calendar cal = Calendar.getInstance();
        if (quarterNumber == 1) {
            cal.set(Calendar.MONTH, 0);
        } else if (quarterNumber == 2) {
            cal.set(Calendar.MONTH, 3);
        } else if (quarterNumber == 3) {
            cal.set(Calendar.MONTH, 6);
        } else {
            cal.set(Calendar.MONTH, 9);
        }

        cal.set(Calendar.DATE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }


    /**
     * 获取 季度 的结束时间
     *
     * @param quarterNumber
     * @return
     */
    public static Date getEndOfQuarter(int quarterNumber) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfQuarter(quarterNumber));
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }


    /**
     * 获取 今年 的开始时间
     *
     * @return
     */
    public static Date getStartOfThisYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return cal.getTime();
    }


    /**
     * 获取 今年 的结束时间
     *
     * @return
     */
    public static Date getEndOfThisYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfThisYear());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }


    /**
     * 获取 去年的 开始时间
     *
     * @return
     */
    public static Date getStartOfLastYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartOfThisYear());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    /**
     * 获取两个时间直接的间隔：单位 秒
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffSecond(Date date1, Date date2) {
        long date1ms = date1.getTime();
        long date2ms = date2.getTime();
        return Math.abs((int) ((date1ms - date2ms) / (1000)));
    }

    /**
     * 获取两个时间直接的间隔：单位 分钟
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffMinute(Date date1, Date date2) {
        long date1ms = date1.getTime();
        long date2ms = date2.getTime();
        return Math.abs((int) ((date1ms - date2ms) / (1000 * 60)));
    }


    /**
     * 获取两个时间直接的间隔：单位 小时
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffHours(Date date1, Date date2) {
        long date1ms = date1.getTime();
        long date2ms = date2.getTime();
        return Math.abs((int) ((date1ms - date2ms) / (1000 * 60 * 60)));
    }

    /**
     * 获取两个时间直接的间隔：单位 天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffDays(Date date1, Date date2) {
        long date1ms = date1.getTime();
        long date2ms = date2.getTime();
        return Math.abs((int) ((date1ms - date2ms) / (1000 * 60 * 60 * 24)));
    }

    /**
     * 获取两个时间直接的间隔：单位 星期
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffWeeks(Date date1, Date date2) {
        long date1ms = date1.getTime();
        long date2ms = date2.getTime();
        return Math.abs((int) ((date1ms - date2ms) / (1000 * 60 * 60 * 24 * 7)));
    }

    /**
     * 获取两个时间直接的间隔：单位 月
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffMonths(Date date1, Date date2) {
        int diffYears = diffYears(date1, date2) * 12;
        int number1 = getMonthNumber(date1);
        int number2 = getMonthNumber(date2);
        return Math.abs(diffYears + number1 - number2);
    }

    /**
     * 获取两个时间直接的间隔：单位 年
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffYears(Date date1, Date date2) {
        int number1 = getYearNumber(date1);
        int number2 = getYearNumber(date2);
        return Math.abs(number1 - number2);
    }


    /**
     * 获取日期的月份
     *
     * @param date
     * @return
     */
    public static int getMonthNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }


    /**
     * 获取日期的季度
     *
     * @param date
     * @return
     */
    public static int getQuarterNumber(Date date) {
        int monthNumber = getMonthNumber(date);
        if (monthNumber >= 1 && monthNumber <= 3) {
            return 1;
        } else if (monthNumber >= 4 && monthNumber <= 6) {
            return 2;
        } else if (monthNumber >= 7 && monthNumber <= 9) {
            return 3;
        } else {
            return 4;
        }
    }


    /**
     * 获取日期的年份
     *
     * @param date
     * @return
     */
    public static int getYearNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }


    /**
     * 获取日期的是当年的第几天
     *
     * @param date
     * @return
     */
    public static int getDayOfYearNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取日期的当月的第几天
     *
     * @param date
     * @return
     */
    public static int getDayOfMonthNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期的当星期的第几天
     *
     * @param date
     * @return
     */
    public static int getDayOfWeekNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * 获取日期的是当年的第个星期
     *
     * @param date
     * @return
     */
    public static int getWeekOfYearNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取日期的当月的第几星期
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonthNumber(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_MONTH);
    }


    /**
     * 取得在指定时间上加减seconds天后的时间
     *
     * @param date    指定的时间
     * @param seconds 秒钟,正为加，负为减
     * @return 在指定时间上加减seconds天后的时间
     */
    public static Date addSeconds(Date date, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }


    /**
     * 取得在指定时间上加减minutes天后的时间
     *
     * @param date    指定的时间
     * @param minutes 分钟,正为加，负为减
     * @return 在指定时间上加减minutes天后的时间
     */
    public static Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    /**
     * 取得在指定时间上加减hours天后的时间
     *
     * @param date  指定的时间
     * @param hours 小时,正为加，负为减
     * @return 在指定时间上加减dhours天后的时间
     */
    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }

    /**
     * 取得在指定时间上加减days天后的时间
     *
     * @param date 指定的时间
     * @param days 天数,正为加，负为减
     * @return 在指定时间上加减days天后的时间
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 取得在指定时间上加减weeks天后的时间
     *
     * @param date  指定的时间
     * @param weeks 星期,正为加，负为减
     * @return 在指定时间上加减weeks天后的时间
     */
    public static Date addWeeks(Date date, int weeks) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, weeks);
        return cal.getTime();
    }

    /**
     * 取得在指定时间上加减months月后的时间
     *
     * @param date   指定时间
     * @param months 月数，正为加，负为减
     * @return 在指定时间上加减months月后的时间
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 取得在指定时间上加减years年后的时间
     *
     * @param date  指定时间
     * @param years 年数，正为加，负为减
     * @return 在指定时间上加减years年后的时间
     */
    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }


    /**
     * 判断 A 的时间是否在 B 的时间 "之后"
     */
    public static boolean isAfter(Date self, Date other) {
        return self != null && other != null && self.getTime() > other.getTime();
    }

    /**
     * 判断 A 的时间是否在 B 的时间 "之后"
     */
    public static boolean isBefore(Date self, Date other) {
        return self != null && other != null && self.getTime() < other.getTime();
    }

    /**
     * 是否是相同的一天
     */
    public static boolean isSameDay(Date self, Date other) {
        return self != null && other != null && getYearNumber(self) == getYearNumber(other)
                && getDayOfYearNumber(self) == getDayOfYearNumber(other);
    }

    /**
     * 是否是相同的星期
     */
    public static boolean isSameWeek(Date self, Date other) {
        return self != null && other != null && getYearNumber(self) == getYearNumber(other)
                && getWeekOfYearNumber(self) == getWeekOfYearNumber(other);
    }

    /**
     * 是否是相同的月份
     */
    public static boolean isSameMonth(Date self, Date other) {
        return self != null && other != null && getYearNumber(self) == getYearNumber(other)
                && getMonthNumber(self) == getMonthNumber(other);
    }

    /**
     * 是否是相同的月份
     */
    public static boolean isSameQuarter(Date self, Date other) {
        return self != null && other != null && getYearNumber(self) == getYearNumber(other)
                && getQuarterNumber(self) == getQuarterNumber(other);
    }

    /**
     * 是否是相同的月份
     */
    public static boolean isSameYear(Date self, Date other) {
        return self != null && other != null && getYearNumber(self) == getYearNumber(other);
    }


    /**
     * 此日期是否是今天
     */
    public static boolean isToday(Date date) {
        return isSameDay(new Date(), date);
    }

    /**
     * 此日期是否是本星期
     */
    public static boolean isThisWeek(Date date) {
        return isSameWeek(new Date(), date);
    }

    /**
     * 此日期是否是本月份
     */
    public static boolean isThisMonth(Date date) {
        return isSameMonth(new Date(), date);
    }


    /**
     * 此日期是否是本月份
     */
    public static boolean isThisQuarter(Date date) {
        return isSameQuarter(new Date(), date);
    }

    /**
     * 此日期是否是本年份
     */
    public static boolean isThisYear(Date date) {
        return date != null && getYearNumber(new Date()) == getYearNumber(date);
    }

    /**
     * 判断是否是润年
     */
    public static boolean isLeapYear(Date date) {
        return date != null && new GregorianCalendar().isLeapYear(getYearNumber(date));
    }

    /**
     * 求出指定的时间那天是星期几
     */
    public static String getWeekDay(Date date) {
        return date == null ? null : DateUtil.WEEKS[getDayOfWeekNumber(date) - 1];
    }


    public static void main(String[] args) {
        System.out.println("两天后的开始时间：" + toDateTimeString(getStartOfDay(addDays(new Date(), 2))));
        System.out.println("两天后的结束时间：" + toDateTimeString(getEndOfDay(addDays(new Date(), 2))));

        System.out.println("CST时间解析：" + toDateTimeString(parseDate("Mon Sep 02 11:23:45 CST 2019")));


        System.out.println("当天24点时间：" + toDateTimeString(getEndOfToday()));
        System.out.println("当前时间：" + toDateTimeString(new Date()));
        System.out.println("当天0点时间：" + toDateTimeString(getStartOfToday()));
        System.out.println("昨天0点时间：" + toDateTimeString(getStartOfYesterday()));
        System.out.println("近7天时间：" + toDateTimeString(getStartOfNearest7Days()));
        System.out.println("本周周一0点时间：" + toDateTimeString(getStartOfThisWeek()));
        System.out.println("本周周日24点时间：" + toDateTimeString(getEndOfThisWeek()));
        System.out.println("本月初0点时间：" + toDateTimeString(getStartOfThisMonth()));
        System.out.println("本月未24点时间：" + toDateTimeString(getEndOfThisMonth()));
        System.out.println("上月初0点时间：" + toDateTimeString(getStartOfLastMonth()));
        System.out.println("本季度开始点时间：" + toDateTimeString(getStartOfThisQuarter()));
        System.out.println("本季度结束点时间：" + toDateTimeString(getEndOfThisQuarter()));
        System.out.println("本年开始点时间：" + toDateTimeString(getStartOfThisYear()));
        System.out.println("本年结束点时间：" + toDateTimeString(getEndOfThisYear()));
        System.out.println("上年开始点时间：" + toDateTimeString(getStartOfLastYear()));
        System.out.println("=============");
        System.out.println("秒间隔：" + diffSecond(parseDate("2020-02-11 12:21:55"), parseDate("2020-02-11 12:22:58")));
        System.out.println("分钟间隔：" + diffMinute(parseDate("2020-02-11 12:21:55"), parseDate("2020-02-11 12:22:01")));
        System.out.println("小时间隔：" + diffHours(parseDate("2020-02-11 12:21:55"), parseDate("2020-02-12 12:22:01")));
        System.out.println("天间隔：" + diffDays(parseDate("2020-02-11 12:21:55"), parseDate("2020-02-12 12:22:01")));
        System.out.println("星期间隔：" + diffWeeks(parseDate("2020-01-11 12:21:55"), parseDate("2020-02-12 12:22:01")));
        System.out.println("月间隔：" + diffMonths(parseDate("2019-10-11 12:21:55"), parseDate("2020-09-11 12:21:55")));
        System.out.println("年间隔：" + diffYears(parseDate("1990-01-11 12:21:55"), parseDate("2020-02-12 12:22:01")));
        System.out.println("当前年份：" + getYearNumber(new Date()));
        System.out.println("当前月份：" + getMonthNumber(new Date()));
        System.out.println("=============");

        System.out.println("新增秒：" + toDateTimeString(addSeconds(parseDate("2020-02-11 12:21:55"), 20)));
        System.out.println("新增分钟：" + toDateTimeString(addMinutes(parseDate("2020-02-11 12:21:55"), 20)));
        System.out.println("新增小时：" + toDateTimeString(addHours(parseDate("2020-02-11 12:21:55"), 20)));
        System.out.println("新增天：" + toDateTimeString(addDays(parseDate("2020-02-11 12:21:55"), 20)));
        System.out.println("新增星期：" + toDateTimeString(addWeeks(parseDate("2020-02-11 12:21:55"), 10)));
        System.out.println("新增月份：" + toDateTimeString(addMonths(parseDate("2020-02-11 12:21:55"), 20)));
        System.out.println("新增年份：" + toDateTimeString(addYears(parseDate("2020-02-11 12:21:55"), 20)));

        System.out.println("=============");
        System.out.println("今天星期：" + getWeekDay(parseDate("2020-11-24")));
        System.out.println("isToday：" + isToday(parseDate("2020-12-01")));
        System.out.println("isThisWeek：" + isThisWeek(parseDate("2020-11-24")));
        System.out.println("isThisMonth：" + isThisMonth(parseDate("2020-10-02")));
        System.out.println("isThisQuarter：" + isThisQuarter(parseDate("2020-10-02")));
        System.out.println("isThisYear：" + isThisYear(parseDate("2020-02-02")));
        System.out.println("第1季度：" + toDateTimeString(getEndOfQuarter(1)));
        System.out.println("第2季度：" + toDateTimeString(getEndOfQuarter(2)));
        System.out.println("第3季度：" + toDateTimeString(getEndOfQuarter(3)));
        System.out.println("第4季度：" + toDateTimeString(getEndOfQuarter(4)));
        System.out.println("本季度：" + toDateTimeString(getStartOfThisQuarter()));
        System.out.println("本季度：" + toDateTimeString(getEndOfThisQuarter()));


        System.out.println("datetime-local解析：" + parseDate("2022-12-03T16:00"));

    }
}
