package me.eddiep.system;

public class TimeUtils {

    public static String toFriendlyTime(long duration) {
        int minutes = (int) ((duration / 1000) / 60);
        int seconds = (int) ((duration / 1000) % 60);

        return minutes + " minute" + (minutes != 1 ? "s" : "") + " and " + seconds + " second" + (seconds != 1 ? "s" : "") + "!";
    }
}
