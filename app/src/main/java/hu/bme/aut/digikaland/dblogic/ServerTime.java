package hu.bme.aut.digikaland.dblogic;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ServerTime {
    public static Date getTime(){
        return instance.instanceTime();
    }

    private static ServerTime instance = new ServerTime();

    private Date instanceTime(){
        return new TimeCreator().time;
    }

    private class TimeCreator{
        @ServerTimestamp
        private Date time = new Date();
    }
}
