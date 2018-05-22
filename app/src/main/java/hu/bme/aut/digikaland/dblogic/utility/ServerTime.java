package hu.bme.aut.digikaland.dblogic.utility;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Ezen az osztályon keresztül mindig friss szerveridőt lehet lekérni.
 */
public class ServerTime {

    /**
     * A szerveridőt ezzel lehet lekérni.
     * @return A Firebase szerver ideje.
     */
    public static Date getTime(){
        return instance.instanceTime();
    }

    private static ServerTime instance = new ServerTime();

    private Date instanceTime(){
        return new TimeCreator().time;
    }

    // minden lekérdezésnél létrehozunk egy újat, mert ha az engine-k tárolnák,
    // akkor csak egyszer kerül beállításra
    private class TimeCreator{
        @ServerTimestamp
        private Date time = new Date();
    }
}
