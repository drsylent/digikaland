package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;

public class ResultsEngine {
    private static final ResultsEngine ourInstance = new ResultsEngine();

    public static ResultsEngine getInstance(CommunicationInterface c)
    {   ourInstance.comm = c;
        return ourInstance;
    }

    private ResultsEngine() {
    }

    private CommunicationInterface comm;

    public void loadResults(){
        if(teams.isEmpty() || points.isEmpty()) downloadResults();
        else comm.resultsLoaded(teams, points);
    }

    private void downloadResults(){
        RaceRoleHandler.getRaceReference().collection("endresults")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    teams.add(document.getString("teamname"));
                                    points.add(document.getDouble("points"));
                                }
                                comm.resultsLoaded(teams, points);
                            }catch (RuntimeException e){
                                comm.resultsError(ErrorType.DatabaseError);
                            }
                        } else {
                            comm.resultsError(ErrorType.NoContact);
                        }
                    }
                });
    }

    private ArrayList<String> teams = new ArrayList<>();
    private ArrayList<Double> points = new ArrayList<>();

    public interface CommunicationInterface{
        void resultsLoaded(ArrayList<String> teamNames, ArrayList<Double> teamPoints);
        void resultsError(ErrorType type);
    }
}
