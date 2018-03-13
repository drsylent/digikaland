package hu.bme.aut.digikaland.ui.common.objectives;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;

public class CustomAnswerObjectiveFragment extends ObjectiveFragment {

    private EditText etAnswer;

    public CustomAnswerObjectiveFragment() {
        // Required empty public constructor
    }

    public static CustomAnswerObjectiveFragment newInstance(CustomAnswerObjective obj) {
        CustomAnswerObjectiveFragment fragment = new CustomAnswerObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void upload() {
        CustomAnswerObjective obj = (CustomAnswerObjective) getObjective();
        obj.upload(etAnswer.getText().toString());
    }

    // TODO: minden Androidnál elmenti pl ezt a szöveget?
    /*
    private static final String ARG_ANSWER = "ans";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_ANSWER, etAnswer.getText().toString());
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_answer_objective, container, false);
        TextView tvQuestion = root.findViewById(R.id.customQuestion);
        tvQuestion.setText(getObjective().getQuestion());
        etAnswer = root.findViewById(R.id.customAnswer);
        return root;
    }
}
