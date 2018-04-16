package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.CustomAnswerObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Olyan feladatot reprezentáló osztály, melyre egy saját, szöveges választ kell adni.
 */
public class CustomAnswerObjective extends Objective {

    public String getAnswer() {
        return answer;
    }

    private String answer;

    public CustomAnswerObjective(String q){
        super(q);
    }

    @Override
    public ObjectiveFragment createFragment() {
        return CustomAnswerObjectiveFragment.newInstance(this);
    }

    public void setAnswer(String ans){
        answer = ans;
    }
}
