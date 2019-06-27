package com.arthurwosniaki.trainometer.utils;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Converters {
    private static String TAG = Converters.class.getSimpleName();

    @TypeConverter
    public static Long fromLocalDate(LocalDate date) {
        if (date==null) {
            return(null);
        }

        Log.d(TAG, "Date = " + date);
        ZoneId zoneId = ZoneId.systemDefault();

        long longDate = date.atStartOfDay(zoneId).toEpochSecond() * 1000;
        Log.d(TAG, "Long = " + longDate);

        return(longDate);
    }

    @TypeConverter
    public static LocalDate toLocalDate(Long millisSinceEpoch) {
        if (millisSinceEpoch==null) {
            return(null);
        }

        Log.d(TAG, "Long = " + millisSinceEpoch);

        LocalDate date = Instant.ofEpochMilli(millisSinceEpoch).atZone(ZoneId.systemDefault()).toLocalDate();
        Log.d(TAG, "Date = " + date);

        return date;
    }

    @TypeConverter
    public static Long fromLocalDateTime(LocalDateTime date) {
        if (date==null) {
            return(null);
        }

        Log.d(TAG, "Date = " + date);
        ZoneId zoneId = ZoneId.systemDefault();

        long longDate = date.toInstant(ZoneOffset.UTC).toEpochMilli();
        Log.d(TAG, "Long = " + longDate);

        return(longDate);
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(Long millisSinceEpoch) {
        if (millisSinceEpoch==null) {
            return(null);
        }

        Log.d(TAG, "Long = " + millisSinceEpoch);

        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millisSinceEpoch), ZoneOffset.UTC);
        Log.d(TAG, "Date = " + date);

        return date;
    }

    public static String longToString(Long l){
        return Long.toString(l);
    }

    public static String longTimeToString(Long l){
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC-3"));

        return formatter.format(new Time(l));
    }

    public static long stringTimeToLong(String s){
        try {
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC-3"));
            Time timeValue = new Time(formatter.parse(s).getTime());

            return timeValue.getTime();

        } catch (ParseException e) {
            e.printStackTrace();

            return 0;
        }
    }

    public static String calendarToString(Calendar c){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        return sdf.format(c.getTime());
    }

    public static LocalDate stringToLocalDate(String s){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return LocalDate.parse(s, formatter);
    }

    public static String localDateToString(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return formatter.format(date);
    }

    public static float roundDecimals(float number){
        return Math.round(number * 10f) / 10f;
    }

}
