package hu.bme.aut.digikaland.dblogic;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.MultipleChoiceSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by Sylent on 2018. 05. 10..
 */

public class SolutionDownloadEngine {
    private static final SolutionDownloadEngine ourInstance = new SolutionDownloadEngine();

    public static SolutionDownloadEngine getInstance(CommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private CommunicationInterface comm;

    private SolutionDownloadEngine() {
    }

    public void loadSolutions(String stationId, String teamId){
        new SolutionLoader(stationId, teamId).startDownload();
    }

    private class SolutionLoader{
        private int solutionSum = 0;
        private String stationId;
        private String teamId;
        private int objectiveNumber = 0;
        private int solutionNumber = 0;
        private boolean errorEnded = false;
        private boolean firstDone = false;
        private Date uploadTime;
        private String teamName;
        private int penalty;
        private ArrayList<Objective> objectives = new ArrayList<>();
        private ArrayList<Solution> solutions = new ArrayList<>();

        private SolutionLoader(String stationId, String teamId){
            this.stationId = stationId;
            this.teamId = teamId;
        }

        private void startDownload(){
            downloadTeamName();
        }

        private void downloadPenaltyData(){
            final DocumentReference teamRef = RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId);
            teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                solutionSumData(document.getLong("penalty").intValue());
                            } catch (RuntimeException e){
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

        private void downloadTeamName(){
            final DocumentReference teamRef = RacePermissionHandler.getInstance().getRaceReference().collection("teams").document(teamId);
            teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                teamName = document.getString("name");
                                downloadPenaltyData();
                            } catch (RuntimeException e){
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

        private void solutionSumData(final int penaltyrate){
            RacePermissionHandler.getInstance().getRaceReference().collection("teams").document(teamId).collection("stations")
                    .whereEqualTo("station", RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    if(task.getResult().size() != 1) loaderError(ErrorType.IllegalCharacter);
                                    else{
                                        DocumentSnapshot result = task.getResult().getDocuments().get(0);
                                        uploadTime = result.getDate("timedone");
                                        Date endTime = result.getDate("timeend");
                                        if(uploadTime.after(endTime)) penalty = penaltyrate;
                                        else penalty = 0;
                                        findTeamDocument();
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

        private void findTeamDocument(){
            RacePermissionHandler.getInstance().getRaceReference().collection("stations").document(stationId)
                    .collection("teams").whereEqualTo("reference", RacePermissionHandler.getInstance().getRaceReference().collection("teams").document(teamId))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    downloadSolutions(task.getResult().getDocuments().get(0).getReference());
                                }catch (RuntimeException e){
                                    loaderError(ErrorType.DatabaseError);
                                }
                            } else {
                                loaderError(ErrorType.NoContact);
                            }
                        }
                    });
        }

        private void downloadSolutions(DocumentReference teamRef){
            teamRef.collection("solutions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    solutionSum = task.getResult().size();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ObjectiveType type = ObjectiveType.valueOf(document.getString("type"));
                                        DocumentReference objectiveRef = document.getDocumentReference("objective");
                                        DocumentReference solutionRef = document.getDocumentReference("solution");
                                        switch (type){
                                            case MultipleChoice: downloadMultipleChoiceObjective(objectiveRef);
                                                                 downloadMultipleChoiceSolutionData(solutionRef);   break;
                                            case CustomAnswer: downloadQuestionObjective(objectiveRef, type);
                                                               downloadStringAnswerSolutionData(solutionRef, type); break;
                                            case TrueFalse: downloadQuestionObjective(objectiveRef, type);
                                                            downloadTrueFalseSolutionData(solutionRef); break;
                                            case Physical: downloadQuestionObjective(objectiveRef, type);
                                                           downloadStringAnswerSolutionData(solutionRef, type); break;
                                            case Picture: downloadPictureObjective(objectiveRef);
                                                          downloadPictureAnswerSolutionData(solutionRef);  break;
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
                                objectiveProgressMade(obj);
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
                                objectiveProgressMade(obj);
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
                                objectiveProgressMade(obj);
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

        private void objectiveProgressMade(Objective obj){
            if(!errorEnded) {
                objectives.add(obj);
                if (++objectiveNumber == solutionSum) {
                    // visszarendezzük megfelelő sorrendbe, aszinkronitás miatt felborulhatott
                    Collections.sort(objectives);
                    if(firstDone) mergeResults();
                    else firstDone = true;
                    //objectiveMap.put(stationId, objectives);
                    //comm.solutionsLoaded(objectives);
                }
            }
        }


        private void solutionProgressMade(Solution sol){
            if(!errorEnded) {
                solutions.add(sol);
                if (++solutionNumber == solutionSum) {
                    // visszarendezzük megfelelő sorrendbe, aszinkronitás miatt felborulhatott
                    Collections.sort(solutions);
                    if(firstDone) mergeResults();
                    else firstDone = true;
                }
            }
        }

        private void mergeResults(){
            for(int i = 0; i < solutionSum; i++){
                solutions.get(i).setObjective(objectives.get(i));
            }
            comm.solutionsLoaded(solutions, penalty, uploadTime, teamName);
        }

        private void downloadTrueFalseSolutionData(DocumentReference ref){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                int currentPoints = 0;
                                // TODO: automata megoldás itt
                                if(document.contains("points")) currentPoints = document.getLong("points").intValue();
                                Solution sol = new TrueFalseSolution(currentPoints, document.getLong("maxpoints").intValue(), document.getBoolean("answer"));
                                sol.setId(document.getId());
                                solutionProgressMade(sol);
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

        private void downloadPictureAnswerSolutionData(DocumentReference ref){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                int currentPoints = 0;
                                if(document.contains("points")) currentPoints = document.getLong("points").intValue();
                                int maxPoints = document.getLong("maxpoints").intValue();
                                ArrayList<String> answer = (ArrayList<String>) document.get("answer");
                                PictureSolution sol = new PictureSolution(currentPoints, maxPoints, answer);
                                sol.setId(document.getId());
                                preparePictures(sol);
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

        private String fileNameGetter(String onlinePath){
            String[] parts = onlinePath.split("/");
            return parts[parts.length-1];
        }

        private String directoryCreator(){
            return getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Digikaland/downloads/";
        }

        private String filePathCreator(String fileName){
            return directoryCreator() + fileName;
        }

        private void preparePictures(PictureSolution pictureSolution){
            // TODO: használni cache-t, ha lehet!
            for(String onlinePath : pictureSolution.getAnswer()) {
                String name = fileNameGetter(onlinePath);
                File file = new File(filePathCreator(name));
                if(file.exists()) continue;
                else{
                    downloadPicture(onlinePath, pictureSolution);
                }
            }
        }

        private HashMap<String, Integer> pictureDownloadDB = new HashMap<>();

        private void pictureDownloadCompleted(PictureSolution solution){
            String solutionId = solution.getId();
            if(pictureDownloadDB.containsKey(solutionId))
                pictureDownloadDB.put(solutionId, pictureDownloadDB.get(solutionId)+1);
            else pictureDownloadDB.put(solutionId, 1);
            if(pictureDownloadDB.get(solutionId) == solution.getAnswer().size())
                solutionProgressMade(solution);
        }
// TODO: uriknak kéne lenniük file:///
        private void downloadPicture(final String onlinePath, final PictureSolution solution){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(onlinePath);
            File storageDir = new File(directoryCreator());
            if(!storageDir.exists()) storageDir.mkdir();
            try {
                //final File imageFile = new File(filePathCreator(fileNameGetter(onlinePath)));
                String imageFileName = fileNameGetter(onlinePath).split("\\.")[0];
//                imageFile.createNewFile();
                final File imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                Uri photoUri = FileProvider.getUriForFile((Context) comm,
                        "hu.bme.aut.digikaland.fileprovider",
                        imageFile);
                storageReference.getFile(photoUri).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        pictureDownloadCompleted(solution);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int code = ((StorageException) e).getHttpResultCode();
                        imageFile.delete();
                        loaderError(ErrorType.DownloadError);
                    }
                });
            }catch (Exception e){
                loaderError(ErrorType.DownloadError);
            }
        }

        private void downloadStringAnswerSolutionData(DocumentReference ref, final ObjectiveType type){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                int currentPoints = 0;
                                if(document.contains("points")) currentPoints = document.getLong("points").intValue();
                                int maxPoints = document.getLong("maxpoints").intValue();
                                String answer = document.getString("answer");
                                Solution sol = (Solution) type.getSolutionClass().
                                        getConstructor(int.class, int.class, String.class).newInstance(currentPoints, maxPoints, answer);
                                sol.setId(document.getId());
                                solutionProgressMade(sol);
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

        private void downloadMultipleChoiceSolutionData(DocumentReference ref){
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                int currentPoints = 0;
                                // TODO: automata megoldás itt
                                if(document.contains("points")) currentPoints = document.getLong("points").intValue();
                                Solution sol = new MultipleChoiceSolution(currentPoints, document.getLong("maxpoints").intValue(), document.getLong("answer").intValue());
                                sol.setId(document.getId());
                                solutionProgressMade(sol);
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

        // összefogjuk a hibaüzeneteket, hogy ne jusson ki annyi, illetve letiltjuk,
        // hogy a nem teljesen letöltött adat megjelenjen
        private void loaderError(ErrorType type){
            errorEnded = true;
            comm.solutionsLoadError(type);
        }
    }

    // stationid-vel mapelve, elvileg nem változik, szóval jó ha eltároljuk hosszútávra
//    private Map<String, ArrayList<Objective>> objectiveMap = new HashMap<>();


    public interface CommunicationInterface{
        void solutionsLoaded(ArrayList<Solution> solutions, int penalty, Date uploadTime, String teamName);
        void solutionsLoadError(ErrorType type);
    }
}
