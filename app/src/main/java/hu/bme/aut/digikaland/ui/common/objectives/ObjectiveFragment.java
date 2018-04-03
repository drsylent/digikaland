package hu.bme.aut.digikaland.ui.common.objectives;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import hu.bme.aut.digikaland.entities.objectives.Objective;

public abstract class ObjectiveFragment extends Fragment {
    protected static final String ARG_OBJECTIVE = "objective";
    protected static final String ARG_EDIT = "edit";
    protected static final String ARG_ANSWER = "answer";

    private static int tagNumber = 0;

    public static String generateTag(){
        String tag = "ObjectiveFragmentTag" + tagNumber;
        tagNumber++;
        return tag;
    }

    private Objective objective;
    protected boolean editable;

    public ObjectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            objective = (Objective) getArguments().getSerializable(ARG_OBJECTIVE);
            editable = getArguments().getBoolean(ARG_EDIT);
        }
    }

    public Objective getObjective(){
        return objective;
    }

    public abstract void upload();
}
