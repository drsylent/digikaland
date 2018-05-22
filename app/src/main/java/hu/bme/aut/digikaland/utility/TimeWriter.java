package hu.bme.aut.digikaland.utility;

/**
 * Egy idő kiíró segédosztály.
 */
public class TimeWriter {
    /**
     * Egy olyan stringet ad vissza, melynek a formázása megfelelő a visszaszámláláshoz.
     * @param inputSeconds A visszaszámlálásból hátralévő idő másodpercekben.
     * @return A megformázott string.
     */
    public static String countdownFormat(long inputSeconds){
        long hours = inputSeconds/3600;
        long minutes = (inputSeconds / 60) % 60;
        long seconds = inputSeconds % 60;
        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }
}
