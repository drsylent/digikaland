package hu.bme.aut.digikaland.dblogic;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.digikaland.entities.Picture;
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
            else if(objective.getClass() == PictureObjective.class)
                new PictureUploader((PictureObjective) objective, solutionId).uploadPictures();
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
                        comm.uploadError(ErrorType.UploadError);
                    }
                });
    }

    private class PictureUploader{
        private PictureObjective objective;
        int counter = 0;
        private ArrayList<String> filepaths = new ArrayList<>();
        int completed = 0;
        private String solutionId;
        private PictureUploader(PictureObjective obj, String sId){
            objective = obj;
            solutionId = sId;
        }

        private String filePathGenerator(){
            String path = RacePermissionHandler.getInstance().getRaceReference().getId() + "/" + "images/";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            return path + "JPEG_" + timeStamp + "_" + objective.getStationId() + Integer.toString(counter++) + ".jpg";
        }

        private void uploadPictures(){
            for(Uri picture : objective.getPictures()){
                uploadPicture(picture);
            }
        }

        private void pictureUploaded(String path){
            filepaths.add(path);
            if(filepaths.size() == objective.getPictures().size()) {
                RacePermissionHandler.getInstance().getRaceReference().collection("solutions").document(solutionId)
                        .update("answer", filepaths)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pictureSolutionUploaded();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                comm.uploadError(ErrorType.UploadError);
                            }
                        });
            }
        }

        private void pictureSolutionUploaded(){
            comm.uploadCompleted();
        }

        private void uploadPicture(Uri file){
            final String filepath = filePathGenerator();
            StorageReference pictureRef = FirebaseStorage.getInstance().getReference().child(filepath);
            UploadTask uploadTask = pictureRef.putFile(file);

            // Register observers to listen for when the upload is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    comm.uploadError(ErrorType.PictureUploadError);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pictureUploaded(filepath);
                }
            });
        }
    }

    public interface CommunicationInterface{
        void uploadCompleted();
        void uploadError(ErrorType errorType);
    }
}
