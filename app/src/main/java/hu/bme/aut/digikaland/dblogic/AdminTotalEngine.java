package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.enumeration.LoadResult;
import hu.bme.aut.digikaland.dblogic.enumeration.RaceState;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;

/**
 * A teljes adminoknak hozzáférést biztosít ez a szolgáltatás az adatbázishoz.
 */
public class AdminTotalEngine {
    private static final AdminTotalEngine ourInstance = new AdminTotalEngine();

    public static AdminTotalEngine getInstance(AdminTotalCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private AdminTotalCommunicationInterface comm;

    private AdminTotalEngine() {
    }

    /**
     * A teljes adminok fő nézetének megjelenítéséhez szükséges adatok betöltése.
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
                        stationSum = document.getLong("stationnumbers").intValue();
                        teamSum = document.getLong("teamnumbers").intValue();
                        try{
                            switch (RaceState.valueOf(document.getString("status"))){
                                case NotStarted: loadStartingState(); break;
                                case Started: loadRunningState(); break;
                                case Ended: endingStateLoaded(); break;
                            }
                        }catch (IllegalArgumentException e){
                            comm.totalAdminError(ErrorType.DatabaseError);
                        }catch (RuntimeException e){
                            comm.totalAdminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.totalAdminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.totalAdminError(ErrorType.NoContact);
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
                            location = new Location(document.getString("startingaddr"),
                                    document.getString("startingaddr-detailed"));
                            time = document.getDate("startingtime");
                            startingStateLoaded();
                        }catch(RuntimeException e){
                            comm.totalAdminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.totalAdminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.totalAdminError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadRunningState(){
        final DocumentReference docRef = RaceRoleHandler.getRaceReference();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            location = new Location(document.getString("endingaddr"),
                                    document.getString("endingaddr-detailed"));
                            time = document.getDate("endingtime");
                            new StatisticsLoader().loadStatistics();
                        }catch(RuntimeException e){
                            comm.totalAdminError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.totalAdminError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.totalAdminError(ErrorType.NoContact);
                }
            }
        });
    }

    private class StatisticsLoader{
        private int done = 0;
        private int evaluated = 0;
        private int counter = 0;
        private boolean errorFree = true;

        private void loadStatistics(){
            final CollectionReference stationRef = RaceRoleHandler
                    .getRaceReference().collection("stations");
            stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        try {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                loadStation(document.getReference());
                            }
                        } catch (RuntimeException e){
                            error(ErrorType.DatabaseError);
                        }
                    } else {
                        error(ErrorType.NoContact);
                    }
                }
            });
        }

        private void loadStation(DocumentReference stationRef){
            CollectionReference teamsRef = stationRef.collection("teams");
            teamsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        try {
                            boolean evaluatedAll = true;
                            boolean doneAll = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                EvaluationStatus status =
                                        EvaluationStatus.valueOf(document.getString("status"));
                                switch (status){
                                    case Done: evaluatedAll = false; break;
                                    case NotArrivedYet: evaluatedAll = false; doneAll = false; break;
                                }
                            }
                            if(doneAll){
                                if(evaluatedAll) stationLoaded(EvaluationStatus.Evaluated);
                                else stationLoaded(EvaluationStatus.Done);
                            }
                            else stationLoaded(EvaluationStatus.NotArrivedYet);
                        } catch (RuntimeException e){
                            error(ErrorType.DatabaseError);
                        }
                    } else {
                        error(ErrorType.NoContact);
                    }
                }
            });
        }

        private void error(ErrorType type){
            if(errorFree) comm.totalAdminError(type);
            else errorFree = false;
        }

        private void stationLoaded(EvaluationStatus status){
            if(errorFree) {
                if (status == EvaluationStatus.Evaluated){ evaluated++; done++; }
                else if (status == EvaluationStatus.Done) done++;
                if (++counter == stationSum) {
                    statistics = new EvaluationStatistics(evaluated, done, stationSum);
                    runningStateLoaded();
                }
            }
        }
    }

    /**
     * A verseny állapotát frissíti a megadott állapotra.
     * @param state A verseny új állapota.
     */
    public void updateRaceStatus(RaceState state){
        RaceRoleHandler.getRaceReference()
                .update("status", state.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.statusUpdateSuccessful();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.totalAdminError(ErrorType.UploadError);
                    }
                });
    }

    /**
     * Betölti a csapatok listáját.
     */
    public void loadTeamList(){
        CollectionReference teamsRef = RaceRoleHandler
                .getRaceReference().collection("teams");
        teamsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        ArrayList<Team> teams = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Team team = new Team(document.getString("name"),
                                    EvaluationStatus.NotArrivedYet);
                            team.id = document.getId();
                            teams.add(team);
                        }
                        comm.teamListLoaded(teams);
                    } catch (RuntimeException e){
                        comm.totalAdminError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.totalAdminError(ErrorType.NoContact);
                }
            }
        });
    }

    public LoadResult getLoadResult() {
        return loadResult;
    }

    public Location getLastLoadedLocation() {
        return location;
    }

    public Date getLastLoadedTime() {
        return time;
    }

    private EvaluationStatistics statistics;

    public EvaluationStatistics getStatistics(){
        return statistics;
    }

    public int getStationSum() {
        return stationSum;
    }

    public int getTeamSum() {
        return teamSum;
    }

    private LoadResult loadResult;

    private void startingStateLoaded(){
        loadResult = LoadResult.Starting;
        comm.startingStateLoaded();
    }

    private void runningStateLoaded(){
        loadResult = LoadResult.Running;
        comm.runningStateLoaded();
    }

    private void endingStateLoaded(){
        loadResult = LoadResult.Ending;
        comm.endingStateLoaded();
    }

    private void resetData(){
        loadResult = null;
        stationSum = -1;
        teamSum = -1;
        location = null;
        time = null;
    }

    private Location location;
    private Date time;

    private int stationSum = -1;
    private int teamSum = -1;

    public interface AdminTotalCommunicationInterface {
        void totalAdminError(ErrorType type);
        void startingStateLoaded();
        void statusUpdateSuccessful();
        void runningStateLoaded();
        void endingStateLoaded();
        void teamListLoaded(ArrayList<Team> teams);
    }
}
