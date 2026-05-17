package src;

import java.util.concurrent.TimeUnit;

public class Utility {
    public static String ConvertMillis(long duration){
        String result;
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            result = (hours + "." + minutes + "." + seconds);
        } else {
            result = (days + "." + hours + "." + minutes + "." + seconds);
        }
        return result;
    }
}
