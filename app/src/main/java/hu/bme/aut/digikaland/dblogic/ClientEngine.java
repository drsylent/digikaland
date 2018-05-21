package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.enumeration.LoadResult;
import hu.bme.aut.digikaland.dblogic.enumeration.RaceState;
import hu.bme.aut.digikaland.entities.Location;

/**
 * Created by Sylent on 2018. 04. 06..
 */

public class ClientEngine {
    private static final ClientEngine ourInstance = new ClientEngine();

    static ClientEngine getInstance(){
        return ourInstance;
    }

    public static ClientEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private CommunicationInterface comm = null;
    private ClientEngine() {
    }

    private LoadResult loadResult = null;

    public LoadResult getLoadResult() {
        return loadResult;
    }

    public void loadTeamName(){
        if(teamName == null) downloadTeamName();
        else comm.teamNameLoaded();
    }

    public void loadCompletedStations(){
        if(completedStations == -1) downloadCompletedStations();
        else comm.completedStationsLoaded();
    }

    private void downloadCompletedStations(){
        completedStations = 0;
        RacePermissionHandler.getInstance().getTeamReference().collection("stations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.contains("done") && document.getBoolean("done")) completedStations++;
                                }
                                comm.completedStationsLoaded();
                            }catch (RuntimeException e){
                                comm.clientError(ErrorType.DatabaseError);
                            }
                        } else {
                            comm.clientError(ErrorType.NoContact);
                        }
                    }
                });
    }

    private void resetData(){
        stationNumber = -1;
        completedStations = -1;
        stationSum = -1;
        raceName = null;
        teamName = null;
        location = null;
        startingTime = null;
        endingTime = null;
        stationId = null;
        geoPoint = null;
    }

    public void loadState(){
        resetData();
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("races").document(CodeHandler.getInstance().getRaceCode());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        stationSum = document.getLong("stationnumbers").intValue();
                        raceName = document.getString("racename");
                        try{
                            switch (RaceState.valueOf(document.getString("status"))){
                                case NotStarted: loadStartingState(); break;
                                case Started: loadRunningState(); break;
                                case Ended: endingStateLoaded(); break;
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
                            startingStateLoaded();
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
                            runningStateLoaded();
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

    private void downloadTeamName(){
        final DocumentReference teamStationRef = RacePermissionHandler.getInstance().getTeamReference();
        teamStationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            teamName = document.getString("name");
                            comm.teamNameLoaded();
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
                            distanceActivatable = document.contains("distance");
                            if(distanceActivatable) activationDistance = document.getDouble("distance");
                            nfcCode = document.getString("nfccode");
                            if(onStation) stationStateLoaded();
                            else runningStateLoaded();
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

    private void stationStateLoaded(){
        loadResult = LoadResult.Station;
        comm.stationStateLoaded();
    }

    private void endingStateLoaded(){
        loadResult = LoadResult.Ending;
        comm.endingStateLoaded();
    }

    private void startingStateLoaded(){
        loadResult = LoadResult.Starting;
        comm.startingStateLoaded();
    }

    private void runningStateLoaded(){
        loadResult = LoadResult.Running;
        comm.runningStateLoaded();
    }

    public int getLastLoadedStationNumber() {
        return stationNumber;
    }

    public int getStationSum() {
        return stationSum;
    }

    public int getStationNumber() {
        return stationNumber;
    }

    private int stationNumber = -1;

    private int completedStations = -1;

    public int getCompletedStations() {
        return completedStations;
    }

    private int stationSum = -1;

    private String raceName = null;

    public String getRaceName() {
        return raceName;
    }

    public String getTeamName() {
        return teamName;
    }

    private String teamName = null;

    private Location location = null;

    private Date startingTime = null;
    private Date endingTime = null;

    public String getLastLoadedStationId() {
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

    public boolean isDistanceActivatable() {
        return distanceActivatable;
    }

    public boolean isNfcActivatable(){
        return nfcCode != null;
    }

    public double getActivationDistance() {
        return activationDistance;
    }

    public String getNfcCode() {
        return nfcCode;
    }

    private boolean distanceActivatable = false;

    private double activationDistance = -1;

    private String nfcCode = null;

    @ServerTimestamp
    private Date serverTime = new Date();

    public void startStation(){
        final DocumentReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId);
        stationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            uploadStationStart(document.getLong("time"));
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

    private void uploadStationStart(long secondsLimit){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(serverTime);
        calendar.add(Calendar.SECOND, Long.valueOf(secondsLimit).intValue());
        RacePermissionHandler.getInstance().getTeamReference().collection("stations").document(Integer.toString(stationNumber))
                .update("timeend", calendar.getTime())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.stationStarted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.clientError(ErrorType.DatabaseError);
                    }
                });
    }

    public interface CommunicationInterface{
        void clientError(ErrorType type);
        void startingStateLoaded();
        void runningStateLoaded();
        void stationStateLoaded();
        void endingStateLoaded();
        void teamNameLoaded();
        void completedStationsLoaded();
        void stationStarted();
    }
}
