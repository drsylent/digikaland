package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.ui.common.objectives.PictureObjectiveFragment;

public class PictureSolutionFragment extends SolutionFragment {

    public PictureSolutionFragment() {
        // Required empty public constructor
    }

    public static PictureSolutionFragment newInstance(PictureSolution sol, int current, int max) {
        PictureSolutionFragment fragment = new PictureSolutionFragment();
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
        PictureSolution solution = (PictureSolution) getSolution();
        PictureObjective objective = (PictureObjective) solution.getObjective();
        getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                PictureEvaluateFragment.newInstance(solution, 0, 3)).commit();
        PointDisplayFragment fragment = PointDisplayFragment.newInstance(currentPoints, maxPoints, getTag());
        setPointHolder(fragment);
        getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent, fragment).commit();
        return root;
    }
}
