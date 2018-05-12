package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;

public abstract class SolutionFragment extends Fragment {
    protected static final String ARG_SOLUTION = "solution";
    private static int tagNumber = 0;

    public static String generateTag(){
        String tag = "SolutionFragmentTag" + tagNumber;
        tagNumber++;
        return tag;
    }

    protected static Bundle createBundle(Solution sol){
        Bundle args = new Bundle();
        args.putSerializable(ARG_SOLUTION, sol);
        return args;
    }

    private Solution solution;

    public SolutionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            solution = (Solution) getArguments().getSerializable(ARG_SOLUTION);
        }
    }

    public Solution getSolution(){
        return solution;
    }
}
