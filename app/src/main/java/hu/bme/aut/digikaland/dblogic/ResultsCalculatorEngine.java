package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;

public class ResultsCalculatorEngine {
    private class Result implements Comparable<Result>{
        private String teamName;
        private String teamId;
        private double points = 0;
        private Result(String id, String name){
            teamId = id;
            teamName = name;
        }
        private void addPoints(double point){
            points+=point;
        }

        @Override
        public int compareTo(@NonNull Result result) {
            return Double.compare(this.points, result.points);
        }
    }

    private CommunicationInterface comm;
    private int stationSum = -1;
    private ArrayList<Result> results = new ArrayList<>();
    private ArrayList<Integer> stationObjectives = new ArrayList<>();

    public ResultsCalculatorEngine(CommunicationInterface c, int stationSum){
        this.stationSum = stationSum;
        comm = c;
    }

    public void uploadResults(){
        getStationObjectiveNumbers(1);
        getTeams();
    }

    // station id-k 1-től kezdődnek
    private void getStationObjectiveNumbers(final int index){
        final CollectionReference stationObjectiveRef = RaceRoleHandler.getRaceReference()
                .collection("stations").document(Integer.toString(index)).collection("objectives");
        stationObjectiveRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        stationObjectives.add(task.getResult().size());
                        if(index == stationSum) preparationCompleted();
                        else getStationObjectiveNumbers(index+1);
                    } catch (RuntimeException e){
                        comm.resultsUploadError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.resultsUploadError(ErrorType.NoContact);
                }
            }
        });
    }

    private void getTeams(){
        final CollectionReference teamsRef =
                RaceRoleHandler.getRaceReference().collection("teams");
        teamsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Result result = new Result(document.getId(),
                                    document.getString("name"));
                            results.add(result);
                        }
                        preparationCompleted();
                    } catch (RuntimeException e){
                        comm.resultsUploadError(ErrorType.DatabaseError);
                    }
                } else {
                    comm.resultsUploadError(ErrorType.NoContact);
                }
            }
        });
    }

    private int prepared = 0;

    private void preparationCompleted(){
        if(++prepared == 2) downloadResults();
    }

    private int solutionNumber = 0;

    private void downloadResults(){
        teamSum = results.size();
        for(Integer i : stationObjectives) solutionNumber+=i;
        for(Result result : results){
            new PointsDownloader(result).downloadPoints();
        }
    }

    private class PointsDownloader{
        private Result result;
        private PointsDownloader(Result result){
            this.result = result;
        }

        private String createId(int stationNumber, int objectiveNumber, String teamId){
            return stationNumber + "_" + objectiveNumber + "_" + teamId;
        }

        private void downloadPoints(){
            for(int i = 1; i <= stationSum; i++)
                for(int j = 1; j <= stationObjectives.get(i-1); j++){
                    downloadSolution(createId(i, j, result.teamId));
                }
        }

        private void downloadSolution(String id){
            final DocumentReference solutionRef = RaceRoleHandler.getRaceReference()
                    .collection("solutions").document(id);
            solutionRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            try {
                                double points = 0;
                                if(document.contains("truepoints"))
                                    points = document.getDouble("truepoints");
                                addPoint(points);
                            } catch (RuntimeException e){
                                comm.resultsUploadError(ErrorType.DatabaseError);
                            }
                        } else {
                            addPoint(0);
                        }
                    } else {
                        comm.resultsUploadError(ErrorType.NoContact);
                    }
                }
            });
        }

        private int counter = 0;

        private void addPoint(double point){
            result.addPoints(point);
            if(++counter == solutionNumber) pointsDownloaded();
        }
    }

    private int teamSum;

    private int teamCounter = 0;

    private void pointsDownloaded(){
        if(++teamCounter == teamSum){
            Collections.sort(results);
            uploadResults_truly();
        }
    }

    private void uploadResults_truly(){
        for(int i = results.size()-1, position = 1; i >= 0; i--, position++){
            uploadResult(position, results.get(i));
        }
//        for(int i = 0; i < results.size(); i++){
//            uploadResult(i+1, results.get(i));
//        }
    }

    private void uploadResult(int position, Result result){
        final DocumentReference endResultsReference = RaceRoleHandler.getRaceReference().collection("endresults").document(Integer.toString(position));
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("points", result.points);
        updateData.put("teamname", result.teamName);
        endResultsReference.set(updateData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        resultUploaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        comm.resultsUploadError(ErrorType.UploadError);
                    }
                });
    }

    private int resultsCounter = 0;

    private void resultUploaded(){
        if(++resultsCounter == teamSum) comm.resultsUploaded();
    }

    public interface CommunicationInterface{
        void resultsUploaded();
        void resultsUploadError(ErrorType type);
    }
}
