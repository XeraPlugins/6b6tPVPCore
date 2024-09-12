package me.ian.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author SevJ6
 */
public class Utils {

    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(formatter);
    }

}
