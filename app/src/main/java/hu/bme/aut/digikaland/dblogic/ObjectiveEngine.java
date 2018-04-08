package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;

/**
 * Created by Sylent on 2018. 04. 08..
 */

public class ObjectiveEngine {
    private static final ObjectiveEngine ourInstance = new ObjectiveEngine();

    public static ObjectiveEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private ObjectiveEngine() {
    }

    public void loadObjectives(String stationId){
        if(objectiveMap.containsKey(stationId)) comm.objectivesLoaded(objectiveMap.get(stationId));
        else downloadObjectives(stationId);
    }

    private int objectiveSum = -1;
    private String stationId;
    private int objectiveNumber;

    private void downloadObjectives(String stationId){
        objectiveNumber = 0;
        this.stationId = stationId;
        RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId).collection("objectives")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                objectiveSum = task.getResult().size();
                                ArrayList<Objective> objs = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ObjectiveType type = ObjectiveType.valueOf(document.getString("type"));
                                    Log.e("Objective type download", type.name());
                                    DocumentReference objectiveRef = document.getDocumentReference("objective");
                                    switch (type){
                                        case MultipleChoice:    break;
                                        case CustomAnswer: downloadQuestionObjective(objectiveRef, objs, type); break;
                                        case TrueFalse: downloadQuestionObjective(objectiveRef, objs, type); break;
                                        case Physical: downloadQuestionObjective(objectiveRef, objs, type); break;
                                        case Picture: downloadPictureObjective(objectiveRef, objs);break;
                                    }
                                }
                            }catch (RuntimeException e){
                                comm.objectiveLoadError(ErrorType.DatabaseError);
                            }
                        } else {
                            comm.objectiveLoadError(ErrorType.NoContact);
                        }
                    }
                });
    }

    private void downloadQuestionObjective(DocumentReference ref, final ArrayList<Objective> objectives, final ObjectiveType type){
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
                            progressMade(objectives, obj);
                        }catch(Exception e){
                            comm.objectiveLoadError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.objectiveLoadError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.objectiveLoadError(ErrorType.NoContact);
                }
            }
        });
    }

    private void downloadPictureObjective(DocumentReference ref, final ArrayList<Objective> objectives){
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
                            progressMade(objectives, obj);
                        }catch(RuntimeException e){
                            comm.objectiveLoadError(ErrorType.DatabaseError);
                        }
                    } else {
                        comm.objectiveLoadError(ErrorType.RaceNotExists);
                    }
                } else {
                    comm.objectiveLoadError(ErrorType.NoContact);
                }
            }
        });
    }

    private void progressMade(ArrayList<Objective> objectives, Objective obj){
        Log.e("Objective Id", obj.getId());
        objectives.add(obj);
        if(++objectiveNumber == objectiveSum){
            // visszarendezzük megfelelő sorrendbe, aszinkronitás miatt felborulhatott
            Collections.sort(objectives);
            objectiveMap.put(stationId, objectives);
            comm.objectivesLoaded(objectives);
        }
    }

    // stationid-vel mapelve, elvileg nem változik, szóval jó ha eltároljuk hosszútávra
    private Map<String, ArrayList<Objective>> objectiveMap = new HashMap<>();

    private CommunicationInterface comm;

    public interface CommunicationInterface{
        void objectivesLoaded(ArrayList<Objective> objectives);
        void objectiveLoadError(ErrorType type);
    }
}
