package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.enumeration.LoadResult;
import hu.bme.aut.digikaland.dblogic.enumeration.RaceState;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

/**
 * Egy singleton szolgáltatás, melyen keresztül a teljes adminok kapcsolódhatnak az adatbázishoz.
 */
public class AdminStationEngine {
    private static final AdminStationEngine ourInstance = new AdminStationEngine();

    public static AdminStationEngine getInstance(AdminStationCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private AdminStationCommunicationInterface comm = null;

    private AdminStationEngine() {
    }

    private LoadResult loadResult = null;

    public LoadResult getLoadResult() {
        return loadResult;
    }

    /**
     * Betölt minden szükséges adatot ahhoz, hogy a teljes admin főképernyője működjön.
     */
    public void loadState(){
        resetData();
        final DocumentReference docRef = RaceRoleHandler.getRaceReference();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        teamSum = document.getLong("teamnumbers").intValue();
                        try{
                            switch (RaceState.valueOf(document.getString("status"))){
                                case NotStarted: loadStartingState(); break;
                                case Started: loadRunningState(); break;
                                case Ended: endingStateLoaded(); break;
                            }
                        }catch (IllegalArgumentException e){
                            comm.adminError(ErrorType.DatabaseError);
                        }catch (RuntimeException e){
                            comm.adminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.adminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.adminError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadStartingState(){
        final DocumentReference docRef = RaceRoleHandler.getRaceReference();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            location = new Location(document.getString("startingaddr"), document.getString("startingaddr-detailed"));
                            startingTime = document.getDate("startingtime");
                            startingStateLoaded();
                        }catch(RuntimeException e){
                            comm.adminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.adminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.adminError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadRunningState(){
        final DocumentReference stationRef = RaceRoleHandler.getStationReference();
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
                            comm.adminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.adminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.adminError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadStationState(){
        final CollectionReference stationRef = RaceRoleHandler.getStationReference().collection("teams");
        stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        done = 0;
                        evaluated = 0;
                        boolean firstNotArrived = true;
                        boolean firstDone = true;
                        DocumentReference notArrivedTeam = null;
                        DocumentReference doneTeam = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EvaluationStatus status = EvaluationStatus.valueOf(document.getString("status"));
                            switch (status){
                                case Done: done++;
                                    if(firstDone){
                                        doneTeam = document.getDocumentReference("reference");
                                        nextEvaluateTeamId = doneTeam.getId();
                                        firstDone = false;
                                    }
                                break;
                                case Evaluated: done++; evaluated++; break;
                                case NotArrivedYet:
                                    if(firstNotArrived){
                                        notArrivedTeam = document.getDocumentReference("reference");
                                        firstNotArrived = false;
                                    }
                                    break;
                            }
                        }
                        if(firstNotArrived) teamNameLoaded();
                        else loadNextTeamInfo(notArrivedTeam, true);
                        if(firstDone) teamNameLoaded();
                        else loadNextTeamInfo(doneTeam, false);
                    } catch (RuntimeException e){
                        comm.adminError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.adminError(ErrorType.NoContact);
                }
            }
        });
    }

    private int teamNames = 0;
    private void teamNameLoaded(){
        if(++teamNames == 2) {
            teamNames = 0;
            runningStateLoaded();
        }
    }

    private void loadNextTeamInfo(final DocumentReference docRef, final boolean contactNeeded){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            if(contactNeeded) {
                                arrivingTeamName = document.getString("name");
                                loadTeamContact(document.getDocumentReference("captain"));
                            }
                            else {
                                teamNameLoaded();
                            }
                        } catch (RuntimeException e){
                            comm.adminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.adminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.adminError(ErrorType.NoContact);
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
                            teamNameLoaded();
                        } catch (RuntimeException e){
                            comm.adminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.adminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.adminError(ErrorType.NoContact);
                }
            }
        });
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
        arrivingTeamName = null;
        captainContact = null;
        done = -1;
        evaluated = -1;
        location = null;
        startingTime = null;
    }

    private int evaluated = -1;

    public int getEvaluated() {
        return evaluated;
    }

    public int getDone() {
        return done;
    }

    private int done = -1;

    public String getNextEvaluateTeamId() {
        return nextEvaluateTeamId;
    }

    private String nextEvaluateTeamId = null;

    private Contact captainContact = null;

    public Contact getNextTeamContact() {
        return captainContact;
    }

    private int teamSum = -1;

    public int getTeamSum() {
        return teamSum;
    }

    public String getArrivingTeamName() {
        return arrivingTeamName;
    }

    private String arrivingTeamName = null;

    private Location location = null;

    private Date startingTime = null;

    public String getMyStationId(){ return RaceRoleHandler.getStationReference().getId(); }

    public Location getLastLoadedLocation(){
        return location;
    }

    public Date getLastLoadedStartingTime() {
        return startingTime;
    }

    public interface AdminStationCommunicationInterface {
        void adminError(ErrorType type);
        void startingStateLoaded();
        void runningStateLoaded();
        void endingStateLoaded();
    }
}
