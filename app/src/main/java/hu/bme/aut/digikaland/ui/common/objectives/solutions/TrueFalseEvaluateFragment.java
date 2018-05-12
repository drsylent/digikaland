package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;
import hu.bme.aut.digikaland.ui.common.objectives.TrueFalseObjectiveFragment;

public class TrueFalseEvaluateFragment extends EvaluateFragment {

    public TrueFalseEvaluateFragment() {
        // Required empty public constructor
    }

    public static TrueFalseEvaluateFragment newInstance(TrueFalseSolution sol, int current, int max) {
        TrueFalseEvaluateFragment fragment = new TrueFalseEvaluateFragment();
        fragment.setArguments(createBundle(sol, current, max));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_evaluate, container, false);
        TrueFalseSolution solution = (TrueFalseSolution) getSolution();
        TrueFalseObjective objective = (TrueFalseObjective) solution.getObjective();
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                    TrueFalseObjectiveFragment.newInstance(objective, false, solution.getAnswer())).commit();
            PointDisplayFragment fragment = PointDisplayFragment.newInstance(currentPoints, maxPoints, getTag());
            setPointHolder(fragment);
            getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent, fragment, PointDisplayFragment.generateTag()).commit();
        }
        else setPointHolder(savedInstanceState.getString(ARG_POINTFRAGTAG));
        return root;
    }
}
