package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.Contact;

/**
 * Created by Sylent on 2018. 05. 10..
 */

public class ContactsEngineFull {
    private static final ContactsEngineFull ourInstance = new ContactsEngineFull();

    public static ContactsEngineFull getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    public void loadAllData(){
        if(dataLoaded) comm.allDataLoaded(totalAdmins, stationAdmins, teamCaptains);
        else new AllContactDownloader().downloadAllData();
    }

    private CommunicationInterface comm;

    private ContactsEngineFull() {
    }

    private boolean dataLoaded = false;

    private ArrayList<Contact> totalAdmins = new ArrayList<>();

    private HashMap<String, ArrayList<Contact>> stationAdmins = new HashMap<>();

    private HashMap<String, Contact> teamCaptains = new HashMap<>();

    private class AllContactDownloader implements ContactsEngine.CommunicationInterface{
        private ContactsEngine db = ContactsEngine.getInstance(this);
        private ArrayList<String> stationIds = new ArrayList<>();
        private ArrayList<String> stationNames = new ArrayList<>();
        private ArrayList<String> teamIds = new ArrayList<>();
        private ArrayList<String> teamNames = new ArrayList<>();
        private int done = 0;
        private int stations = 0;
        private int teams = 0;

        private void downloadAllData(){
            loadStationIds();
            loadTeamIds();
            loadTotalAdmins();
        }

        private void loadStationIds(){
            final CollectionReference stationRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations");
            stationRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        try {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                stationIds.add(document.getId());
                                stationNames.add(document.getString("address"));
                            }
                            for(String id : stationIds){
                                db.loadStationAdmins(id);
                            }
                        } catch (RuntimeException e){
                            comm.contactsError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.contactsError(ErrorType.NoContact);
                    }
                }
            });
        }

        private void loadTeamIds(){
            final CollectionReference teamRef = RacePermissionHandler.getInstance().getRaceReference().collection("teams");
            teamRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        try {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                teamIds.add(document.getId());
                                teamNames.add(document.getString("name"));
                            }
                            for(String id : teamIds){
                                db.loadCaptain(id);
                            }
                        } catch (RuntimeException e){
                            comm.contactsError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.contactsError(ErrorType.NoContact);
                    }
                }
            });
        }

        private void loadTotalAdmins(){
            db.loadTotalAdmins();
        }

        @Override
        public void totalAdminsLoaded() {
            totalAdmins = db.getTotalAdmins();
            completed();
        }

        @Override
        public void stationAdminsLoaded() {
            if(stationIds.size() == ++stations){
                for(int i = 0; i < stationIds.size(); i++)
                    stationAdmins.put(stationNames.get(i), db.getStationAdmins(stationIds.get(i)));
                completed();
            }
        }

        @Override
        public void captainLoaded() {
            if(teamIds.size() == ++teams) {
                for(int i = 0; i < teamIds.size(); i++)
                    teamCaptains.put(teamNames.get(i), db.getCaptain(teamIds.get(i)));
                completed();
            }
        }

        private void completed(){
            if(++done == 3) {
                dataLoaded = true;
                comm.allDataLoaded(totalAdmins, stationAdmins, teamCaptains);
            }
        }

        @Override
        public void contactsError(ErrorType type) {
            comm.contactsError(type);
        }
    }


    public interface CommunicationInterface{
        void allDataLoaded(ArrayList<Contact> totalAdmins, HashMap<String, ArrayList<Contact>> stationAdmins, HashMap<String, Contact> captains);
        void contactsError(ErrorType type);
    }
}
