package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.MultipleChoiceObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Created by Sylent on 2018. 03. 03..
 */

public class MultipleChoiceObjective extends Objective {
    private String answer[];

    public MultipleChoiceObjective(String q, String answers[]){
        super(q);
        answer = answers;
    }

    public String getAnswer(int index){
        return answer[index];
    }

    public void upload(int index){
        // feltoltes
    }

    @Override
    public ObjectiveFragment createFragment() {
        return MultipleChoiceObjectiveFragment.newInstance(this);
    }
}
