package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.util.Log;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;

/**
 * Egy kiértékelő fragment, melyhez hozzátartozik a pontszámító is, illetve a feladat,
 * melynek megoldását nem lehet szerkeszteni.
 */
public abstract class EvaluateFragment extends SolutionFragment {
    protected static final String ARG_CURRENTPOINTS = "cur";
    protected static final String ARG_MAXPOINTS = "max";
    protected static final String ARG_POINTFRAGTAG = "fragtag";
    private static int tagNumber = 0;
    private PointDisplayFragment pointHolder;

    public static String generateTag(){
        String tag = "SolutionFragmentTag" + tagNumber;
        tagNumber++;
        return tag;
    }

    protected static Bundle createBundle(Solution sol, int current, int max){
        Bundle args = createBundle(sol);
        args.putInt(ARG_CURRENTPOINTS, current);
        args.putInt(ARG_MAXPOINTS, max);
        return args;
    }

    protected int currentPoints;
    protected int maxPoints;

    public EvaluateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPoints = getArguments().getInt(ARG_CURRENTPOINTS);
            maxPoints = getArguments().getInt(ARG_MAXPOINTS);
        }
    }

    protected void setPointHolder(String tag){
        pointHolder = (PointDisplayFragment) getChildFragmentManager().findFragmentByTag(tag);
    }

    protected void setPointHolder(PointDisplayFragment frag){
        pointHolder = frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_POINTFRAGTAG, pointHolder.getTag());
    }

    public void setPoint(int points){
        currentPoints = points;
        getSolution().setCurrentPoints(points);
        pointHolder.setPoints(points);
        Log.e("PointHolder Tag", pointHolder.getTag());
    }
}
