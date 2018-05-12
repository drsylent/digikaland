package hu.bme.aut.digikaland.dblogic.enumeration;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;

/**
 * Created by Sylent on 2018. 05. 12..
 */

public class EvaluatorEngine {
    private static final EvaluatorEngine ourInstance = new EvaluatorEngine();

    public static EvaluatorEngine getInstance(CommunicationInterface c)
    {
        ourInstance.comm = c;
        return ourInstance;
    }

    private CommunicationInterface comm;

    private EvaluatorEngine() {
    }

    public void uploadEvaluation(String solutionId, int points){
        RacePermissionHandler.getInstance().getRaceReference().collection("solutions").document(solutionId)
                .update("points", points)
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

    public void updateEvaluationStatus(String stationId, String teamId){
        RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId).collection("teams")
                .whereEqualTo("reference", RacePermissionHandler.getInstance().getRaceReference().collection("teams").document(teamId))
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

    public interface CommunicationInterface{
        void evaluationUploaded();
        void evaluationStatusUpdated();
        void evaluationUploadError(ErrorType type);
    }

}
