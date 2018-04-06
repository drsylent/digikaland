package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RaceNameGetter {
    private static final RaceNameGetter ourInstance = new RaceNameGetter();

    // Huzamosabb ideig ezt nem használjuk, csak erre a lekérésre - a csatolást bevonjuk ezért
    // a példánykérésbe minden alkalommal.
    public static RaceNameGetter getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private RaceNameGetter() {
    }

    private CommunicationInterface comm;

    public void loadRaceName(@NonNull String raceCode){
        if(raceCode.isEmpty()){
            comm.raceNameLoadingError(ErrorType.EmptyField);
            return;
        }
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("races").document(raceCode);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.e("Firebase", "DocumentSnapshot data: " + document.get("RaceName"));
                        if(document.get("RaceName") instanceof String)
                            comm.raceNameLoaded((String) document.get("RaceName"));
                        else comm.raceNameLoadingError(ErrorType.DatabaseError);
                    } else {
                        comm.raceNameLoadingError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.raceNameLoadingError(ErrorType.NoContact);
                }
            }
        });
    }

    public interface CommunicationInterface{
        void raceNameLoaded(String result);
        void raceNameLoadingError(ErrorType type);
    }
}
