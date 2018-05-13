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

import java.util.Date;

import hu.bme.aut.digikaland.dblogic.enumeration.LoadResult;
import hu.bme.aut.digikaland.dblogic.enumeration.RaceState;
import hu.bme.aut.digikaland.entities.Location;

/**
 * Created by Sylent on 2018. 05. 13..
 */

public class AdminTotalEngine {
    private static final AdminTotalEngine ourInstance = new AdminTotalEngine();

    public static AdminTotalEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private CommunicationInterface comm;

    private AdminTotalEngine() {
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
//                                case Started: loadRunningState(); break;
//                                case Ended: endingStateLoaded(); break;
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

    public void updateRaceStatus(RaceState state){
        RacePermissionHandler.getInstance().getRaceReference()
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

    public LoadResult getLoadResult() {
        return loadResult;
    }

    public Location getLastLoadedLocation() {
        return location;
    }

    public Date getLastLoadedStartingTime() {
        return startingTime;
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

    private void resetData(){
        loadResult = null;
        stationSum = -1;
        teamSum = -1;
        location = null;
        startingTime = null;
        geoPoint = null;
    }

    private Location location;
    private Date startingTime;
    private GeoPoint geoPoint;

    private int stationSum = -1;
    private int teamSum = -1;

    public interface CommunicationInterface{
        void totalAdminError(ErrorType type);
        void startingStateLoaded();
        void statusUpdateSuccessful();
    }
}
