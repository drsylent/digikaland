package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.MultipleChoiceSolution;
import hu.bme.aut.digikaland.ui.common.objectives.MultipleChoiceObjectiveFragment;

public class MultipleChoiceEvaluateFragment extends EvaluateFragment {

    public MultipleChoiceEvaluateFragment() {
        // Required empty public constructor
    }

    public static MultipleChoiceEvaluateFragment newInstance(MultipleChoiceSolution sol, int current, int max) {
        MultipleChoiceEvaluateFragment fragment = new MultipleChoiceEvaluateFragment();
        fragment.setArguments(createBundle(sol, current, max));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_evaluate, container, false);
        MultipleChoiceSolution solution = (MultipleChoiceSolution) getSolution();
        MultipleChoiceObjective objective = (MultipleChoiceObjective) solution.getObjective();
        if(savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                    MultipleChoiceObjectiveFragment.newInstance(objective, false, solution.getAnswer())).commit();
            PointDisplayFragment fragment = PointDisplayFragment.newInstance(currentPoints, maxPoints, getTag());
            setPointHolder(fragment);
            getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent, fragment, PointDisplayFragment.generateTag()).commit();
        }
        else{
            setPointHolder(savedInstanceState.getString(ARG_POINTFRAGTAG));
        }
        return root;
    }
}
