package hu.bme.aut.digikaland.ui.common.objectives;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;

public class PhysicalObjectiveFragment extends ObjectiveFragment {

    public PhysicalObjectiveFragment() {
        // Required empty public constructor
    }

    public static PhysicalObjectiveFragment newInstance(PhysicalObjective obj) {
        PhysicalObjectiveFragment fragment = new PhysicalObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void upload() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_physical_objective, container, false);
        TextView tvQuestion = root.findViewById(R.id.physicalQuestion);
        tvQuestion.setText(getObjective().getQuestion());
        return root;
    }
}
