package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.Contact;

/**
 * Ezen a szolgáltatáson keresztül lehet elérni az egyes versenyben részt vevő személyek
 * kapcsolattartási adatait.
 */
public class ContactsEngine {
    private static final ContactsEngine ourInstance = new ContactsEngine();

    public static ContactsEngine getInstance(ContactsEngineCommunicationInterface c)
    {
        ourInstance.comm = c;
        return ourInstance;
    }

    private ContactsEngineCommunicationInterface comm;

    private ContactsEngine() {
    }

    /**
     * Betölti az összes teljes admint.
     */
    public void loadTotalAdmins(){
        if(totalAdmins.isEmpty()) downloadTotalAdmins();
        else comm.totalAdminsLoaded();
    }

    /**
     * Betölti a megadott állomáshoz tartozó admint.
     * @param stationId Az állomás azonosítója.
     */
    public void loadStationAdmins(String stationId){
        if(stations.containsKey(stationId)) comm.stationAdminsLoaded();
        else downloadStationAdmins(stationId);
    }

    /**
     * Betölti a megadott csapat kapitányát.
     * @param teamId A csapat azonosítója.
     */
    public void loadCaptain(String teamId){
        if(captains.containsKey(teamId)) comm.captainLoaded();
        else downloadTeamCaptain(teamId);
    }

    private int totalAdminSum = 0;

    private void downloadData(final DocumentReference from, final ArrayList<Contact> to, final int targetSize){
        from.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            to.add(document.toObject(Contact.class));
                            if(to.size() == targetSize) downloadCompleted(to);
                        } catch (RuntimeException e){
                            comm.contactsError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.contactsError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.contactsError(ErrorType.NoContact);
                }
            }
        });
    }

    private void downloadCompleted(ArrayList<Contact> list){
        if(list == totalAdmins) comm.totalAdminsLoaded();
        if(stations.containsValue(list)) comm.stationAdminsLoaded();
    }

    private void downloadTotalAdmins(){
        final DocumentReference totalAdminRef = RaceRoleHandler.getRaceReference().collection("contacts").document("total");
        totalAdminRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            totalAdminSum = document.getData().values().size();
                            for(Object ref : document.getData().values()){
                                downloadData((DocumentReference) ref, totalAdmins, totalAdminSum);
                            }
                        } catch (RuntimeException e){
                            comm.contactsError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.contactsError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.contactsError(ErrorType.NoContact);
                }
            }
        });
    }

    private void downloadStationAdmins(final String id){
        final DocumentReference totalAdminRef = RaceRoleHandler.getRaceReference().collection("contacts").document("st" + id);
        totalAdminRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            int stationAdminsSum = document.getData().values().size();
                            ArrayList<Contact> stationAdmins = new ArrayList<>();
                            stations.put(id, stationAdmins);
                            for(Object ref : document.getData().values()){
                                downloadData((DocumentReference) ref, stationAdmins, stationAdminsSum);
                            }
                        } catch (RuntimeException e){
                            comm.contactsError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.contactsError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.contactsError(ErrorType.NoContact);
                }
            }
        });
    }

    private void downloadTeamCaptain(final String id){
        final DocumentReference totalAdminRef = RaceRoleHandler.getRaceReference().collection("contacts").document(id);
        totalAdminRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        try {
                            captains.put(id, document.toObject(Contact.class));
                            comm.captainLoaded();
                        } catch (RuntimeException e){
                            comm.contactsError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.contactsError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.contactsError(ErrorType.NoContact);
                }
            }
        });
    }

    private ArrayList<Contact> totalAdmins = new ArrayList<>();
    private Map<String, ArrayList<Contact>> stations = new HashMap<>();
    private Map<String, Contact> captains = new HashMap<>();

    public ArrayList<Contact> getTotalAdmins(){
        return totalAdmins;
    }

    public ArrayList<Contact> getStationAdmins(String id){
        return stations.get(id);
    }

    public Contact getCaptain(String id){
        return captains.get(id);
    }

    public interface ContactsEngineCommunicationInterface {
        void totalAdminsLoaded();
        void stationAdminsLoaded();
        void captainLoaded();
        void contactsError(ErrorType type);
    }
}
