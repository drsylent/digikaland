package hu.bme.aut.digikaland;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.station.Station;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveTeam;

/**
 * Created by Sylent on 2018. 05. 11..
 */

public class AdminStationEngine {
    private static final AdminStationEngine ourInstance = new AdminStationEngine();

    public static AdminStationEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private ArrayList<StationAdminPerspective> stations = new ArrayList<>();

    private int stationNumber = 0;

    private int teamSum = -1;

    private CommunicationInterface comm;

    private AdminStationEngine() {
    }

    public void loadStationDatas(int teamSum){
        stations.clear();
        this.teamSum = teamSum;
        final CollectionReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations");
        stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        stationNumber = 0;
                        for (QueryDocumentSnapshot stationDoc : task.getResult()) {
                            String id = stationDoc.getId();
                            Station station = new Station(id, stationNumber++);
                            loadEvaluationDatas(station);
                        }
                    } catch (RuntimeException e){
                        comm.adminStationError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.adminStationError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadEvaluationDatas(final Station station){
        final CollectionReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations")
                .document(station.id).collection("teams");
        stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        int done = 0;
                        int evaluated = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EvaluationStatus status = EvaluationStatus.valueOf(document.getString("status"));
                            switch (status){
                                case Done: done++; break;
                                case Evaluated: done++; evaluated++; break;
                            }
                        }
                        stationLoaded(new StationAdminPerspectiveSummary(station, evaluated, done, teamSum));
                    } catch (RuntimeException e){
                        comm.adminStationError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.adminStationError(ErrorType.NoContact);
                }
            }
        });
    }

    private void stationLoaded(StationAdminPerspective station){
        stations.add(station);
        if(stations.size() == stationNumber){
            Collections.sort(stations);
            comm.loadCompleted(stations);
        }
    }

    public interface CommunicationInterface{
        void adminStationError(ErrorType type);
        void loadCompleted(ArrayList<StationAdminPerspective> list);
    }
}
