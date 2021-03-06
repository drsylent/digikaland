package hu.bme.aut.digikaland.dblogic;

import android.os.Environment;
import android.support.annotation.NonNull;

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
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.enumeration.ObjectiveType;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.MultipleChoiceSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * A megoldások letöltését ezen a szolgáltatáson keresztül lehet megtenni.
 */
public class SolutionDownloadEngine {
    private static final SolutionDownloadEngine ourInstance = new SolutionDownloadEngine();

    public static SolutionDownloadEngine getInstance(SolutionDownloadCommunicationInterface c) {
        ourInstance.comm = c;
        return ourInstance;
    }

    private SolutionDownloadCommunicationInterface comm;

    private SolutionDownloadEngine() {
    }

    /**
     * A megadott csapat adott állomáshoz tartozó megoldásainak letöltése.
     * @param stationId Az állomás azonosítója, melyhez a megoldások tartoznak.
     * @param teamId A csapat azonosítója, melynek a megoldásaira kíváncsiak vagyunk.
     */
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
            final DocumentReference teamRef = RaceRoleHandler.getRaceReference().collection("stations").document(stationId);
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
            final DocumentReference teamRef = RaceRoleHandler.getRaceReference().collection("teams").document(teamId);
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
            RaceRoleHandler.getRaceReference().collection("teams").document(teamId).collection("stations")
                    .whereEqualTo("station", RaceRoleHandler.getRaceReference().collection("stations").document(stationId))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    if(task.getResult().size() != 1) loaderError(ErrorType.DatabaseError);
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
            RaceRoleHandler.getRaceReference().collection("stations").document(stationId)
                    .collection("teams").whereEqualTo("reference", RaceRoleHandler.getRaceReference().collection("teams").document(teamId))
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
                                                          new PictureSolutionDownloader().downloadPictureAnswerSolutionData(solutionRef);  break;
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
                }
            }
        }


        private void solutionProgressMade(Solution sol){
            if(!errorEnded) {
                sol.setPenalty(penalty);
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
                                boolean answer = document.getBoolean("answer");
                                int maxpoints = document.getLong("maxpoints").intValue();
                                if(document.contains("auto")){
                                    boolean auto = document.getBoolean("auto");
                                    if(auto == answer) currentPoints = maxpoints;
                                }
                                if(document.contains("points")) currentPoints = document.getLong("points").intValue();
                                Solution sol = new TrueFalseSolution(currentPoints, maxpoints, answer);
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

        private class PictureSolutionDownloader{
            private ArrayList<String> downloadPaths;
            private PictureSolution solution;

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
                                    downloadPaths = (ArrayList<String>) document.get("answer");
                                    solution = new PictureSolution(currentPoints, maxPoints);
                                    solution.setId(document.getId());
                                    preparePictures();
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

            private void preparePictures(){
                for(String onlinePath : downloadPaths) {
                    String name = fileNameGetter(onlinePath);
                    File file = new File(filePathCreator(name));
                    if(file.exists()) pictureDownloadCompleted();
                    else{
                        downloadPicture(onlinePath);
                    }
                }
            }

            private int counter = 0;

            private void pictureDownloadCompleted(){
                if(++counter == downloadPaths.size()) {
                    ArrayList<String> filePaths = new ArrayList<>();
                    for(String onlinePath : downloadPaths)
                        filePaths.add(filePathCreator(fileNameGetter(onlinePath)));
                    solution.setAnswers(filePaths);
                    solution.uriizeFilePaths();
                    solutionProgressMade(solution);
                }
            }

            private void downloadPicture(final String onlinePath){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(onlinePath);
                File storageDir = new File(directoryCreator());
                if(!storageDir.exists()) storageDir.mkdir();
                try {
                    final File imageFile = new File(filePathCreator(fileNameGetter(onlinePath)));
                    imageFile.createNewFile();
                    storageReference.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            pictureDownloadCompleted();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageFile.delete();
                            loaderError(ErrorType.DownloadError);
                        }
                    });
                }catch (Exception e){
                    loaderError(ErrorType.DownloadError);
                }
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
                                int maxpoints = document.getLong("maxpoints").intValue();
                                int answer = document.getLong("answer").intValue();
                                if(document.contains("auto")){
                                    int auto = document.getLong("auto").intValue();
                                    if(auto == answer) currentPoints = maxpoints;
                                }
                                if(document.contains("points")) currentPoints = document.getLong("points").intValue();
                                Solution sol = new MultipleChoiceSolution(currentPoints, maxpoints, answer);
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

    public interface SolutionDownloadCommunicationInterface {
        void solutionsLoaded(ArrayList<Solution> solutions, int penalty, Date uploadTime, String teamName);
        void solutionsLoadError(ErrorType type);
    }
}
