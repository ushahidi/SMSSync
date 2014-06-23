package org.addhen.smssync.util;

/**
 * Created by Kamil Kalfas(kkalfas@soldevelo.com) on 20.05.14.
 */
public final class TimeFrequencyUtil {

    public static final String DEFAULT_TIME_FREQUENCY = "05:00";

    private static final int ONE_HOUR = 3600000;
    private static final int ONE_MINUTE = 60000;

    private TimeFrequencyUtil() {
    }

    public static long calculateInterval(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]) * ONE_HOUR +
                Integer.parseInt(pieces[1]) * ONE_MINUTE;
    }


}
