package hu.bme.aut.digikaland.ui.common.objectives;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;

public class MultipleChoiceObjectiveFragment extends ObjectiveFragment {
    public MultipleChoiceObjectiveFragment() {
        // Required empty public constructor
    }

    RadioGroup rgAnswer;
    final int rid0 = R.id.multipleChoice0;
    final int rid1 = R.id.multipleChoice1;
    final int rid2 = R.id.multipleChoice2;
    final int rid3 = R.id.multipleChoice3;
    private int answerindex;

    public static MultipleChoiceObjectiveFragment newInstance(MultipleChoiceObjective obj) {
        return newInstance(obj, true, -1);
    }

    public static MultipleChoiceObjectiveFragment newInstance(MultipleChoiceObjective obj, boolean editable, int answerindex) {
        MultipleChoiceObjectiveFragment fragment = new MultipleChoiceObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        args.putBoolean(ARG_EDIT, editable);
        if(!editable) args.putInt(ARG_ANSWER, answerindex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(!editable) answerindex = getArguments().getInt(ARG_ANSWER);
        }
    }

    @Override
    public void upload() {
        MultipleChoiceObjective obj = (MultipleChoiceObjective) getObjective();
        switch (rgAnswer.getCheckedRadioButtonId()){
            case rid0:
                obj.setChosen(0);
                break;
            case rid1:
                obj.setChosen(1);
                break;
            case rid2:
                obj.setChosen(2);
                break;
            case rid3:
                obj.setChosen(3);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_multiple_choice_objective, container, false);
        TextView tvQuestion = root.findViewById(R.id.multipleChoiceQuestion);
        tvQuestion.setText(getObjective().getQuestion());
        rgAnswer = root.findViewById(R.id.multipleChoiceAnswer);
        MultipleChoiceObjective obj = (MultipleChoiceObjective) getObjective();
        int rid[] = { rid0, rid1, rid2, rid3 };
        RadioButton rbSet;
        for(int i = 0; i < rid.length; i++){
            rbSet = root.findViewById(rid[i]);
            rbSet.setText(obj.getAnswer(i));
            if(!editable) rbSet.setEnabled(false);
        }
        if(!editable) rgAnswer.check(rid[answerindex]);
        return root;
    }
}
