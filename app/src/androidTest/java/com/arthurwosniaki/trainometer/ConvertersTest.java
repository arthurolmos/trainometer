package com.arthurwosniaki.trainometer;

import android.support.test.runner.AndroidJUnit4;

import com.arthurwosniaki.trainometer.utils.Converters;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class ConvertersTest {

    @Test
    public void convertLocalDate(){
        LocalDate localDate = LocalDate.now();
        System.out.print(localDate);

        long toLong = Converters.fromLocalDate(localDate);
        System.out.print(toLong);

        LocalDate fromLong = Converters.toLocalDate(toLong);
        System.out.print(fromLong);
    }

    @Test
    public void convertLocalDateTime(){
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.print(localDateTime);

        long toLong = Converters.fromLocalDateTime(localDateTime);
        System.out.print(toLong);

        LocalDateTime fromLong = Converters.toLocalDateTime(toLong);
        System.out.print(fromLong);
    }
}
