package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.CustomAnswerObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Created by Sylent on 2018. 03. 03..
 */

public class CustomAnswerObjective extends Objective {

    public CustomAnswerObjective(String q){
        super(q);
    }

    @Override
    public ObjectiveFragment createFragment() {
        return CustomAnswerObjectiveFragment.newInstance(this);
    }

    public void upload(String answer){
        // feltoltes
    }
}
