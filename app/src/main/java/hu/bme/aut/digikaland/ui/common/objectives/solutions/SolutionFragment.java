package hu.bme.aut.digikaland.ui.common.objectives.solutions;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;

public abstract class SolutionFragment extends Fragment {
    protected static final String ARG_SOLUTION = "solution";
    protected static final String ARG_CURRENTPOINTS = "cur";
    protected static final String ARG_MAXPOINTS = "max";
    private static int tagNumber = 0;
    private PointDisplayFragment pointHolder;

    public static String generateTag(){
        String tag = "SolutionFragmentTag" + tagNumber;
        tagNumber++;
        return tag;
    }

    protected static Bundle createBundle(Solution sol, int current, int max){
        Bundle args = new Bundle();
        args.putSerializable(ARG_SOLUTION, sol);
        args.putInt(ARG_CURRENTPOINTS, current);
        args.putInt(ARG_MAXPOINTS, max);
        return args;
    }

    private Solution solution;
    protected int currentPoints;
    protected int maxPoints;

    public SolutionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            solution = (Solution) getArguments().getSerializable(ARG_SOLUTION);
            currentPoints = getArguments().getInt(ARG_CURRENTPOINTS);
            maxPoints = getArguments().getInt(ARG_MAXPOINTS);
        }
    }

    protected void setPointHolder(PointDisplayFragment frag){
        pointHolder = frag;
    }

    public Solution getSolution(){
        return solution;
    }

    public abstract void upload();
    public void setPoint(int points){
        pointHolder.setPoints(points);
    }
}
