package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;

/**
 * Created by Sylent on 2018. 04. 16..
 */

public class SolutionEngine {
    private static final SolutionEngine ourInstance = new SolutionEngine();

    private CommunicationInterface comm;

    public static SolutionEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private SolutionEngine() {
    }

    public void uploadSolutions(ArrayList<Objective> objectives, String teamId){
        int index = 1;
        for(Objective objective : objectives){
            String solutionId = documentIdGenerator(objective.getStationId(), index++, teamId);
            if(objective.getClass() == TrueFalseObjective.class)
                uploadTrueFalse((TrueFalseObjective) objective, solutionId);
            else if(objective.getClass() == MultipleChoiceObjective.class)
                uploadMultipleChoice((MultipleChoiceObjective) objective, solutionId);
            else if(objective.getClass() == CustomAnswerObjective.class)
                uploadCustomAnswer((CustomAnswerObjective) objective, solutionId);
            else if(objective.getClass() == PhysicalObjective.class)
                uploadPhysicalObjective(solutionId);
//            else if(objective.getClass() == PictureObjective.class)
//                uploadTrueFalse((PictureObjective) objective, solutionId);
        }
    }

    private String documentIdGenerator(String stationId, int index, String teamId){
        return stationId + "_" + Integer.toString(index) + "_" + teamId;
    }

    private void uploadTrueFalse(TrueFalseObjective objective, String solutionId){
        RacePermissionHandler.getInstance().getRaceReference().collection("solutions").document(solutionId)
                .update("answer", objective.getAnswer())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.uploadCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.uploadError(ErrorType.DatabaseError);
                    }
        });
    }

    private void uploadMultipleChoice(MultipleChoiceObjective objective, String solutionId){
        RacePermissionHandler.getInstance().getRaceReference().collection("solutions").document(solutionId)
                .update("answer", objective.getChosenIndex())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.uploadCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.uploadError(ErrorType.DatabaseError);
                    }
                });
    }

    private void uploadCustomAnswer(CustomAnswerObjective objective, String solutionId){
        uploadStringAnswer(objective.getAnswer(), solutionId);
    }

    private void uploadPhysicalObjective(String solutionId){
        uploadStringAnswer("complete", solutionId);
    }

    private void uploadStringAnswer(String answer, String solutionId){
        RacePermissionHandler.getInstance().getRaceReference().collection("solutions").document(solutionId)
                .update("answer", answer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comm.uploadCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.uploadError(ErrorType.DatabaseError);
                    }
                });
    }

    public interface CommunicationInterface{
        void uploadCompleted();
        void uploadError(ErrorType errorType);
    }
}
