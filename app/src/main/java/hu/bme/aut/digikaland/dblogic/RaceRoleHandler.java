package hu.bme.aut.digikaland.dblogic;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;

/**
 * Ez a szolgáltatás kezeli, a felhasználó milyen szerepben van a verseny során,
 * illetve az ebből következő dokumentumok referenciáit is innen lehet elérni.
 */
public class RaceRoleHandler {
    private static final RaceRoleHandler ourInstance = new RaceRoleHandler();

    // kommunikálni csak a StartupActivityvel fog
    public static RaceRoleHandler getInstance(RaceRoleCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private RaceRoleHandler() {
    }

    private RaceRoleCommunicationInterface comm = null;

    // A visszatérési értékkel jelezzük, hogy most kéne-e beállításnak történnie, vagy nem.
    public boolean startUp(SharedPreferences preferences){
        CodeHandler ch = CodeHandler.getInstance();
        if(ch.initialize(preferences)) {
            loadPermissionData(ch.getRaceCode(), ch.getRoleCode());
            return true;
        }
        return false;
    }

    public void loadPermissionData(@NonNull String raceCode, @NonNull final String roleCode){
        if(raceCode.isEmpty()){
            comm.permissionError(ErrorType.EmptyField);
            return;
        }
        final DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("races").document(raceCode);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        loadRole(docRef, roleCode);
                    } else {
                        if(comm != null) comm.permissionError(ErrorType.RaceNotExists);
                    }
                } else {
                    if(comm != null) comm.permissionError(ErrorType.NoContact);
                }
            }
        });
    }

    private void loadRole(DocumentReference docRef, String roleCode){
        docRef = docRef.collection("codes").document(roleCode);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        if(document.get("role") instanceof String)
                            modeSet((String) document.get("role"));
                        else comm.permissionError(ErrorType.DatabaseError);
                        if(document.contains("reference"))
                            if(document.get("reference") instanceof DocumentReference)
                                setReference(document.getDocumentReference("reference"));
                            else comm.permissionError(ErrorType.DatabaseError);
                        if(comm != null) comm.permissionReady();
                    } else {
                        if(comm != null) comm.permissionError(ErrorType.RoleNotExists);
                    }
                } else {
                    if(comm != null) comm.permissionError(ErrorType.NoContact);
                }
            }
        });
    }

    private void modeSet(String toConvert){
        toConvert = toConvert.toLowerCase();
        switch (toConvert){
            case "total": mainMode = MainMode.Admin; adminMode = AdminMode.Total; break;
            case "station": mainMode = MainMode.Admin; adminMode = AdminMode.Station; break;
            case "captain": mainMode = MainMode.Client; clientMode = ClientMode.Captain; break;
            case "normal": mainMode = MainMode.Client; clientMode = ClientMode.Normal; break;
            default: if(comm != null) comm.permissionError(ErrorType.DatabaseError);
        }
    }

    private void setReference(DocumentReference ref){
        if(mainMode == MainMode.Client) teamReference = ref;
        else if(adminMode == AdminMode.Station) stationReference = ref;
    }

    public static void reset(){
        ourInstance.mainMode = null;
        ourInstance.adminMode = null;
        ourInstance.clientMode = null;
    }

    // mivel ezek staticok, nem kell elkérni feleslegesen a példányt a RacePermissionHandlerből

    public static MainMode getMainMode() {
        return ourInstance.mainMode;
    }

    public static AdminMode getAdminMode() {
        return ourInstance.adminMode;
    }

    public static ClientMode getClientMode() {
        return ourInstance.clientMode;
    }

    /**
     * A kliens csapatához tartozó dokumentum referenciáját adja vissza.
     * @return A kliens csapatához tartozó dokumentum.
     */
    public static DocumentReference getTeamReference() {
        return ourInstance.teamReference;
    }

    /**
     * Az állomás admin állomásához tartozó dokumentum referenciáját adja vissza.
     * @return Az állomás admin állomásához tartozó dokumentum.
     */
    public static DocumentReference getStationReference() {
        return ourInstance.stationReference;
    }

    /**
     * A versenyt tároló dokumentum referenciáját adja vissza.
     * @return A versenyt tároló dokumentum.
     */
    public static DocumentReference getRaceReference(){
        return FirebaseFirestore.getInstance().collection("races")
                .document(CodeHandler.getInstance().getRaceCode());
    }

    private MainMode mainMode = null;
    private AdminMode adminMode = null;
    private ClientMode clientMode = null;
    private DocumentReference teamReference = null;
    private DocumentReference stationReference = null;

    public enum MainMode{
        Admin,
        Client
    }

    public enum AdminMode{
        Total,
        Station
    }

    public enum ClientMode{
        Captain,
        Normal
    }

    public interface RaceRoleCommunicationInterface {
        void permissionError(ErrorType type);
        void permissionReady();
    }
}
