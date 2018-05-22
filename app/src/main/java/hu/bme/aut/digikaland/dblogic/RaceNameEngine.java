package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;

/**
 * A versenyek nevét ezen a szolgáltatáson keresztül lehet lekérni.
 */
public class RaceNameEngine {
    private static final RaceNameEngine ourInstance = new RaceNameEngine();

    // Huzamosabb ideig ezt nem használjuk, csak erre a lekérésre - a csatolást bevonjuk ezért
    // a példánykérésbe minden alkalommal.
    public static RaceNameEngine getInstance(RaceNameCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private RaceNameEngine() {
    }

    private RaceNameCommunicationInterface comm;

    /**
     * Végrehajtja a verseny nevének betöltését.
     * @param raceCode
     */
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
                        try{
                            comm.raceNameLoaded(document.getString("racename"));
                        }catch(RuntimeException e){
                            comm.raceNameLoadingError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.raceNameLoadingError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.raceNameLoadingError(ErrorType.NoContact);
                }
            }
        });
    }

    public interface RaceNameCommunicationInterface {
        void raceNameLoaded(String result);
        void raceNameLoadingError(ErrorType type);
    }
}
