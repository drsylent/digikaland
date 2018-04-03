package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.PhysicalSolution;
import hu.bme.aut.digikaland.ui.common.objectives.PhysicalObjectiveFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class PhysicalSolutionFragment extends SolutionFragment {
    public PhysicalSolutionFragment() {
        // Required empty public constructor
    }

    public static PhysicalSolutionFragment newInstance(PhysicalSolution sol, int current, int max) {
        PhysicalSolutionFragment fragment = new PhysicalSolutionFragment();
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
        PhysicalSolution solution = (PhysicalSolution) getSolution();
        PhysicalObjective objective = (PhysicalObjective) solution.getObjective();
        getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                PhysicalObjectiveFragment.newInstance(objective)).commit();
        PointDisplayFragment fragment = PointDisplayFragment.newInstance(currentPoints, maxPoints, getTag());
        setPointHolder(fragment);
        getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent, fragment).commit();
        return root;
    }
}
