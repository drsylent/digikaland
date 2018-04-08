package hu.bme.aut.digikaland.ui.common.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import hu.bme.aut.digikaland.R;

public class ResultsFragment extends Fragment {
    private static final String ARG_TEAMS = "teams";
    private static final String ARG_POINTS = "points";

//    private String[] teamList;
//    private int[] pointList;
    private ArrayList<String> teamList;
    private ArrayList<Integer> pointList;

    private ResultsFragmentListener activity;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(String[] t, int[] p){
        return null;
    }

    public static ResultsFragment newInstance(ArrayList<String> teams, ArrayList<Integer> points) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TEAMS, teams);
        args.putIntegerArrayList(ARG_POINTS, points);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            teamList = getArguments().getStringArray(ARG_TEAMS);
//            pointList = getArguments().getIntArray(ARG_POINTS);
            teamList = getArguments().getStringArrayList(ARG_TEAMS);
            pointList = getArguments().getIntegerArrayList(ARG_POINTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_results, container, false);
//        if(savedInstanceState == null)
//        for(int i = 0; i < teamList.length; i++){
//            ResultsElementFragment element = ResultsElementFragment.newInstance(i+1, teamList[i], pointList[i]);
//            getChildFragmentManager().beginTransaction().add(R.oldId.resultsContent, element).commit();
//        }
        if(savedInstanceState == null) {
            for(int i = 0; i < teamList.size(); i++){
                ResultsElementFragment element = ResultsElementFragment.newInstance(i+1, teamList.get(i), pointList.get(i));
                getChildFragmentManager().beginTransaction().add(R.id.resultsContent, element).commit();
            }
        }
        Button newRace = root.findViewById(R.id.clientResultsNewRace);
        newRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onNewRaceStart();
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultsFragmentListener) {
            activity = (ResultsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResultsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public interface ResultsFragmentListener{
        void onNewRaceStart();
    }

}
