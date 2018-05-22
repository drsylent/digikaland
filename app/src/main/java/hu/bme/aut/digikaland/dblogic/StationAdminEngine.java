package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.station.Station;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveTeam;

/**
 * Egy singleton szolgáltatás, melyen keresztül az adminok az állomásokkal kapcsolatos
 * lekérdezéseiket végezhetik az adatbázisból.
 */
public class StationAdminEngine {
    private static final StationAdminEngine ourInstance = new StationAdminEngine();

    public static StationAdminEngine getInstance(StationAdminCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private ArrayList<StationAdminPerspective> stations = new ArrayList<>();

    private int stationNumber = 0;

    private StationAdminCommunicationInterface comm;

    private StationAdminEngine() {
    }

    /**
     * Betölti az összes állomás elérhető állapotát.
     * @param teamSum Hány csapat van összesen a versenyen.
     */
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
                            GeoPoint point = stationDoc.getGeoPoint("geodata");
                            loadEvaluationDatas(station, point);
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

    private void loadEvaluationDatas(final Station station, final GeoPoint point){
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
                        EvaluationStatistics stats = new EvaluationStatistics(evaluated, done, teamSum);
                        stationLoaded(new StationAdminPerspectiveSummary(station, stats, point));
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

    /**
     * Egy konkrét állomásnak az adatait tölti be.
     * @param stationId Az adott állomás azonosítója.
     */
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

    /**
     * Egy állomáshoz tartozó csapatlistát tölt be.
     * @param stationId Az állomás azonosítója.
     */
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

    private int stationSum;
    private boolean errorFreeStationTeam = true;
    private ArrayList<StationAdminPerspective> stationTeams = new ArrayList<>();

    /**
     * Egy csapathoz tartozó állomásadatokat tölt be (végzett-e már, ki van értékelve, stb, megfelelő sorrendben).
     * @param teamId Csapat azonosítója.
     */
    public void loadStationDataForTeam(final String teamId){
        stationTeams.clear();
        final CollectionReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("teams")
                .document(teamId).collection("stations");
        stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        stationSum = task.getResult().size();
                        int i = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference stationRef = document.getDocumentReference("station");
                            Station station = new Station(stationRef.getId(), i++);
                            loadEvaluationDataForTeam(teamId, station);
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

    private void loadEvaluationDataForTeam(String teamId, final Station station){
        RacePermissionHandler.getInstance().getRaceReference().collection("stations")
                .document(station.id).collection("teams")
                .whereEqualTo("reference", RacePermissionHandler.getInstance().getRaceReference().collection("teams").document(teamId))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().getDocuments().size() != 1)
                                errorEvaluationDataForTeam(ErrorType.DatabaseError);
                            else{
                                DocumentSnapshot stationData = task.getResult().getDocuments().get(0);
                                EvaluationStatus status = EvaluationStatus.valueOf(stationData.getString("status"));
                                stationTeamDataLoaded(new StationAdminPerspectiveTeam(station, status));
                            }
                        } else {
                            errorEvaluationDataForTeam(ErrorType.NoContact);
                        }
                    }
                });
    }

    private void errorEvaluationDataForTeam(ErrorType type){
        if(errorFreeStationTeam) comm.adminStationError(type);
        else errorFreeStationTeam = false;
    }

    private void stationTeamDataLoaded(StationAdminPerspectiveTeam station){
        if(errorFreeStationTeam){
            stationTeams.add(station);
            if(stationTeams.size() == stationSum){
                Collections.sort(stationTeams);
                comm.stationTeamDataLoaded(stationTeams);
            }
        }
    }

    /**
     * Egy állomást elindít manuálisan egy csapat számára.
     * @param stationId Az állomás azonosítója.
     * @param teamId A csapat azonosítója.
     */
    public void startStation(String stationId, String teamId){
        new StationStarter(stationId, teamId).startStation();
    }

    private class StationStarter{
        private String stationId;
        private String teamId;
        private String currentStationId;
        private long secondsLimit;
        private StationStarter(String stId, String tId){
            stationId = stId;
            teamId = tId;
        }

        private void startStation(){
            final DocumentReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId);
            stationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                secondsLimit = document.getLong("time");
                                getStationNumber();
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

        private void getStationNumber(){
            RacePermissionHandler.getInstance().getRaceReference().collection("teams")
                    .document(teamId).collection("stations")
                    .whereEqualTo("station", RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().getDocuments().size() != 1)
                                    comm.adminStationError(ErrorType.DatabaseError);
                                else{
                                    DocumentSnapshot stationData = task.getResult().getDocuments().get(0);
                                    currentStationId = stationData.getId();
                                    uploadStationStart();
                                }
                            } else {
                                comm.adminStationError(ErrorType.NoContact);
                            }
                        }
                    });
        }

        private void uploadStationStart(){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(ServerTime.getTime());
            calendar.add(Calendar.SECOND, Long.valueOf(secondsLimit).intValue());
            RacePermissionHandler.getInstance().getRaceReference().collection("teams").document(teamId).collection("stations").document(currentStationId)
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
                            comm.adminStationError(ErrorType.DatabaseError);
                        }
                    });
        }

    }


    public interface StationAdminCommunicationInterface {
        void stationStarted();
        void adminStationError(ErrorType type);
        void stationTeamDataLoaded(ArrayList<StationAdminPerspective> stations);
        void allStationLoadCompleted(ArrayList<StationAdminPerspective> list);
        void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins);
        void allTeamStatusLoaded(ArrayList<Team> teams);
    }
}
