package hu.bme.aut.digikaland.dblogic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
        FirebaseFirestore.getInstance().collection("races").document(CodeHandler.getInstance().getRaceCode()).collection("endresults")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    teams.add(document.getString("teamname"));
                                    points.add(document.getLong("points").intValue());
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
    private ArrayList<Integer> points = new ArrayList<>();

    public interface CommunicationInterface{
        void resultsLoaded(ArrayList<String> teamNames, ArrayList<Integer> teamPoints);
        void resultsError(ErrorType type);
    }
}
