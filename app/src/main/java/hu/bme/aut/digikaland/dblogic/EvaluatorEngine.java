package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;

/**
 * A kiértékelések feltöltését elvégző szolgáltatás.
 */
public class EvaluatorEngine {
    private static final EvaluatorEngine ourInstance = new EvaluatorEngine();

    public static EvaluatorEngine getInstance(EvaluatorCommunicationInterface c)
    {
        ourInstance.comm = c;
        return ourInstance;
    }

    private EvaluatorCommunicationInterface comm;

    private EvaluatorEngine() {
    }

    /**
     * Egy kiértékelés feltöltését végzi el.
     * @param solution A megoldás, mely ki lett értékelve.
     */
    public void uploadEvaluation(Solution solution){
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("points", solution.getCurrentPoints());
        updateData.put("truepoints", solution.getCurrentPoints()*(1-solution.getPenalty()*0.01));
        RaceRoleHandler.getRaceReference().collection("solutions").document(solution.getId())
                .update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.evaluationUploaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.evaluationUploadError(ErrorType.DatabaseError);
                    }
                });
    }

    /**
     * A kiértékelés tényét rögzíti egy állomáson egy csapathoz.
     * @param stationId Az állomás azonosítója.
     * @param teamId A csapat azonosítója.
     */
    public void updateEvaluationStatus(String stationId, String teamId){
        RaceRoleHandler.getRaceReference().collection("stations").document(stationId).collection("teams")
                .whereEqualTo("reference", RaceRoleHandler.getRaceReference().collection("teams").document(teamId))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().getDocuments().size() != 1)
                                comm.evaluationUploadError(ErrorType.DatabaseError);
                            else{
                                DocumentSnapshot station = task.getResult().getDocuments().get(0);
                                updateStationStatus2(station.getReference());
                            }
                        } else {
                            comm.evaluationUploadError(ErrorType.NoContact);
                        }
                    }
                });
    }

    private void updateStationStatus2(DocumentReference teamStationRef){
        final Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", EvaluationStatus.Evaluated.toString());
        teamStationRef.set(updateData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.evaluationStatusUpdated();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.evaluationUploadError(ErrorType.UploadError);
                    }
                });
    }

    public interface EvaluatorCommunicationInterface {
        void evaluationUploaded();
        void evaluationStatusUpdated();
        void evaluationUploadError(ErrorType type);
    }

}
