package com.m3u8.download.video.m3u8.utils;

/**
 * @author Small_Tsk
 * @create 2023-06-14
 **/
public class TimeUtils {
    public static String convertSecondsToTime(long seconds) {
        long days = seconds / (24 * 60 * 60);
        seconds %= (24 * 60 * 60);
        long hours = seconds / (60 * 60);
        seconds %= (60 * 60);
        long minutes = seconds / 60;
        seconds %= 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        if (days > 0) {
            timeString = String.format("%d:%s", days, timeString);
        }

        return timeString;
    }
}
