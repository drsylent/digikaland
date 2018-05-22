package hu.bme.aut.digikaland.entities.objectives;

import java.util.ArrayList;

import hu.bme.aut.digikaland.ui.common.objectives.MultipleChoiceObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

public class MultipleChoiceObjective extends Objective {
    public int getChosenIndex() {
        return chosenIndex;
    }

    private int chosenIndex;

    private ArrayList<String> answers;

    public MultipleChoiceObjective(String q, ArrayList<String> a){
        super(q);
        answers = a;
    }

    public String getAnswer(int index){
        return answers.get(index);
    }

    public void setChosen(int index){
        chosenIndex = index;
    }

    @Override
    public ObjectiveFragment createFragment() {
        return MultipleChoiceObjectiveFragment.newInstance(this);
    }
}
