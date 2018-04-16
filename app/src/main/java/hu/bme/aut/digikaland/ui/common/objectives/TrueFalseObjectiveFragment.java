package hu.bme.aut.digikaland.ui.common.objectives;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;

public class TrueFalseObjectiveFragment extends ObjectiveFragment {

    private CheckBox cbAnswer;
    private boolean answer;

    public TrueFalseObjectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void upload() {
        TrueFalseObjective obj = (TrueFalseObjective) getObjective();
        obj.setAnswer(cbAnswer.isChecked());
    }

    public static TrueFalseObjectiveFragment newInstance(TrueFalseObjective obj) {
        return newInstance(obj, true, false);
    }

    public static TrueFalseObjectiveFragment newInstance(TrueFalseObjective obj, boolean editable, boolean answer) {
        TrueFalseObjectiveFragment fragment = new TrueFalseObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        args.putBoolean(ARG_EDIT, editable);
        args.putBoolean(ARG_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(!editable) answer = getArguments().getBoolean(ARG_ANSWER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_true_false_objective, container, false);
        TextView tvQuestion = root.findViewById(R.id.trueFalseQuestion);
        tvQuestion.setText(getObjective().getQuestion());
        cbAnswer = root.findViewById(R.id.trueFalseCheckBox);
        if(!editable) {
            cbAnswer.setChecked(answer);
            cbAnswer.setEnabled(false);
        }
        return root;
    }
}
