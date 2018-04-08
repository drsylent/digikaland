package hu.bme.aut.digikaland.entities.objectives;

import java.util.ArrayList;

import hu.bme.aut.digikaland.ui.common.objectives.MultipleChoiceObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

public class MultipleChoiceObjective extends Objective {
    // TODO: mockolás miatt még itt hagyva
    private String answer[];

    public MultipleChoiceObjective(String q, String answers[]){
        super(q);
        answer = answers;
    }

    //    public String getAnswer(int index){
//        return answer[index];
//    }

    /////


    private ArrayList<String> answers;

    public MultipleChoiceObjective(String q, ArrayList<String> a){
        super(q);
        answers = a;
    }

    // TODO: Mock off
    public String getAnswer(int index){
        if(answers != null)
        return answers.get(index);
        else return answer[index];
    }

    public void upload(int index){
        // feltoltes
    }

    @Override
    public ObjectiveFragment createFragment() {
        return MultipleChoiceObjectiveFragment.newInstance(this);
    }
}
