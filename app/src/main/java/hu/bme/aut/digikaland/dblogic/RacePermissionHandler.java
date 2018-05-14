package hu.bme.aut.digikaland.dblogic;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;

public class RacePermissionHandler {
    private static final RacePermissionHandler ourInstance = new RacePermissionHandler();

    // TODO: vagy inkább ezt kivenni? elég sokan használják, de csak a startup használja kommunikálásra...
    public static RacePermissionHandler getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    public static RacePermissionHandler getInstance() {
        return ourInstance;
    }

    private RacePermissionHandler() {
    }

    private CommunicationInterface comm = null;

    // TODO: feltehetően nem fog kelleni
    public RacePermissionHandler attach(CommunicationInterface c){
        comm = c;
        return this;
    }

    public void detach(){
        comm = null;
    }

    public boolean isReady() {
        return ready;
    }
    // TODO: kell? callbackel szólunk úgyis...
    private boolean ready = false;

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
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("races").document(raceCode);
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
        ready = true;
        switch (toConvert){
            case "total": mainMode = MainMode.Admin; adminMode = AdminMode.Total; break;
            case "station": mainMode = MainMode.Admin; adminMode = AdminMode.Station; break;
            case "captain": mainMode = MainMode.Client; clientMode = ClientMode.Captain; break;
            case "normal": mainMode = MainMode.Client; clientMode = ClientMode.Normal; break;
            default: if(comm != null) comm.permissionError(ErrorType.DatabaseError);
        }
        // Lehet bent marad a korábbi admin... biztos mindig meglesz a reset hívás?
    }

    private void setReference(DocumentReference ref){
        if(mainMode == MainMode.Client) teamReference = ref;
        else if(adminMode == AdminMode.Station) stationReference = ref;
    }

    public void reset(){
        ready = false;
        mainMode = null;
        adminMode = null;
        clientMode = null;
    }

    public MainMode getMainMode() {
        return mainMode;
    }

    public AdminMode getAdminMode() {
        return adminMode;
    }

    public ClientMode getClientMode() {
        return clientMode;
    }

    public DocumentReference getTeamReference() {
        return teamReference;
    }

    public DocumentReference getStationReference() {
        return stationReference;
    }

    public DocumentReference getRaceReference(){ return FirebaseFirestore.getInstance().collection("races").document(CodeHandler.getInstance().getRaceCode()); }

    private MainMode mainMode = null;
    private AdminMode adminMode = null;
    private ClientMode clientMode = null;
    private DocumentReference teamReference = null;
    // ha ez lesz az állomásadminnak
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

    public interface CommunicationInterface{
        void permissionError(ErrorType type);
        void permissionReady();
    }
}
