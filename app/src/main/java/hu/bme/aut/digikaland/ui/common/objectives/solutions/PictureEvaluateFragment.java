package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;

public class PictureEvaluateFragment extends EvaluateFragment {

    public PictureEvaluateFragment() {
        // Required empty public constructor
    }

    public static PictureEvaluateFragment newInstance(PictureSolution sol, int current, int max) {
        PictureEvaluateFragment fragment = new PictureEvaluateFragment();
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
        if(savedInstanceState == null){
            getChildFragmentManager().beginTransaction().add(R.id.evaluateQuestionContent,
                    PictureSolutionFragment.newInstance(solution)).commit();
            PointDisplayFragment fragment = PointDisplayFragment.newInstance(currentPoints, maxPoints, getTag());
            setPointHolder(fragment);
            getChildFragmentManager().beginTransaction().add(R.id.evaluatePointContent, fragment, PointDisplayFragment.generateTag()).commit();
        }
        else setPointHolder(savedInstanceState.getString(ARG_POINTFRAGTAG));
        return root;
    }
}
