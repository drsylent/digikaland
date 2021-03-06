package hu.bme.aut.digikaland.ui.common.objectives;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;

public class CustomAnswerObjectiveFragment extends ObjectiveFragment {
    private EditText etAnswer;
    private String answer = null;
    private CustomObjectiveListener objectiveActivity;

    public CustomAnswerObjectiveFragment() {
        // Required empty public constructor
    }

    public static CustomAnswerObjectiveFragment newInstance(CustomAnswerObjective obj) {
        return newInstance(obj, true, null);
    }

    public static CustomAnswerObjectiveFragment newInstance(CustomAnswerObjective obj, boolean editable, String answer) {
        CustomAnswerObjectiveFragment fragment = new CustomAnswerObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        args.putBoolean(ARG_EDIT, editable);
        if(!editable) args.putString(ARG_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(!editable) answer = getArguments().getString(ARG_ANSWER);
        }
    }

    private boolean isInputValid(String string){
        return Pattern.matches("\\w{0,150}", string);
    }

    @Override
    public void upload() {
        CustomAnswerObjective obj = (CustomAnswerObjective) getObjective();
        String answer = etAnswer.getText().toString();
        if(isInputValid(answer))
            obj.setAnswer(answer);
        else objectiveActivity.inputValidationError();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_answer_objective, container, false);
        TextView tvQuestion = root.findViewById(R.id.customQuestion);
        tvQuestion.setText(getObjective().getQuestion());
        etAnswer = root.findViewById(R.id.customAnswer);
        if(!editable) etAnswer.setText(answer);
        etAnswer.setEnabled(editable);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CustomObjectiveListener) {
            objectiveActivity = (CustomObjectiveListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CustomObjectiveListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        objectiveActivity = null;
    }

    public interface CustomObjectiveListener{
        void inputValidationError();
    }
}
