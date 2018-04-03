package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;
import hu.bme.aut.digikaland.ui.common.objectives.TrueFalseObjectiveFragment;

public class TrueFalseSolutionFragment extends SolutionFragment {

    public TrueFalseSolutionFragment() {
        // Required empty public constructor
    }

    public static TrueFalseSolutionFragment newInstance(TrueFalseSolution sol, int current, int max) {
        TrueFalseSolutionFragment fragment = new TrueFalseSolutionFragment();
        fragment.setArguments(createBundle(sol, current, max));
        return fragment;
    }

    @Override
    public void upload() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_evaluate, container, false);
        TrueFalseSolution solution = (TrueFalseSolution) getSolution();
        TrueFalseObjective objective = (TrueFalseObjective) solution.getObjective();
        getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                TrueFalseObjectiveFragment.newInstance(objective, false, solution.getAnswer())).commit();
        getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent,
                PointDisplayFragment.newInstance(currentPoints, maxPoints)).commit();
        return root;
    }
}
