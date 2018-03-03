package hu.bme.aut.digikaland.ui.common.objectives;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public static MultipleChoiceObjectiveFragment newInstance(MultipleChoiceObjective obj) {
        MultipleChoiceObjectiveFragment fragment = new MultipleChoiceObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void upload() {
        MultipleChoiceObjective obj = (MultipleChoiceObjective) getObjective();
        switch (rgAnswer.getCheckedRadioButtonId()){
            case rid0:
                obj.upload(0);
                break;
            case rid1:
                obj.upload(1);
                break;
            case rid2:
                obj.upload(2);
                break;
            case rid3:
                obj.upload(3);
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
        RadioButton rbSet = root.findViewById(rid0);
        rbSet.setText(obj.getAnswer(0));
        rbSet = root.findViewById(rid1);
        rbSet.setText(obj.getAnswer(1));
        rbSet = root.findViewById(rid2);
        rbSet.setText(obj.getAnswer(2));
        rbSet = root.findViewById(rid3);
        rbSet.setText(obj.getAnswer(3));
        return root;
    }
}
