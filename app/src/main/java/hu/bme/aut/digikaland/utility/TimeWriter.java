package hu.bme.aut.digikaland.utility;

import java.util.Date;
import java.util.Formatter;

/**
 * Created by Sylent on 2018. 02. 28..
 */

public class TimeWriter {
    public static String countdownFormat(long inputSeconds){
        long hours = inputSeconds/3600;
        long minutes = (inputSeconds / 60) % 60;
        long seconds = inputSeconds % 60;
        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }

    public static String dateFormat(Date date){
        return String.format("%1$tY.%1$tm.%1$td. %1$tH:%1$tM", date);
    }
}
