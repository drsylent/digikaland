package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.CustomAnswerSolution;
import hu.bme.aut.digikaland.ui.common.objectives.CustomAnswerObjectiveFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class CustomAnswerSolutionFragment extends SolutionFragment {

    public CustomAnswerSolutionFragment() {
        // Required empty public constructor
    }

    public static CustomAnswerSolutionFragment newInstance(CustomAnswerSolution sol, int current, int max) {
        CustomAnswerSolutionFragment fragment = new CustomAnswerSolutionFragment();
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
        CustomAnswerSolution solution = (CustomAnswerSolution) getSolution();
        CustomAnswerObjective objective = (CustomAnswerObjective) solution.getObjective();
        getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                CustomAnswerObjectiveFragment.newInstance(objective, false, solution.getAnswer())).commit();
        getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent,
                PointDisplayFragment.newInstance(currentPoints, maxPoints)).commit();
        return root;
    }
}
