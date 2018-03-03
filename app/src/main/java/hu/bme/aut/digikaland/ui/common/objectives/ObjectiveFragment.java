package hu.bme.aut.digikaland.ui.common.objectives;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.Objective;

public abstract class ObjectiveFragment extends Fragment {
    protected static final String ARG_OBJECTIVE = "objective";

    private Objective objective;

    public ObjectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            objective = (Objective) getArguments().getSerializable(ARG_OBJECTIVE);
        }
    }

    public Objective getObjective(){
        return objective;
    }

    public abstract void upload();
}
