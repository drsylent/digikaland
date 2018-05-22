package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.enumeration.StationStatusFromClient;
import hu.bme.aut.digikaland.entities.station.StationClientPerspective;

/**
 * A kliensek az állomással kapcsolatos komplexebb adatokat ezen a szolgáltatáson keresztül
 * érhetik el.
 */
public class StationClientEngine {
    private static final StationClientEngine ourInstance = new StationClientEngine();

    public static StationClientEngine getInstance(StationClientCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private StationClientEngine() {
    }

    private StationClientCommunicationInterface comm;

    /**
     * Az állomások listájának betöltése.
     */
    public void loadStationList(){
        RaceRoleHandler.getTeamReference().collection("stations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<StationClientPerspective> list = new ArrayList<>();
                            try {
                                int counter = 1;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    list.add(createStation(document, counter++));
                                }
                                comm.stationListLoaded(list);
                            }catch (RuntimeException e){
                                comm.stationLoadingError(ErrorType.DatabaseError);
                            }
                        } else {
                            comm.stationLoadingError(ErrorType.NoContact);
                        }
                    }
                });
    }

    private StationClientPerspective createStation(DocumentSnapshot doc, int number){
        StationStatusFromClient status;
        if(doc.contains("timeend"))
            if(doc.getBoolean("done")) status = StationStatusFromClient.Done;
            else status = StationStatusFromClient.Started;
        else status = StationStatusFromClient.NotStarted;
        try {
            return new StationClientPerspective(doc.getDocumentReference("station").getId(), number, status);
        }
        catch (RuntimeException e){
            throw e;
        }
    }

    public interface StationClientCommunicationInterface {
        void stationListLoaded(ArrayList<StationClientPerspective> stations);
        void stationLoadingError(ErrorType type);
    }
}
