package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.enumeration.ObjectiveType;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;

/**
 * Ezen a szolgáltatáson keresztül lehet a feladatok letöltését elvégezni.
 */
public class ObjectiveEngine {
    private static final ObjectiveEngine ourInstance = new ObjectiveEngine();

    public static ObjectiveEngine getInstance(ObjectiveCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private ObjectiveEngine() {
    }

    /**
     * Az állomáshoz tartozó feladatok letöltését végzi.
     * @param stationId Az állomás azonosítója.
     */
    public void loadObjectives(String stationId){
        if(objectiveMap.containsKey(stationId)) comm.objectivesLoaded(objectiveMap.get(stationId));
        else new ObjectiveLoader(stationId).downloadObjectives();
    }

    private class ObjectiveLoader{
        private int objectiveSum = 0;
        private String stationId;
        private int objectiveNumber = 0;
        private boolean errorEnded = false;
        private ArrayList<Objective> objectives = new ArrayList<>();

        private ObjectiveLoader(String stationId){
            this.stationId = stationId;
        }

        private void downloadObjectives(){
            RaceRoleHandler.getRaceReference().collection("stations").document(stationId).collection("objectives")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    objectiveSum = task.getResult().size();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ObjectiveType type = ObjectiveType.valueOf(document.getString("type"));
                                        DocumentReference objectiveRef = document.getDocumentReference("objective");
                                        switch (type){
                                            case MultipleChoice: downloadMultipleChoiceObjective(objectiveRef); break;
                                            case CustomAnswer: downloadQuestionObjective(objectiveRef, type); break;
                                            case TrueFalse: downloadQuestionObjective(objectiveRef, type); break;
                                            case Physical: downloadQuestionObjective(objectiveRef, type); break;
                                            case Picture: downloadPictureObjective(objectiveRef); break;
                                        }
                                    }
                                }catch (RuntimeException e){
                                    loaderError(ErrorType.DatabaseError);
                                }
                            } else {
                                loaderError(ErrorType.NoContact);
                            }
                        }
                    });
        }

        private void downloadMultipleChoiceObjective(DocumentReference ref){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                String question = document.getString("question");
                                ArrayList<String> answerlist = (ArrayList<String>) document.get("answers");
                                Objective obj = new MultipleChoiceObjective(question, answerlist);
                                obj.setId(document.getId());
                                obj.setStationId(stationId);
                                progressMade(obj);
                            }catch(Exception e){
                                loaderError(ErrorType.DatabaseError);
                            }
                        } else {
                            loaderError(ErrorType.RaceNotExists);
                        }
                    } else {
                        loaderError(ErrorType.NoContact);
                    }
                }
            });
        }

        private void downloadQuestionObjective(DocumentReference ref, final ObjectiveType type){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                String question = document.getString("question");
                                Objective obj = (Objective) type.getObjectiveClass().getConstructor(String.class).newInstance(question);
                                obj.setId(document.getId());
                                obj.setStationId(stationId);
                                progressMade(obj);
                            }catch(Exception e){
                                loaderError(ErrorType.DatabaseError);
                            }
                        } else {
                            loaderError(ErrorType.RaceNotExists);
                        }
                    } else {
                        loaderError(ErrorType.NoContact);
                    }
                }
            });
        }

        private void downloadPictureObjective(DocumentReference ref){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                String question = document.getString("question");
                                int maxPictures = document.getLong("maxPictures").intValue();
                                Objective obj = new PictureObjective(question, maxPictures);
                                obj.setId(document.getId());
                                obj.setStationId(stationId);
                                progressMade(obj);
                            }catch(RuntimeException e){
                                loaderError(ErrorType.DatabaseError);
                            }
                        } else {
                            loaderError(ErrorType.RaceNotExists);
                        }
                    } else {
                        loaderError(ErrorType.NoContact);
                    }
                }
            });
        }

        private void progressMade(Objective obj){
            if(!errorEnded) {
                objectives.add(obj);
                if (++objectiveNumber == objectiveSum) {
                    // visszarendezzük megfelelő sorrendbe, aszinkronitás miatt felborulhatott
                    Collections.sort(objectives);
                    objectiveMap.put(stationId, objectives);
                    comm.objectivesLoaded(objectives);
                }
            }
        }

        // összefogjuk a hibaüzeneteket, hogy ne jusson ki annyi, illetve letiltjuk,
        // hogy a nem teljesen letöltött adat megjelenjen
        private void loaderError(ErrorType type){
            errorEnded = true;
            comm.objectiveLoadError(type);
        }
    }

    // stationid-vel mapelve, elvileg nem változik, szóval jó ha eltároljuk hosszútávra
    private Map<String, ArrayList<Objective>> objectiveMap = new HashMap<>();

    private ObjectiveCommunicationInterface comm;

    public interface ObjectiveCommunicationInterface {
        void objectivesLoaded(ArrayList<Objective> objectives);
        void objectiveLoadError(ErrorType type);
    }
}
