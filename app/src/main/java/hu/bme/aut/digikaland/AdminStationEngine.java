package hu.bme.aut.digikaland;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import hu.bme.aut.digikaland.dblogic.ContactsEngine;
import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.station.Station;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;

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

    private CommunicationInterface comm;

    private AdminStationEngine() {
    }

    public void loadStationDatas(int teamSum) {
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
                    } catch (RuntimeException e) {
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
            comm.allStationLoadCompleted(stations);
        }
    }

    public void loadStationData(String stationId){
        if(locations.containsKey(stationId)){
            Location location = locations.get(stationId);
            ArrayList<Contact> stationAdmins = admins.get(stationId);
            comm.stationSummaryLoaded(stationId, location, stationAdmins);
        }
        else new SummaryLoader(stationId).startLoad();
    }

    private class SummaryLoader implements ContactsEngine.CommunicationInterface{
        private Location location;
        private String stationId;
        private ArrayList<Contact> stationAdmins;

        private SummaryLoader(String stationId){
            this.stationId = stationId;
        }

        private void startLoad(){
            final DocumentReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId);
            stationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                location = new Location(document.getString("address"), document.getString("address-detailed"));
                                //geoPoint = document.getGeoPoint("geodata");
                                loadContact();
                            } catch (RuntimeException e){
                                comm.adminStationError(ErrorType.DatabaseError);
                            }
                        } else {
                            comm.adminStationError(ErrorType.RaceNotExists);
                        }
                    } else {
                        comm.adminStationError(ErrorType.NoContact);
                    }
                }
            });
        }

        private void loadContact(){
            ContactsEngine.getInstance(this).loadStationAdmins(stationId);
        }

        @Override
        public void stationAdminsLoaded() {
            stationAdmins = ContactsEngine.getInstance(this).getStationAdmins(stationId);
            summaryLoadCompleted(stationId, location, stationAdmins);
        }

        @Override
        public void totalAdminsLoaded() {

        }

        @Override
        public void captainLoaded() {

        }

        @Override
        public void contactsError(ErrorType type) {
            comm.adminStationError(type);
        }
    }

    private void summaryLoadCompleted(String stationId, Location location, ArrayList<Contact> stationAdmins){
        locations.put(stationId, location);
        admins.put(stationId, stationAdmins);
        comm.stationSummaryLoaded(stationId, location, stationAdmins);
    }

    private HashMap<String, Location> locations = new HashMap<>();
    private HashMap<String, ArrayList<Contact>> admins = new HashMap<>();

    private ArrayList<Team> teams = new ArrayList<>();
    private int teamSum;

    public void loadTeamList(String stationId){
        teams.clear();
        final CollectionReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations")
                .document(stationId).collection("teams");
        stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        teamSum = task.getResult().size();
                        int i = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EvaluationStatus status = EvaluationStatus.valueOf(document.getString("status"));
                            DocumentReference teamRef = document.getDocumentReference("reference");
                            loadTeamName(teamRef, status, i++);
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

    private void loadTeamName(final DocumentReference teamRef, final EvaluationStatus status, final int number){
        teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            String teamName = document.getString("name");
                            Team loaded = new Team(teamName, status);
                            loaded.arrivingNumber = number;
                            loaded.id = document.getId();
                            teamLoaded(loaded);
                        } catch (RuntimeException e){
                            comm.adminStationError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.adminStationError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.adminStationError(ErrorType.NoContact);
                }
            }
        });
    }

    private void teamLoaded(Team loaded){
        teams.add(loaded);
        if(teams.size() == teamSum){
            Collections.sort(teams);
            comm.allTeamStatusLoaded(teams);
        }
    }

    public interface CommunicationInterface {
        void adminStationError(ErrorType type);
        void allStationLoadCompleted(ArrayList<StationAdminPerspective> list);
        void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins);
        void allTeamStatusLoaded(ArrayList<Team> teams);
    }
}
