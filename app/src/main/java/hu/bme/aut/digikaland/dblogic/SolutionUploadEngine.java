package hu.bme.aut.digikaland.dblogic;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;

/**
 * Created by Sylent on 2018. 04. 16..
 */

public class SolutionUploadEngine {
    private static final SolutionUploadEngine ourInstance = new SolutionUploadEngine();

    private CommunicationInterface comm;

    public static SolutionUploadEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private SolutionUploadEngine() {
    }

    public void uploadSolutions(ArrayList<Objective> objectives, String teamId){
        new Uploader(objectives, teamId).start();
    }

    private class Uploader{
        ArrayList<Objective> objectives;
        private String stationId;
        private String teamId;
        private int uploadDone = 0;

        private Uploader(ArrayList<Objective> objectives, String teamId){
            this.objectives = objectives;
            this.teamId = teamId;
            stationId = objectives.get(0).getStationId();
        }

        private void start(){
            int index = 1;
            for(Objective objective : objectives){
                String solutionId = documentIdGenerator(index++);
                if(objective.getClass() == TrueFalseObjective.class)
                    uploadTrueFalse((TrueFalseObjective) objective, solutionId);
                else if(objective.getClass() == MultipleChoiceObjective.class)
                    uploadMultipleChoice((MultipleChoiceObjective) objective, solutionId);
                else if(objective.getClass() == CustomAnswerObjective.class)
                    uploadCustomAnswer((CustomAnswerObjective) objective, solutionId);
                else if(objective.getClass() == PhysicalObjective.class)
                    uploadPhysicalObjective(solutionId);
                else if(objective.getClass() == PictureObjective.class)
                    new PictureUploader(this, (PictureObjective) objective, solutionId).uploadPictures();
            }
        }

        private void solutionUploaded(){
            if(++uploadDone == objectives.size()){
                StatusUpdater statusUpdater = new StatusUpdater(stationId);
                statusUpdater.updateStationStatus();
                statusUpdater.updateTeamStationStatus();
                statusUpdater.updateTeamStationNumber();
            }
        }

        private String documentIdGenerator(int index){
            return stationId + "_" + Integer.toString(index) + "_" + teamId;
        }

        private void uploadTrueFalse(TrueFalseObjective objective, String solutionId){
            RacePermissionHandler.getInstance().getRaceReference().collection("solutions").document(solutionId)
                    .update("answer", objective.getAnswer())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            solutionUploaded();
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
                            solutionUploaded();
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
                            solutionUploaded();
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


    private class PictureUploader{
        private PictureObjective objective;
        private int counter = 0;
        private ArrayList<String> filepaths = new ArrayList<>();
        private Uploader uploadMaster;
        private String solutionId;
        private PictureUploader(Uploader callback, PictureObjective obj, String sId){
            uploadMaster = callback;
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
                                uploadMaster.solutionUploaded();
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

    private class StatusUpdater{
        private String stationId;
        private int statusDone = 0;

        private StatusUpdater(String sId){
            stationId = sId;
        }

        private DocumentReference getStationReference(){
            return RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId);
        }

        private void statusUploadCompleted(){
            if(++statusDone == 3) comm.uploadCompleted();
        }

        private void updateTeamStationNumber(){
            RacePermissionHandler.getInstance().getTeamReference()
                    .update("stationnumber", ClientEngine.getInstance().getStationNumber()+1)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            statusUploadCompleted();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            comm.uploadError(ErrorType.DatabaseError);
                        }
                    });
        }

        private void updateTeamStationStatus(){
            RacePermissionHandler.getInstance().getTeamReference().collection("stations").whereEqualTo("station", getStationReference())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().getDocuments().size() != 1)
                                    comm.uploadError(ErrorType.DatabaseError);
                                else{
                                    DocumentSnapshot station = task.getResult().getDocuments().get(0);
                                    updateTeamStationStatus2(station.getReference());
                                }
                            } else {
                                comm.uploadError(ErrorType.NoContact);
                            }
                        }
                    });
        }

        private void updateTeamStationStatus2(DocumentReference teamStationRef){
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("done", true);
            updateData.put("timedone", ServerTime.getTime());
            teamStationRef.set(updateData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            statusUploadCompleted();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            comm.uploadError(ErrorType.UploadError);
                        }
                    });
        }

        private void updateStationStatus(){
            RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId).collection("teams")
                    .whereEqualTo("reference", RacePermissionHandler.getInstance().getTeamReference())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().getDocuments().size() != 1)
                                    comm.uploadError(ErrorType.DatabaseError);
                                else{
                                    DocumentSnapshot station = task.getResult().getDocuments().get(0);
                                    updateStationStatus2(station.getReference());
                                }
                            } else {
                                comm.uploadError(ErrorType.NoContact);
                            }
                        }
                    });
        }

        private void updateStationStatus2(DocumentReference teamStationRef){
            final Map<String, Object> updateData = new HashMap<>();
            updateData.put("status", EvaluationStatus.Done.toString());
            teamStationRef.set(updateData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            statusUploadCompleted();
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

    public interface CommunicationInterface{
        void uploadCompleted();
        void uploadError(ErrorType errorType);
    }
}
