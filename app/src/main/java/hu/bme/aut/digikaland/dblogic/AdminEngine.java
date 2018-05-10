package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import hu.bme.aut.digikaland.dblogic.enumeration.LoadResult;
import hu.bme.aut.digikaland.dblogic.enumeration.RaceState;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

/**
 * Created by Sylent on 2018. 05. 10..
 */

public class AdminEngine {
    private static final AdminEngine ourInstance = new AdminEngine();

    public static AdminEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private CommunicationInterface comm = null;

    private AdminEngine() {
    }

    private LoadResult loadResult = null;

    public LoadResult getLoadResult() {
        return loadResult;
    }

    public void loadState(){
        resetData();
        final DocumentReference docRef = RacePermissionHandler.getInstance().getRaceReference();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        stationSum = document.getLong("stationnumbers").intValue();
                        teamSum = document.getLong("teamnumbers").intValue();
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
        final DocumentReference stationRef = RacePermissionHandler.getInstance().getStationReference();
        stationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            location = new Location(document.getString("address"), document.getString("address-detailed"));
                            //geoPoint = document.getGeoPoint("geodata");
                            loadStationState();
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

    private void loadStationState(){
        final CollectionReference stationRef = RacePermissionHandler.getInstance().getStationReference().collection("teams");
        stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        done = 0;
                        evaluated = 0;
                        teamId = null;
                        boolean firstNotArrived = true;
                        DocumentReference docRef = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EvaluationStatus status = EvaluationStatus.valueOf(document.getString("status"));
                            switch (status){
                                case Done: done++; break;
                                case Evaluated: done++; evaluated++; break;
                                case NotArrivedYet:
                                    if(firstNotArrived){
                                        docRef = document.getDocumentReference("team");
                                        teamId = docRef.getId();
                                        firstNotArrived = false;
                                    }
                                    break;
                            }
                        }
                        if(firstNotArrived) runningStateLoaded();
                        else loadNextTeamInfo(docRef);
                    } catch (RuntimeException e){
                        comm.clientError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.clientError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadNextTeamInfo(DocumentReference docRef){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            teamName = document.getString("name");
                            loadTeamContact(document.getDocumentReference("captain"));
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

    private void loadTeamContact(DocumentReference docRef){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            captainContact = document.toObject(Contact.class);
                            runningStateLoaded();
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
//        comm.stationStateLoaded();
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

    private void resetData(){
        stationNumber = -1;
        completedStations = -1;
        stationSum = -1;
        raceName = null;
        teamName = null;
        captainContact = null;
        done = -1;
        evaluated = -1;
        location = null;
        startingTime = null;
        endingTime = null;
        stationId = null;
        geoPoint = null;
    }

    private int evaluated = -1;

    public int getEvaluated() {
        return evaluated;
    }

    public int getDone() {
        return done;
    }

    private int done = -1;

    private String teamId = null;

    private Contact captainContact = null;

    public Contact getNextTeamContact() {
        return captainContact;
    }

    public String getNextTeamId() {
        return teamId;
    }

    public int getLastLoadedStationNumber() {
        return stationNumber;
    }

    public int getStationSum() {
        return stationSum;
    }

    private int stationNumber = -1;

    private int completedStations = -1;

    public int getCompletedStations() {
        return completedStations;
    }

    private int stationSum = -1;

    private int teamSum = -1;

    public int getTeamSum() {
        return teamSum;
    }

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

    private String stationId = null;

    public String getMyStationId(){ return RacePermissionHandler.getInstance().getStationReference().getId(); }

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
//        void stationStateLoaded();
        void endingStateLoaded();
//        void teamNameLoaded();
//        void completedStationsLoaded();
    }
}
