package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

import hu.bme.aut.digikaland.entities.Location;

/**
 * Created by Sylent on 2018. 04. 06..
 */

public class ClientEngine {
    private static final ClientEngine ourInstance = new ClientEngine();

    public static ClientEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private CommunicationInterface comm = null;
    private ClientEngine() {
    }

    public void loadState(){
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("races").document(CodeHandler.getInstance().getRaceCode());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        stationSum = document.getLong("stationnumbers").intValue();
                        try{
                            switch (RaceState.valueOf(document.getString("status"))){
                                case NotStarted: loadStartingState(); break;
                                case Started: loadRunningState(); break;
                                case Ended: loadEndingState(); break;
                            }
                        }catch (IllegalArgumentException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }catch (RuntimeException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.clientError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadStartingState(){
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("races").document(CodeHandler.getInstance().getRaceCode());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            location = new Location(document.getString("startingaddr"), document.getString("startingaddr-detailed"));
                            startingTime = document.getDate("startingtime");
                            geoPoint = document.getGeoPoint("startinggeo");
                            comm.startingStateLoaded();
                        }catch(RuntimeException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.clientError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadRunningState(){
        final DocumentReference teamRef = RacePermissionHandler.getInstance().getTeamReference();
        teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            stationNumber = document.getLong("stationnumber").intValue();
                            if(stationNumber <= stationSum)loadTeamStation(stationNumber);
                            else loadEndingLocation();
                        } catch (RuntimeException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.clientError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadEndingLocation(){
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("races").document(CodeHandler.getInstance().getRaceCode());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            location = new Location(document.getString("endingaddr"), document.getString("endingaddr-detailed"));
                            startingTime = document.getDate("endingtime");
                            geoPoint = document.getGeoPoint("endinggeo");
                            comm.runningStateLoaded();
                        }catch(RuntimeException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.clientError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadTeamStation(int number){
        final DocumentReference teamStationRef = RacePermissionHandler.getInstance().getTeamReference().collection("stations").document(Integer.toString(number));
        teamStationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            startingTime = document.getDate("timestart");
                            DocumentReference stationRef = document.getDocumentReference("station");
                            stationId = stationRef.getId();
                            if(document.contains("timeend") && !document.getBoolean("done")){
                                endingTime = document.getDate("timeend");
                                loadStation(stationRef, true);
                            }
                            else{
                                loadStation(stationRef, false);
                            }
                        } catch (RuntimeException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.clientError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadStation(DocumentReference ref, final boolean onStation){
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            location = new Location(document.getString("address"), document.getString("address-detailed"));
                            geoPoint = document.getGeoPoint("geodata");
                            if(onStation) comm.stationStateLoaded();
                            else comm.runningStateLoaded();
                        } catch (RuntimeException e){
                            comm.clientError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.clientError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadEndingState(){
        comm.endingStateLoaded();
    }

    public int getLastLoadedStationNumber() {
        return stationNumber;
    }

    public int getStationSum() {
        return stationSum;
    }

    private int stationNumber = -1;

    private int stationSum = -1;

    private Location location = null;

    private Date startingTime = null;
    private Date endingTime = null;

    public String getStationId() {
        return stationId;
    }

    public String getTeamId(){
        return RacePermissionHandler.getInstance().getTeamReference().getId();
    }

    private String stationId = null;

    private GeoPoint geoPoint = null;

    public Location getLastLoadedLocation(){
        return location;
    }

    public Date getLastLoadedStartingTime() {
        return startingTime;
    }

    public Date getLastLoadedEndingTime() {
        return endingTime;
    }

    public GeoPoint getLastLoadedGeoPoint(){
        return geoPoint;
    }

    public interface CommunicationInterface{
        void clientError(ErrorType type);
        void startingStateLoaded();
        void runningStateLoaded();
        void stationStateLoaded();
        void endingStateLoaded();
    }
}
