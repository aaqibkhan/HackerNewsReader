package aaqib.taskaaqib.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import aaqib.taskaaqib.R;

/**
 * Helper utility containing miscellaneous functions
 */
public class AppUtil {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a", Locale.ENGLISH);
    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    /**
     * Reads inputStream into a String
     *
     * @param stream The inputStream to read
     * @return String from the inputStream
     */
    public static String readStream(InputStream stream) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Gets meaningful string for the given time
     * compared with current time
     *
     * @param context     Activity or Application Context
     * @param currentTime The current time in milliseconds
     * @param time        The time to compare in milliseconds
     * @return Difference in time or the full formatted date-time if difference is more than a week
     */
    public static String getItemTime(Context context, long currentTime, long time) {
        long diff = currentTime - time;
        if (diff < 0) {
            return sdf.format(new Date(time));
        } else if (diff == 0) {
            return context.getString(R.string.time_just_now);
        } else {
            int temp;
            if (diff > ONE_WEEK) {
                return sdf.format(new Date(time));
            } else if (diff > ONE_DAY) {
                temp = (int) (diff / ONE_DAY);
                return context.getResources().getQuantityString(R.plurals.time_days, temp, temp);
            } else if (diff > ONE_HOUR) {
                temp = (int) (diff / ONE_HOUR);
                return context.getResources().getQuantityString(R.plurals.time_hours, temp, temp);
            } else if (diff > ONE_MINUTE) {
                temp = (int) (diff / ONE_MINUTE);
                return context.getResources().getQuantityString(R.plurals.time_minutes, temp, temp);
            } else {
                temp = (int) (diff / ONE_SECOND);
                return context.getResources().getQuantityString(R.plurals.time_seconds, temp, temp);
            }
        }
    }

    /**
     * Gets the item ID from given URL
     *
     * @param url The HackerNews API url
     * @return Item ID found in the url else -1
     */
    public static long getItemIDFromURL(String url) {
        long itemID = -1;
        if (url != null && url.trim().length() > 0) {
            int startIndex = url.lastIndexOf("/");
            int endIndex = url.lastIndexOf(".json");
            if (startIndex != -1 && endIndex != -1) {
                String itemIDStr = url.substring(startIndex + 1, endIndex);
                itemID = Long.parseLong(itemIDStr);
            }
        }
        return itemID;
    }

}
